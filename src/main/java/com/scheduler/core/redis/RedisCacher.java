package com.scheduler.core.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scheduler.core.exceptions.exception.BadRequestException;
import jakarta.enterprise.context.ApplicationScoped;
import redis.clients.jedis.Jedis;

import java.util.List;

@ApplicationScoped
public class RedisCacher {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public <T> void saveInCache(final String key, final T type) {
        final Thread thread = new Thread(() -> {
            try (Jedis jedis = RedisConnector.getJedisPool().getResource()) {
                final String json = MAPPER.writeValueAsString(type);
                jedis.set(key, json);

            } catch (final Exception e) {
                throw new BadRequestException(e.getMessage());
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            thread.interrupt();
            throw new BadRequestException(e.getMessage());
        }
    }

    public <T> T getFromCache(final String key, final Class<T> classe) {
        final Jedis jedis = RedisConnector.getJedisPool().getResource();
        T parsedObject = null;
        try {
            final String jsonString = jedis.get(key);
            if (jsonString != null) {
                parsedObject = MAPPER.readValue(jsonString, classe);
            }
        } catch (final Exception e) {
            throw new BadRequestException(e.getMessage());
        } finally {
            jedis.close();
        }
        return parsedObject;
    }

    public void deleteKey(final String key) {
        try (Jedis jedis = RedisConnector.getJedisPool().getResource()) {
            jedis.del(key);
        } catch (final Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public <T> List<T> getListFromCache(final String key, final Class<T> classe) {
        List<T> objectList = null;
        try (Jedis jedis = RedisConnector.getJedisPool().getResource()) {
            final String jsonArray = jedis.get(key);
            if (jsonArray != null) {
                objectList = MAPPER.readValue(jsonArray,
                        MAPPER.getTypeFactory().constructCollectionType(List.class, classe));
            }
        } catch (final Exception e) {
            throw new BadRequestException(e.getMessage());
        }
        return objectList;
    }

}
