package com.ren.jdbc.cache;

import java.util.Iterator;
import java.util.LinkedHashMap;


public class FIFOCache<K, V> extends AbstractCache<K, V>{

    public FIFOCache(int capacity) {
        super(capacity);
        map = new LinkedHashMap<>(capacity+1, 1.0F, false);
    }

    @Override
    protected void eliminate() {
        Iterator<CacheValue<K, V>> iterator = map.values().iterator();
        K first = null;
        while (iterator.hasNext()) {
            CacheValue<K, V> cv = iterator.next();
            if (cv.isTimeOut()) {
                iterator.remove();
            }else if (first == null) {
                first = cv.key;
            }
        }
        if (!isFull()) {
            return;
        }
        if (first != null) {
            map.remove(first);
        }
    }
    
}
