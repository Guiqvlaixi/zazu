package com.raphael.zazu.call.aliyun.domain;

import com.aliyun.core.logging.ClientLogger;
import com.aliyun.sdk.service.rocketmq20220801.models.ListConsumerGroupsRequest;
import com.aliyun.sdk.service.rocketmq20220801.models.ListConsumerGroupsResponse;
import com.aliyun.sdk.service.rocketmq20220801.models.ListConsumerGroupsResponseBody;
import com.raphael.zazu.call.AbstractCaller;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 获取指定的实例下的所有消费组
 *
 * @author Raphael
 * @since 2026-09-02 20:01
 */
public class ListGroupsReq
    extends AbstractCaller<String, List<ListConsumerGroupsResponseBody.List>> {

    private final ClientLogger LOG = new ClientLogger(ListInstancesReq.class);

    @Override
    public List<ListConsumerGroupsResponseBody.List> call(String instanceId) {
        ListConsumerGroupsRequest req = ListConsumerGroupsRequest
            .builder()
            .instanceId(instanceId)
            .pageSize(100)
            .build();

        return process(CLIENT.listConsumerGroups(req));
    }

    private List<ListConsumerGroupsResponseBody.List> process(CompletableFuture<ListConsumerGroupsResponse> future) {
        Function<ListConsumerGroupsResponse, List<ListConsumerGroupsResponseBody.List>> func = groups ->
            Optional.ofNullable(groups)
                .map(ListConsumerGroupsResponse::getBody)
                .map(ListConsumerGroupsResponseBody::getData)
                .map(ListConsumerGroupsResponseBody.Data::getList)
                .orElse(Collections.emptyList())
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<ListConsumerGroupsResponseBody.List> groups = Collections.emptyList();
        try {
            groups = future.thenApply(func).get();
        } catch (Exception e) {
            LOG.error("[ListGroupsEx]", e);
        }

        return groups;
    }

}
