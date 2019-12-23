chapter3:简单的时间打印
chapter4:粘包和拆包练习
chapter4_1:添加LineBasedFrameDecoder和StringDecoder解决半包问题
chapter7: 使用MessagePack编解码,注意使用MessagePack时序列化的对象一定要注解@Message,否则无法传递数据



开发笔记：
    发现服务端能正常收到客户端的数据，但瞬间客户端就直接断开连接了，查了好久发现是
    代码写法：f.channel().closeFuture();后面漏掉了.sync() ;
    正确写法为: f.channel().closeFuture().sync() ;

