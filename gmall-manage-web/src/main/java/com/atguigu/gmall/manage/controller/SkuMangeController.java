package com.atguigu.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.BaseSaleAttr;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.bean.SpuInfo;
import com.atguigu.gmall.service.ManageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class SkuMangeController {
    @Reference
    private ManageService manageService;

    @RequestMapping(value = "saveSku",method = RequestMethod.POST)
    @ResponseBody
    public String saveSku(SkuInfo skuInfo){
        // 调用服务层
        // ctrl+alt+enter
        manageService.saveSku(skuInfo);
        return "success";
    }

}
