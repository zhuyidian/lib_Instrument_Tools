package com.coocaa.remoteplatform.baseability.abilities.rtc;

/**
 * Native methods for creating and managing pseudoterminal subprocesses. C code is in jni/rtc.cpp.
 */
final class JNI {

    static {
        System.loadLibrary("rtc");
    }

    public static native int rtcSetTime(long millis);

    public static native long rtcReadTime();

    public static native int cancelRtc();

}
