package com.ren.jdbc.statement;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ren.jdbc.config.ColumnConfig;
import com.ren.jdbc.config.Configuration;
import com.ren.jdbc.config.TableConfig;
import com.ren.jdbc.core.SqlRunner;
import com.ren.jdbc.utils.CommonUtils;

public class CascadeBeanHandler<E> {
    private Configuration config;
    private StatementMapper sm;
    private E obj;
    private SqlRunner sr = null;
    private Map<String, Object> map;
    public CascadeBeanHandler(Configuration config, StatementMapper sm, E obj, Map<String, Object> map) {
        super();
        this.config = config;
        this.sm = sm;
        this.obj = obj;
        sr = new SqlRunner(sm.getConn());
        this.map = map;
    }

    /**
     * 对于@One
     * 如果role_id 为查询的字段为NULL.
     *
     */
    public void fillCascade() {
        // 拿到配置的 oneSql 和 manysql
        try {
            Class clazz = obj.getClass();
            TableConfig tableConfig = config.getTableConfigMap().get(clazz);

            for (Iterator<ColumnConfig> iterator = tableConfig.columnConfigList(e->{return e.isOne();}).iterator();
                    iterator.hasNext();) {
                ColumnConfig columnConfig =  iterator.next();
                Field field = obj.getClass().getDeclaredField(columnConfig.getAttrName());
                field.setAccessible(true);
                // 设置了不级联的, 如果 role_id 为NULL， myRole=null.
                // role_id 不为NULL , 默认的无参数构造函数创建 bean, 然后设置 主键属性.

                Object v = map.get(columnConfig.getLabelName());// role_id -> 1 or null
                if (!columnConfig.isCascade()) {
                    // 如果 v = null, 对于field 的值也是null
                    if (v == null) {
                        field.set(obj, null);
                    } else {
                        // v != null, 它是Ref的Id 属性的值.
                        Object refBean = columnConfig.getRef().newInstance();
                        Field idField = columnConfig.getRef().getDeclaredField(columnConfig.getRefIdColumnConfig().getAttrName());
                        idField.setAccessible(true);
                        idField.set(refBean, v);
                        field.set(obj, refBean);
                    }
                }else {
                    // 查询出相关联的 one
                    Map<String, Object> clomn2Value = sr.selectOne(columnConfig.getSql(), v);
                    // 如果 查询出来是NULL
                    if (clomn2Value == null){
                        field.set(obj, null);
                    } else {
                        // 放入
                        field.set(obj, config.getTableConfigMap().get(columnConfig.getRef()).
                                mapToBean(clomn2Value, columnConfig.getRef()));
                    }
                }
                
            }
            // 级联@Many.
            /**
             * 如果它可以有@Many, 但是没有写，也是可以的.
             */
            tableConfig.columnConfigList(e->{return e.isMany();}).forEach(e->{
                Class manyListType = e.getManyListType();
                Field field = null;
                try {
                    field = obj.getClass().getDeclaredField(e.getAttrName());
                    field.setAccessible(true);

                    List<Object> objValue = new ArrayList<>();
                    // 如果不设置级联查询. 给一个空的LIST
                    if (e.isCascade() == false){
                        field.set(obj, objValue);
                    } else {
                        List<Map<String, Object>> lists = sr.selectAll(e.getSql(), CommonUtils.getIdArgs(tableConfig, obj));
                        for (Map<String, Object> map : lists) { //
                            objValue.add(config.getTableConfigMap().get(e.getManyListType()).mapToBean(map, e.getManyListType()));
                        }
                        field.set(obj, objValue);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
//            throw new RuntimeException(e.getMessage());
        }
    }
    
    public Configuration getConfig() {
        return config;
    }

    public void setConfig(Configuration config) {
        this.config = config;
    }

    public StatementMapper getSm() {
        return sm;
    }

    public void setSm(StatementMapper sm) {
        this.sm = sm;
    }

    public E getObj() {
        return obj;
    }

    public void setObj(E obj) {
        this.obj = obj;
    }

}
