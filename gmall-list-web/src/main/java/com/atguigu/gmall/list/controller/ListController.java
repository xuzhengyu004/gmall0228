package com.atguigu.gmall.list.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.BaseAttrInfo;
import com.atguigu.gmall.bean.BaseAttrValue;
import com.atguigu.gmall.bean.SkuLsParams;
import com.atguigu.gmall.bean.SkuLsResult;
import com.atguigu.gmall.service.ListService;
import com.atguigu.gmall.service.ManageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Controller
public class ListController {

    @Reference
    private ManageService manageService;

    @Reference
    private ListService listService;
    /*list*/
    @RequestMapping(value = "list.html",method = RequestMethod.GET)
    //@ResponseBody
    public String getList(SkuLsParams skuLsParams, Model model){
        //从es中得到的数据
        SkuLsResult skuLsResult = listService.search(skuLsParams);
        System.out.println(JSON.toJSONString(skuLsResult));
        /*将数据保存到model中，供前台使用*/
        model.addAttribute("skuLsInfoList",skuLsResult.getSkuLsInfoList());

        //查出平台属性， 和平台属性值 ，attrValurIdList是value属性id的集合
        List<String> attrValueIdList = skuLsResult.getAttrValueIdList();

        //根据id查询数据，从哪个项目中的得来，manageService
        List<BaseAttrInfo> attrList = manageService.getAttrList(attrValueIdList);
        //保存，传递到前台
        model.addAttribute("attrList",attrList);


        // 找出现哪些属性被选中了！
        ArrayList<BaseAttrValue> baseAttrValueArrayList = new ArrayList<>();
        //  做个url拼接 ,参数skuLsParams.
        String makeUrl =  makeUrlParam(skuLsParams );
        // makeUrl 是针对于属性id而来，而SkuLsParams 也有可能会携带跟属性id相同的查询条件。【如果有相同，则应该去掉相同部分条件】
        //  itco 在集合遍历期间应该使用 迭代器，不能使用for循环
        for (Iterator<BaseAttrInfo> iterator = attrList.iterator(); iterator.hasNext(); ) {
            // 取得每一个BaseAttrInfo对象
            BaseAttrInfo baseAttrInfo = iterator.next();
            // 取得每个属性值
            List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
            // 循环
            for (BaseAttrValue baseAttrValue : attrValueList) {

                if (baseAttrValue.getId()!=null&& baseAttrValue.getId().length()>0){
                    // baseAttrValue.getId() 跟 SkuLsParams中的id集合是否相同
                    if (skuLsParams.getValueId()!=null && skuLsParams.getValueId().length>0){

                        for (String valueId : skuLsParams.getValueId()) {

                            if (valueId.equals(baseAttrValue.getId())){
                                iterator.remove();
                                // 创建一个被选中属性值的对象
                                BaseAttrValue baseAttrValueSelected   = new BaseAttrValue();
                                // 屏幕尺寸:5.1-5.5英寸
                                baseAttrValueSelected.setValueName(baseAttrInfo.getAttrName()+":"+baseAttrValue.getValueName());
                                // 添加之前去重复
                                String urlParam = makeUrlParam(skuLsParams, valueId);
                                baseAttrValueSelected.setUrlParam(urlParam);
                                // 将更改后的baseAttrValue对象添加到一个被选中的集合中。
                                baseAttrValueArrayList.add(baseAttrValueSelected);
                            }
                        }
                    }
                }
            }
        }

        int totalPages = (int) ((skuLsResult.getTotal() + skuLsParams.getPageSize()-1)/skuLsParams.getPageSize());
        // skuLsResult.setTotalPages(totalPages);
        model.addAttribute("totalPages",totalPages);
        model.addAttribute("pageNo",skuLsParams.getPageNo());

        // 将从新制作的url保存起来
        model.addAttribute("urlParam",makeUrl);

        // 将被选中的属性值的集合保存起来
        model.addAttribute("baseAttrValuesList",baseAttrValueArrayList);
        // 全局检索值
        model.addAttribute("keyword",skuLsParams.getKeyword());



        return "list";
    }

    // 拼接方法 ，判断页面传递过来的参数，在makeUrl中是否存在
    private String makeUrlParam(SkuLsParams skuLsParams,String... excludeValueIds) {
        // 拼接字符串
        String makeUrl = "";
        if (skuLsParams.getKeyword()!=null && skuLsParams.getKeyword().length()>0){
            makeUrl+="keyword="+skuLsParams.getKeyword();
        }
        // 三级分类id
        if (skuLsParams.getCatalog3Id()!=null && skuLsParams.getCatalog3Id().length()>0){
            if (makeUrl.length()>0){
                makeUrl+="&";
            }
            makeUrl+="catalog3Id="+skuLsParams.getCatalog3Id();
        }
        // 属性id
        if (skuLsParams.getValueId()!=null && skuLsParams.getValueId().length>0){
            // 循环 fori
            for (int i = 0; i < skuLsParams.getValueId().length; i++) {
                String valueId = skuLsParams.getValueId()[i];
                // 传递进来的值，跟valueId做比较，一样就不拼接！
                if (excludeValueIds !=null && excludeValueIds.length>0){
                    String excludeValueId = excludeValueIds[0];
                    if (excludeValueId.equals(valueId)){
                        // 后面代码不走。
                        continue;
                    }
                }
                if (makeUrl.length()>0){
                    makeUrl+="&";
                }
                makeUrl+="valueId="+valueId;
            }
        }

        return makeUrl;

    }
}
