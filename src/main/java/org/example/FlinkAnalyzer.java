package org.example;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;

import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.triggers.ProcessingTimeTrigger;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.Properties;

public class FlinkAnalyzer {
    public static void main(String[] args) throws Exception {
        // Создаем среду выполнения
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        String brokers = "host.docker.internal:9092";//"localhost:9094"; // Замените на ваши настройки
        String topic = "topic1"; // Замените на ваш топик

        // Настройки Kafka
        Properties props = new Properties();
        props.put("bootstrap.servers", brokers);
        props.put("group.id", "t1");

        // Создаем KafkaSource с использованием вашего десериализатора
        KafkaSource<Event> source = KafkaSource.<Event>builder()
                .setBootstrapServers(brokers)
                .setTopics(topic)
                .setGroupId("t1")
                .setStartingOffsets(OffsetsInitializer.latest()) // Читаем с начала
                .setValueOnlyDeserializer(new EventDeserializer()) // Используем ваш десериализатор
                .setProperties(props)
                .build();

        DataStream<Event> eventStream = env.fromSource(source,
                        WatermarkStrategy.forBoundedOutOfOrderness(Duration.ofSeconds(5)), "Kafka Source")
                .assignTimestampsAndWatermarks(WatermarkStrategy.<Event>forBoundedOutOfOrderness(Duration.ofSeconds(5))
                        .withTimestampAssigner((event, timestamp) -> event.getEventTimelng()));

        // Выводим события перед агрегацией
        eventStream.print();

        DataStream<Event> maxMag = eventStream
                .map(e -> {System.out.println(e.getEventTimelng()); return e;})
                .keyBy(e -> e.region) // Группируем по региону
                .window(TumblingProcessingTimeWindows.of(Time.seconds(10))) // Устанавливаем окно в 10 секунд
                .trigger(ProcessingTimeTrigger.create())
                .reduce(new ReduceFunction<Event>() {
                    @Override
                    public Event reduce(Event value1, Event value2) {
                        Event value3 = value1.mag > value2.mag ? value1 : value2; // Находим максимальную магнитуду
                        System.out.println(value3.mag);
                        return value3;
                    }
                });
        maxMag.print(); // Выводим результаты на консоль

        DataStream<Event> corls = eventStream
                .map(e -> {System.out.println(e.getEventTimelng()); return e;})
                .windowAll(TumblingProcessingTimeWindows.of(Time.seconds(10))) // Устанавливаем окно в 10 секунд
                .trigger(ProcessingTimeTrigger.create())
                .reduce(new ReduceFunction<Event>() {
                    @Override
                    public Event reduce(Event value1, Event value2) {
                        double R = 6371.00;
                        double lat1 = value1.lat;
                        double lat2 = value2.lat;
                        double lon1 = value1.lon;
                        double lon2 = value2.lon;
                        double deg2rad = Math.PI / 180.0;
                        double dLat = deg2rad * (lat2 - lat1);  // deg2rad below
                        double dLon = deg2rad * (lon2-lon1);
                        double a =
                                Math.sin(dLat/2) * Math.sin(dLat/2) +
                                        Math.cos(deg2rad*lat1) * Math.cos(deg2rad*lat2) *
                                                Math.sin(dLon/2) * Math.sin(dLon/2)
                                ;
                        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
                        double d = R * c; // Distance in km
                        if (d <= 20){
                            value1.corr_region = value2.region;
                            System.out.println(value1.mag + " " + d);
                            return value1;
                        }
                        else{
                            return value2;
                        }
                    }
                });
        corls.print();

        Properties KafkaProps = new Properties();
        KafkaProps.put("bootstrap.servers", "host.docker.internal:9092"/*"localhost:9094"*/);
        KafkaProps.put("key.serializer", StringSerializer.class.getName());
        KafkaProps.put("value.serializer", EventSerializer.class.getName());
        KafkaProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, EventSerializer.class.getName());
        KafkaProps.put("acks", "1");

        KafkaSink<Event> sink = KafkaSink.<Event>builder()
                .setBootstrapServers("host.docker.internal:9092")
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setTopic("topic2")
                        .setKafkaValueSerializer(EventSerializer.class)
                        .build())
                .setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                .build();

        KafkaSink<Event> sink_cor = KafkaSink.<Event>builder()
                .setBootstrapServers("host.docker.internal:9092")
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setTopic("topic3")
                        .setKafkaValueSerializer(EventSerializer.class)
                        .build())
                .setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                .build();

// Подключаем sink к результатирующему потоку
        maxMag.sinkTo(sink);
        corls.sinkTo(sink_cor);
        env.execute("Flink Kafka Consumer with Event Deserializer");
    }
}
