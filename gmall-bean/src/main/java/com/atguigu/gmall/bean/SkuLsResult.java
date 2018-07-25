package com.atguigu.gmall.bean;

import java.io.Serializable;
import java.util.List;
/*
* 从es中得到的数据，转换成我们的实体类数据，将数据在页面显示
* */
public class SkuLsResult implements Serializable {
    //es中每一条数据的集合
    List<SkuLsInfo> skuLsInfoList;
    //总条数
    long total;
    //总页数
    long totalPages;
    //属性id
    List<String> attrValueIdList;

    public SkuLsResult() {
    }

    public SkuLsResult(List<SkuLsInfo> skuLsInfoList, long total, long totalPages, List<String> attrValueIdList) {
        this.skuLsInfoList = skuLsInfoList;
        this.total = total;
        this.totalPages = totalPages;
        this.attrValueIdList = attrValueIdList;
    }

    public List<SkuLsInfo> getSkuLsInfoList() {
        return skuLsInfoList;
    }

    public void setSkuLsInfoList(List<SkuLsInfo> skuLsInfoList) {
        this.skuLsInfoList = skuLsInfoList;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(long totalPages) {
        this.totalPages = totalPages;
    }

    public List<String> getAttrValueIdList() {
        return attrValueIdList;
    }

    public void setAttrValueIdList(List<String> attrValueIdList) {
        this.attrValueIdList = attrValueIdList;
    }
}
