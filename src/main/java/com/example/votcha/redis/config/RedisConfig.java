package com.example.votcha.redis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory connectionFactory){
        RedisTemplate<String,Object> template = new RedisTemplate<>();

        template.setConnectionFactory(connectionFactory);
        // key serialize
        template.setKeySerializer(RedisSerializer.string());
        // value serialize
        template.setValueSerializer(RedisSerializer.json());
        // hash key serialize
        template.setHashKeySerializer(RedisSerializer.string());
        // hash value serialize
        template.setHashValueSerializer(RedisSerializer.json());

        template.afterPropertiesSet();

        return template;
    }


}
