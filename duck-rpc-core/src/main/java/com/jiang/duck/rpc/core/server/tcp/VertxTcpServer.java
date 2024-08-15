package com.jiang.duck.rpc.core.server.tcp;


import com.jiang.duck.rpc.core.server.HttpServer;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;

/**
 * vertx 实现tcp连接
 */
public class VertxTcpServer implements HttpServer {
    @Override
    public void doStart(int port) {
        Vertx vertx = Vertx.vertx();
        //创建Tcp 服务器：
        NetServer server = vertx.createNetServer();

        //处理请求：
//        server.connectHandler(socket -> {
//            //处理连接：
//            socket.handler(buffer -> {
//                byte[] requestData = buffer.getBytes();
//                //专门处理请求数据，并返回response:
//                byte[] responseData = handleRequest(requestData);
//                //发送请求：
//                socket.write(Buffer.buffer(responseData));
//            });
//        });

        //设置TcpServeHandler
        server.connectHandler(new TcpServeHandler());
        /**
         * 监听指定端口：
         */
        server.listen(port, netServerAsyncResult -> {
            if (netServerAsyncResult.succeeded()){
                System.out.println("Tcp serve is listening on port:" +port);
            }else {
                //返回失败的结果：
                System.out.println("Tcp serve failed to start: " + netServerAsyncResult.cause());
            }
        });
    }

    /**
     * 处理请求数据
     *
     * @param requestData
     * @return
     */
    private byte[] handleRequest(byte[] requestData) {
        //todo 处理请求；
        return "hello,client".getBytes();
    }

    /**
     * 测试：
     * @param args
     */
    public static void main(String[] args) {
        new VertxTcpServer().doStart(8888);
    }
}
