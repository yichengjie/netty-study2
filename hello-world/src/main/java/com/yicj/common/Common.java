package com.yicj.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class Common {

    public static String toJsonStr(Object obj){
        //重复引用单个关闭
        return JSON.toJSONString(obj, SerializerFeature.DisableCircularReferenceDetect);
    }
}
