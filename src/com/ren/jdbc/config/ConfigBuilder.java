package com.ren.jdbc.config;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

import com.ren.jdbc.annotation.Column;
import com.ren.jdbc.annotation.Generatekey;
import com.ren.jdbc.annotation.Id;
import com.ren.jdbc.annotation.Many;
import com.ren.jdbc.annotation.One;
import com.ren.jdbc.annotation.POJO;
import com.ren.jdbc.exception.ConfigException;
import com.ren.jdbc.sql.BoundSqlBuilder;

public class ConfigBuilder {
    private String XML = "MyRatis.properties";
    private Properties PROPERTIES = new Properties();
    public Configuration build() {
        return build(XML);
    }


    public Configuration build(String xml) {
        Configuration configuration = new Configuration();
        try {
            PROPERTIES.load(ConfigBuilder.class.getClassLoader().getResourceAsStream(xml));
            for (Object key : PROPERTIES.keySet()){
                String skey = (String) key;
                String className = (String) PROPERTIES.getProperty(skey);
                Class clazz = Class.forName(className);
                TableConfig tableConfig = getConfigFromClass(clazz);
                configuration.getTableConfigMap().put(clazz, tableConfig);
            }

            // 初始化 RefIdColumnConfig
            initRefIdColumnConfig(configuration.getTableConfigMap());

        } catch (Exception e){
            e.printStackTrace();
        }
        return new BoundSqlBuilder(configuration).buildConfig();
    }
    private TableConfig getConfigFromClass(Class clazz) throws ConfigException {
        TableConfig tableConfig = new TableConfig();
        Annotation pojo = clazz.getDeclaredAnnotation(POJO.class);
        if (pojo == null) {
            throw new ConfigException("No POJO at class :" + clazz.getName());
        }
        String tableName = ((POJO)pojo).value();
        if ("".equals(tableName)) {
            // 使用类的简单名
            tableName = clazz.getSimpleName().toLowerCase();
        }
        tableConfig.setTableName(tableName);
        pojo = clazz.getDeclaredAnnotation(Generatekey.class);
        if (pojo != null) {
            tableConfig.setUseGenerate(((Generatekey)pojo).value());
        }else {
            tableConfig.setUseGenerate(false);
        }
        tableConfig.setPojoClass(clazz);
        // 解析 ColumnConfig
        revoleAttribute(tableConfig.getColumnConfigs(), clazz);
        return tableConfig;
    }
    private void revoleAttribute(List<ColumnConfig> columnConfigList, Class clazz){
        /**
         * 遍历class 所有属性
         */

        for (Field field : clazz.getDeclaredFields()) {
            ColumnConfig columnConfig = new ColumnConfig();
            Annotation an = field.getAnnotation(Column.class);
            String column = null;
            // field column
            if (an != null) {
                column = ((Column)an).value();
                if ("".equals(column)) {
                    column = field.getName().toLowerCase();
                }
                columnConfig.setLabelName(column);
                columnConfig.setColumn(true);// default column is false, so set column = false
            }
            // @Id
            an = field.getAnnotation(Id.class);
            if (an != null) {
                column = ((Id)an).value();
                if ("".equals(column)) {
                    column = field.getName().toLowerCase();
                }
                columnConfig.setId(true);
                columnConfig.setLabelName(column);
            }
            // @Many
            an = field.getAnnotation(Many.class);
            if (an != null) {
                Many many = (Many) an;
                columnConfig.setMany(true);
                columnConfig.setCascade(many.cascade());
                columnConfig.setManyListType(many.value());

                column = ""; // 为了让他不为NULL
            }
            // @One
            an = field.getAnnotation(One.class);
            if (an != null) {
                One one = (One) an;
                column = one.value(); // teacher_id

                if ("".equals(column)) {
                    column = field.getName();
                }
                // ref 是One 引用的字段类型 字符串,
//                if ("".equals(ref)) {
//                    ref = field.getType().getName();
//                }else {
//                    ref = field.getType().getName() + "#" + ref;
//                }
                columnConfig.setOne(true);
                columnConfig.setLabelName(column);
                columnConfig.setCascade(one.cascade());
                columnConfig.setRef(field.getType());
            }
            // 如果不符合任意一个, 不处理它.
            if (column != null){
                columnConfig.setAttrName(field.getName());
                columnConfigList.add(columnConfig);
            }
        }
    }

    private void initRefIdColumnConfig(Map<Class, TableConfig> map){
        for (Class clazz : map.keySet()) {
            TableConfig tableConfig = map.get(clazz);
            // 迭代所有@One属性
            for (ColumnConfig cc : tableConfig.columnConfigList(e->{return e.isOne();})){
                TableConfig ref = map.get(cc.getRef());
                List<ColumnConfig> list = ref.columnConfigList(a->{return a.isId();});
                if (list.isEmpty() || list.size() > 1){
                    throw  new RuntimeException("在 " + ref.getPojoClass() + " 中不存在 @Id 或者存在多个 @Id");
                }
                cc.setRefIdColumnConfig(list.get(0));
            }
        }
    }
    
}
