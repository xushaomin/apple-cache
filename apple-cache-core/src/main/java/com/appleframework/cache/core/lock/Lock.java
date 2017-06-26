package com.appleframework.cache.core.lock;

public interface Lock {

	/**
	 * @return true if lock is acquired, false acquire timeouted
	 * @throws InterruptedException
	 *             in case of thread interruption
	 */
	public boolean lock(String lockKey) throws InterruptedException;

	/**
	 * Acqurired lock release.
	 */
	public void unlock(String lockKey);

}
