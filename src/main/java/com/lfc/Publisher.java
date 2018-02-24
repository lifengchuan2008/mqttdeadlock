/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lfc;

import io.vertx.core.Vertx;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by ppatiern on 06/03/17.
 */
public class Publisher {

    private static final Logger LOG = LoggerFactory.getLogger(Publisher.class);

    private static final String MQTT_MESSAGE = "Message from publisher ";

    private static int counter = 0;


    public static void main(String[] args) {

        Vertx vertx = Vertx.vertx();

        try {

            String clientId = "you mqtt  clientId";
            String topic = "you  mqtt  topic ";
            int connectionTimeout = 30000;
            String username = "you  mqtt username";
            String password = "you  mqtt password";
            boolean cleanSession = true;
            String host = "you  mqtt host";
            int port = 1883;

            MemoryPersistence persistence = new MemoryPersistence();
            MqttClient client = new MqttClient(String.format("tcp://%s:%d", host, port), clientId, persistence);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(username);
            options.setPassword(password.toCharArray());
            options.setCleanSession(cleanSession);
            client.connect(options);

            vertx.setPeriodic(40, t -> {

                try {
                    client.publish(topic, (MQTT_MESSAGE + counter++).getBytes(), 1, false);
                } catch (MqttException e) {
                    e.printStackTrace();
                }

            });

            System.in.read();
            client.disconnect();

            vertx.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
