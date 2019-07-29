package com.ren.jdbc.config;

import com.ren.jdbc.utils.BeanUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TableConfig {
    /**
     * 是否使用了自增主键
     */
    private boolean useGenerate = false;
    /**
     * 数据库表的名称
     */
    private String tableName;
    /**
     * 对应的POJO类型
     */
    private Class pojoClass;
    /**
     * 所有的列的配置
     */
    private final List<ColumnConfig> columnConfigs = new ArrayList<>();

    /**
     * 根据指定筛选条件 返回数据库字段列表, 列表的顺序依赖于原始配置列表的顺序
     * @return
     */
    public List<String> columnList(Predicate<ColumnConfig> predicate){
        return columnConfigs.stream().filter(predicate)
                .map(e -> {return e.getLabelName();}).collect(Collectors.toList());
    }

    /**
     * 根据指定筛选条件, 返回ColumnConfig列表, 其顺序基于原始配置列表的顺序.
     * @param predicate
     * @return
     */
    public List<ColumnConfig> columnConfigList(Predicate<ColumnConfig> predicate) {
        return columnConfigs.stream().filter(predicate).collect(Collectors.toList());
    }
    private List<ColumnConfig> columnConfigList;

    /**
     * 由数据库查询出的Map, 数据库字段-> 值
     * @param map
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T mapToBean(Map<String, Object> map, Class<T> clazz){
        Map<String, Object> javaMap = new HashMap<>();
        columnConfigs.stream().forEach(e->{
            String labelName = e.getLabelName();
            if (map.containsKey(labelName)){ // @id @column @one
                // @Ones will ignore
                if (!e.isOne()) {
                    javaMap.put(e.getAttrName(), map.get(labelName));
                }
            }
        });
        return BeanUtils.getBean(javaMap, clazz);
    }
    /* getter and setter */
    public boolean isUseGenerate() {
        return useGenerate;
    }

    public void setUseGenerate(boolean useGenerate) {
        this.useGenerate = useGenerate;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Class getPojoClass() {
        return pojoClass;
    }

    public void setPojoClass(Class pojoClass) {
        this.pojoClass = pojoClass;
    }

    public List<ColumnConfig> getColumnConfigs() {
        return columnConfigs;
    }

}
