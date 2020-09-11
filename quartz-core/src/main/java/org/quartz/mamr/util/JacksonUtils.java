package org.quartz.mamr.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Jackson工具类
 *
 * @author mamr
 * @date 2020/6/17 4:02 下午
 */
public class JacksonUtils {
    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

    private static ObjectMapper objectMapper;

    static {
        objectMapper = serializingObjectMapper();
    }

    public static ObjectMapper serializingObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        objectMapper.registerModule(javaTimeModule);
        return objectMapper;
    }

    private static class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
        @Override
        public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException {
            gen.writeString(value.format(DateTimeFormatter.ofPattern(PATTERN)));
        }
    }

    public static class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext deserializationContext)
                throws IOException {
            return LocalDateTime.parse(p.getValueAsString(), DateTimeFormatter.ofPattern(PATTERN));
        }
    }

    public static String objectToString(Object o) throws JsonProcessingException {
        return objectMapper.writeValueAsString(o);
    }

    public static <T, V> Map<T, V> objectToMap(Object o, Class<T> l1, Class<V> l2) throws JsonProcessingException {
        JavaType javaType = getCollectionType(HashMap.class, l1, l2);
        return objectMapper.readValue(objectToString(o), javaType);
    }

    public static <T, V> Map<T, V> jsonStrToMap(String jsonStr, Class<T> l1, Class<V> l2) throws JsonProcessingException {
        JavaType javaType = getCollectionType(HashMap.class, l1, l2);
        return objectMapper.readValue(jsonStr, javaType);
    }

    public static <T> T stringToObject(String str, Class<T> cl) throws JsonProcessingException {
        return objectMapper.readValue(str, cl);
    }

    public static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }

    public static String mapToString(Map map) throws JsonProcessingException {
        return objectMapper.writeValueAsString(map);
    }
}

