package com.jiang.duck.rpc.core.server.pack;

import io.vertx.core.Vertx;
import io.vertx.core.net.NetSocket;

/***
 * 测试半粘包和粘包问题
 *
 * 测试客户端
 */
public class VertxTcpPackClient {


    public void start() {

        Vertx vertx = Vertx.vertx();
        vertx.createNetClient().connect(8888, "localhost", netSocketAsyncResult -> {
            if (netSocketAsyncResult.succeeded()) {
                System.out.println("Connected to TcpServe");
                NetSocket netSocket = netSocketAsyncResult.result();
                //发送消息
                for (int i = 0; i < 1000; i++) {
                    netSocket.write("hello,serve！hello,serve！hello,serve！");
                }
                //接受响应：
                netSocket.handler(buffer -> {
                    System.out.println("Received response from serve" + buffer.toString());
                });
            } else {
                System.out.println("Failed to connect to TcpServe");
            }
        });

    }

    public static void main(String[] args) {
        VertxTcpPackClient vertxTcpPackClient = new VertxTcpPackClient();
        vertxTcpPackClient.start();
    }
}
