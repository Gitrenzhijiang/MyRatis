package com.ren.jdbc.statement;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Map;
import java.util.stream.Collectors;

import com.ren.jdbc.config.Configuration;
import com.ren.jdbc.config.TableConfig;
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
        TableConfig tableConfig = config.getTableConfigMap().get(clazz);
        Map<SqlType, BoundSql> map = config.getSqlsByClass(clazz);
        BoundSql bs = map.get(SqlType.SELECT);
        // 使用复制的bs
        bs = copy(bs);

        bs.setCommons(CommonUtils.getIdsCommons(tableConfig));
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
        TableConfig tableConfig = config.getTableConfigMap().get(clazz);
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
     * BoundSQL 的args 已经处理好.
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
        TableConfig tableConfig = config.getTableConfigMap().get(clazz);
        Map<SqlType, BoundSql> map = config.getSqlsByClass(clazz);
        BoundSql bs = map.get(SqlType.DELETE);
        bs = copy(bs);
        
        bs.setCommons(CommonUtils.getIdsCommons(tableConfig));
        bs.addArgs(CommonUtils.getIdArgs(tableConfig, obj));
        
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
        TableConfig tableConfig = config.getTableConfigMap().get(clazz);
        Map<SqlType, BoundSql> map = config.getSqlsByClass(clazz);
        BoundSql getbs = map.get(SqlType.INSERT);
        final BoundSql bs = copy(getbs);
        bs.setUseGeneratedKey(tableConfig.isUseGenerate());
        // view: columns + ids + onesmap.keyset
        if (tableConfig.isUseGenerate() == false) {
            bs.getView().addAll(tableConfig.columnList(e->{return e.isId();}));
        }
        bs.getView().addAll(tableConfig.columnList(e->{return e.isOne();}));


        bs.addArgs(CommonUtils.getArgs(obj, tableConfig
                .columnConfigList(e->{return e.isColumn();}).stream()
                .map(e -> {return e.getAttrName();}).collect(Collectors.toList())));
        if (tableConfig.isUseGenerate() == false) {
            bs.addArgs(CommonUtils.getIdArgs(tableConfig, obj));
        }
        // 如果 引用的对象为NULL， 说明这个关联的字段为NULL,
        // 如果 引用的对象不为NULL， 关联的字段为 引用对象主键
        tableConfig.columnConfigList(e->{return e.isOne();}).forEach(e->{
            // 拿到引用的对象
            try {
                Field field = obj.getClass().getDeclaredField(e.getAttrName());
                field.setAccessible(true);
                Object ref = field.get(obj);
                if (ref != null){
                    // 我们肯定使用的它的唯一id
                    Object[] tempIds = CommonUtils.getIdArgs(config.getTableConfigMap().get(e.getRef()), ref);
                    assert  tempIds.length == 1;
                    bs.addArg(tempIds[0]);
                } else {
                    bs.addArg(null);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        });
        return new StatementMapper(conn, bs);
    }
    
    
    public StatementMapper updateOne(Class<?> clazz, Configuration config, Connection conn, Object obj) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, ClassNotFoundException {
        TableConfig tableConfig = config.getTableConfigMap().get(clazz);
        Map<SqlType, BoundSql> map = config.getSqlsByClass(clazz);
        BoundSql upsql = map.get(SqlType.UPDATE);
        final BoundSql bs = copy(upsql);
        // id参数作为条件
        bs.setCommons(CommonUtils.getIdsCommons(tableConfig));

        // 添加args
        tableConfig.columnConfigList(e->{return e.isOne() || e.isColumn();}).forEach(e->{
            Field field = null;
            Object ref = null;
            try {
                field = obj.getClass().getDeclaredField(e.getAttrName());
                field.setAccessible(true);
                ref = field.get(obj);
                if (ref != null) {
                    if (e.isColumn()) {
                        bs.addArg(ref);
                    } else {
                        // ref 中 找 @ID 的值
                        Object[] temp = CommonUtils.getIdArgs(config.getTableConfigMap().get(e.getRef()), ref);
                        assert temp.length == 1;
                        bs.addArg(temp[0]);
                    }
                } else {
                    bs.addArg(null);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        bs.addArgs(CommonUtils.getIdArgs(tableConfig, obj));
        return new StatementMapper(conn, bs);
    }
}
