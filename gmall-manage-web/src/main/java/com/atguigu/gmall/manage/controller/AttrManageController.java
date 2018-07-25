package com.atguigu.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.service.ListService;
import com.atguigu.gmall.service.ManageService;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Controller
public class AttrManageController {

    @Reference
    private ManageService manageService;

    @Reference
    private ListService listService;

    @RequestMapping("attrListPage")
    public String getAttrListPage(){
        return "attrListPage";
    }
    /*
    * 获得一级分类信息
    */
    @RequestMapping(value = "getCatalog1" ,method = RequestMethod.POST)
    @ResponseBody
    public List<BaseCatalog1> getCatalog1(){
        List<BaseCatalog1> catalog1List = manageService.getCatalog1();
        return catalog1List;
    }

    /*
    * 获得二级分类信息
    */
    @ResponseBody
    @RequestMapping(value = "getCatalog2" ,method = RequestMethod.POST)
    public List<BaseCatalog2> getCatalog2(String catalog1Id){
        List<BaseCatalog2> catalog2List = manageService.getCatalog2(catalog1Id);
        return catalog2List;
    }

    /*
    * 获得三级分类信息
    */
    @ResponseBody
    @RequestMapping(value = "getCatalog3" ,method = RequestMethod.POST)
    public List<BaseCatalog3> getCatalog3(String catalog2Id){
        List<BaseCatalog3> catalog3List = manageService.getCatalog3(catalog2Id);
        return catalog3List;
    }
    /*
    * 获得属性信息
    */
    @RequestMapping(value = "attrInfoList",method = RequestMethod.GET)
    @ResponseBody
    public List<BaseAttrInfo> attrInfoList(String catalog3Id){
        return manageService.getAttrList(catalog3Id);
    }

    /*
    * 接收前台的json数据
    */
    @RequestMapping(value = "saveAttrInfo",method = RequestMethod.POST)
    @ResponseBody
    public String saveAttrInfo(BaseAttrInfo baseAttrInfo){
        manageService.saveAttrInfo(baseAttrInfo);
        return "success";
    }

    /*返回没有任何显示的消息*/
    @ResponseBody
    @RequestMapping("getAttrValueList")
    public List<BaseAttrValue> getAttrValueList(String attrId){
        //根据attrId查询
        BaseAttrInfo attrInfo = manageService.getAttrInfo(attrId);
        return attrInfo.getAttrValueList();
    }
    //根据什么来进行添加到es中 SkuId
    @RequestMapping(value = "onSale",method = RequestMethod.GET)
    @ResponseBody
    public void onSale(String skuId){
        //通过商品的id 查找商品
        SkuInfo skuInfo = manageService.getSkuInfo(skuId);
        SkuLsInfo skuLsInfo = new SkuLsInfo();
        //数据库skuInfo的商品，应该赋给es中定义好的skuInfo
        //属性拷贝 BeanUtils.copyProperties()
        try {
            BeanUtils.copyProperties(skuLsInfo,skuInfo);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        //坑！商品的销售要放进去
        listService.saveSkuLsInfo(skuLsInfo);
    }


}
