package com.jiang.duck.rpc.core.server.pack;

import com.jiang.duck.rpc.core.server.HttpServer;
import com.jiang.duck.rpc.core.server.tcp.VertxTcpServer;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VertxTcpPackServe implements HttpServer {
    @Override
    public void doStart(int port) {
        //创建服务段：
        Vertx vertx = Vertx.vertx();
        NetServer netServer = vertx.createNetServer();

        //处理请求：
        netServer.connectHandler(netSocket -> {
            netSocket.handler(buffer -> {
                //判断长度：
                String testMessage = "hello,serve！hello,serve！hello,serve！";
                int messageLength = testMessage.getBytes().length;
                if (buffer.getBytes().length < messageLength) {
                    System.out.println("半粘包length:"+ buffer.getBytes().length);
                }

                if (buffer.getBytes().length>messageLength){
                    System.out.println("粘包length：" + buffer.getBytes().length);
                }
                //符合长度：
                String str = new String(buffer.getBytes(0, messageLength));
                System.out.println(str);
                if (testMessage.equals(str)){
                    System.out.println("good");
                }
            });
        });

        //启动服务监听端口
        netServer.listen(port, netServerAsyncResult -> {
            if (netServerAsyncResult.succeeded()) {
                log.info("TcpServe started on port " + port);
            } else {
                log.info("Failed to start TcpServe" + netServerAsyncResult.cause());
            }
        });
    }


    public static void main(String[] args) {
        VertxTcpPackServe vertxTcpPackServe = new VertxTcpPackServe();
        vertxTcpPackServe.doStart(8888);
    }
}
