package org.example;

import java.time.OffsetDateTime;
import java.util.Properties;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.json.JSONObject;

class DemoProducerCallback implements Callback {
    public void onCompletion(RecordMetadata metadata, Exception exception) {
        if (exception != null) {
            System.err.println("Error while producing message to topic :" + metadata.topic());
            exception.printStackTrace();
        } else {
            System.out.println("Message sent successfully to topic " + metadata.topic() + " partition " + metadata.partition() + " with offset " + metadata.offset());
        }
    }
}


public class KProducer {

    public static void kproducer(String key, Event value) {
        Properties KafkaProps = new Properties();
        KafkaProps.put("bootstrap.servers", "localhost:9094");
        KafkaProps.put("key.serializer", StringSerializer.class.getName());
        KafkaProps.put("value.serializer", EventSerializer.class.getName());
        KafkaProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, EventSerializer.class.getName());
        KafkaProps.put("acks", "1");

        Producer<String, Event> producer = new KafkaProducer<>(KafkaProps);

        ProducerRecord<String, Event> record = new ProducerRecord<>("topic1", key, value);

        try {
            producer.send(record, new DemoProducerCallback());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /*public static void main(String[] args) throws Exception {
        kproducer();
    }*/
}