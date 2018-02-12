package com.lfc.service.impl;

import com.google.inject.Inject;
import com.lfc.service.KafkaService;
import io.vertx.core.Future;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.kafka.client.producer.KafkaProducer;
import io.vertx.reactivex.kafka.client.producer.KafkaProducerRecord;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.protocol.SecurityProtocol;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Objects;
import java.util.Properties;

public class KafkaServiceImpl implements KafkaService {

    private static final Logger log = LogManager.getLogger(KafkaServiceImpl.class);

    private final Vertx vertx;
    private KafkaProducer<String, byte[]> producer;
    private String topic = "test";

    @Inject
    public KafkaServiceImpl(Vertx vertx) {
        this.vertx = vertx;

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


        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getCanonicalName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                ByteArraySerializer.class.getCanonicalName());


        Future<KafkaProducer> kafkaProducerFuture = Future.future();

        vertx.<KafkaProducer>executeBlocking(future -> {
            producer = KafkaProducer.create(vertx, properties);
            future.complete(producer);
        }, res -> {
            kafkaProducerFuture.<KafkaProducer>complete(res.result());
        });

    }

    @Override
    public void sendMsg(byte[] msg) {
        producer.rxWrite(KafkaProducerRecord.create(topic, msg)).subscribe(metadata -> {

            log.debug("kafka send msg");
        });
    }
}
