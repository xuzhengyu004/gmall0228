package com.atguigu.gmall.item.controller;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.SkuImage;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.bean.SkuSaleAttrValue;
import com.atguigu.gmall.bean.SpuSaleAttr;
import com.atguigu.gmall.config.LoginRequire;
import com.atguigu.gmall.service.ListService;
import com.atguigu.gmall.service.ManageService;
import groovy.util.ObjectGraphBuilder;
import jdk.nashorn.internal.runtime.arrays.IteratorAction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import sun.awt.util.IdentityArrayList;

import java.util.HashMap;
import java.util.List;

@Controller
public class ItemController {

    @Reference
    private ManageService manageService;

    @Reference
    private ListService listService;



    /*restful风格 skuId=商品id号*/
    @RequestMapping("{skuId}.html")
    /*该控制器需要登录*/
    @LoginRequire(autoRedirect = true)
    public String skuInfoPage(@PathVariable(value = "skuId") String skuId, Model model){

        /*根据skuId查询商品信息，以及商品对应的skuImg信息*/
        SkuInfo skuInfo = manageService.getSkuInfo(skuId);
//        List<SkuImage> list = skuInfo.getSkuImageList();
//        for (int i = 0; i < list.size(); i++) {
//            String url = list.get(i).getImgUrl();
//            System.out.println(url);
//        }
        /*保存对象，在页面中显示*/
        model.addAttribute("skuInfo",skuInfo);

        //显示销售属性值，销售属性
        List<SpuSaleAttr> saleAttrList = manageService.selectSpuSaleAttrListCheckBySku(skuInfo);

        //组装后台传递到前台的json字符串
        List<SkuSaleAttrValue> skuSaleAttrValueListBySpu = manageService.getSkuSaleAttrValueListBySpu(skuInfo.getSpuId());
        // "1|20" ："17"    {"6|4":"7"}
        //先声明一个字符串
        String valueIdsKey = "";
        //需要定义一个map集合
        HashMap<String, String> map = new HashMap<String, String>();
        //循环拼接
        for (int i = 0; i < skuSaleAttrValueListBySpu.size(); i++) {
            //取得第一个值
            SkuSaleAttrValue skuSaleAttrValue = skuSaleAttrValueListBySpu.get(i);
            //什么时候加|
            if (valueIdsKey.length()>0){
                valueIdsKey += "|";
            }
            valueIdsKey += skuSaleAttrValue.getSaleAttrValueId();

            //停止拼接
            if((i+1)==skuSaleAttrValueListBySpu.size() || !skuSaleAttrValue.getSkuId().equals(skuSaleAttrValueListBySpu.get(i+1).getSkuId())){
                map.put(valueIdsKey,skuSaleAttrValue.getSkuId());
                valueIdsKey += "";
            }
        }
        //将map转换成json字符串
        String valuesSkuJson = JSON.toJSONString(map);

        System.out.println(valuesSkuJson);
        //放到前台
        model.addAttribute("valuesSkuJson",valuesSkuJson);

        model.addAttribute("saleAttrList",saleAttrList);
        listService.incrHotScore(skuId);

        return "item";
    }
}