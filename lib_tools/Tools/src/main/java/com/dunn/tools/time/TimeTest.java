package com.dunn.tools.time;

public class TimeTest {
    /**
     * 说明:
     * 1,外部字段
     *      1>,clientId: com.coocaa.remoteplatform.baseability 用来区分命令在终端的执行者
     *      2>,timestamp: 指令当前时间戳，1615337069729
     *      3>,cmdType: 0:关机, 1:开机(不支持), 2:重启, 3:音量控制
     *      4>,msgId: 2531152042538311687
     *      5>,pushId: 9db5257125bc42648466fc59133da25dvq5jOXoD
     * 2,指令类型区分根据外部的 cmdType 字段
     * 3,定时区分根据内部的 planType 字段
     *      1>,一次性定时类型取 delay & time
     *      2>,每天定时类型取 time
     *      3>,每周定时类型取 week
     *      4>,每月定时类型取 month
     */


    /**
     * 即时
     */
    public static String json_shutdown = "{\"planType\":0}";
    public static String json_reboot = "{\"planType\":0}";
    public static String json_volume = "{\"volume\":80,\"planType\":0}";



    /**
     * 定时关机
     */
    //一次性
    //public static String jsonDelay1_shutDown = "{\"planType\":1,\"delay\":\"2021-08-26 17:30:40\",\"time\":[\"17:30:40\",\"18:30:40\"],\"switch\":0}";
    public static String jsonDelay_shutDown = "{" +
            "  \"planType\": 1," +
            "  \"delay\": \"2021-08-26 17:30:40\"," +
            "  \"time\": [" +
            "    \"17:30:40\"" +
            "  ]," +
            "  \"switch\": 0" +
            "}";
    //每天
    //public static String jsonDay1_shutDown = "{\"planType\":2,\"time\":[\"17:30:40\",\"18:30:40\"],\"switch\":0}";
    public static String jsonDay_shutDown = "{" +
            "  \"planType\": 2," +
            "  \"time\": [" +
            "    \"17:30:40\"," +
            "    \"18:30:40\"" +
            "  ]," +
            "  \"switch\": 0" +
            "}";
    //每周
    //public static String jsonWeek1_shutDown = "{\"planType\":5,\"week\":[{\"number\":\"1\",\"time\":[\"17:30:40\",\"18:30:40\"]},{\"number\":\"2\",\"time\":[\"17:30:40\",\"18:30:40\"]},{\"number\":\"3\",\"time\":[\"17:30:40\",\"18:30:40\"]},{\"number\":\"4\",\"time\":[\"17:30:40\",\"18:30:40\"]},{\"number\":\"5\",\"time\":[\"17:30:40\",\"18:30:40\"]},{\"number\":\"6\",\"time\":[\"17:30:40\",\"18:30:40\"]},{\"number\":\"7\",\"time\":[\"17:30:40\",\"18:30:40\"]}],\"time\":[\"17:30:40\",\"18:30:40\"],\"switch\":0}";
    public static String jsonWeek_shutDown = "{" +
            "  \"planType\": 5," +
            "  \"week\": [" +
            "    {" +
            "      \"number\": \"1\"," +
            "      \"time\": [" +
            "        \"17:30:40\"," +
            "        \"18:30:40\"" +
            "      ]" +
            "    }," +
            "    {" +
            "      \"number\": \"2\"," +
            "      \"time\": [" +
            "        \"17:30:40\"," +
            "        \"18:30:40\"" +
            "      ]" +
            "    }," +
            "    {" +
            "      \"number\": \"3\"," +
            "      \"time\": [" +
            "        \"17:30:40\"," +
            "        \"18:30:40\"" +
            "      ]" +
            "    }," +
            "    {" +
            "      \"number\": \"4\"," +
            "      \"time\": [" +
            "        \"17:30:40\"," +
            "        \"18:30:40\"" +
            "      ]" +
            "    }," +
            "    {" +
            "      \"number\": \"5\"," +
            "      \"time\": [" +
            "        \"17:30:40\"," +
            "        \"18:30:40\"" +
            "      ]" +
            "    }," +
            "    {" +
            "      \"number\": \"6\"," +
            "      \"time\": [" +
            "        \"17:30:40\"," +
            "        \"18:30:40\"" +
            "      ]" +
            "    }," +
            "    {" +
            "      \"number\": \"7\"," +
            "      \"time\": [" +
            "        \"17:30:40\"," +
            "        \"18:30:40\"" +
            "      ]" +
            "    }" +
            "  ]," +
            "  \"time\": [" +
            "    \"17:30:40\"," +
            "    \"18:30:40\"" +
            "  ]," +
            "  \"switch\": 0" +
            "}";
    //每月
    //public static String jsonMonth1_shutDown = "{\"planType\":6,\"month\":[{\"number\":\"1\",\"time\":[\"17:30:40\",\"18:30:40\"]},{\"number\":\"2\",\"time\":[\"17:30:40\",\"18:30:40\"]},{\"number\":\"3\",\"time\":[\"17:30:40\",\"18:30:40\"]},{\"number\":\"4\",\"time\":[\"17:30:40\",\"18:30:40\"]},{\"number\":\"5\",\"time\":[\"17:30:40\",\"18:30:40\"]}],\"time\":[\"17:30:40\",\"18:30:40\"],\"switch\":0}";
    public static String jsonMonth_shutDown = "{" +
            "  \"planType\": 6," +
            "  \"month\": [" +
            "    {" +
            "      \"number\": \"1\"," +
            "      \"time\": [" +
            "        \"17:30:40\"," +
            "        \"18:30:40\"" +
            "      ]" +
            "    }," +
            "    {" +
            "      \"number\": \"2\"," +
            "      \"time\": [" +
            "        \"17:30:40\"," +
            "        \"18:30:40\"" +
            "      ]" +
            "    }," +
            "    {" +
            "      \"number\": \"3\"," +
            "      \"time\": [" +
            "        \"17:30:40\"," +
            "        \"18:30:40\"" +
            "      ]" +
            "    }," +
            "    {" +
            "      \"number\": \"4\"," +
            "      \"time\": [" +
            "        \"17:30:40\"," +
            "        \"18:30:40\"" +
            "      ]" +
            "    }," +
            "    {" +
            "      \"number\": \"5\"," +
            "      \"time\": [" +
            "        \"17:30:40\"," +
            "        \"18:30:40\"" +
            "      ]" +
            "    }" +
            "  ]," +
            "  \"time\": [" +
            "    \"17:30:40\"," +
            "    \"18:30:40\"" +
            "  ]," +
            "  \"switch\": 0" +
            "}";


