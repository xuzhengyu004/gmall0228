package com.atguigu.gmall.bean;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

public class SkuAttrValue implements Serializable {

    @Id
    @Column
    String id;

    @Column
    String attrId;

    @Column
    String valueId;

    @Column
    String skuId;

    public SkuAttrValue() {
    }

    public SkuAttrValue(String id, String attrId, String valueId, String skuId) {
        this.id = id;
        this.attrId = attrId;
        this.valueId = valueId;
        this.skuId = skuId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAttrId() {
        return attrId;
    }

    public void setAttrId(String attrId) {
        this.attrId = attrId;
    }

    public String getValueId() {
        return valueId;
    }

    public void setValueId(String valueId) {
        this.valueId = valueId;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }
}