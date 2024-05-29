package com.jiang.duck.rpc.easy.server;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;

public class VertxHttpServer implements HttpServer {
    @Override
    public void doStart(int port) {
        //使用Vertx:
        Vertx vertx = Vertx.vertx();
        //创建一个服务器:
        io.vertx.core.http.HttpServer httpServer = vertx.createHttpServer();
        //请求
        httpServer.requestHandler(request -> {
            //处理http请求
            System.out.println("Received request: " + request.method() + " "+ request.uri());

            //响应内容
            HttpServerResponse response = request.response();
            response.putHeader("content-type", "text/plain");
            // Write to the response and end it
//            response.end("Hello World!");
        });
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
