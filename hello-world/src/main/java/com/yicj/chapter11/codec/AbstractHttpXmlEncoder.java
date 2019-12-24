package com.yicj.chapter11.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.JiBXException;

import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.List;

//client使用
public abstract class AbstractHttpXmlEncoder<T> extends MessageToMessageEncoder<T> {
    private IBindingFactory factory = null ;
    private StringWriter writer = null ;
    final static String CHARSET_NAME = "UTF-8" ;
    final static Charset UTF_8 = Charset.forName(CHARSET_NAME) ;

    protected ByteBuf encode0(ChannelHandlerContext ctx ,Object body) throws Exception {
        factory = BindingDirectory.getFactory(body.getClass()) ;
        writer = new StringWriter() ;
        IMarshallingContext mctx = factory.createMarshallingContext() ;
        mctx.setIndent(2);
        mctx.marshalDocument(body,CHARSET_NAME,null,writer);
        String xmlStr = writer.toString() ;
        writer.close();
        writer = null ;
        ByteBuf encodBuf = Unpooled.copiedBuffer(xmlStr,UTF_8) ;
        return encodBuf ;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //释放资源
        if(writer != null){
            writer.close();
            writer = null ;
        }
    }
}
