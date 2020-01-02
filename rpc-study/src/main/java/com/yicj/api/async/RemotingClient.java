package com.yicj.api.async;

import com.yicj.rpc.model.RemotingTransporter;

public interface RemotingClient extends BaseRemotingService {
    //向某个地址发送request的请求，并且远程返回RemotingTransporter的结果，调用超时时间是timeoutMillis
    RemotingTransporter invokeSync(final String addr ,final RemotingTransporter request, final long timeoutMillis) ;
}
