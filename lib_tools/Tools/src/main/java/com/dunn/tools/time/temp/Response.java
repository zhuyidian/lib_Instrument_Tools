package com.dunn.tools.time.temp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by hcDarren on 2017/11/18.
 * 服务器响应
 */
public class Response {
    private final InputStream inputStream;// Skin
    public Response(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String string() {
        return convertStreamToString(inputStream);
    }

    public String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
