package com.atguigu.gmall.cart.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.CartInfo;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.cart.constant.CartConst;
import com.atguigu.gmall.cart.mapper.CartInfoMapper;
import com.atguigu.gmall.config.RedisUtil;
import com.atguigu.gmall.service.CartService;
import com.atguigu.gmall.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.*;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartInfoMapper cartInfoMapper;

    @Autowired
    private RedisUtil redisUtil;
    // 需要调用getSkuInfo(userId)
    @Reference
    private ManageService manageService;

    @Override
    public void addToCart(String skuId, String userId, Integer skuNum) {
        // 查看购物车CartInfo中是否有该商品
        CartInfo cartInfoQuery = new CartInfo();
        cartInfoQuery.setSkuId(skuId);
        cartInfoQuery.setUserId(userId);

        CartInfo cartInfoExist  = cartInfoMapper.selectOne(cartInfoQuery);
        if (cartInfoExist !=null){
            cartInfoExist.setSkuNum(cartInfoExist.getSkuNum()+skuNum);
            cartInfoMapper.updateByPrimaryKeySelective(cartInfoExist);
            // 更改也需要变动缓存
        }else{
            // 没有创建购物车
            // 根据skuId 查找商品信息
            SkuInfo skuInfo = manageService.getSkuInfo(skuId);
            CartInfo cartInfo = new CartInfo();
            cartInfo.setSkuId(skuId);
            cartInfo.setUserId(userId);
            cartInfo.setSkuNum(skuNum);
            cartInfo.setSkuName(skuInfo.getSkuName());
            // 实施价格
            cartInfo.setSkuPrice(skuInfo.getPrice());
            // 添加购物车时候的价格
            cartInfo.setCartPrice(skuInfo.getPrice());
            cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
            // 新添加的购物车插入的时候，也需要插入缓存
            cartInfoMapper.insertSelective(cartInfo);
            // 将插入的信息也给已经存在的购物车对象
            cartInfoExist=cartInfo;

        }
        // 想办法将购物车数据放到redis中
        // hset(key,field,value) :key = （user:userId:cart）
        String userCartKey = CartConst.USER_KEY_PREFIX+userId+CartConst.USER_CART_KEY_SUFFIX;
        // 获取redis对象
        Jedis jedis = redisUtil.getJedis();
        // 保存的数据
        String cartJson  = JSON.toJSONString(cartInfoExist);
        jedis.hset(userCartKey,skuId,cartJson );
        // 细节的地方！user:userId+:info
        String userInfoKey = CartConst.USER_KEY_PREFIX+userId+CartConst.USERINFOKEY_SUFFIX;
        Long ttl = jedis.ttl(userInfoKey);
        /*设置过期时间*/
        jedis.expire(userCartKey,ttl.intValue());
        jedis.close();
    }

    @Override
    public List<CartInfo> getCartList(String userId) {
        // 看缓存，数据库！   缓存key
        String userCartKey = CartConst.USER_KEY_PREFIX+userId+CartConst.USER_CART_KEY_SUFFIX;
        // 创建redis对象 hset(key,field,value); hget(key,field) hget(userCartKey , skuId);
        Jedis jedis = redisUtil.getJedis();
        // redis=hash --- java=list;
        List<String> cartJsons  = jedis.hvals(userCartKey);
        // 循环
        // 准备一个新的集合：
        if (cartJsons!=null && cartJsons.size()>0){
            List<CartInfo> cartInfoList = new ArrayList<>();
            for (String cartJson : cartJsons) {
                // 将对象转换成cartInfo对象
                CartInfo cartInfo = JSON.parseObject(cartJson, CartInfo.class);
                cartInfoList.add(cartInfo);
            }
            // hash 有顺序么？ 根据id进行排序 time 外部比较器
            cartInfoList.sort(new Comparator<CartInfo>() {
                @Override
                public int compare(CartInfo o1, CartInfo o2) {
                    return o1.getId().compareTo(o2.getId());
                }
            });
            return cartInfoList;
        }else {
            // 走数据库，验价过程！ sku_Info 中的price ，car_Info cartprice ,将数据库中的数据放入缓存
            List<CartInfo> cartInfoList = loadCartCache(userId);
            return  cartInfoList;

        }
    }

    @Override
    public List<CartInfo> mergeToCartList(List<CartInfo> cartListFromCookie, String userId) {
        // 循环判断
        List<CartInfo> cartInfoListDB = cartInfoMapper.selectCartListWithCarPrice(userId);
        for (CartInfo cartInfoCK : cartListFromCookie) {
            // 有相同的，没有相同[insert]
            boolean isMatch = false;
            for (CartInfo infoDB : cartInfoListDB) {
                // 如果skuId 相同，则说明是同一个商品，则数据要增加
                if (cartInfoCK.getSkuId().equals(infoDB.getSkuId())){
                    infoDB.setSkuNum(cartInfoCK.getSkuNum()+infoDB.getSkuNum());
                    cartInfoMapper.updateByPrimaryKeySelective(infoDB);
                    isMatch=true;
                }
            }
            // 插入信息
            if (!isMatch){
                // userId 赋值
                cartInfoCK.setUserId(userId);
                cartInfoMapper.insertSelective(cartInfoCK);
            }
        }

        // loadCartCache ： 根据userId 先查数据库，在放缓存
        List<CartInfo> cartInfoList = loadCartCache(userId);
        // 需要跟cookie中的数据就行匹配：{isChecked} ,根据skuId 。并且 isChecked = “1”;从新更新数据库！
        for (CartInfo cartInfo : cartInfoList) {
            for (CartInfo info : cartListFromCookie) {
                // 找skuId
                if (cartInfo.getSkuId().equals(info.getSkuId())){
                    // 并且isChecked 为 1
                    if ("1".equals(info.getIsChecked())){
                        // 将数据从新更新，redis
                        cartInfo.setIsChecked("1");
                        checkCart(userId,cartInfo.getSkuId(),info.getIsChecked());
                    }
                }
            }
        }
        return cartInfoList;

    }
    /*购物车商品选中的状态*/
    @Override
    public void checkCart(String userId, String skuId, String isChecked) {
        // 取出redis 数据  定义key
        String userCartKey = CartConst.USER_KEY_PREFIX+userId+CartConst.USER_CART_KEY_SUFFIX;
        Jedis jedis = redisUtil.getJedis();
        // hget:因为skuId 是唯一的！
        String cartInfoJson = jedis.hget(userCartKey, skuId);
        // 将字符串转换成对象
        CartInfo cartInfo = JSON.parseObject(cartInfoJson, CartInfo.class);
        // 对该对象进行修改状态
        cartInfo.setIsChecked(isChecked);
        // 将修改后的数据存入redis
        jedis.hset(userCartKey,skuId,JSON.toJSONString(cartInfo));
        // 将所有选中的商品保存到一个新的key中
        String cartIsCheckKey = CartConst.USER_KEY_PREFIX+userId+CartConst.USER_CHECKED_KEY_SUFFIX;
        // 将数据放入redis,当 isChecked 为 1 时创建，0删除
        if ("1".equals(isChecked)){
            jedis.hset(cartIsCheckKey,skuId,JSON.toJSONString(cartInfo));
        }else {
            jedis.hdel(cartIsCheckKey,skuId);
        }
    }

    /*订单中获取购物车选中的列表*/
    @Override
    public List<CartInfo> getCartCheckedList(String userId) {
        //根据userId获得redis中的key
        String userCheckedKey  =
                CartConst.USER_KEY_PREFIX + userId + CartConst.USER_CART_KEY_SUFFIX;
        /*创建redis的对象 */
        Jedis jedis = redisUtil.getJedis();
        List<String> cartCheckedList  = jedis.hvals(userCheckedKey);
        /*将字符串集合转换成对象，创建集合*/
        List<CartInfo> newCartList = new ArrayList<>();
        for (String cartJson : cartCheckedList) {
            /*将字符串集合转换成对象*/
            CartInfo cartInfo = JSON.parseObject(cartJson,CartInfo.class);
            newCartList.add(cartInfo);
        }
        return newCartList;
    }

    private List<CartInfo> loadCartCache(String userId) {
        // 在mapper中写个方法，
        List<CartInfo> cartInfoList = cartInfoMapper.selectCartListWithCarPrice(userId);
        //  判断集合
        if (cartInfoList!=null && cartInfoList.size()>0){
            // 准备放入redis
            Jedis jedis = redisUtil.getJedis();
            // 对数据进行转换 hset(key,field,value);
            String userCartKey = CartConst.USER_KEY_PREFIX+userId+CartConst.USER_CART_KEY_SUFFIX;
            // field，value === 正好对应上我们的map.put(field,value) jedis.hmset(userCartKey,map);
            Map<String,String> map = new HashMap<>(cartInfoList.size());
            for (CartInfo cartInfo : cartInfoList) {
                // 将cartInfo 转换成对象
                String cartJson  = JSON.toJSONString(cartInfo);
                map.put(cartInfo.getSkuId(),cartJson);
            }
            // 往redis 中添加数据
            jedis.hmset(userCartKey,map);
            jedis.close();
        }
        return cartInfoList;
    }

}