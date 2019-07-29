package com.ren.jdbc.cache;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
/**
 * sql 语句执行
 * String 源码
 * JDK源码
 * 
 */
/**
 * 缓存对象的生存期和session的生存期类似, 从最后一次访问时间开始计时
 * 
 * 缓存算法是指令的一个明细表，用于决定缓存系统中哪些数据应该被删去
 * @author REN
 *
 * @param <K>
 * @param <V>
 */
public abstract class AbstractCache<K, V> implements Cache<K, V> {
    // 永久
    private final static int PERMANENT = -1;
    /**
     * 存储缓存的Map
     */
    protected Map<K, CacheValue<K, V>> map;
    /**
     * 缓存的容量
     */
    protected final int capacity;
    
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    
    private final Lock readLock = readWriteLock.readLock();
    
    private final Lock writeLock = readWriteLock.writeLock();
    
    
    
    public AbstractCache(int capacity) {
        this.capacity = capacity;
    }
    
    @Override
    public boolean isFull() {
        return map.size() == capacity;
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public V get(K key) {
        
        try {
            readLock.lock();
            CacheValue<K, V> cv = map.get(key);
            if (cv == null) {
                return null;
            }
            if (cv.isTimeOut()) {
                map.remove(key);
                return null;
            }
            return cv.getValue();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void put(K key, V value) {
        put(key, value, PERMANENT);
    }

    @Override
    public void put(K key, V value, long existenceTime) {
        try {
            writeLock.lock();
            if (isFull()) {
                eliminate();
            }
            map.put(key, new CacheValue<>(key, value, existenceTime));
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void removeByKey(K key) {
        try {
            writeLock.lock();
            map.remove(key);
        } finally {
            writeLock.unlock();
        }
    }
    // 淘汰对象
    protected abstract void eliminate();
    @Override
    public void clear() {
        map.clear();
    }
    
    static class CacheValue<K, V> {
        CacheValue(K k, V value, long existenceTime) {
            this.value = value;
            this.key = k;
            this.lastAccess = System.currentTimeMillis();
            this.accessCount = 0;
            this.existenceTime = existenceTime;
        }
        long accessCount;
        long lastAccess;// 最后一次访问时间
        final long existenceTime;// 生存时间
        V value;
        K key;
        
        boolean isTimeOut() {
            if (existenceTime < 0) { // 永不过期
                return false;
            }
            return lastAccess + existenceTime < System.currentTimeMillis();
        }
        
        V getValue() {
            this.lastAccess = System.currentTimeMillis();
            this.accessCount++;
            return value;
        }
    }
}
