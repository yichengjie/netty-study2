package com.yicj.chapter12.codec;

import com.yicj.chapter12.entity.Header;
import com.yicj.chapter12.entity.NettyMessage;
import com.yicj.chapter12.util.MarshallingDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class NettyMessageDecoder extends LengthFieldBasedFrameDecoder {
    private MarshallingDecoder marshallingDecoder ;

    public NettyMessageDecoder(int maxFrameLength,
           int lengthFieldOffset, int lengthFieldLength) throws IOException {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
        this.marshallingDecoder = new MarshallingDecoder() ;
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx,in) ;
        if(frame == null){
            return null ;
        }
        NettyMessage message = new NettyMessage() ;
        Header header = new Header() ;
        header.setCrcCode(frame.readInt());
        header.setLength(frame.readInt());
        header.setSessionID(frame.readLong());
        header.setType(frame.readByte());
        header.setPriority(frame.readByte());
        int size = frame.readInt() ;
        if(size > 0){
            Map<String,Object> attch = new HashMap<>(size) ;
            for(int i = 0 ; i < size ; i++){
                int keySize = frame.readInt() ;
                byte [] keyArray = new byte[keySize] ;
                frame.readBytes(keyArray) ;
                String key = new String(keyArray,"UTF-8") ;
                attch.put(key,marshallingDecoder.decode(frame)) ;
            }
            header.setAttachment(attch);
        }
        if(frame.readableBytes() > 4){
            message.setBody(marshallingDecoder.decode(frame));
        }
        message.setHeader(header);
        log.info("NettyMessageDecoder decode msg : {}" ,message);
        return message ;
    }
}
