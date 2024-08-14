package com.jiang.duck.rpc.core.server.tcp.back;

import io.vertx.core.Vertx;
import io.vertx.core.net.NetSocket;

/**
 * Tcp连接的客户端
 */
public class VertxTcpClient_back {


    public void start() {

        Vertx vertx = Vertx.vertx();
        //连接服务端：
        vertx.createNetClient().connect(8888, "localhost", netSocketAsyncResult -> {
            if (netSocketAsyncResult.succeeded()) {
                System.out.println("Succeed to connect Tcp serve");
                NetSocket socket = netSocketAsyncResult.result();
                //发送数据：
                socket.write("hello Tcp serve");
                //接受数据：
                socket.handler(buffer -> {
                    System.out.println("Received response from Tcp serve: "+ buffer.toString());
                });
            } else {
                System.out.println("Failed to connect Tcp serve" + netSocketAsyncResult.cause());
            }

        });
    }

    public static void main(String[] args) {
        new VertxTcpClient_back().start();
    }
}
