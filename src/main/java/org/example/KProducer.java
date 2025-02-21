package org.example;

import java.util.Properties;

import org.apache.kafka.clients.producer.*;

class DemoProducerCallback implements Callback {
    @Override
    public void onCompletion(RecordMetadata recordMetadata, Exception e) {
        if (e != null) {
            e.printStackTrace();
        }
    }
}

public class KProducer {

    public void kproducer(String in, String out) {
        Properties KafkaProps = new Properties();
        KafkaProps.put("bootstrap.servers", "localhost:29092");
        KafkaProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        KafkaProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        KafkaProps.put("acks", "1");

        Producer producer = new KafkaProducer<String, String>(KafkaProps);

        ProducerRecord<String, String> record = new ProducerRecord<>("topic1", in, out);

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