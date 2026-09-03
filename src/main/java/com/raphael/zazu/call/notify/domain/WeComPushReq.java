package com.raphael.zazu.call.notify.domain;

import com.aliyun.core.logging.ClientLogger;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.raphael.zazu.Constant;
import com.raphael.zazu.call.ICallAble;
import com.raphael.zazu.call.notify.SubscriptAlarm;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.util.Timeout;

import java.nio.charset.StandardCharsets;

/**
 * 把订阅关系不一致的消费组推送到企业微信群机器人
 *
 * @author Raphael
 * @since 2026-09-03 10:56
 */
public class WeComPushReq implements ICallAble<SubscriptAlarm, String> {

    private final ClientLogger LOG = new ClientLogger(WeComPushReq.class);

    private final CloseableHttpClient CLIENT = HttpClients.createDefault();

    private static final Gson GSON = new Gson();

    private static final RequestConfig CONFIG = RequestConfig
        .custom()
        .setResponseTimeout(Timeout.ofSeconds(3))
        .build();

    @Override
    public String call(SubscriptAlarm alarm) {
        HttpPost post = new HttpPost(Constant.WE_COM_WEBHOOK);
        post.setConfig(CONFIG);
        post.setEntity(new StringEntity(
            GSON.toJson(
                new Message(markdown(alarm))), ContentType.APPLICATION_JSON
            )
        );

        String res = "";
        try {
            res = CLIENT.execute(
                post,
                resp -> EntityUtils.toString(resp.getEntity(), StandardCharsets.UTF_8)
            );
        } catch (Exception e) {
            LOG.error("[WeComPushEx]", e);
        }

        return res;
    }

    /**
     * 排版成企业微信支持的markdown
     */
    private String markdown(SubscriptAlarm alarm) {
        StringBuilder md = new StringBuilder("## RocketMQ 订阅关系不一致告警\n")
            .append("**MQ 实例**：").append(title(alarm.getInstanceRemark(), alarm.getInstanceId())).append('\n')
            .append("**异常消费组**：").append(alarm.getGroups().size()).append(" 个\n");

        for (SubscriptAlarm.Group group : alarm.getGroups()) {
            md.append("\n**消费组**：").append(title(group.getRemark(), group.getGroupId())).append('\n')
                .append("> 不一致 Topic：`").append(String.join("`、`", group.getTopics())).append("`\n");
        }

        return md.append("\n<font color=\"comment\">同一个消费组下的机器，订阅的 Topic 或 Tag 不一样，")
            .append("会导致一部分消息漏消费。请确认这些消费组的订阅代码是否一致，改好后重新发布。</font>")
            .toString();
    }

    /**
     * 优先展示备注，没有备注就只展示id
     */
    private String title(String remark, String id) {
        if (remark == null || remark.trim().isEmpty()) {
            return "`" + id + "`";
        }

        return remark + " `" + id + "`";
    }

    /**
     * 企业微信markdown消息
     */
    @SuppressWarnings("all")
    private static class Message {

        @SerializedName("msgtype")
        private final String msgType = "markdown";

        private final Markdown markdown;

        Message(String content) {
            this.markdown = new Markdown(content);
        }

        @SuppressWarnings("all")
        private static class Markdown {

            private final String content;

            Markdown(String content) {
                this.content = content;
            }

        }

    }

}
