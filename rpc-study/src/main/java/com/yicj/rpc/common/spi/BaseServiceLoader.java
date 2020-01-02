package com.yicj.rpc.common.spi;

import java.util.ServiceLoader;

//SPI loader
public final class BaseServiceLoader {
    public static <S> S load(Class<S> serviceClass) {
        return ServiceLoader.load(serviceClass).iterator().next();
    }
}