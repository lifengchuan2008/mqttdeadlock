package com.lfc;

import com.google.common.collect.Lists;
import com.lfc.guice.GuiceInjector;
import com.lfc.verticle.MqttVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;

import java.util.List;

public class MainTest {


    public static void main(String[] args) {

        Vertx vertx = Vertx.vertx();

        JsonObject config = new JsonObject();
        List<String> guiceBinders = Lists.newArrayList(GuiceInjector.class.getName());
        config.put("guice_binder", guiceBinders);
        DeploymentOptions options = new DeploymentOptions().setConfig(config).setInstances(1).setWorker(true);

        String deploymentName = "java-guice:" + MqttVerticle.class.getName();

        vertx.rxDeployVerticle(deploymentName, options).subscribe(result -> {

        });

    }
}
