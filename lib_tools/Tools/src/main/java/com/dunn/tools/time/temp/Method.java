package com.dunn.tools.time.temp;

/**
 * Created by hcDarren on 2017/11/18.
 */

public enum Method {
    POST("POST"),GET("GET"),HEAD("HEAD"),DELETE("DELETE"),PUT("PUT"),PATCH("PATCH");
    String name;

    Method(String name) {
        this.name = name;
    }

    public boolean doOutput() {
        switch (this){
            case POST:
            case PUT:
                return true;
        }
        return false;
    }
}
