package org.example;

import java.time.OffsetDateTime;
import java.util.Properties;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.json.JSONObject;

class DemoProducer implements Callback {
    public void onCompletion(RecordMetadata metadata, Exception exception) {
        if (exception != null) {
            System.err.println("Error while producing message to topic :" + metadata.topic());
            exception.printStackTrace();
        } else {
            System.out.println("Message sent successfully to topic " + metadata.topic() + " partition " + metadata.partition() + " with offset " + metadata.offset());
        }
    }
}


public class Prod {

    public static void kprod() {
        Properties KafkaProps = new Properties();
        KafkaProps.put("bootstrap.servers", "localhost:9094");
        KafkaProps.put("key.serializer", StringSerializer.class.getName());
        KafkaProps.put("value.serializer", EventSerializer.class.getName());
        KafkaProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, EventSerializer.class.getName());
        KafkaProps.put("acks", "1");

        Producer<String, Event> producer = new KafkaProducer<>(KafkaProps);
        Event event = new Event("2023-10-01T12:11:55Z", "US", 10.0, 43.0, 55.0, 5.0);

        try {
            producer.send(new ProducerRecord<>("topic1", "1", event), new DemoProducer());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            producer.close(); // Закрытие продюсера
        }
    }

    public static void main(String[] args) {
        kprod();
    }
}