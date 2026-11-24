package com.taiyidu.taiyiducommon.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

@Configuration
public class RedisConfig {
    /**
     * 这个是一个序列化器将object转化成string存在redis让排查的时候能让人明白问题
     * @param factory redis连接工厂
     * @return redis模板
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // 复用公共序列化器
        template.setValueSerializer(getJsonSerializer());
        template.setHashValueSerializer(getJsonSerializer());

        template.afterPropertiesSet();
        return template;
    }

    /**
     * 这个是缓存管理器，用于管理缓存
     * @param factory redis连接工厂
     * @return 缓存管理器
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
        // 复用公共序列化器
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(getJsonSerializer()));

        // 匿名内部类，重写 createRedisCache 方法实现【防雪崩】
        return new RedisCacheManager(
                RedisCacheWriter.nonLockingRedisCacheWriter(factory),
                config
        ) {
            @Override
            protected RedisCache createRedisCache(String name, RedisCacheConfiguration cacheConfig) {
                long baseMinutes = 30;
                long randomMinutes = ThreadLocalRandom.current().nextInt(1, 6);
                long finalTtl = baseMinutes + randomMinutes;

                return super.createRedisCache(name, cacheConfig.entryTtl(Duration.ofMinutes(finalTtl)));
            }
        };
    }


    /**
     * 抽离出一个公共的序列化器构建方法，避免重复代码
     */
    private Jackson2JsonRedisSerializer<Object> getJsonSerializer() {
        Jackson2JsonRedisSerializer<Object> jsonSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);

        om.registerModule(new JavaTimeModule());
        // 关闭时间戳格式，将其转换为 ISO 字符串（如 "2023-10-25T14:30:00"）
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        jsonSerializer.setObjectMapper(om);
        return jsonSerializer;
    }
}