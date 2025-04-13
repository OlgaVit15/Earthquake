package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class EventDeserializer implements DeserializationSchema<Event> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Event deserialize(byte[] message) throws IOException {
        if (message == null || message.length == 0) {
            return null; // Возвращаем null, если нет данных
        }
        // Десериализуем JSON в объект Event
        return objectMapper.readValue(message, Event.class);
    }

    @Override
    public boolean isEndOfStream(Event nextElement) {
        return false; // Указываем, что это не конец потока
    }

    @Override
    public TypeInformation<Event> getProducedType() {
        return TypeInformation.of(Event.class); // Указываем тип данных
    }
}
