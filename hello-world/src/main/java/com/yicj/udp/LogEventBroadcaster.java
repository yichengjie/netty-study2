package com.yicj.udp;

import com.yicj.udp.codec.LogEventEncoder;
import com.yicj.udp.entity.LogEvent;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;

public class LogEventBroadcaster {

    private final EventLoopGroup group ;
    private final Bootstrap bootstrap ;
    private final File file ;

    public LogEventBroadcaster(InetSocketAddress address, File file){
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap() ;
        bootstrap.group(group) ;
        bootstrap.channel(NioDatagramChannel.class) ;
        bootstrap.option(ChannelOption.SO_BROADCAST, true) ;
        bootstrap.handler(new LogEventEncoder(address)) ;
        this.file = file ;
    }


    public void run() throws Exception {
        Channel ch = bootstrap.bind(0).sync().channel() ;
        long pointer = 0 ;
        for(;;){
            long len = file.length() ;
            if(len < pointer){
                //file was reset
                pointer = len ;
            }else if(len > pointer) {
                //Content was added
                RandomAccessFile raf = new RandomAccessFile(file,"r") ;
                raf.seek(pointer);
                String line ;
                while ((line = raf.readLine())!= null){
                    LogEvent logEvent = new LogEvent(null, -1, file.getAbsolutePath(), line);
                    ch.writeAndFlush(logEvent) ;
                }
                pointer = raf.getFilePointer() ;
                raf.close();
            }
            try {
                Thread.sleep(1000);
            }catch (InterruptedException e){
                Thread.interrupted() ;
                break;
            }
        }
    }

    public void stop(){
        group.shutdownGracefully() ;
    }

    public static void main(String[] args) throws Exception {
        String filepath = "D:\\opt\\app\\faretools\\logs\\log_info.log" ;
        LogEventBroadcaster broadcaster = new LogEventBroadcaster(
                new InetSocketAddress("255.255.255.255", 8080) ,new File(filepath)) ;
        try {
            broadcaster.run();
        }finally {
            broadcaster.stop();
        }
    }


}
