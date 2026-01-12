package com.covoiturage.messaginggateway.config;


import com.covoiturage.messaginggateway.model.ConversationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, ConversationContext> redisTemplate(
            RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, ConversationContext> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Sérialiseurs
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(ConversationContext.class));
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(ConversationContext.class));

        template.afterPropertiesSet();
        return template;
    }
}