    /**
     * 定时重启
     */
    //一次性
    //public static String jsonDelay1_reboot = "{\"planType\":1,\"delay\":\"2021-08-26 17:30:40\",\"time\":[\"17:30:40\",\"18:30:40\"],\"switch\":2}";
    public static String jsonDelay_reboot = "{" +
            "  \"planType\": 1," +
            "  \"delay\": \"2021-08-26 17:30:40\"," +
            "  \"time\": [" +
            "    \"17:30:40\"" +
            "  ]," +
            "  \"switch\": 2" +
            "}";
    //每天
    //public static String jsonDay1_reboot = "{\"planType\":2,\"time\":[\"17:30:40\",\"18:30:40\"],\"switch\":2}";
    public static String jsonDay_reboot = "{" +
            "  \"planType\": 2," +
            "  \"time\": [" +
            "    \"17:30:40\"," +
            "    \"18:30:40\"" +
            "  ]," +
            "  \"switch\": 2" +
            "}";
    //每周
    //public static String jsonWeek1_reboot = "{\"planType\":5,\"week\":[{\"number\":\"1\",\"time\":[\"17:30:40\",\"18:30:40\"]},{\"number\":\"2\",\"time\":[\"17:30:40\",\"18:30:40\"]},{\"number\":\"3\",\"time\":[\"17:30:40\",\"18:30:40\"]},{\"number\":\"4\",\"time\":[\"17:30:40\",\"18:30:40\"]},{\"number\":\"5\",\"time\":[\"17:30:40\",\"18:30:40\"]},{\"number\":\"6\",\"time\":[\"17:30:40\",\"18:30:40\"]},{\"number\":\"7\",\"time\":[\"17:30:40\",\"18:30:40\"]}],\"time\":[\"17:30:40\",\"18:30:40\"],\"switch\":2}";
    public static String jsonWeek_reboot = "{" +
            "  \"planType\": 5," +
            "  \"week\": [" +
            "    {" +
            "      \"number\": \"1\"," +
            "      \"time\": [" +
            "        \"17:30:40\"," +
            "        \"18:30:40\"" +
            "      ]" +
            "    }," +
            "    {" +
            "      \"number\": \"2\"," +
            "      \"time\": [" +
            "        \"17:30:40\"," +
            "        \"18:30:40\"" +
            "      ]" +
            "    }," +
            "    {" +
            "      \"number\": \"3\"," +
            "      \"time\": [" +
            "        \"17:30:40\"," +
            "        \"18:30:40\"" +
            "      ]" +
            "    }," +
            "    {" +
            "      \"number\": \"4\"," +
            "      \"time\": [" +
            "        \"17:30:40\"," +
            "        \"18:30:40\"" +
            "      ]" +
            "    }," +
            "    {" +
            "      \"number\": \"5\"," +
            "      \"time\": [" +
            "        \"17:30:40\"," +
            "        \"18:30:40\"" +
            "      ]" +
            "    }," +
            "    {" +
            "      \"number\": \"6\"," +
            "      \"time\": [" +
            "        \"17:30:40\"," +
            "        \"18:30:40\"" +
            "      ]" +
            "    }," +
            "    {" +
            "      \"number\": \"7\"," +
            "      \"time\": [" +
            "        \"17:30:40\"," +
            "        \"18:30:40\"" +
            "      ]" +
            "    }" +
            "  ]," +
            "  \"time\": [" +
            "    \"17:30:40\"," +
            "    \"18:30:40\"" +
            "  ]," +
            "  \"switch\": 2" +
            "}";
    //每月
    //public static String jsonMonth1_reboot = "{\"planType\":6,\"month\":[{\"number\":\"1\",\"time\":[\"17:30:40\",\"18:30:40\"]},{\"number\":\"2\",\"time\":[\"17:30:40\",\"18:30:40\"]},{\"number\":\"3\",\"time\":[\"17:30:40\",\"18:30:40\"]},{\"number\":\"4\",\"time\":[\"17:30:40\",\"18:30:40\"]},{\"number\":\"5\",\"time\":[\"17:30:40\",\"18:30:40\"]}],\"time\":[\"17:30:40\",\"18:30:40\"],\"switch\":2}";
    public static String jsonMonth_reboot = "{" +
            "  \"planType\": 6," +
            "  \"month\": [" +
            "    {" +
            "      \"number\": \"1\"," +
            "      \"time\": [" +
            "        \"17:30:40\"," +
            "        \"18:30:40\"" +
            "      ]" +
            "    }," +
            "    {" +
            "      \"number\": \"2\"," +
            "      \"time\": [" +
            "        \"17:30:40\"," +
            "        \"18:30:40\"" +
            "      ]" +
            "    }," +
            "    {" +
            "      \"number\": \"3\"," +
            "      \"time\": [" +
            "        \"17:30:40\"," +
            "        \"18:30:40\"" +
            "      ]" +
            "    }," +
            "    {" +
            "      \"number\": \"4\"," +
            "      \"time\": [" +
            "        \"17:30:40\"," +
            "        \"18:30:40\"" +
            "      ]" +
            "    }," +
            "    {" +
            "      \"number\": \"5\"," +
            "      \"time\": [" +
            "        \"17:30:40\"," +
            "        \"18:30:40\"" +
            "      ]" +
            "    }" +
            "  ]," +
            "  \"time\": [" +
            "    \"17:30:40\"," +
            "    \"18:30:40\"" +
            "  ]," +
            "  \"switch\": 2" +
            "}";


