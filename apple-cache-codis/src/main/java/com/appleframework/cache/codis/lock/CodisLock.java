package com.appleframework.cache.codis.lock;

import java.util.List;

import org.apache.log4j.Logger;

import com.appleframework.cache.codis.CodisResourcePool;
import com.appleframework.cache.core.lock.Lock;
import com.appleframework.cache.core.utils.SequenceUtility;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

/**
 * Redis distributed lock implementation.
 *
 * @author cruise.xu
 */
public class CodisLock implements Lock {

	private static Logger logger = Logger.getLogger(CodisLock.class);

	private CodisResourcePool codisResourcePool;

	/**
	 * 锁超时时间，防止线程在入锁以后，无限的执行等待
	 */
	private long acquireTimeout = 60000;

	/**
	 * 锁等待时间，防止线程饥饿
	 */
	private long timeout = 10000;
	
	
	private static String sequence = SequenceUtility.getSequence();
	
	
	private String keyPrefix = "lock:";
	
	public void setKeyPrefix(String keyPrefix) {
		this.keyPrefix = keyPrefix;
	}

	/**
	 * Detailed constructor with default acquire timeout 10000 msecs and lock
	 * expiration of 60000 msecs.
	 *
	 * @param lockKey
	 *            lock key (ex. account:1, ...)
	 */
	public CodisLock(CodisResourcePool codisResourcePool) {
		this.codisResourcePool = codisResourcePool;
	}

	/**
	 * Detailed constructor with default lock expiration of 60000 msecs.
	 *
	 */
	public CodisLock(CodisResourcePool codisResourcePool, long timeout) {
		this(codisResourcePool);
		this.timeout = timeout;
	}

	/**
	 * Detailed constructor.
	 *
	 */
	public CodisLock(CodisResourcePool codisResourcePool, long acquireTimeout, long timeout) {
		this(codisResourcePool, timeout);
		this.acquireTimeout = acquireTimeout;
	}
	
	private String genIdentifier() {
		return sequence + "-" + Thread.currentThread().getId();
	}
	
	/**
	 * 加锁
	 * 
	 * @param lockKey
	 *            锁的key
	 * @param acquireTimeout
	 *            获取超时时间
	 * @param timeout
	 *            锁的超时时间
	 * @return 锁标识
	 */
	public void lock(String lockKey, long acquireTimeout, long timeout) {
		try (Jedis jedis = codisResourcePool.getResource()) {
			// 随机生成一个value
			String identifier = this.genIdentifier();
			
			// 锁名，即key值
			lockKey = keyPrefix + lockKey;
			// 超时时间，上锁后超过此时间则自动释放锁
			int lockExpire = (int) (timeout / 1000);

			// 获取锁的超时时间，超过这个时间则放弃获取锁
			long end = System.currentTimeMillis() + acquireTimeout;
			while (System.currentTimeMillis() < end) {
				if (jedis.setnx(lockKey, identifier) == 1) {
					jedis.expire(lockKey, lockExpire);
					break;
				}
				// 返回-1代表key没有设置超时时间，为key设置一个超时时间
				if (jedis.ttl(lockKey) == -1) {
					jedis.expire(lockKey, lockExpire);
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	/**
	 * 获得 lock. 实现思路: 主要是使用了redis 的setnx命令,缓存了锁. reids缓存的key是锁的key,所有的共享,
	 * value是锁的到期时间(注意:这里把过期时间放在value了,没有时间上设置其超时时间) 执行过程:
	 * 1.通过setnx尝试设置某个key的值,成功(当前没有这个锁)则返回,成功获得锁
	 * 2.锁已经存在则获取锁的到期时间,和当前时间比较,超时的话,则设置新的值
	 *
	 * @return true if lock is acquired, false acquire timeouted
	 * @throws InterruptedException
	 *             in case of thread interruption
	 */
	public void lock(String lockKey) {
		this.lock(lockKey, acquireTimeout, timeout);
	}
	
	@Override
	public boolean tryLock(String lockKey, long timeout) {
		boolean lockedSuccess = false;
		try (Jedis jedis = codisResourcePool.getResource()) {
			// 随机生成一个value
			String identifier = this.genIdentifier();
			// 锁名，即key值
			lockKey = keyPrefix + lockKey;
			// 超时时间，上锁后超过此时间则自动释放锁
			int lockExpire = (int) (timeout / 1000);
			if (jedis.setnx(lockKey, identifier) == 1) {
				jedis.expire(lockKey, lockExpire);
				lockedSuccess = true;
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return lockedSuccess;
	}
	
	/**
     * 获得 lock.
     * 实现思路: 主要是使用了redis 的setnx命令,缓存了锁.
     * reids缓存的key是锁的key,所有的共享, value是锁的到期时间(注意:这里把过期时间放在value了,没有时间上设置其超时时间)
     * 执行过程:
     * 1.通过setnx尝试设置某个key的值,成功(当前没有这个锁)则返回,成功获得锁
     * 2.锁已经存在则获取锁的到期时间,和当前时间比较,超时的话,则设置新的值
     *
     * @return true if lock is acquired, false acquire timeouted
     * @throws InterruptedException in case of thread interruption
     */
	@Override
	public boolean tryLock(String lockKey) {
		return this.tryLock(lockKey, timeout);
	}

	@Override
	public boolean isLocked(String lockKey) {
		boolean isLocked = false;
		try (Jedis jedis = codisResourcePool.getResource()) {
			// 随机生成一个value
			String identifier = this.genIdentifier();
			// 锁名，即key值
			lockKey = keyPrefix + lockKey;
			String value = jedis.get(lockKey);
			if (null != value && value.equals(identifier)) {
				isLocked = true;
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return isLocked;
	}

	/**
	 * 释放锁
	 * 
	 * @param lockName
	 *            锁的key
	 * @param identifier
	 *            释放锁的标识
	 * @return
	 */
	public void unlock(String lockKey) {
		String identifier = this.genIdentifier();
		lockKey = keyPrefix + lockKey;
		try (Jedis jedis = codisResourcePool.getResource()) {
			while (true) {
				// 监视lock，准备开始事务
				jedis.watch(lockKey);
				// 通过前面返回的value值判断是不是该锁，若是该锁，则删除，释放锁
				String value = jedis.get(lockKey);
				if (identifier.equals(value)) {
					Transaction transaction = jedis.multi();
					transaction.del(lockKey);
					List<Object> results = transaction.exec();
					if (results == null) {
						continue;
					}
				}
				jedis.unwatch();
				break;
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

}
