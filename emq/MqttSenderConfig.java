package com.example.emq;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * mq发送配置
 *
 * @author: zhou
 * @date: 2020/7/27
 */
@Configuration
@IntegrationComponentScan
@Slf4j
public class MqttSenderConfig {
    @Value("${mqtt.user}")
    private String username;

    @Value("${mqtt.pwd}")
    private String password;

    @Value("${mqtt.url}")
    private String hostUrl;

    @Value("${mqtt.port}")
    private String port;

    @Value("${mqtt.client.id}")
    private String clientId;

    @Value("${mqtt.default.topic}")
    private String defaultTopic;

    /**
     * 连接设置
     *
     * @return
     */
    @Bean
    public MqttConnectOptions getMqttConnectOptions() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setUserName(username);
        mqttConnectOptions.setPassword(password.toCharArray());
        mqttConnectOptions.setServerURIs(new String[]{"ws://" + hostUrl + ":" + port});
        mqttConnectOptions.setKeepAliveInterval(2);
        return mqttConnectOptions;
    }

    /**
     * 客户端工厂
     *
     * @return
     */
    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(getMqttConnectOptions());
        return factory;
    }

    /**
     * 发布通知
     *
     * @return
     */
    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(clientId, mqttClientFactory());
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic(defaultTopic);
        return messageHandler;
    }

    /**
     * 发布通道为直连
     *
     * @return
     */
    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    /**
     * mqtt消息发送
     */
    @Component
    @MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
    public interface MsgSend {

        /**
         * 发送信息到MQTT服务器
         *
         * @param data 发送的文本
         */
        void sendToMqtt(String data);

        /**
         * 发送信息到MQTT服务器
         *
         * @param topic   主题
         * @param payload 消息主体
         */
        void sendToMqtt(String payload, @Header(MqttHeaders.TOPIC) String topic);

        /**
         * 发送信息到MQTT服务器
         *
         * @param topic   主题
         * @param qos     对消息处理的几种机制。
         *                0 表示的是订阅者没收到消息不会再次发送，消息会丢失。
         *                1 表示的是会尝试重试，一直到接收到消息，但这种情况可能导致订阅者收到多次重复消息。
         *                2 多了一次去重的动作，确保订阅者收到的消息有一次。
         * @param payload 消息主体
         */
        void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic, @Header(MqttHeaders.QOS) int qos, String payload);
    }
}
