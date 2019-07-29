package com.ren.jdbc.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;

import java.util.List;

import java.util.stream.Collectors;

import com.ren.jdbc.config.ColumnConfig;
import com.ren.jdbc.config.TableConfig;
import com.ren.jdbc.sql.BoundSql;

public class CommonUtils {
    /**
     * 
     *  将一个POJO类中的 @Id 属性拿出, @Id 的顺序依照 原始列顺序.
     * @param tableConfig
     * @param obj
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     * @throws SecurityException
     */
    public static Object[] getIdArgs(TableConfig tableConfig, final Object obj)
            throws IllegalArgumentException, SecurityException {
        List<ColumnConfig> ids = tableConfig.columnConfigList(e->{return e.isId();});
        return ids.stream().map(e->{
            try {
                Field field = obj.getClass().getDeclaredField(e.getAttrName());
                field.setAccessible(true);
                return field.get(obj);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            throw new RuntimeException(tableConfig.getPojoClass() + "内 " + e.getAttrName() + " 属性为NULL");
        }).collect(Collectors.toList()).toArray();
    }
    /**
     * 把一个tableConfig 取出其ID列=?
     * 例如: id1 = ? and id2 = ?
     * @param tableConfig
     * @return
     */
    public static String getIdsCommons(TableConfig tableConfig) {
        StringBuilder sb = new StringBuilder();
        // 查询所有的@Id
        List<ColumnConfig> ids = tableConfig.columnConfigList(e->{return e.isId();});
        for (int i = 0; i < ids.size(); i++){
            sb.append(BoundSql.SPACE + ids.get(i).getLabelName() + BoundSql.EQUAL + BoundSql.QM + BoundSql.SPACE);
            if (i != ids.size() - 1){
                sb.append(BoundSql.SPACE + BoundSql.AND + BoundSql.SPACE);
            }
        }
        return sb.toString();
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
    
}
