package com.ren.jdbc.statement;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ren.jdbc.config.Configuration;
import com.ren.jdbc.config.PJConfig;
import com.ren.jdbc.core.SqlRunner;
import com.ren.jdbc.utils.BeanUtils;
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
    
    public void fillCascade() {
        // 拿到配置的 oneSql 和 manysql
        try {
            Class clazz = obj.getClass();
            PJConfig pjconfig = config.getPojoConfigMap().get(clazz);
            Map<String, String> onesqls = pjconfig.getOnesSql();
            for (Iterator iterator = onesqls.keySet().iterator(); iterator.hasNext();) {
                String fieldName = (String) iterator.next();
                Field field = obj.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                // 设置了不级联删除的，直接,
                if (pjconfig.getUseCascadeMap().get(fieldName)==false) {
                    //  teacher_id -> Teacher全名#[teacher的属性名]
                    Map<String, String> onesMap = pjconfig.getOnesMap();
                    // 这是关联的beanmap,只包含ids
                    Map<String, Object> refBean = new HashMap<>();
                    for (String teacher_id : onesMap.keySet()) {
                        if (pjconfig.getOnesJavaName().get(fieldName).equals(teacher_id)) {
                            refBean.put(teacher_id, map.get(teacher_id));
                            field.set(obj, BeanUtils.getBean(
                                    CommonUtils.mapOne(pjconfig, config.getPojoConfigMap().get(field.getType()), refBean), 
                                    field.getType()));
                            break;
                        }
                    }
                }else {
                    // 查询出相关联的 one
                    Map<String, Object> beanMap = sr.selectOne(pjconfig.getOnesSql().get(fieldName), 
                            CommonUtils.getOneArgs(pjconfig, obj, map));
                    
                    // 放入
                    field.set(obj, config.getPojoConfigMap().get(field.getType())
                            .getBeanMapNamePreHandler()
                            .map(beanMap, field.getType()));
                }
                
            }
            // 再把级联的 List,用manys保证顺序
            Map<String, String> manys = pjconfig.getManySql();
            for (Iterator iterator = manys.keySet().iterator(); iterator.hasNext();) {
                String fn = (String) iterator.next();
                Field field = obj.getClass().getDeclaredField(fn);
                field.setAccessible(true);
                List<Object> objValue = new ArrayList<>();
                // 设置了不级联查询List的，直接,给予一个空的list
                if (pjconfig.getUseCascadeMap().get(fn)==false) {
                    field.set(obj, objValue);
                    continue;
                }
                List<Map<String, Object>> lists = sr.selectAll(pjconfig.getManySql().get(fn), CommonUtils.getIdArgs(pjconfig, obj));
                for (Map<String, Object> map : lists) { //
                    objValue.add(config.getPojoConfigMap().get(pjconfig.getManys().get(fn))
                            .getBeanMapNamePreHandler().map(map, pjconfig.getManys().get(fn)));
                }
                field.set(obj, objValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
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
