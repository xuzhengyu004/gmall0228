package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.SkuLsInfo;
import com.atguigu.gmall.bean.SkuLsParams;
import com.atguigu.gmall.bean.SkuLsResult;

//商品检索的接口
public interface ListService {
    //保存商品信息到es
    public void saveSkuLsInfo(SkuLsInfo skuLsInfo);

    /*准备完成dsl语句的功能*/
    public SkuLsResult search(SkuLsParams skuLsParams);

    /*准备更新redis，更新redis达到一定的次数，更新es*/
    public void incrHotScore(String skuId);

}
