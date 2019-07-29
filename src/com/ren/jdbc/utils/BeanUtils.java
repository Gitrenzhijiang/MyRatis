package com.ren.jdbc.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class BeanUtils {
    public static <T> T getBean(Map<String, Object> map, Class<T> clazz){
        Field fs[] = clazz.getDeclaredFields();
        Set<String> names = map.keySet();
        T obj = null;
        try {
            obj = clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        for (Iterator iterator = names.iterator(); iterator.hasNext();) {
            String name = (String) iterator.next();
            for (Field f : fs) {
                if (f.getName().equals(name)) {
                    f.setAccessible(true);
                    try {
                        f.set(obj, map.get(name));
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                        throw e;
                    } catch (IllegalAccessException e2) {
                        e2.printStackTrace();
                    }
                    break;
                }
            }
        }
        return (T)obj;
    }
}
