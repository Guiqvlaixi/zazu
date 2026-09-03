# zazu

阿里云 RocketMQ 5.x「订阅关系不一致」巡检员儿。  

阿里云开放接口文档：https://api.aliyun.com/api/RocketMQ
定时扫描指定 MQ 实例下的所有消费组，找出订阅关系不一致的消费组，并把结果以 markdown 消息推送到企业微信群机器人。

## 运行前必须做的两件事

### 1. 配置阿里云 AK 环境变量

```bash
export ALIBABA_CLOUD_ACCESS_KEY_ID=你的AccessKeyId
export ALIBABA_CLOUD_ACCESS_KEY_SECRET=你的AccessKeySecret
```
或者
在 IDE 里运行的话，加在 Run Configuration 的 Environment variables 里。
### 2. 改 `com.raphael.zazu.Constant` 里的配置

| 常量 | 说明 |
| --- | --- |
| `REGION_ID` | 实例所在地域，例如 `cn-hangzhou` |
| `END_POINT` | 服务接入点，跟地域保持一致，格式 `rocketmq.<地域>.aliyuncs.com` |
| `INSTANCES_NAMES` | 需要巡检的实例名称（控制台上的名称，不是实例 id）；留空表示账号下所有实例都巡检 |
| `WE_COM_WEBHOOK` | 企业微信群机器人的 Webhook 地址，把 `key` 换成自己群里机器人的 |

## 巡检流程
1. `ListInstancesReq` 拉取账号下的 MQ 实例，按 `INSTANCES_NAMES` 过滤
2. `ListGroupsReq` 拉取实例下的消费组（单页 100 个）
3. `ListGroupSubscriptReq` 查每个消费组的订阅关系，只保留状态为**在线**且 `consistency = false` 的记录
4. 同一个实例下的问题消费组汇总成一条 `SubscriptAlarm`，Topic 自动去重
5. `WeComPushReq` 排版成 markdown 后 POST 给群机器人；一个实例一条消息，没有问题就不推送

## 告警样例

```
RocketMQ 订阅关系不一致告警
MQ 实例：测试开发 rmq-cn-xxxxxxxxxx
异常消费组：2 个

消费组：订单中心 GID_order
> 不一致 Topic：TP_ORDER_CREATE、TP_ORDER_PAY

消费组：库存中心 GID_stock
> 不一致 Topic：TP_STOCK_DEDUCT

同一个消费组下的机器，订阅的 Topic 或 Tag 不一样，会导致一部分消息漏消费。
请确认这些消费组的订阅代码是否一致，改好后重新发布。
```
