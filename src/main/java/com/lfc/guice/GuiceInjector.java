package com.lfc.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.lfc.service.KafkaService;
import com.lfc.service.MqttService;
import com.lfc.service.impl.KafkaServiceImpl;
import com.lfc.service.impl.MqttServiceImpl;
import io.vertx.reactivex.core.Vertx;

public class GuiceInjector extends AbstractModule {

    @Provides
    @Singleton
    public Vertx getVertx() {
        return Vertx.vertx();
    }


    @Override
    protected void configure() {

        bind(MqttService.class).to(MqttServiceImpl.class).in(Singleton.class);
        bind(KafkaService.class).to(KafkaServiceImpl.class).in(Singleton.class);

    }
}
