package com.sparkLab.study.common.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager();

        manager.registerCustomCache(
                "accountToMentee",
                Caffeine.newBuilder()
                        .maximumSize(100_000)
                        .expireAfterWrite(10, TimeUnit.MINUTES)
                        .build()
        );

        manager.registerCustomCache(
                "dailyPlan",
                Caffeine.newBuilder()
                        .maximumSize(50_000)
                        .expireAfterWrite(1, TimeUnit.DAYS)
                        .build()
        );

        return manager;
    }
}

