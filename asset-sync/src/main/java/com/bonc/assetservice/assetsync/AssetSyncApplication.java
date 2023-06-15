package com.bonc.assetservice.assetsync;


import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Description: 临时建立一个标签的服务的boot 项目帮助调试
 * @Author: huyang
 * @Email huyang@bonc.com.cn
 * @Date: 2022/6/13 11:54
 * @Version: V1.0
 */
@SuppressWarnings("SpringComponentScan")
@SpringBootApplication(scanBasePackages = {"com.bonc.module","com.bonc.assetservice.*"})
@EnableEncryptableProperties
@EnableScheduling
@EnableAsync(proxyTargetClass=true)
public class AssetSyncApplication {

    public static void main(String[] args) {
        System.setProperty("jasypt.encryptor.algorithm", "PBEWithMD5AndDES");
        System.setProperty("jasypt.encryptor.password", "pMsmcCFSU6I0Zrzbonc");
        System.setProperty("jasypt.encryptor.pool-size", "1");
        System.setProperty("jasypt.encryptor.providerName", "SunJCE");
        System.setProperty("jasypt.encryptor.ivGeneratorClassName", "org.jasypt.iv.NoIvGenerator");
        System.setProperty("jasypt.encryptor.key-obtention-iterations", "1000");
        System.setProperty("jasypt.encryptor.string-output-type", "base64");
        SpringApplication.run(AssetSyncApplication.class, args);
    }

    /***
     *
     *  ExecutorService pool = Executors.newFixedThreadPool(1);
     *
     *         String subExpression = "UNICOMCLOUD_ASSETS_1||UNICOMCLOUD_ASSETS_7";
     *
     *         try {
     *             //log.info("初始化消费端...");
     *             String consumer_group="";
     *             String rocketmq_namesrv_addr="";
     *             DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumer_group);
     *             consumer.setNamesrvAddr(rocketmq_namesrv_addr);//127.0.0.1:10911
     *             //消费模式:一个新的订阅组第一次启动从队列的最后位置开始消费 后续再启动接着上次消费的进度开始消费
     *             //consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
     *             //订阅主题和 标签（ * 代表所有标签)下信息
     * //				UNICOMCLOUD_ASSETS_1 表资产
     * //				UNICOMCLOUD_ASSETS_2 文件资产
     * //				UNICOMCLOUD_ASSETS_3 数据集资产
     * //				UNICOMCLOUD_ASSETS_4 视图资产
     * //				UNICOMCLOUD_ASSETS_5 标签资产
     * //				UNICOMCLOUD_ASSETS_6 指标资产
     * //				UNICOMCLOUD_ASSETS_7 字段资产
     *             consumer.subscribe("gd-unicom-topic", subExpression);
     *             // //注册消费的监听 并在此监听中消费信息，并返回消费的状态信息
     *             consumer.registerMessageListener(new MessageListenerConcurrently() {
     *                 @Override
     *                 public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
     *                     for (MessageExt msg : msgs) {
     *                         //log.info("获取到msg==》{}", msg.toString());
     *                         pool.submit(new WorkerThread(msg));
     *                     }
     *                     return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
     *                 }
     *             });
     *             consumer.start();
     *             //log.info("创建被动消费成功");
     *         } catch (Exception e) {
     *            // log.info("创建被动消费者失败...");
     *             e.printStackTrace();
     *         }
     *
     *         try {
     *             String data_mb = "{\"assetsId\":\"%s\",\"status\":\"0\",\"classesId\":\"1\"}";
     *             String asset_mb = "{\"classesId\":\"7\",\"tableAssetsId\":\"%s\",\"assetsId\":\"%s\",\"status\":\"0\"}";
     *             String data_package ="data_package"; //PropsUtil.get("data_package");
     *             String assets = "assets";//PropsUtil.get("assets");
     *             List<String> dataList = new ArrayList<>();
     *             List<String> assetList = new ArrayList<>();
     *             if (StringUtils.isNotBlank(data_package)) {
     *                 String[] data_packages = data_package.split(";");
     *                 for (String dataPackage : data_packages) {
     *                     String format = String.format(data_mb, dataPackage.trim());
     *                     dataList.add(format);
     *                 }
     *             }
     *             if (StringUtils.isNotBlank(assets)) {
     *                 String[] assetss = assets.split(";");
     *                 for (String s : assetss) {
     *                     String[] split = s.split(",");
     *                     String s1 = split[0];
     *                     for (int i = 1; i < split.length; i++) {
     *                         String format = String.format(asset_mb, s1.trim(), split[i].trim());
     *                         assetList.add(format);
     *                     }
     *                 }
     *             }
     *             System.out.println("开始发送");
     *
     *             String consumer_group="";
     *             String rocketmq_namesrv_send="";
     *
     *             DefaultMQProducer producer = new DefaultMQProducer(consumer_group);
     *             producer.setNamesrvAddr(rocketmq_namesrv_send);
     *             producer.start();
     *             if (dataList != null && dataList.size() > 0) {
     *                 for (String s : dataList) {
     *                     Message msg = new Message("gd-unicom-topic", "UNICOMCLOUD_ASSETS_1", (s).getBytes(RemotingHelper.DEFAULT_CHARSET));
     *                     producer.sendOneway(msg);
     *                 }
     *             }
     *             if (assetList != null && assetList.size() > 0) {
     *                 for (String s : assetList) {
     *                     Message msg = new Message("gd-unicom-topic", "UNICOMCLOUD_ASSETS_7", (s).getBytes(RemotingHelper.DEFAULT_CHARSET));
     *                     producer.sendOneway(msg);
     *                     Thread.sleep(2000);
     *                 }
     *             }
     *             producer.shutdown();
     *         } catch (Exception e) {
     *             e.printStackTrace();
     *             System.out.println("发送失败");
     *         }
     *
     *
     */



}
