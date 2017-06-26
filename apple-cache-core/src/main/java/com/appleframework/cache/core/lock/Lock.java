package com.appleframework.cache.core.lock;

public interface Lock {

	/**
	 * @return lock key
	 */
	public String getLockKey();

	/**
	 * @return true if lock is acquired, false acquire timeouted
	 * @throws InterruptedException
	 *             in case of thread interruption
	 */
	public boolean lock() throws InterruptedException;

	/**
	 * Acqurired lock release.
	 */
	public void unlock();

}
