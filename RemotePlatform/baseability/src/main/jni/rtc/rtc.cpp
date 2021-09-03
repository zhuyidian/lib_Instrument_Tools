#include <android/log.h>

#include <jni.h>
#include <string>
#include <cstdio>
#include <linux/rtc.h>
#include <cmath>
#include <sys/ioctl.h>
#include <ctime>
#include <sys/types.h>
#include <fcntl.h>
#include <unistd.h>
#include <cstdlib>
#include <cerrno>
#include <pthread.h>

#define TAG "RTCTEST"

#define LOGV(TAG, ...) __android_log_print(ANDROID_LOG_VERBOSE, TAG, __VA_ARGS__)
#define LOGE(TAG, ...)  __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

static const char default_rtc[] = "/dev/rtc0";
int fd;

int rtc_init() {
    const char *rtc = default_rtc;
    fd = open(rtc, O_RDONLY);
    if (fd < 0) {
        LOGV(TAG, "opening alarm driver failed: %s", strerror(errno));
        return -1;
    }
    return fd;
}

void rtc_close() {
    if (fd < 0) {
        return;
    }
    close(fd);
}

//utc 时间
//一些机器重启后时钟会被重置，需要重新设置
//时间早于现在时间，闹钟会不起效,而且会把闹钟中断关闭。重启后闹钟失效
//parms: typedef int64_t  jlong;    /* signed 64 bits */
//return 0 if success
int rtc_set_time(jlong millis) {
    LOGV(TAG,"millis :%lld", millis);
    struct timeval tv{};
    int ret;
    if (millis <= 0 || millis / 1000LL >= INT_MAX) {
        return -1;
    }
    tv.tv_sec = (time_t) (millis / 1000LL);
    tv.tv_usec = (suseconds_t) ((millis % 1000LL) * 1000LL);

    struct rtc_time rtc{};
    struct tm tm{}, *gmtime_res;
    gmtime_res = gmtime_r(&tv.tv_sec, &tm);
    if (!gmtime_res) {
        LOGV(TAG,"gmtime_r() failed: %s\n", strerror(errno));
        return -1;
    }
    rtc_init();

    memset(&rtc, 0, sizeof(rtc));
    rtc.tm_sec = tm.tm_sec;
    rtc.tm_min = tm.tm_min;
    rtc.tm_hour = tm.tm_hour;
    rtc.tm_mday = tm.tm_mday;
    rtc.tm_mon = tm.tm_mon;
    rtc.tm_year = tm.tm_year;
    rtc.tm_wday = tm.tm_wday;
    rtc.tm_yday = tm.tm_yday;
    rtc.tm_isdst = tm.tm_isdst;

    struct rtc_wkalrm alarm{};
    alarm.enabled = 1;  //启动闹钟，打开alarm_IRQ
    alarm.pending = 0;
    alarm.time = rtc;
    LOGV(TAG, "rtc.tm_isdst : %d", rtc.tm_isdst);
    LOGV(TAG, "Alarm time now is set to %d-%d-%d %02d:%02d:%02d.\n",alarm.time.tm_mday, alarm.time.tm_mon + 1, alarm.time.tm_year + 1900, alarm.time.tm_hour, alarm.time.tm_min, alarm.time.tm_sec);
    ret = ioctl(fd, RTC_WKALM_SET, &alarm);
    LOGV(TAG, "set alarm retval : %d errno: %d", ret, errno);
    if (ret == -1) {
        if (errno == ENOTTY) {
            fprintf(stderr,"\n...Alarm IRQs not supported.\n");
        }
        perror("RTC_ALM_SET ioctl");
    }
    rtc_close();
    return ret;
}


//set the TZ environment variable to UTC, call mktime and restore the value of TZ.
time_t my_timegm(struct tm *tm)
{
    time_t ret;
    char *tz;

    tz = getenv("TZ");
    LOGV(TAG, "before TZ: %s",tz);
    setenv("TZ", "", 1);
    tzset();
    ret = mktime(tm);
    LOGV(TAG, "TZ: %s",getenv("TZ"));
    if (tz)
        setenv("TZ", tz, 1);
    else
        unsetenv("TZ");
    tzset();
    LOGV(TAG, "after TZ: %s",tz);
    return ret;
}

