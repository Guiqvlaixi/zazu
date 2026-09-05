package com.raphael.zazu;

import com.aliyun.core.logging.ClientLogger;
import com.aliyun.sdk.service.rocketmq20220801.models.ListConsumerGroupSubscriptionsResponseBody;
import com.aliyun.sdk.service.rocketmq20220801.models.ListConsumerGroupsResponseBody;
import com.aliyun.sdk.service.rocketmq20220801.models.ListInstancesResponseBody;
import com.raphael.zazu.call.aliyun.domain.ListGroupSubscriptReq;
import com.raphael.zazu.call.aliyun.domain.ListGroupsReq;
import com.raphael.zazu.call.aliyun.domain.ListInstancesReq;
import com.raphael.zazu.call.notify.SubscriptAlarm;
import com.raphael.zazu.call.notify.domain.WeComPushReq;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Raphael
 * @since 2026-09-02 19:53
 */
public class Main {

    static final ScheduledThreadPoolExecutor EXECUTOR = new ScheduledThreadPoolExecutor(1);

    static void main(String[] args) {
        Runnable task = new Task();
        EXECUTOR.scheduleAtFixedRate(task, 0, 5, TimeUnit.MINUTES);
    }

    public static class Task implements Runnable {

        private final ClientLogger LOG = new ClientLogger(Task.class);

        @Override
        public void run() {
            try {
                /* 获取所有MQ实例 */
                List<ListInstancesResponseBody.List> instances = new ListInstancesReq().call();

                /* 本进程反正也没任何别的任务，虚拟线程直接挂载到默认线程池 */
                try (
                    ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
                ) {
                    for (ListInstancesResponseBody.List instance : instances) {
                        Runnable runnable = runnable(instance);
                        executor.submit(runnable);
                    }
                }
            } catch (Exception e) {
                LOG.error("[MainTaskEx]", e);
            }
        }

        private Runnable runnable(ListInstancesResponseBody.List instance) {
            return  () -> {
                String instanceId = instance.getInstanceId();

                SubscriptAlarm alarm = new SubscriptAlarm(instanceId, instance.getRemark());

                /* 该实例下所有消费组 */
                List<ListConsumerGroupsResponseBody.List> groups = new ListGroupsReq().call(instanceId);
                for (ListConsumerGroupsResponseBody.List group : groups) {
                    String groupId = group.getConsumerGroupId();
                    Set<String> topics = noConsistencyTopics(instanceId, groupId);
                    if (!topics.isEmpty()) {
                        alarm.addGroup(groupId, group.getRemark(), topics);
                    }
                }

                /* 通知 */
                if (!alarm.isEmpty()) {
                    new WeComPushReq().call(alarm);
                }
            };
        }

        /**
         * 指定消费组下订阅关系不一致的Topic
         */
        private static Set<String> noConsistencyTopics(String instanceId, String groupId) {
            ListGroupSubscriptReq.GroupContext cxt = new ListGroupSubscriptReq
                .GroupContext(instanceId, groupId);

            return new ListGroupSubscriptReq()
                .call(cxt)
                .stream()
                .map(ListConsumerGroupSubscriptionsResponseBody.Data::getTopicName)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        }

    }

}
