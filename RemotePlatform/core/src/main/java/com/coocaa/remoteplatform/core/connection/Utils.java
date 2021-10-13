package com.coocaa.remoteplatform.core.connection;

import com.coocaa.remoteplatform.core.common.RemoteCommand;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @ClassName: Utils
 * @Author: XuZeXiao
 * @CreateDate: 3/31/21 4:11 PM
 * @Description:
 */
public class Utils {
    private static final String CONTENT_KEY = "content";

    public static RemoteCommand createRemoteCommandFromJson(String json) {
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        JsonElement content = jsonObject.get(CONTENT_KEY);
        if (content != null) {
            jsonObject.addProperty(CONTENT_KEY, content.toString());
        }
        return new Gson().fromJson(jsonObject.toString(), RemoteCommand.class);
    }
}
