package com.coocaa.remoteplatform.core.http;

import java.io.Serializable;

/**
 * @ClassName: HttpResponseData
 * @Author: XuZeXiao
 * @CreateDate: 2021/5/13 11:38
 * @Description:
 */
public class HttpResponseData implements Serializable {
    public String code;
    public String result;
    public String msg;
    public String data;
}
