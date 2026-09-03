package com.raphael.zazu.call.aliyun.domain;

import com.aliyun.core.logging.ClientLogger;
import com.aliyun.sdk.service.rocketmq20220801.models.ListInstancesRequest;
import com.aliyun.sdk.service.rocketmq20220801.models.ListInstancesResponse;
import com.aliyun.sdk.service.rocketmq20220801.models.ListInstancesResponseBody;
import com.raphael.zazu.Constant;
import com.raphael.zazu.call.AbstractCaller;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 获取所有的实例列表
 *
 * @author Raphael
 * @since 2026-09-02 16:34
 */
public class ListInstancesReq
    extends AbstractCaller<Void, List<ListInstancesResponseBody.List>> {

    private final ClientLogger LOG = new ClientLogger(ListInstancesReq.class);

    @Override
    public List<ListInstancesResponseBody.List> call(Void param) {
        ListInstancesRequest listInstancesRequest = ListInstancesRequest
            .builder()
            .build();

        return process(CLIENT.listInstances(listInstancesRequest));
    }

    private List<ListInstancesResponseBody.List> process(CompletableFuture<ListInstancesResponse> future) {
        Function<ListInstancesResponse, List<ListInstancesResponseBody.List>> func = list ->
            Optional.ofNullable(list)
                .map(ListInstancesResponse::getBody)
                .map(ListInstancesResponseBody::getData)
                .map(ListInstancesResponseBody.Data::getList)
                .orElse(Collections.emptyList())
                .stream()
                .filter(Objects::nonNull)
                .filter(this::instanceFilter)
                .collect(Collectors.toList());

        List<ListInstancesResponseBody.List> instances = Collections.emptyList();
        try {
            instances = future.thenApply(func).get();
        } catch (Exception e) {
            LOG.error("[ListInstancesEx]", e);
        }

        return instances;
    }

    /**
     * 如果没配置则不过滤
     */
    boolean instanceFilter(ListInstancesResponseBody.List body) {
        List<String> names = Constant.INSTANCES_NAMES;
        if (names.isEmpty()) {
            return true;
        }

        return names.contains(body.getInstanceName());
    }

}