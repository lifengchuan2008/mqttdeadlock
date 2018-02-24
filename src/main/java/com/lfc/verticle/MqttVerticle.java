package com.lfc.verticle;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ClientAuth;
import io.vertx.mqtt.*;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.kafka.client.producer.KafkaProducer;
import io.vertx.reactivex.kafka.client.producer.KafkaProducerRecord;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class MqttVerticle extends AbstractVerticle {

    private static final Logger log = LogManager.getLogger(MqttVerticle.class);


    private MqttClient client;
    private KafkaProducer<String, byte[]> producer;
    private String topic = "test";

    public static final String MQTT_SERVER_HOST = "localhost";
    public static final int MQTT_SERVER_PORT = 1883;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        super.start(startFuture);


        //mqtt
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
        io.vertx.reactivex.core.Future<MqttClient> mqttClientFuture = io.vertx.reactivex.core.Future.future();
        vertx.executeBlocking(future -> {
            client = MqttClient.create(vertx.getDelegate(), options);
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
                sendMsg(message.payload().getBytes());
            });
            client.exceptionHandler(error -> {
                error.printStackTrace();
                log.error("mqtt  lient  error", error);
            });
        });


        //kafka
        String kafkaBootstrapServers = "127.0.0.1:9092";
        String kafkaGroup = "";
        int kafkaBatchSize = 65536;
        int kafkaLingerMs = 5;
        int kafkaBufferMemory = 33554432;
        Map properties = new Properties();
        properties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaGroup);
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, kafkaBatchSize);
        properties.put(ProducerConfig.LINGER_MS_CONFIG, kafkaLingerMs);
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, kafkaBufferMemory);


        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getCanonicalName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getCanonicalName());


        Future<KafkaProducer> kafkaProducerFuture = Future.future();

        vertx.<KafkaProducer>executeBlocking(future -> {
            producer = KafkaProducer.create(vertx, properties);
            future.complete(producer);
        }, res -> {
            kafkaProducerFuture.<KafkaProducer>complete(res.result());
        });
    }

    public void sendMsg(byte[] msg) {
        producer.rxWrite(KafkaProducerRecord.create(topic, msg)).subscribe(metadata -> {

            log.debug("kafka send msg");
        });
    }


}
