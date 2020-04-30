package com.dame.cn.config.redis;

import org.springframework.stereotype.Component;
import redis.clients.jedis.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author LYQ
 * @description Redis工具类
 * @since 2020/4/21 10:24
 **/
@Component
public class RedisClient {

    public static JedisPool jedisPool;

    public JedisPool getJedisPool() {
        return jedisPool;
    }

    @Resource(name = "jedisPool")
    public void setJedisPool(JedisPool jedisPool) {
        RedisClient.jedisPool = jedisPool;
    }

    public String ping() {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.ping();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String set(final String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.set(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String get(final String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String quit() {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.quit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Boolean exists(final String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.exists(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long del(final String... keys) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.del(keys);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String type(final String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.type(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String flushDB() {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.flushDB();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Set<String> keys(final String pattern) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.keys(pattern);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String randomKey() {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.randomKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String rename(final String oldkey, final String newkey) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.rename(oldkey, newkey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long renamenx(final String oldkey, final String newkey) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.renamenx(oldkey, newkey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long dbSize() {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.dbSize();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long expire(final String key, final int seconds) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.expire(key, seconds);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long expireAt(final String key, final long unixTime) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.expireAt(key, unixTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long ttl(final String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.ttl(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String select(final int index) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.select(index);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long move(final String key, final int dbIndex) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.move(key, dbIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String flushAll() {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.flushAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getSet(final String key, final String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.getSet(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> mget(final String... keys) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.mget(keys);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long setnx(final String key, final String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.setnx(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String setex(final String key, final int seconds, final String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.setex(key, seconds, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String mset(final String... keysvalues) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.mset(keysvalues);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long msetnx(final String... keysvalues) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.msetnx(keysvalues);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long decrBy(final String key, final long integer) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.decrBy(key, integer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long decr(final String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.decr(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long incrBy(final String key, final long integer) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.incrBy(key, integer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long incr(final String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.incr(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long append(final String key, final String value) {

        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.append(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String substr(final String key, final int start, final int end) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.substr(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long hset(final String key, final String field, final String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hset(key, field, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String hget(final String key, final String field) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hget(key, field);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long hsetnx(final String key, final String field, final String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hsetnx(key, field, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String hmset(final String key, final Map<String, String> hash) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hmset(key, hash);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> hmget(final String key, final String... fields) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hmget(key, fields);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long hincrBy(final String key, final String field, final long value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hincrBy(key, field, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Boolean hexists(final String key, final String field) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hexists(key, field);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long hdel(final String key, final String field) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hdel(key, field);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long hlen(final String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hlen(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Set<String> hkeys(final String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hkeys(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> hvals(final String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hvals(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, String> hgetAll(final String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hgetAll(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long rpush(final String key, final String string) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.rpush(key, string);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long lpush(final String key, final String string) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lpush(key, string);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long llen(final String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.llen(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> lrange(final String key, final long start, final long end) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lrange(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String ltrim(final String key, final long start, final long end) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.ltrim(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String lindex(final String key, final long index) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lindex(key, index);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String lset(final String key, final long index, final String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lset(key, index, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long lrem(final String key, final long count, final String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lrem(key, count, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String lpop(final String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lpop(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String rpop(final String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.rpop(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String rpoplpush(final String srckey, final String dstkey) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.rpoplpush(srckey, dstkey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long sadd(final String key, final String... member) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.sadd(key, member);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Set<String> smembers(final String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.smembers(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long srem(final String key, final String... member) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.srem(key, member);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String spop(final String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.spop(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long smove(final String srckey, final String dstkey, final String member) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.smove(srckey, dstkey, member);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long scard(final String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.scard(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Boolean sismember(final String key, final String member) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.sismember(key, member);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Set<String> sinter(final String... keys) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.sinter(keys);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long sinterstore(final String dstkey, final String... keys) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.sinterstore(dstkey, keys);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Set<String> sunion(final String... keys) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.sunion(keys);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long sunionstore(final String dstkey, final String... keys) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.sunionstore(dstkey, dstkey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Set<String> sdiff(final String... keys) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.sdiff(keys);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long sdiffstore(final String dstkey, final String... keys) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.sdiffstore(dstkey, keys);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String srandmember(final String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.srandmember(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> srandmember(final String key, final int count) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.srandmember(key, count);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long zadd(final String key, final double score, final String member) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zadd(key, score, member);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Set<String> zrange(final String key, final int start, final int end) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrange(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long zrem(final String key, final String member) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrem(key, member);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Double zincrby(final String key, final double score, final String member) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zincrby(key, score, member);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long zrank(final String key, final String member) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrank(key, member);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long zrevrank(final String key, final String member) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrevrank(key, member);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Set<String> zrevrange(final String key, final int start, final int end) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrevrange(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Set<Tuple> zrangeWithScores(final String key, final int start, final int end) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrangeWithScores(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Set<Tuple> zrevrangeWithScores(final String key, final int start, final int end) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrevrangeWithScores(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long zcard(final String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zcard(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Double zscore(final String key, final String member) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zscore(key, member);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String watch(final String... keys) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.watch(keys);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> sort(final String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.sort(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> sort(final String key, final SortingParams sortingParameters) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.sort(key, sortingParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> blpop(final int timeout, final String... keys) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.blpop(timeout, keys);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long sort(final String key, final SortingParams sortingParameters, final String dstkey) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.sort(key, sortingParameters, dstkey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long sort(final String key, final String dstkey) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.sort(key, dstkey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> brpop(final int timeout, final String... keys) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.brpop(timeout, keys);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String auth(final String password) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.auth(password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void subscribe(JedisPubSub jedisPubSub, String... channels) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.subscribe(jedisPubSub, channels);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Long publish(String channel, String message) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.publish(channel, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void psubscribe(JedisPubSub jedisPubSub, String... patterns) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.psubscribe(jedisPubSub, patterns);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Long zcount(final String key, final double min, final double max) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zcount(key, min, max);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Set<String> zrangeByScore(final String key, final double min, final double max) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrangeByScore(key, min, max);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Set<String> zrangeByScore(final String key, final String min, final String max) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrangeByScore(key, min, max);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Set<String> zrangeByScore(final String key, final double min, final double max, final int offset, final int count) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrangeByScore(key, min, max, offset, count);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Set<Tuple> zrangeByScoreWithScores(final String key, final double min, final double max) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrangeByScoreWithScores(key, min, max);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Set<Tuple> zrangeByScoreWithScores(final String key, final double min, final double max, final int offset, final int count) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrangeByScoreWithScores(key, min, max, offset, count);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Set<String> zrevrangeByScore(final String key, final double max, final double min) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrevrangeByScore(key, max, min);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Set<String> zrevrangeByScore(final String key, final String max, final String min) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrevrangeByScore(key, max, min);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Set<String> zrevrangeByScore(final String key, final double max, final double min, final int offset, final int count) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrevrangeByScore(key, max, min, offset, count);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Set<Tuple> zrevrangeByScoreWithScores(final String key, final double max, final double min) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrevrangeByScoreWithScores(key, max, min);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Set<Tuple> zrevrangeByScoreWithScores(final String key, final double max, final double min, final int offset, final int count) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrevrangeByScoreWithScores(key, max, min, offset, count);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long zremrangeByRank(final String key, final int start, final int end) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zremrangeByRank(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long zremrangeByScore(final String key, final double start, final double end) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zremrangeByScore(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long zunionstore(final String dstkey, final String... sets) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zunionstore(dstkey, sets);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long zunionstore(final String dstkey, final ZParams params, final String... sets) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zunionstore(dstkey, params, sets);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long zinterstore(final String dstkey, final String... sets) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zinterstore(dstkey, sets);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long zinterstore(final String dstkey, final ZParams params, final String... sets) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zinterstore(dstkey, params, sets);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long strlen(final String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.strlen(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long lpushx(final String key, final String string) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lpushx(key, string);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long persist(final String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.persist(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long rpushx(final String key, final String string) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.rpushx(key, string);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String echo(final String string) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.echo(string);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public String brpoplpush(String source, String destination, int timeout) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.brpoplpush(source, destination, timeout);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean setbit(String key, long offset, boolean value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.setbit(key, offset, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean getbit(String key, long offset) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.getbit(key, offset);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public long setrange(String key, long offset, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.setrange(key, offset, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String getrange(String key, long startOffset, long endOffset) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.getrange(key, startOffset, endOffset);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
