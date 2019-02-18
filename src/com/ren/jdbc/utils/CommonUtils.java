package com.ren.jdbc.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ren.jdbc.config.PJConfig;
import com.ren.jdbc.sql.BoundSql;

public class CommonUtils {
    /**
     * 
     * 把pjconfig 的ids,作为参数取出
     * 
     * @param pjconfig
     * @param obj
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     * @throws SecurityException
     */
    public static Object[] getIdArgs(PJConfig pjconfig, Object obj)
            throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
        List<Object> args = new ArrayList<>();
        for (Iterator iterator = pjconfig.getIdJavas().iterator(); iterator.hasNext();) {
            String jname = (String) iterator.next();
            Field field = obj.getClass().getDeclaredField(jname);
            field.setAccessible(true);
            args.add(field.get(obj));
        }
        return args.toArray();
    }
    /**
     * 把一个pjc 取出其ID列=?
     * 例如: id1 = ? and id2 = ?
     * @param pjc
     * @return
     */
    public static String getIdsCommons(PJConfig pjc) {
        List<String> ids = pjc.getIds();
        StringBuilder coms = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            if (i == 0) {
                coms.append(ids.get(i) + BoundSql.EQUAL + BoundSql.QM);
            } else {
                coms.append(BoundSql.AND + BoundSql.SPACE + ids.get(i) + BoundSql.EQUAL + BoundSql.QM);
            }
        }
        return coms.toString();
    }
    /**
     * map中的是tablefield->value
     * 按照onesjavaname的顺序
     * @param pjconfig
     * @param obj
     * @param map
     * @return
     * @throws NoSuchFieldException
     * @throws SecurityException
     */
    public static Object[] getOneArgs(PJConfig pjconfig, Object obj, Map<String, Object> map) throws NoSuchFieldException, SecurityException {
        List<Object> args = new ArrayList<>();
        // linkedHashMap 支持有序, java one
        for (Iterator iterator = pjconfig.getOnesJavaName().keySet().iterator(); iterator.hasNext();) {
            String jfieldName = (String) iterator.next();
            String v = pjconfig.getOnesJavaName().get(jfieldName); // teacher_id
            args.add(map.get(v));
        }
        return args.toArray();
    }
    /**
     * person 中有teacher_id
     * map: teacher_id->obj 对应与
     * 返回一个teacher.id ->obj
     * @param person
     * @param map
     */
    public static Map<String, Object> mapOne(PJConfig person, PJConfig teacher, Map<String, Object> map) {
        Map<String, Object> copy = new HashMap<>(map);
        for (Iterator iterator = map.keySet().iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            String value = person.getOnesMap().get(key);
            int k = value.lastIndexOf("#");
            if (k != -1) {
                copy.put(value.substring(k), map.get(key));
            }else {
                // need seek at teacher id, the ids`s size must 1
                List<String> ids = teacher.getIds();
                if (ids.size() != 1) {
                    throw new RuntimeException("you must set your @one completly :" + person);
                }
                copy.put(ids.get(0), map.get(key));
            }
        }
        return copy;
    }
    /**
     * 按照jnames给定顺序，获得obj内属性值数组
     * @param obj
     * @param jnames
     * @return
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     */
    public static Object[] getArgs(Object obj, List<String> jnames) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        List<Object> list = new ArrayList<>();
        for (int i = 0;i < jnames.size();i++) {
            Field f = obj.getClass().getDeclaredField(jnames.get(i));
            f.setAccessible(true);
            list.add(f.get(obj));
        }
        return list.toArray();
    }
    // 拿到
    public static String teacher_id2teacher(PJConfig pjc, String table_one) {
        Set<String> set = pjc.getOnesJavaName().keySet();
        for (String str:set) {
            if (pjc.getOnesJavaName().get(str).equals(table_one)) {
                return str;
            }
        }
        return null;
    }
    
}
