package com.coocaa.remoteplatform.core.request;

import java.io.Serializable;
import java.util.List;

public class HttpListResult<T> implements Serializable {
    public int code = 0;
    public String msg = "";
    public List<T> data;
}