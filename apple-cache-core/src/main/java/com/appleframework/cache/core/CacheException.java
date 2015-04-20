package com.appleframework.cache.core;

public class CacheException extends RuntimeException {
	
	private static final long serialVersionUID = -3684238567977062659L;

	/**
     * Default constructor
     */
    public CacheException() {
        super();
    }

    /**
     * Create a new CacheException from a message which explain the nature of
     * the Exception
     */
    public CacheException(String message) {
        super(message);
    }

    
    /**
     * Create a new CacheException from a message and a base throwable
     * exception
     */
    public CacheException(String message, Throwable throwable) {
        super(message, throwable);
    }
    
   
}