package com.jiang.example.provider;

import com.jiang.duck.rpc.easy.server.VertxHttpServer;

public class EasyProviderExample {
    public static void main(String[] args) {
        VertxHttpServer vertxHttpServer = new VertxHttpServer();
        vertxHttpServer.doStart(8020);
    }
}
