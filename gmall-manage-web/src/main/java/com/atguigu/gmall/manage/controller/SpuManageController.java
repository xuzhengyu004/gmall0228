package com.atguigu.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.service.ManageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SpuManageController {

    @Reference
    private ManageService manageService;


    //跳转页面
    @RequestMapping("spuListPage")
    public String getSpuListPage(){
        return "spuListPage";
    }

    //查询
    @RequestMapping("spuList")
    @ResponseBody
    public List<SpuInfo> spuInfoList(String catalog3Id){

        // 根据传递过来的catalog3Id 查询
        SpuInfo spuInfo = new SpuInfo();
        spuInfo.setCatalog3Id(catalog3Id);
        List<SpuInfo> list = manageService.getSpuInfoList(spuInfo);
        return list;
    }

    @RequestMapping("baseSaleAttrList")
    @ResponseBody
    public List<BaseSaleAttr> getBaseSaleAttrList(){
        List<BaseSaleAttr> baseSaleAttrList = manageService.getBaseSaleAttrList();
        return  baseSaleAttrList;
    }

    // 保存属性
    @RequestMapping(value = "saveSpuInfo",method = RequestMethod.POST)
    @ResponseBody
    public void saveSpuInfo(SpuInfo spuInfo){
        // 调用服务层方法
        manageService.saveSpuInfo(spuInfo);
    }

    //
    @RequestMapping("spuImageList")
    @ResponseBody
    public List<SpuImage> spuImageList(String spuId){
        return   manageService.getSpuImageList(spuId);
    }

    //销售属性
    @RequestMapping("spuSaleAttrList")
    @ResponseBody
    public List<SpuSaleAttr> getspuSaleAttrList(String spuId){
        List<SpuSaleAttr> spuSaleAttrList = manageService.getSpuSaleAttrList(spuId);
        for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
            List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
            Map map=new HashMap();
            map.put("total",spuSaleAttrValueList.size());
            map.put("rows",spuSaleAttrValueList);
            // String spuSaleAttrValueJson = JSON.toJSONString(map);
            spuSaleAttr.setSpuSaleAttrValueJson(map);
        }
        return  spuSaleAttrList;
    }

}
