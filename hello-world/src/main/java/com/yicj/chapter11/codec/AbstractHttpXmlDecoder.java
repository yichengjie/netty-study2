package com.yicj.chapter11.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;

import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.List;

@Slf4j
public class AbstractHttpXmlDecoder<T> extends MessageToMessageDecoder<T> {

    private IBindingFactory factory ;
    private StringReader reader ;
    private Class<?> clazz ;
    private boolean isPrint ;
    private final static String CHARSET_NAME = "UTF-8";
    private final static Charset UTF_8 = Charset.forName(CHARSET_NAME) ;
    protected AbstractHttpXmlDecoder(Class<?> clazz){
        this(clazz,false) ;
    }
    protected AbstractHttpXmlDecoder(Class<?> clazz,boolean isPrint){
        this.clazz = clazz ;
        this.isPrint = isPrint ;
    }
    protected Object decode0(ChannelHandlerContext ctx, ByteBuf body) throws Exception {
        factory = BindingDirectory.getFactory(clazz);
        String content = body.toString(UTF_8) ;
        if(isPrint){
            log.info("The body is : " + content);
        }
        reader = new StringReader(content) ;
        IUnmarshallingContext uctx = factory.createUnmarshallingContext() ;
        Object result = uctx.unmarshalDocument(reader);
        reader.close();
        reader = null ;
        return result ;
    }


    @Override
    protected void decode(ChannelHandlerContext ctx, T t, List<Object> list) throws Exception {

    }
}
