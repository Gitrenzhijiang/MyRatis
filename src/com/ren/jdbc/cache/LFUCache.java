package com.ren.jdbc.cache;

import java.util.HashMap;
import java.util.Iterator;
/**
 * 最不频繁使用
 * @author REN
 *
 * @param <K>
 * @param <V>
 */
public class LFUCache<K, V> extends AbstractCache<K, V> {

    public LFUCache(int capacity) {
        super(capacity);
        
        this.map = new HashMap<>(capacity + 1);
    }
    /**
     * 实现清除过期对象和 最不频繁使用的对象
     */
    @Override
    protected void eliminate() {
        Iterator<CacheValue<K, V>> iterator = map.values().iterator();
        Long min = null;
        while (iterator.hasNext()) {
            CacheValue<K, V> cv = iterator.next();
            // 判断是否过期
            if (cv.isTimeOut()) {
                iterator.remove();
                continue;
            }
            // 没有过期,
            if (min == null) {
                min = cv.accessCount;
            }else if (cv.accessCount < min) {
                min = cv.accessCount;
            }
        }
        
        if (!isFull()) {
            return;
        }
        if (min == null) {
            return;
        }
        // 如果此时缓存仍然是满的, 移除 最不频繁使用的
        iterator = map.values().iterator();
        while (iterator.hasNext()) {
            CacheValue<K, V> cv = iterator.next();
            cv.accessCount -= min;
            if (cv.accessCount <= 0) {
                iterator.remove();
            }
        }
    }

}
