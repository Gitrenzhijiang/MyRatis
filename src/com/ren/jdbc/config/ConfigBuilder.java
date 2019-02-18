package com.ren.jdbc.config;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

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
    public Configuration build(String XML) {
        Configuration configuration = null;
        try {
            PROPERTIES.load(ConfigBuilder.class.getClassLoader().getResourceAsStream(XML));
            Set set = PROPERTIES.keySet();
            List<String> pocs = new ArrayList<>();
            for (Object objkey : set) {
                String key = (String)objkey;
                if (!key.equals("package")) {
                    String POClass = PROPERTIES.getProperty(key);
                    pocs.add(POClass);
                }
            }
            configuration = parse(pocs);
        } catch (Exception e) {
            throw new RuntimeException("ConfigurationBuilder解析时出错:",new ConfigException(e));
        }
        return new BoundSqlBuilder(configuration).buildConfig();
    }
    private Configuration parse(List<String> POClass) throws ClassNotFoundException, ConfigException {
        Configuration confi = new Configuration();
        for (String className : POClass) {
            Class clazz = Class.forName(className);
            confi.getPojoConfigMap().put(clazz, parseClass2Pojo(clazz));
        }
        return confi;
    }
    private PJConfig parseClass2Pojo(Class clazz) throws ConfigException {
        PJConfig pjconfig = new PJConfig();
        Annotation pojo = clazz.getDeclaredAnnotation(POJO.class);
        if (pojo == null) {
            throw new ConfigException("No POJO at class :" + clazz.getName());
        }
        String tableName = ((POJO)pojo).value();
        if ("".equals(tableName)) {
            // 使用类的简单名
            tableName = clazz.getSimpleName().toLowerCase();
        }
        pojo = clazz.getDeclaredAnnotation(Generatekey.class);
        if (pojo != null) {
            pjconfig.setUseGenerate(((Generatekey)pojo).value());
        }else {
            pjconfig.setUseGenerate(false);
        }
        pjconfig.setTableName(tableName);
        String column = "";
        for (Field field : clazz.getDeclaredFields()) {
            Annotation an = field.getAnnotation(Column.class);
            if (an != null) {
                column = ((Column)an).value();
                if ("".equals(column)) {
                    column = field.getName().toLowerCase();
                }
                pjconfig.getColumns().add(column);
                pjconfig.getColJavas().add(field.getName());
            }
            an = field.getAnnotation(Id.class);
            if (an != null) {
                column = ((Id)an).value();
                if ("".equals(column)) {
                    column = field.getName().toLowerCase();
                }
                pjconfig.getIds().add(column);
                pjconfig.getIdJavas().add(field.getName());
            }
            an = field.getAnnotation(Many.class);
            if (an != null) {
                pjconfig.getManys().put(field.getName(), ((Many)an).value());
                pjconfig.getUseCascadeMap().put(field.getName(), ((Many)an).cascade());
            }
            an = field.getAnnotation(One.class);
            if (an != null) {
                column = ((One)an).value(); // teacher_id
                String ref = ((One)an).ref(); // 
                if ("".equals(column)) {
                    column = field.getName();
                }
                if ("".equals(ref)) {
                    ref = field.getType().getName();
                }else {
                    ref = field.getType().getName() + "#" + ref;
                }
                pjconfig.getUseCascadeMap().put(field.getName(), ((One)an).cascade());
                pjconfig.getOnesMap().put(column, ref);
                pjconfig.getOnesJavaName().put(field.getName(), column);
            }
        }
        
        return pjconfig;
    }
    
}
