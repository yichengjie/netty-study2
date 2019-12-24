package com.yicj.chapter11.codec.entity;

import io.netty.handler.codec.http.FullHttpResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class HttpXmlResponse {
    private FullHttpResponse response ;
    private Object result ;
}
