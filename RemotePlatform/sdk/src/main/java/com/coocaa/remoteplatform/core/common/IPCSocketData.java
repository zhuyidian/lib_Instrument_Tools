package com.coocaa.remoteplatform.core.common;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * @ClassName: TopicPassThroughData
 * @Author: XuZeXiao
 * @CreateDate: 4/15/21 9:36 PM
 * @Description:
 */
public class IPCSocketData implements Serializable {
    public String method;
    public String topic;
    public String content;

    public IPCSocketData(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            method = jsonObject.getString("method");
            topic = jsonObject.getString("topic");
            content = jsonObject.getString("content");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public IPCSocketData(String method, String topic, String content) {
        this.method = method;
        this.topic = topic;
        this.content = content;
    }

    public String toJsonString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", method);
            jsonObject.put("topic", topic);
            jsonObject.put("content", content);
        } catch (Exception e) {
            return "";
        }
        return jsonObject.toString();
    }
}
