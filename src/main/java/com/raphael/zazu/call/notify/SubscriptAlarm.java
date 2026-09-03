package com.raphael.zazu.call.notify;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 一个实例下所有订阅关系不一致的消费组
 *
 * @author Raphael
 * @since 2026-09-03 10:56
 */
public class SubscriptAlarm {

    /**
     * 实例id
     */
    private final String instanceId;

    /**
     * 实例备注
     */
    private final String instanceRemark;

    /**
     * 订阅关系不一致的消费组
     */
    private final List<Group> groups = new ArrayList<>();

    public SubscriptAlarm(String instanceId, String instanceRemark) {
        this.instanceId = instanceId;
        this.instanceRemark = instanceRemark;
    }

    public void addGroup(String groupId, String groupRemark, Collection<String> topics) {
        groups.add(new Group(groupId, groupRemark, topics));
    }

    public boolean isEmpty() {
        return groups.isEmpty();
    }

    public String getInstanceId() {
        return instanceId;
    }

    public String getInstanceRemark() {
        return instanceRemark;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public static class Group {

        /**
         * 消费组id
         */
        private final String groupId;

        /**
         * 消费组备注
         */
        private final String remark;

        /**
         * 订阅关系不一致的Topic
         */
        private final Collection<String> topics;

        Group(String groupId, String remark, Collection<String> topics) {
            this.groupId = groupId;
            this.remark = remark;
            this.topics = topics;
        }

        public String getGroupId() {
            return groupId;
        }

        public String getRemark() {
            return remark;
        }

        public Collection<String> getTopics() {
            return topics;
        }

    }

}
