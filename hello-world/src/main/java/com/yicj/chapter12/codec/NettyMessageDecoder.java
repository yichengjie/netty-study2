package com.yicj.chapter12.codec;

import com.yicj.chapter12.entity.Header;
import com.yicj.chapter12.entity.NettyMessage;
import com.yicj.chapter12.util.MarshallingDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
        header.setCrcCode(in.readInt());
        header.setLength(in.readInt());
        header.setSessionID(in.readLong());
        header.setType(in.readByte());
        header.setPriority(in.readByte());
        int size = in.readInt() ;
        if(size > 0){
            Map<String,Object> attch = new HashMap<>(size) ;
            for(int i = 0 ; i < size ; i++){
                int keySize = in.readInt() ;
                byte [] keyArray = new byte[keySize] ;
                in.readBytes(keyArray) ;
                String key = new String(keyArray,"UTF-8") ;
                attch.put(key,marshallingDecoder.decode(in)) ;
            }
            header.setAttachment(attch);
        }
        if(in.readableBytes() > 4){
            message.setBody(marshallingDecoder.decode(in));
        }
        message.setHeader(header);
        return message ;
    }
}
