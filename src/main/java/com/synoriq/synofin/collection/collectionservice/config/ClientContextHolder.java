package com.synoriq.synofin.collection.collectionservice.config;

public class ClientContextHolder {


    private static ThreadLocal<String> threadLocal = new ThreadLocal<>();

    public static void setClientContext(String clientId) {
        threadLocal.set(clientId);
    }

    public static String getClientContext() {
        return threadLocal.get();
    }

    public static void clearClientContext() {
        threadLocal.remove();
    }
}
