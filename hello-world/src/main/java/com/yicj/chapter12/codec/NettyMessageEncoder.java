package com.yicj.chapter12.codec;

import com.yicj.chapter12.entity.NettyMessage;
import com.yicj.chapter12.util.MarshallingEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class NettyMessageEncoder extends MessageToMessageEncoder<NettyMessage> {
    private MarshallingEncoder marshallingEncoder ;

    public NettyMessageEncoder() throws IOException {
        this.marshallingEncoder = new MarshallingEncoder() ;
    }
    @Override
    protected void encode(ChannelHandlerContext ctx, NettyMessage msg, List<Object> list) throws Exception {
        if(msg == null || msg.getHeader() == null){
            throw new Exception("The encode message is null") ;
        }
        ByteBuf sendBuf = Unpooled.buffer() ;
        sendBuf.writeInt(msg.getHeader().getCrcCode()) ;
        sendBuf.writeInt(msg.getHeader().getLength()) ;
        sendBuf.writeLong(msg.getHeader().getSessionID()) ;
        sendBuf.writeByte(msg.getHeader().getType()) ;
        sendBuf.writeByte(msg.getHeader().getPriority()) ;
        sendBuf.writeInt(msg.getHeader().getAttachment().size()) ;
        for(Map.Entry<String,Object> param: msg.getHeader().getAttachment().entrySet()){
            String key = param.getKey() ;
            byte [] keyArray = key.getBytes("UTF-8") ;
            sendBuf.writeInt(keyArray.length) ;
            sendBuf.writeBytes(keyArray) ;
            Object value = param.getValue() ;
            marshallingEncoder.encode(value,sendBuf);
        }
        if(msg.getBody() != null){
            marshallingEncoder.encode(msg.getBody(), sendBuf);
        }else {
            sendBuf.writeInt(0) ;
            sendBuf.setIndex(4,sendBuf.readableBytes()) ;
        }
    }
}
