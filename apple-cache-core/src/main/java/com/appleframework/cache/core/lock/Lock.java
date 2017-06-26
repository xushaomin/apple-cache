package com.appleframework.cache.core.lock;

public interface Lock {
	
	/**
     * 加锁
     * @param locaName  锁的key
     * @param acquireTimeout  获取超时时间
     * @param timeout   锁的超时时间
     * @return 锁标识
     */
    public void lock(String lockKey, long acquireTimeout, long timeout);

	/**
	 * @return true if lock is acquired, false acquire timeouted
	 * @throws InterruptedException
	 *             in case of thread interruption
	 */
	public void lock(String lockKey);
	
	/**
     * 加锁
     * @param locaName  锁的key
     * @param acquireTimeout  获取超时时间
     * @param timeout   锁的超时时间
     * @return 锁标识
     */
	public boolean tryLock(String lockKey);
	
	/**
     * 加锁
     * @param lockKey  锁的key
     * @param acquireTimeout  获取超时时间
     * @param timeout   锁的超时时间
     * @return 锁标识
     */
	public boolean tryLock(String lockKey, long timeout);

	/**
     * 解锁
     * @param lockKey  锁的key
     * @return 锁标识
     */
	public void unlock(String lockKey);
	
	/**
     * Checks if this lock locked by any thread
     *
     * @return <code>true</code> if locked otherwise <code>false</code>
     */
    boolean isLocked(String lockKey);

}
