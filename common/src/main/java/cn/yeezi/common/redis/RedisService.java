package cn.yeezi.common.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * redis操作工具类
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    // =============================common============================

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回代表为永久有效
     */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true存在，false不存在
     */
    public Boolean exists(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值或多个值
     */
    @SuppressWarnings("unchecked")
    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete((Collection<String>) CollectionUtils.arrayToList(key));
            }
        }
    }

    // ============================String=============================

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功，false失败
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key     键
     * @param value   值
     * @param timeout 过期时间，如果time小于等于，将设置无限期
     * @param unit    时间单位
     * @return true成功，false失败
     */
    public boolean set(String key, Object value, long timeout, TimeUnit unit) {
        try {
            if (timeout > 0) {
                redisTemplate.opsForValue().set(key, value, timeout, unit);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于)
     * @return 递增后的值
     */
    public Long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递增并设置过期时间
     *
     * @param key     键
     * @param delta   要增加几(大于)
     * @param timeout 过期时间，仅在首次创建时设置
     * @param unit    时间单位
     * @return 递增后的值
     */
    public Long incrWithExpire(String key, long delta, long timeout, TimeUnit unit) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于");
        }
        Long result = redisTemplate.opsForValue().increment(key, delta);
        
        // 只在首次创建时设置过期时间（避免重复设置）
        if (result != null && result.equals(delta) && timeout > 0) {
            redisTemplate.expire(key, timeout, unit);
            log.info("设置递增键过期时间: {}, 过期: {}{}", key, timeout, unit.toString().toLowerCase());
        }
        
        return result;
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(小于)
     * @return 递减后的值
     */
    public Long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    // ================================Hash=================================

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public Object hget(String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true成功，false失败
     */
    public boolean hmset(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     *
     * @param key     键
     * @param map     对应多个键值
     * @param timeout 过期时间
     * @param unit    时间单位
     * @return true成功，false失败
     */
    public boolean hmset(String key, Map<String, Object> map, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (timeout > 0) {
                redisTemplate.expire(key, timeout, unit);
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true成功，false失败
     */
    public boolean hset(String key, String item, Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key     键
     * @param item    项
     * @param value   值
     * @param timeout 过期时间 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @param unit    时间单位
     * @return true成功，false失败
     */
    public boolean hset(String key, String item, Object value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (timeout > 0) {
                redisTemplate.expire(key, timeout, unit);
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }


    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(String key, Object... item) {
        redisTemplate.opsForHash().delete(key, item);
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true存在，false不存在
     */
    public boolean hHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于)
     * @return true成功，false失败
     */
    public double hincr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于)
     * @return true成功，false失败
     */
    public double hdecr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }

    // ============================Set=============================

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return Set
     */
    public Set<Object> sGet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true存在，false不存在
     */
    public Boolean sHasKey(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }


    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public Long sSet(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return 0L;
        }
    }

    /**
     * 将set数据放入缓存
     *
     * @param key     键
     * @param timeout 过期时间
     * @param unit    时间单位
     * @param values  值 可以是多个
     * @return 成功个数
     */
    public Long sSetAndTime(String key, long timeout, TimeUnit unit, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (timeout > 0) {
                redisTemplate.expire(key, timeout, unit);
            }
            return count;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return 0L;
        }
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return Set长度
     */
    public Long sGetSetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return 0L;
        }
    }

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public Long setRemove(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().remove(key, values);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return 0L;
        }
    }

    // ===============================List=================================

    /**
     * start 和 end 偏移量都是基于0的下标，即list的第一个元素下标是0（list的表头），第二个元素下标是1，以此类推。
     * 偏移量也可以是负数，表示偏移量是从list尾部开始计数。 例如， -1 表示列表的最后一个元素，-2 是倒数第二个，以此类推。
     *
     * @param key   键
     * @param start 开始
     * @param end   结束  到 -代表所有值
     * @return 存储在 key 的列表里指定范围内的元素
     */
    public List<Object> lRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }


    /**
     * 如果 key 不存在，那么就被看作是空list，并且返回长度为 0。 当存储在 key 里的值不是一个list的话，会返回error。
     *
     * @param key 键
     * @return 存储在 key 里的list的长度
     */
    public Long lLen(String key) {
        return redisTemplate.opsForList().size(key);
    }


    /**
     * 下标是从0开始索引的，所以 0 是表示第一个元素， 1 表示第二个元素，并以此类推。
     * 负数索引用于指定从列表尾部开始索引的元素。在这种方法下，-1 表示最后一个元素，-2 表示倒数第二个元素，并以此往前推。
     *
     * @param key   键
     * @param index 索引
     * @return 列表里的元素的索引 index 存储在 key 里面
     */
    public Object lIndex(String key, long index) {
        return redisTemplate.opsForList().index(key, index);
    }

    /**
     * 设置 index 位置的list元素的值为 value。
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     */
    public void lSet(String key, long index, Object value) {
        redisTemplate.opsForList().set(key, index, value);
    }

    /**
     * 移除并且返回 key 对应的 list 的第一个元素
     *
     * @param key 键
     * @return 返回第一个元素的值，或者当 key 不存在时返回 nil
     */
    public Object lPop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    /**
     * 将所有指定的值插入到存于 key 的列表的头部。
     * 如果 key 不存在，那么在进行 push 操作前会创建一个空列表。 如果 key 对应的值不是一个 list 的话，那么会返回一个错误。
     * 可以使用一个命令把多个元素 push 进入列表，只需在命令末尾加上多个指定的参数。
     * 元素是从最左端的到最右端的、一个接一个被插入到 list 的头部。
     * 所以对于这个命令例子 LPUSH mylist a b c，返回的列表是 c 为第一个元素， b 为第二个元素， a 为第三个元素。
     *
     * @param key   键
     * @param value list元素
     * @return 在 push 操作后的列表长度
     */
    public Long lPush(String key, Object value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 将所有指定的值插入到存于 key 的列表的头部。如果 key 不存在，那么在进行 push 操作前会创建一个空列表。
     * 如果 key 对应的值不是一个 list 的话，那么会返回一个错误。
     * 可以使用一个命令把多个元素 push 进入列表，只需在命令末尾加上多个指定的参数。
     * 元素是从最左端的到最右端的、一个接一个被插入到 list 的头部。
     * 所以对于这个命令例子 LPUSH mylist a b c，返回的列表是 c 为第一个元素， b 为第二个元素， a 为第三个元素。
     *
     * @param key        键
     * @param collection 集合
     * @return 在 push 操作后的 list 长度
     */
    public Long lPushAll(String key, Collection<Object> collection) {
        return redisTemplate.opsForList().leftPushAll(key, collection);
    }

    /**
     * 移除并返回存于 key 的 list 的最后一个元素。
     *
     * @param key reidsKey
     * @return 最后一个元素的值，或者当 key 不存在的时候返回 nil
     */
    public Object rPop(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }

    /**
     * 向存于 key 的列表的尾部插入所有指定的值。如果 key 不存在，那么会创建一个空的列表然后再进行 push 操作。
     * 当 key 保存的不是一个列表，那么会返回一个错误。
     * 可以使用一个命令把多个元素打入队列，只需要在命令后面指定多个参数。元素是从左到右一个接一个从列表尾部插入。
     * 比如命令 RPUSH mylist a b c 会返回一个列表，其第一个元素是 a ，第二个元素是 b ，第三个元素是 c。
     *
     * @param key   键
     * @param value list元素
     * @return 在 push 操作后的列表长度
     */
    public Long rPush(String key, Object value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }


    /**
     * 向存于 key 的列表的尾部插入所有指定的值。
     * 如果 key 不存在，那么会创建一个空的列表然后再进行 push 操作。 当 key 保存的不是一个列表，那么会返回一个错误。
     * 可以使用一个命令把多个元素打入队列，只需要在命令后面指定多个参数。
     * 元素是从左到右一个接一个从列表尾部插入。 比如命令 RPUSH mylist a b c 会返回一个列表，其第一个元素是 a ，第二个元素是 b ，第三个元素是 c。
     *
     * @param key  键
     * @param list 集合
     * @return 在 push 操作后的 list 长度
     */
    public <T> Long rPushAll(String key, List<T> list) {
        return redisTemplate.opsForList().rightPushAll(key, list.toArray());
    }

    /**
     * 从存于 key 的列表里移除前 count 次出现的值为 value 的元素。 这个 count 参数通过下面几种方式影响这个操作：
     * <p>
     * count > 0: 从头往尾移除值为 value 的元素。
     * count < 0: 从尾往头移除值为 value 的元素。
     * count = 0: 移除所有值为 value 的元素。
     * 比如， LREM list -2 “hello” 会从存于 list 的列表里移除最后两个出现的 “hello”。
     * <p>
     * 需要注意的是，如果list里没有存在key就会被当作空list处理，所以当 key 不存在的时候，这个命令会返回 0。
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public Long lRem(String key, long count, Object value) {
        try {
            return redisTemplate.opsForList().remove(key, count, value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return 0L;
        }

    }

    // ===============================zSet=================================

    public Set<Object> getAllZSet(String key) {
        return redisTemplate.opsForZSet().range(key, 0, -1);
    }

    public void zSetAdd(String key, Object value) {
        redisTemplate.opsForZSet().add(key, value, System.currentTimeMillis());
    }

    public void zSetRemoveAll(String key) {
        redisTemplate.opsForZSet().removeRange(key, 0, -1);
    }

    // ------------------------------------------- 订阅发布 ------------------------------------------

    public void publish(String topic, Object message) {
        redisTemplate.convertAndSend(topic, message);
    }
}
