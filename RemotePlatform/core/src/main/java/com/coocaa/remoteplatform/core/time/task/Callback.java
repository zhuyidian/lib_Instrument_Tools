package com.coocaa.remoteplatform.core.time.task;

import java.io.IOException;


public interface Callback {
    void onFailure(Call call, IOException e);

    void onSuccess(Call call, String msg) throws IOException;
}