    /**
     * 定时音量
     */
    //一次性
    //public static String jsonDelay1_volume = "{\"volume\":50,\"planType\":1,\"delay\":\"2021-08-26 16:31:39\",\"time\":[\"16:31:39\"],\"switch\":3}";
    public static String jsonDelay_volume = "{" +
            "  \"volume\": 50," +
            "  \"planType\": 1," +
            "  \"delay\": \"2021-08-26 16:31:39\"," +
            "  \"time\": [" +
            "    \"16:31:39\"" +
            "  ]," +
            "  \"switch\": 3" +
            "}";
    //每天
    //public static String jsonDay1_volume = "{\"volume\":50,\"planType\":2,\"time\":[\"16:31:39\",\"17:31:39\"],\"switch\":3}";
    public static String jsonDay_volume = "{" +
            "  \"volume\": 50," +
            "  \"planType\": 2," +
            "  \"time\": [" +
            "    \"16:31:39\"," +
            "    \"17:31:39\"" +
            "  ]," +
            "  \"switch\": 3" +
            "}";
    //每周
    //public static String jsonWeek1_volume = "{\"volume\":50,\"planType\":5,\"week\":[{\"number\":\"1\",\"time\":[\"16:31:39\",\"17:31:39\"]},{\"number\":\"2\",\"time\":[\"16:31:39\",\"17:31:39\"]},{\"number\":\"3\",\"time\":[\"16:31:39\",\"17:31:39\"]}],\"time\":[\"16:31:39\",\"17:31:39\"],\"switch\":3}";
    public static String jsonWeek_volume = "{" +
            "  \"volume\": 50," +
            "  \"planType\": 5," +
            "  \"week\": [" +
            "    {" +
            "      \"number\": \"1\"," +
            "      \"time\": [" +
            "        \"16:31:39\"," +
            "        \"17:31:39\"" +
            "      ]" +
            "    }," +
            "    {" +
            "      \"number\": \"2\"," +
            "      \"time\": [" +
            "        \"16:31:39\"," +
            "        \"17:31:39\"" +
            "      ]" +
            "    }," +
            "    {" +
            "      \"number\": \"3\"," +
            "      \"time\": [" +
            "        \"16:31:39\"," +
            "        \"17:31:39\"" +
            "      ]" +
            "    }" +
            "  ]," +
            "  \"time\": [" +
            "    \"16:31:39\"," +
            "    \"17:31:39\"" +
            "  ]," +
            "  \"switch\": 3" +
            "}";
    //每月
    //public static String jsonMonth1_volume = "{\"volume\":50,\"planType\":6,\"month\":[{\"number\":\"10\",\"time\":[\"16:31:39\",\"17:31:39\"]},{\"number\":\"11\",\"time\":[\"16:31:39\",\"17:31:39\"]},{\"number\":\"12\",\"time\":[\"16:31:39\",\"17:31:39\"]},{\"number\":\"13\",\"time\":[\"16:31:39\",\"17:31:39\"]},{\"number\":\"14\",\"time\":[\"16:31:39\",\"17:31:39\"]},{\"number\":\"15\",\"time\":[\"16:31:39\",\"17:31:39\"]}],\"time\":[\"16:31:39\",\"17:31:39\"],\"switch\":3}";
    public static String jsonMonth_volume = "{" +
            "  \"volume\": 50," +
            "  \"planType\": 6," +
            "  \"month\": [" +
            "    {" +
            "      \"number\": \"10\"," +
            "      \"time\": [" +
            "        \"16:31:39\"," +
            "        \"17:31:39\"" +
            "      ]" +
            "    }," +
            "    {" +
            "      \"number\": \"11\"," +
            "      \"time\": [" +
            "        \"16:31:39\"," +
            "        \"17:31:39\"" +
            "      ]" +
            "    }," +
            "    {" +
            "      \"number\": \"12\"," +
            "      \"time\": [" +
            "        \"16:31:39\"," +
            "        \"17:31:39\"" +
            "      ]" +
            "    }," +
            "    {" +
            "      \"number\": \"13\"," +
            "      \"time\": [" +
            "        \"16:31:39\"," +
            "        \"17:31:39\"" +
            "      ]" +
            "    }," +
            "    {" +
            "      \"number\": \"14\"," +
            "      \"time\": [" +
            "        \"16:31:39\"," +
            "        \"17:31:39\"" +
            "      ]" +
            "    }," +
            "    {" +
            "      \"number\": \"15\"," +
            "      \"time\": [" +
            "        \"16:31:39\"," +
            "        \"17:31:39\"" +
            "      ]" +
            "    }" +
            "  ]," +
            "  \"time\": [" +
            "    \"16:31:39\"," +
            "    \"17:31:39\"" +
            "  ]," +
            "  \"switch\": 3" +
            "}";
}
