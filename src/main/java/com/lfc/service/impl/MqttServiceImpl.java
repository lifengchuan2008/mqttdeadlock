package com.lfc.service.impl;

import com.google.inject.Inject;
import com.lfc.service.KafkaService;
import com.lfc.service.MqttService;
import io.vertx.core.Vertx;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import io.vertx.reactivex.core.Future;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MqttServiceImpl implements MqttService {
    private static final Logger log = LogManager.getLogger(MqttServiceImpl.class);

    private final Vertx vertx;
    private MqttClient client;
    private final KafkaService kafkaService;

    @Inject
    public MqttServiceImpl(Vertx vertx, KafkaService kafkaService) {
        this.vertx = vertx;
        this.kafkaService = kafkaService;


        String clientId = "you mqtt  clientId";
        String topic = "you  mqtt  topic ";
        int connectionTimeout = 30000;
        String username = "you  mqtt username";
        String password = "you  mqtt password";
        boolean cleanSession = true;
        String host = "you  mqtt host";
        int port = 1883;
        MqttClientOptions options = new MqttClientOptions().setUsername(username)
                .setPassword(password).setClientId(clientId).setCleanSession(cleanSession);
        options.setConnectTimeout(connectionTimeout);
        Future<MqttClient> mqttClientFuture = Future.future();
        vertx.executeBlocking(future -> {
            client = MqttClient.create(vertx, options);
            future.complete(client);
        }, res -> {
            mqttClientFuture.complete((MqttClient) res.result());
        });
        mqttClientFuture.setHandler(result -> {
            client.connect(port, host, sr -> {
                if (sr.succeeded()) {
                    client.subscribe(topic, 0, done -> {
                        log.debug("=====" + done.result());
                    });
                    log.debug("===connected===");
                }
            });
            client.publishHandler(message -> {
                log.debug("send msg");
                kafkaService.sendMsg(message.payload().getBytes());
            });
            client.exceptionHandler(error -> {
                error.printStackTrace();
                log.error("mqtt  lient  error", error);
            });
        });
    }

}
