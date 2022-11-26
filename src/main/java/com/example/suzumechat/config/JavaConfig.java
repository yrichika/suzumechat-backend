package com.example.suzumechat.config;

import java.time.Duration;
import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;


@Configuration
@EnableCaching
public class JavaConfig {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    // customizing caching durations for different cache keys.
    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {

        return (builder) -> builder
            .withCacheConfiguration("guestChannelToken", RedisCacheConfiguration
                .defaultCacheConfig().entryTtl(Duration.ofMinutes(10))
                .serializeValuesWith(SerializationPair.fromSerializer(
                    new GenericJackson2JsonRedisSerializer())))
            .withCacheConfiguration("hostChannelToken", RedisCacheConfiguration
                .defaultCacheConfig().entryTtl(Duration.ofMinutes(10)));
    }

    // setting the same duration for all caching
    // @Bean
    // public RedisCacheConfiguration cacheConfiguration() {
    // return
    // RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(10))
    // .disableCachingNullValues().serializeValuesWith(
    // SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
    // }
}
