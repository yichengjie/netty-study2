package com.yicj.rpc.common.serializer;

import com.yicj.rpc.common.spi.BaseServiceLoader;

//序列化的入口,基于SPI方式
public final class SerializerHolder {
    // SPI
    private static final Serializer serializer = BaseServiceLoader.load(Serializer.class);
    public static Serializer serializerImpl() {
        return serializer;
    }
}