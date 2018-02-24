package com.lfc;

import com.lfc.verticle.MqttVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;

public class MainTest {


    public static void main(String[] args) {

        Vertx vertx = Vertx.vertx();

        JsonObject config = new JsonObject();
        DeploymentOptions options = new DeploymentOptions().setConfig(config).setInstances(1).setWorker(true);

        String deploymentName = MqttVerticle.class.getName();

        vertx.rxDeployVerticle(deploymentName, options).subscribe(result -> {

        });

    }
}
