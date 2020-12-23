package com.example.emq;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Component;

/**
 * @author long
 * @date 2020/10/29
 */
@Slf4j
@Component
public class ConfigCallback implements MqttCallback {

    @Override
    public void connectionLost(Throwable cause) {
        log.info("连接断开，进行重连:{}", cause);
        cause.printStackTrace();
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) {
        log.info("ConfigCallback接收消息主题:{},接收消息Qos:{},接收消息内容:{}", topic, mqttMessage.getQos(), new String(mqttMessage.getPayload()));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        log.info("deliveryComplete:{}", iMqttDeliveryToken.isComplete());
    }
}
