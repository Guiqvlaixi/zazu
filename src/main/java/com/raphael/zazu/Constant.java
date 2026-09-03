package com.raphael.zazu;

import java.util.Collections;
import java.util.List;

/**
 * @author Raphael
 * @since 2026-09-02 19:30
 */
public final class Constant {

    /**
     * Regin Id
     */
    public static final String REGION_ID = "cn-hangzhou";

    /**
     * End Point
     */
    public static final String END_POINT = "rocketmq.cn-hangzhou.aliyuncs.com";

    /**
     * 需要检测的实例
     */
    public static final List<String> INSTANCES_NAMES = Collections.singletonList("测试开发");

    /**
     * 企业微信群机器人的Webhook，key换成自己的
     */
    public static final String WE_COM_WEBHOOK = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=939eb5bb-3f95-4b47-954c-526c629f6431";

}
