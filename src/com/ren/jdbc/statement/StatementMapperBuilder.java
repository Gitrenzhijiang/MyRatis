package com.ren.jdbc.statement;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ren.jdbc.config.Configuration;
import com.ren.jdbc.config.PJConfig;
import com.ren.jdbc.sql.BoundSql;
import com.ren.jdbc.sql.SqlType;
import com.ren.jdbc.utils.CommonUtils;

public class StatementMapperBuilder {
    /**
     * 使用主键查询单个对象的statementMapper生成
     * @param clazz
     * @param config
     * @param conn
     * @return
     */
    public StatementMapper selectOneById(Class clazz, Configuration config, Connection conn) {
        PJConfig pjc = config.getPojoConfigMap().get(clazz);
        Map<SqlType, BoundSql> map = config.getSqlsByClass(clazz);
        BoundSql bs = map.get(SqlType.SELECT);
        // 使用复制的bs
        bs = copy(bs);
        if (pjc.getIds().size() != 1) {
            throw new RuntimeException("只适用与一个@Id的对象");
        }
        bs.setCommons(pjc.getIds().get(0) + BoundSql.EQUAL + BoundSql.QM);
        return new StatementMapper(conn, bs);
    }
    /**
     * 查询statementMapper
     * @param clazz
     * @param config
     * @param conn
     * @param condition
     * @return
     */
    public StatementMapper selectList(Class clazz, Configuration config, Connection conn, String condition) {
        PJConfig pjc = config.getPojoConfigMap().get(clazz);
        Map<SqlType, BoundSql> map = config.getSqlsByClass(clazz);
        BoundSql bs = map.get(SqlType.SELECT);
        // 使用复制的bs
        bs = copy(bs);
        
        bs.setCommons(condition);
        return new StatementMapper(conn, bs);
    }
    
    
    private BoundSql copy(BoundSql bs) {
        return new BoundSql(bs);
    }
    /**
     * 删除某个对象的sm
     * @param clazz
     * @param config
     * @param conn
     * @return
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     */
    public StatementMapper deleteOne(Class<?> clazz, Configuration config, Connection conn, Object obj) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        PJConfig pjc = config.getPojoConfigMap().get(clazz);
        Map<SqlType, BoundSql> map = config.getSqlsByClass(clazz);
        BoundSql bs = map.get(SqlType.DELETE);
        bs = copy(bs);
        
        bs.setCommons(CommonUtils.getIdsCommons(pjc));
        bs.addArgs(CommonUtils.getIdArgs(pjc, obj));
        
        return new StatementMapper(conn, bs);
    }
    /**
     * 插入
     * @param clazz
     * @param config
     * @param conn
     * @param obj
     * @return
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @throws ClassNotFoundException 
     */
    public StatementMapper insertOne(Class<?> clazz, Configuration config, Connection conn, Object obj) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, ClassNotFoundException {
        PJConfig pjc = config.getPojoConfigMap().get(clazz);
        Map<SqlType, BoundSql> map = config.getSqlsByClass(clazz);
        BoundSql bs = map.get(SqlType.INSERT);
        bs = copy(bs);
        bs.setUseGeneratedKey(pjc.isUseGenerate());
        // view: columns + ids + onesmap.keyset
        if (pjc.isUseGenerate() == false) {
            bs.getView().addAll(pjc.getIds());
        }
        bs.getView().addAll(pjc.getOnesMap().keySet());
        bs.addArgs(CommonUtils.getArgs(obj, pjc.getColJavas())); // 放入columns
        if (pjc.isUseGenerate() == false) {
            bs.addArgs(CommonUtils.getIdArgs(pjc, obj));
        }
        // onesmap的值插入,如果是null,
     // teacher_id 的值要拿到 
        for (Iterator iterator = pjc.getOnesMap().keySet().iterator(); iterator.hasNext();) {
            String teacher_id = (String) iterator.next();
            // 根据teacher_id拿到 teacher的class
            String cn = pjc.getOnesMap().get(teacher_id);
            String ref_name = null; // 关联到teacher的属性名称
            int k = cn.lastIndexOf("#");
            if (k != -1) {
                ref_name = cn.substring(k+1);
            }else {
                List<String> ids = config.getPojoConfigMap().get(Class.forName(cn)).getIds();
                if (ids.size() != 1) {
                    throw new RuntimeException("please set @one ref at:" + cn);
                }
                ref_name = ids.get(0);
            }
            // 先拿到teacher
            Field field = obj.getClass().getDeclaredField(CommonUtils.teacher_id2teacher(pjc, teacher_id));
            field.setAccessible(true);
            Object teacher = field.get(obj);
            if (teacher == null) {
                bs.addArg(null);
                continue;
            }
            Field refn = teacher.getClass().getDeclaredField(ref_name);
            refn.setAccessible(true);
            Object value = refn.get(teacher);
            bs.addArg(value);
            
        }
        return new StatementMapper(conn, bs);
    }
    
    
    public StatementMapper updateOne(Class<?> clazz, Configuration config, Connection conn, Object obj) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, ClassNotFoundException {
        PJConfig pjc = config.getPojoConfigMap().get(clazz);
        Map<SqlType, BoundSql> map = config.getSqlsByClass(clazz);
        BoundSql bs = map.get(SqlType.UPDATE);
        bs = copy(bs);
        // id参数作为条件
        bs.setCommons(CommonUtils.getIdsCommons(pjc));
        // 把column映射成javaFiled name
        
        bs.addArgs(CommonUtils.getArgs(obj, pjc.getColJavas())); // 放入columns
        // teacher_id 的值要拿到 
        for (Iterator iterator = pjc.getOnesMap().keySet().iterator(); iterator.hasNext();) {
            String teacher_id = (String) iterator.next();
            // 根据teacher_id拿到 teacher的class
            String cn = pjc.getOnesMap().get(teacher_id);
            String ref_name = null; // 关联到teacher的属性名称
            int k = cn.lastIndexOf("#");
            if (k != -1) {
                ref_name = cn.substring(k+1);
            }else {
                List<String> ids = config.getPojoConfigMap().get(Class.forName(cn)).getIds();
                if (ids.size() != 1) {
                    throw new RuntimeException("please set @one ref at:" + cn);
                }
                ref_name = ids.get(0);
            }
            // 先拿到teacher
            Field field = obj.getClass().getDeclaredField(CommonUtils.teacher_id2teacher(pjc, teacher_id));
            field.setAccessible(true);
            Object teacher = field.get(obj);
            Field refn = teacher.getClass().getDeclaredField(ref_name);
            refn.setAccessible(true);
            bs.addArg(refn.get(teacher));
        }
        bs.addArgs(CommonUtils.getIdArgs(pjc, obj));
        return new StatementMapper(conn, bs);
    }
}
