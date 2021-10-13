package com.coocaa.remoteplatform.core.request;

import java.io.Serializable;

public class HttpResult<T> implements Serializable {
    public int code = 0;
    public String msg = "";
    public T data;
}