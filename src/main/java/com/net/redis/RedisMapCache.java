package com.net.redis;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.net.annotation.LogIt;
import com.net.util.LogUtil;

import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;

@Service("redisMapCache")
public class RedisMapCache {

	private static Logger logger = LogUtil.getLogger();

	@Value("${redis.retryCount:3}")
	private int retryCount;

	@Value("${redis.sleepTime:500}")
	private int sleepTime;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	private Set<Class<? extends Exception>> retryFor;

	public RedisMapCache() {
		super();
		retryFor = new HashSet<Class<? extends Exception>>();
		retryFor.add(JedisException.class);
		retryFor.add(JedisConnectionException.class);
		retryFor.add(RedisConnectionFailureException.class);
	}

	public void put(final String masterKey, final String subKey, final Object obj) {
		logger.info("Method: put(), Redis Key: " + masterKey + " Child Key: " + subKey);
		Runnable runnable = new Runnable() {
			public void run() {
				RedisMapCache.this.redisTemplate.opsForHash().put(masterKey, subKey, obj);
			}
		};
		try {
			RetryableOperation.create(runnable).retry(retryCount, sleepTime, retryFor);
		} catch (Exception e) {
			throw new JedisException(e);
		}
	}

	public void putAll(final String masterKey, final Map<String, ? extends Object> map) {
		logger.info("Method: putAll(), Redis Key: " + masterKey);
		Runnable runnable = new Runnable() {
			public void run() {
				RedisMapCache.this.redisTemplate.opsForHash().putAll(masterKey, map);
			}
		};
		try {
			RetryableOperation.create(runnable).retry(retryCount, sleepTime, retryFor);
		} catch (Exception e) {
			throw new JedisException(e);
		}
	}
	
	@LogIt
	public Object get(final String masterKey, final String subKey) {
		Callable<Object> callable = new Callable() {
			public Object call() throws Exception {
				return RedisMapCache.this.redisTemplate.opsForHash().get(masterKey, subKey);
			}
		};
		try {
			return RetryableOperation.create(callable).retry(retryCount, sleepTime, retryFor);
		} catch (Exception e) {
			throw new JedisException(e);
		}
	}

	public Map<Object, Object> getAll(final String masterKey) {
		Callable<Map<Object, Object>> callable = new Callable() {
			public Map<Object, Object> call() throws Exception {
				return RedisMapCache.this.redisTemplate.opsForHash().entries(masterKey);
			}
		};
		try {
			return (Map) RetryableOperation.create(callable).retry(retryCount, sleepTime, retryFor);
		} catch (Exception e) {
			throw new JedisException(e);
		}
	}

	public void delete(final String masterKey, final String subKey) {
		logger.info("Method: delete(), Redis Key: " + masterKey + " Child Key: " + subKey);
		Runnable runnable = new Runnable() {
			public void run() {
				RedisMapCache.this.redisTemplate.opsForHash().delete(masterKey, new Object[] { subKey });
			}
		};
		try {
			RetryableOperation.create(runnable).retry(retryCount, sleepTime, retryFor);
		} catch (Exception e) {
			throw new JedisException(e);
		}
	}

	public void deleteAll(final String masterKey) {
		logger.info("Method: deleteAll(), Redis Key: " + masterKey);
		Runnable runnable = new Runnable() {
			public void run() {
				RedisMapCache.this.redisTemplate.delete(masterKey);
			}
		};
		try {
			RetryableOperation.create(runnable).retry(retryCount, sleepTime, retryFor);
		} catch (Exception e) {
			throw new JedisException(e);
		}
	}

	public List<Object> values(final String masterKey) {
		Callable<List<Object>> callable = new Callable() {
			public List<Object> call() throws Exception {
				return RedisMapCache.this.redisTemplate.opsForHash().values(masterKey);
			}
		};
		try {
			return (List) RetryableOperation.create(callable).retry(retryCount, sleepTime, retryFor);
		} catch (Exception e) {
			throw new JedisException(e);
		}
	}

	public Set<Object> getAllKeys(final String masterKey) {
		Callable<Set<Object>> callable = new Callable() {
			public Set<Object> call() throws Exception {
				return RedisMapCache.this.redisTemplate.opsForHash().keys(masterKey);
			}
		};
		try {
			return (Set) RetryableOperation.create(callable).retry(retryCount, sleepTime, retryFor);
		} catch (Exception e) {
			throw new JedisException(e);
		}
	}

	public boolean hasKey(final String masterKey, final String subKey) {
		Callable<Boolean> callable = new Callable() {
			public Boolean call() throws Exception {
				return RedisMapCache.this.redisTemplate.opsForHash().hasKey(masterKey, subKey);
			}
		};
		try {
			return ((Boolean) RetryableOperation.create(callable).retry(retryCount, sleepTime, retryFor))
					.booleanValue();
		} catch (Exception e) {
			throw new JedisException(e);
		}
	}

	public long size(final String masterKey) {
		Callable<Long> callable = new Callable() {
			public Long call() throws Exception {
				return RedisMapCache.this.redisTemplate.opsForHash().size(masterKey);
			}
		};
		try {
			return ((Long) RetryableOperation.create(callable).retry(retryCount, sleepTime, retryFor)).longValue();
		} catch (Exception e) {
			throw new JedisException(e);
		}
	}

	public List<Object> multiGet(final String masterKey, final List<Object> subKeys) {
		Callable<List<Object>> callable = new Callable() {
			public List<Object> call() throws Exception {
				return RedisMapCache.this.redisTemplate.opsForHash().multiGet(masterKey, subKeys);
			}
		};
		try {
			return (List) RetryableOperation.create(callable).retry(retryCount, sleepTime, retryFor);
		} catch (Exception e) {
			throw new JedisException(e);
		}
	}

	public RedisTemplate<String, Object> getRedisTemplate() {
		return this.redisTemplate;
	}

	public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

}
