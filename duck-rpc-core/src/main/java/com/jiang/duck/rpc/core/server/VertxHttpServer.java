package com.jiang.duck.rpc.core.server;

import io.vertx.core.Vertx;

public class VertxHttpServer implements HttpServer {
    @Override
    public void doStart(int port) {
        //使用Vertx:
        Vertx vertx = Vertx.vertx();
        //创建一个服务器:
        io.vertx.core.http.HttpServer httpServer = vertx.createHttpServer();
        //请求
        //绑定请求处理器：
        httpServer.requestHandler(new HttpServerHandler());
        //Future接口 lambda表达式
        httpServer.listen(port,result -> { //异步返回类
            if (result.succeeded()){
                System.out.println("server is listening on port " + port);
            }else {
                System.out.println("Failed to start server " + result.cause());
            }
        });
    }
}
