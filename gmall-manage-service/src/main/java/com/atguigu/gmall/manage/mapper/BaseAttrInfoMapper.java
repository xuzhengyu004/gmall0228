package com.atguigu.gmall.manage.mapper;

import com.atguigu.gmall.bean.BaseAttrInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseAttrInfoMapper extends Mapper<BaseAttrInfo> {

    /*根据三级分类id查询平台属性，平台属性值*/
    List<BaseAttrInfo> getBaseAttrInfoListByCatalog3Id(Long catalog3Id);
    /*根据id查询数据,  @Param 必填！*/
    List<BaseAttrInfo> selectAttrInfoListByIds(@Param(value = "ValueIds") String ValueIds);
}
