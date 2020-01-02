package com.yicj.rpc.remoting.netty.codec;

import com.yicj.rpc.model.RemotingTransporter;
import com.yicj.rpc.common.protocal.LaopopoProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.IOException;

import static com.yicj.rpc.common.serializer.SerializerHolder.serializerImpl;
import static com.yicj.rpc.common.protocal.LaopopoProtocol.MAGIC;

@ChannelHandler.Sharable
public class RemotingTransporterEncoder extends MessageToByteEncoder<RemotingTransporter> {
    @Override
    protected void encode(ChannelHandlerContext ctx, RemotingTransporter msg, ByteBuf out) throws IOException {
        doEncodeRemotingTransporter(msg, out);
    }

    private void doEncodeRemotingTransporter(RemotingTransporter msg, ByteBuf out) throws IOException {
        byte[] body = serializerImpl().writeObject(msg.getCustomHeader());

        byte isCompress = LaopopoProtocol.UNCOMPRESS;
//		if(body.length > 1024){ //经过测试，压缩之后的效率低于不压缩
//			isCompress = LaopopoProtocol.COMPRESS;
//			body = Snappy.compress(body);
//		}
        out.writeShort(MAGIC). 	           //协议头
        writeByte(msg.getTransporterType())// 传输类型 sign 是请求还是响应
        .writeByte(msg.getCode())          // 请求类型requestcode 表明主题信息的类型，也代表请求的类型
        .writeLong(msg.getOpaque())        //requestId
        .writeInt(body.length)             //length
        .writeByte(isCompress)			   //是否压缩
        .writeBytes(body);
    }
}
