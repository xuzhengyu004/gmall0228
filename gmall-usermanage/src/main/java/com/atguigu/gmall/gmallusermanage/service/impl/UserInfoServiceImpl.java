package com.atguigu.gmall.gmallusermanage.service.impl;



import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.UserInfo;
import com.atguigu.gmall.config.RedisUtil;
import com.atguigu.gmall.gmallusermanage.mapper.UserInfoMapper;
import com.atguigu.gmall.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    public String userKey_prefix="user:";
    public String userinfoKey_suffix=":info";
    public int userKey_timeOut=60*60;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public List<UserInfo> findall() {
        return userInfoMapper.selectAll();
    }

    @Override
    public List<UserInfo> findlike() {
        Example example = new Example(UserInfo.class);
        example.createCriteria().andLike("loginName","%张%");
        List<UserInfo> userInfos = userInfoMapper.selectByExample(example);

        return userInfos;
    }

    @Override
    public void addUserInfo(UserInfo userInfo) {
        userInfoMapper.insert(userInfo);
    }

    @Override
    public void updateUserInfo(UserInfo userInfo) {

        userInfoMapper.updateByPrimaryKey(userInfo);
    }

    @Override
    public void updateUserInfo1(UserInfo userInfo) {
        //根据名称修改
        Example example = new Example(UserInfo.class);
        example.createCriteria().andEqualTo("loginName",userInfo.getLoginName());
        userInfoMapper.updateByExampleSelective(userInfo,example);
    }
    /*用户登录成功后，保存到redis中*/
    @Override
    public UserInfo login(UserInfo userInfo) {
        // 需要给密码加密
        String passwd = userInfo.getPasswd();
        // spring 框架的工具类。
        String newpassword = DigestUtils.md5DigestAsHex(passwd.getBytes());
        userInfo.setPasswd(newpassword);
        UserInfo info = userInfoMapper.selectOne(userInfo);
        if (info!=null){
            // 将用户信息放入redis！
            Jedis jedis = redisUtil.getJedis();
            jedis.setex(userKey_prefix+info.getId()+userinfoKey_suffix,userKey_timeOut, JSON.toJSONString(info));
            jedis.close();
            return info;
        }
        return null;
    }

    /*验证，根据userId查找查找userInfo的信息*/
    @Override
    public UserInfo verify(String userId) {
        // 去缓存中查询是否有redis
        Jedis jedis = redisUtil.getJedis();
        String key = userKey_prefix+userId+userinfoKey_suffix;
        String userJson = jedis.get(key);
        // 延长时效
        jedis.expire(key,userKey_timeOut);
        /*判断redis中的userJson是否存在*/
        if (userJson!=null){
            UserInfo userInfo = JSON.parseObject(userJson, UserInfo.class);
            return  userInfo;
        }
        return  null;
    }

    @Override
    public void deleteUserIndo(UserInfo userInfo) {
        String id = userInfo.getId();
        userInfoMapper.deleteByPrimaryKey(id);
    }


}
