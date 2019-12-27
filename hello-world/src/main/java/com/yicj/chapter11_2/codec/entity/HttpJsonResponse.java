package com.yicj.chapter11_2.codec.entity;

import io.netty.handler.codec.http.FullHttpResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HttpJsonResponse {
    private FullHttpResponse response ;
    private Object result ;
}
