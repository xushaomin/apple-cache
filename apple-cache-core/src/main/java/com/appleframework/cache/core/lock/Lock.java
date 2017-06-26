package com.appleframework.cache.core.lock;

public interface Lock {

	/**
	 * 加锁
	 * 
	 * @param locaName
	 *            锁的key
	 * @param acquireTimeout
	 *            获取超时时间
	 * @param timeout
	 *            锁的超时时间
	 */
	public void lock(String lockKey, long acquireTimeout, long timeout);

	/**
	 * 加锁
	 * 
	 * @param locaName
	 *            锁的key
	 */
	public void lock(String lockKey);

	/**
	 * 加锁
	 * 
	 * @param lockKey
	 *            锁的key
	 * @return 是否加锁成功
	 */
	public boolean tryLock(String lockKey);

	/**
	 * 加锁
	 * 
	 * @param lockKey
	 *            锁的key
	 * @param timeout
	 *            锁的超时时间
	 * @return 是否加锁成功
	 */
	public boolean tryLock(String lockKey, long timeout);

	/**
	 * 解锁
	 * 
	 * @param lockKey
	 *            锁的key
	 */
	public void unlock(String lockKey);

	/**
	 * 判断是否处于锁状态
	 * 
	 * @param lockKey
	 *            锁的key
	 * @return true=正处于锁状态 false=未加锁
	 */
	public boolean isLocked(String lockKey);

}
