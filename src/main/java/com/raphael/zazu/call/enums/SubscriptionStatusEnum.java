package com.raphael.zazu.call.enums;

import com.aliyun.sdk.service.rocketmq20220801.models.ListConsumerGroupSubscriptionsResponseBody;

/**
 * @author Raphael
 * @since 2026-09-02 21:32
 */
public enum SubscriptionStatusEnum {

    ONLINE("ONLINE", "在线"),

    OFFLINE("OFFLINE", "离线"),

    ;

    private final String status;

    private final String desc;

    SubscriptionStatusEnum(String status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static boolean isOnline(ListConsumerGroupSubscriptionsResponseBody.Data data) {
        return ONLINE.status.equals(data.getSubscriptionStatus());
    }

}
