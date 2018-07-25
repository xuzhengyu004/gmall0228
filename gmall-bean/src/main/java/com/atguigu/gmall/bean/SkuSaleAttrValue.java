package com.atguigu.gmall.bean;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;

public class SkuSaleAttrValue implements Serializable {

    @Id
    @Column
    String id;

    @Column
    String skuId;

    @Column
    String saleAttrId;

    @Column
    String saleAttrValueId;

    @Column
    String saleAttrName;

    @Column
    String saleAttrValueName;

    //判断标志，当前是否修改属性，属性值的商品
    @Transient
    String isChecked;

    public String getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(String isChecked) {
        this.isChecked = isChecked;
    }

    public SkuSaleAttrValue() {
    }

    public SkuSaleAttrValue(String id, String skuId, String saleAttrId, String saleAttrValueId, String saleAttrName, String saleAttrValueName) {
        this.id = id;
        this.skuId = skuId;
        this.saleAttrId = saleAttrId;
        this.saleAttrValueId = saleAttrValueId;
        this.saleAttrName = saleAttrName;
        this.saleAttrValueName = saleAttrValueName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public String getSaleAttrId() {
        return saleAttrId;
    }

    public void setSaleAttrId(String saleAttrId) {
        this.saleAttrId = saleAttrId;
    }

    public String getSaleAttrValueId() {
        return saleAttrValueId;
    }

    public void setSaleAttrValueId(String saleAttrValueId) {
        this.saleAttrValueId = saleAttrValueId;
    }

    public String getSaleAttrName() {
        return saleAttrName;
    }

    public void setSaleAttrName(String saleAttrName) {
        this.saleAttrName = saleAttrName;
    }

    public String getSaleAttrValueName() {
        return saleAttrValueName;
    }

    public void setSaleAttrValueName(String saleAttrValueName) {
        this.saleAttrValueName = saleAttrValueName;
    }
}
