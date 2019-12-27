package com.yicj.chapter11_2.codec.entity;

import io.netty.handler.codec.http.FullHttpRequest;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HttpJsonRequest {
    private FullHttpRequest request ;
    private Object body ;
}
