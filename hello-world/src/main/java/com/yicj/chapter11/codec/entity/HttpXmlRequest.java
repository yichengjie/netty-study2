package com.yicj.chapter11.codec.entity;

import io.netty.handler.codec.http.FullHttpRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

@Data
@AllArgsConstructor
public class HttpXmlRequest {
    private FullHttpRequest request ;
    private Object body ;
}
