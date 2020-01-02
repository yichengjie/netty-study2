package com.yicj.udp.entity;

import lombok.Data;

import java.net.InetSocketAddress;

@Data
public class LogEvent {
    public static final byte SEPARATOR = (byte) '$' ;
    private final InetSocketAddress source ;
    private final String logfile ;
    private final String msg ;
    private final long received ;

    public LogEvent(String logfile, String msg){
        this(null, -1, logfile, msg) ;
    }

    public LogEvent(InetSocketAddress source,
                    long received, String logfile, String msg){
        this.source = source ;
        this.received = received ;
        this.logfile = logfile ;
        this.msg = msg ;
    }
}
