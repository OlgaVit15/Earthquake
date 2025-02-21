package org.example;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.json.JSONObject;

import java.time.Duration;
import java.util.*;

public class KConsumer {
    public void consumer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:29092");
        props.put("group.id", "t1");
        props.put("key.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer =
                new KafkaConsumer<String, String>(props);

        try {
            consumer.subscribe(Collections.singletonList("topic1"));
            Duration timeout = Duration.ofMillis(10);
            Map<String, Integer> eartqMap = new HashMap<>();
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(timeout);
                for (ConsumerRecord<String, String> record : records) {
                    String reg = record.key();
                    String mag = record.value();
                    System.out.printf("topic = " + record.topic() + " partition = " + record.partition()
                            + " offset = " + record.offset() +
                            " key = "  + reg + " value = " + mag);
                    int updatedCount = 1;
                    if (eartqMap.containsKey(record.value())) {
                        updatedCount = eartqMap.get(record.value()) + 1;
                    }
                    eartqMap.put(record.value(), updatedCount);
                    JSONObject json = new JSONObject(eartqMap);
                    System.out.println(json.toString());
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args){
        KConsumer c = new KConsumer();
        c.consumer();
    }
}
