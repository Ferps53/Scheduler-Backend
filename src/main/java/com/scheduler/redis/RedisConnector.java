package com.scheduler.redis;

import com.scheduler.exceptions.exception.BadRequestException;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Startup
@Singleton
public final class RedisConnector {

    private static JedisPool jedisPool;

    @ConfigProperty(name = "redis.url")
    String redisUrl;

    @ConfigProperty(name = "redis.user")
    String redisUser;

    @ConfigProperty(name = "redis.password")
    String redisPassword;

    @ConfigProperty(name = "redis.port")
    Integer redisPort;

    public static JedisPool getJedisPool() {
        return jedisPool;
    }

    @PostConstruct
    public synchronized void initConnection() {
        try {
            if (redisPassword.isEmpty()) {
                jedisPool = new JedisPool(new JedisPoolConfig(), redisUrl, redisPort);
            } else {
                jedisPool = new JedisPool(new JedisPoolConfig(), redisUrl, redisPort, redisUser, redisPassword);
            }
        } catch (Exception e) {
            throw new BadRequestException(e);
        }
    }
}
