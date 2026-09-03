package com.raphael.zazu.call.aliyun.domain;

import com.aliyun.core.logging.ClientLogger;
import com.aliyun.sdk.service.rocketmq20220801.models.ListConsumerGroupSubscriptionsRequest;
import com.aliyun.sdk.service.rocketmq20220801.models.ListConsumerGroupSubscriptionsResponse;
import com.aliyun.sdk.service.rocketmq20220801.models.ListConsumerGroupSubscriptionsResponseBody;
import com.raphael.zazu.call.AbstractCaller;
import com.raphael.zazu.call.enums.SubscriptionStatusEnum;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 获取指定InstanceId指定GroupId下的订阅关系
 * @author Raphael
 * @since 2026-09-02 21:18
 */
public class ListGroupSubscriptReq
    extends AbstractCaller<ListGroupSubscriptReq.GroupContext, List<ListConsumerGroupSubscriptionsResponseBody.Data>> {

    private final ClientLogger LOG = new ClientLogger(ListGroupSubscriptReq.class);

    @Override
    public List<ListConsumerGroupSubscriptionsResponseBody.Data> call(GroupContext context) {
        ListConsumerGroupSubscriptionsRequest req = ListConsumerGroupSubscriptionsRequest
            .builder()
            .instanceId(context.getInstanceId())
            .consumerGroupId(context.getGroupId())
            .build();

        return process(CLIENT.listConsumerGroupSubscriptions(req));
    }

    List<ListConsumerGroupSubscriptionsResponseBody.Data> process(CompletableFuture<ListConsumerGroupSubscriptionsResponse> future) {
        List<ListConsumerGroupSubscriptionsResponseBody.Data> list = Collections.emptyList();

        try {
            ListConsumerGroupSubscriptionsResponse resp = future.get();
            List<ListConsumerGroupSubscriptionsResponseBody.Data> bodys = Optional.ofNullable(resp)
                .map(ListConsumerGroupSubscriptionsResponse::getBody)
                .map(ListConsumerGroupSubscriptionsResponseBody::getData)
                .orElse(Collections.emptyList());

            list = bodys.stream()
                .filter(SubscriptionStatusEnum::isOnline)
                .filter(e -> Objects.equals(e.getConsistency(), false))
                .collect(Collectors.toList());
        } catch (Exception e) {
            LOG.error("[ListGroupSubscriptEx]", e);
        }

        return list;
    }

    public static class GroupContext {

        /**
         * 实例id
         */
        private final String instanceId;

        /**
         * 消费组id
         */
        private final String groupId;

        public GroupContext(String instanceId, String groupId) {
            this.instanceId = instanceId;
            this.groupId = groupId;
        }

        public String getInstanceId() {
            return instanceId;
        }

        public String getGroupId() {
            return groupId;
        }

    }

}
