package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.*;

import java.util.List;

public interface ManageService {
    //查询一级分类
    public List<BaseCatalog1> getCatalog1();
    //根据一级ID查询二级分类
    public List<BaseCatalog2> getCatalog2(String catalog1Id);
    //根据二级ID查询三级分类
    public List<BaseCatalog3> getCatalog3(String catalog2Id);
    //根据三级ID查询id属性
    public List<BaseAttrInfo> getAttrList(String catalog3Id);
    //保存数据方法
    void saveAttrInfo(BaseAttrInfo baseAttrInfo);
    //获取属性值数据
    BaseAttrInfo getAttrInfo(String attrId);
    //查询spu信息
    List<SpuInfo> getSpuInfoList(SpuInfo spuInfo);
    //查询所有销售属性列表
    List<BaseSaleAttr> getBaseSaleAttrList();
    //保存方法
    void saveSpuInfo(SpuInfo spuInfo);
    //根据前台传递的spuId查询spuImage列表
    List<SpuImage> getSpuImageList(String spuId);
    //根据前台传递的spuId查询销售属性列表
    List<SpuSaleAttr> getSpuSaleAttrList(String spuId);
    //sku保存信息
    void saveSku(SkuInfo skuInfo);
    //根据skuId查询信息
    SkuInfo getSkuInfo(String skuId);

    //根据skuId，spuId查询属性值
    List<SpuSaleAttr> selectSpuSaleAttrListCheckBySku(SkuInfo skuInfo);

    //根据spuId拼接属性值方法
    List<SkuSaleAttrValue> getSkuSaleAttrValueListBySpu(String spuId);
    //根据属性id查询属性名称，以及属性值
    List<BaseAttrInfo> getAttrList(List<String> attrValueIdList);
}