//utc 时间
//return -1 if fail
jlong rtc_read_time() {
    rtc_init();
    int ret;

    struct rtc_wkalrm alarm{};
    memset(&alarm, 0, sizeof(alarm));
    ret = ioctl(fd, RTC_WKALM_RD, &alarm);
    if (ret == -1) {
        perror("RTC_ALM_READ ioctl");
        rtc_close();
        return ret;
    }
    LOGV(TAG, "Alarm state enabled: %d pending: %d  isdst: %d", alarm.enabled, alarm.pending, alarm.time.tm_isdst);
    LOGV(TAG, "Alarm UTC time is %d-%d-%d %02d:%02d:%02d.\n",alarm.time.tm_mday, alarm.time.tm_mon + 1, alarm.time.tm_year + 1900, alarm.time.tm_hour, alarm.time.tm_min, alarm.time.tm_sec);

    struct tm tm{};
    memset(&tm, 0, sizeof(tm));
    tm.tm_sec = alarm.time.tm_sec;
    tm.tm_min = alarm.time.tm_min;
    tm.tm_hour = alarm.time.tm_hour;
    tm.tm_mday = alarm.time.tm_mday;
    tm.tm_mon = alarm.time.tm_mon;
    tm.tm_year = alarm.time.tm_year;
    tm.tm_wday = alarm.time.tm_wday;
    tm.tm_yday = alarm.time.tm_yday;
    tm.tm_isdst = alarm.time.tm_isdst;

    //timelocal and timegm are nonstandard GNU extensions that are also present on the BSDs. Avoid their use; see NOTES.
    //long long alarm_time = timegm(&tm) * 1000LL;
    long long alarm_time = my_timegm(&tm) * 1000LL;
    LOGV(TAG,"millis :%lld", alarm_time);
    rtc_close();
    return alarm_time;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_coocaa_remoteplatform_baseability_abilities_rtc_JNI_rtcSetTime (
        JNIEnv* env,
        jclass clazz,
        jlong millis) {
    return rtc_set_time(millis);
}

extern "C" JNIEXPORT jlong JNICALL
Java_com_coocaa_remoteplatform_baseability_abilities_rtc_JNI_rtcReadTime (
        JNIEnv* env,
        jclass clazz) {
    return rtc_read_time();
}

extern "C" JNIEXPORT jint JNICALL
Java_com_coocaa_remoteplatform_baseability_abilities_rtc_JNI_cancelRtc(
        JNIEnv* env,
        jclass clazz) {
    int ret;
    rtc_init();
    /* Disable alarm interrupts */
    //关闭alarm/wakeupalarm中断。但是不会改变已经设置的中断触发时刻。
    //虽然返回-1，实际上操作成功(可能是驱动没实现)
    ret = ioctl(fd, RTC_AIE_OFF);
    LOGV(TAG, "cancel rtc ret : %d", ret);
    if (ret == -1) {
        perror("RTC_AIE_OFF ioctl");
    }
    rtc_close();

    //有些机器取消中断(可能是驱动没实现)，但关机依然会定时重启，所以这里设置一个超大值
    //用Unix带符号的32位整数时间格式来表示的最新时间是2038年1月19日03:14:07UTC，这是1970年1月1日之后过了2147483647秒。
    // 过了那个时间后，由于整数溢出，时间值将作为负数来存储，系统会将日期读为1901年12月13日，而不是2038年1月19日。
    return rtc_set_time(INT_MAX*999LL);
}

//函数对应驱动没实现的会卡死
[[noreturn]] void* wait_interrupt(void* args) {
    int retval;
    unsigned long tmp, data;
    int cnt = 0;
    LOGV(TAG,"Alarm rang.");
    rtc_init();
    ///* This blocks until the alarm ring causes an interrupt */
    //retval = read(fd, &data, sizeof(unsigned long));
    if (retval == -1) {
        perror("read");
    }
    while(true) {
        struct timeval tv = {5, 0};     /* 5 second timeout on select */
        fd_set readfds;
        FD_ZERO(&readfds);
        FD_SET(fd, &readfds);
        /* The select will wait until an RTC interrupt happens. */
        retval = select(fd + 1, &readfds, NULL, NULL, &tv);
        LOGV(TAG, "Alarm rang. %d  %d", cnt, retval);
        if (retval == -1) {
            perror("select");
        }
        /* This read won't block unlike the select-less case above. */
        retval = read(fd, &data, sizeof(unsigned long));
        if (retval == -1) {
            perror("read");
        }
        fflush(stderr);
        // LOGV(TAG, " okay. Alarm rang.");
    }
    rtc_close();
}

//test
extern "C" JNIEXPORT jstring JNICALL
Java_com_coocaa_remoteplatform_baseability_abilities_rtc_JNI_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    LOGV(TAG,"hello");
    //rtc_set_time(time(nullptr) *1000*2);
    long long now_time = (long long)(time(nullptr))*1000;
    LOGV(TAG,"time now : %lld",now_time);
    //rtc();
    //cancel_rtc();
    rtc_set_time(now_time+1*60*1000);
    rtc_read_time();

    //rtc_set_time(now_time-100*60*1000);
    //rtc_read_time();
    //pthread_t tids;
    //int ret = pthread_create(&tids, NULL, wait_interrupt, NULL);

    return env->NewStringUTF(hello.c_str());
}



