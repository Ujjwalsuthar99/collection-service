package com.synoriq.synofin.collection.collectionservice.config;

public class DatabaseContextHolder {

    private static ThreadLocal<String> CONTEXT = new ThreadLocal<>();

    public static void set(String databaseEnvironment) {
        CONTEXT.set(databaseEnvironment);
    }

    public static String getEnvironment() {
        return CONTEXT.get();
    }

    public static void reset() {
        CONTEXT.remove();
    }

}