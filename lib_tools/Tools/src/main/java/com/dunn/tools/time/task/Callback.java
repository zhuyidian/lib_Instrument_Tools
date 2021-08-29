package com.dunn.tools.time.task;

import com.dunn.tools.time.temp.Response;

import java.io.IOException;


public interface Callback {
    void onFailure(Call call, IOException e);

    void onSuccess(Call call, String msg) throws IOException;
}
