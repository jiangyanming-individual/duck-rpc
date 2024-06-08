package com.jiang.duck.rpc.core.register;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.kv.GetResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class EtcdRegister {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // create client using endpoints
        Client client = Client.builder().endpoints("http://localhost:2379").build();
        System.out.println("client connect success");
        KV kvClient = client.getKVClient();
        ByteSequence key = ByteSequence.from("test_key".getBytes());
        ByteSequence value = ByteSequence.from("test_value".getBytes());
        // put key和value：
        kvClient.put(key, value).get();

        // get the CompletableFuture
        CompletableFuture<GetResponse> getFuture = kvClient.get(key);
        System.out.println(getFuture.get());

        // get the value from CompletableFuture
        GetResponse response = getFuture.get();
        System.out.println(response.getKvs());

        // 删除key
        kvClient.delete(key).get();
    }
}
