package com.lfc.verticle;

import com.google.inject.Inject;
import com.lfc.service.KafkaService;
import com.lfc.service.MqttService;
import io.vertx.core.Future;
import io.vertx.reactivex.core.AbstractVerticle;

public class MqttVerticle extends AbstractVerticle {
    @Inject
    private MqttService mqttService;
    @Inject
    private KafkaService kafkaService;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        super.start(startFuture);
    }
}
