package com.ren.jdbc.core;

import java.util.List;

public interface SqlSession {
    /**
     * 根据给定的唯一id查询
     * @param clazz
     * @param ids
     * @return
     */
    <T> T selectOne(Class<T> clazz, Object id);
    /**
     * @param clazz
     * @param condition 属性名1=?, 属性名2=? limit ?,? ;condition 就是写在where 后面的条件,属性名
     *  必须是和数据库表字段相同的名称
     * @return
     */
    <E> List<E> selectList(Class<E> clazz, String condition, Object...values);
    <E> List<E> selectList(Class<E> clazz);
    /**
     * 不会级联删除,如果该对象无法删除，将会抛出SQLException异常
     * 如果obj==null,不会有任何变化
     * @param obj
     * @return
     */
    <E>int delete(E obj);
    /**
     * 修改,可以修改该对象内 关联的单个对象的的ID值(或者有多个id，需要同时修改) 
     * 对于对象内的List关联，改动对于数据库没有任何意义
     * 如果obj==null,不会有任何变化
     * @param obj
     * @return
     */
    <E>int update(E obj);
    /**
     * 如果obj==null,不会有任何变化
     * 保存该对象,如果该对象的插入必须要某个关联对象的外键，那么必须给关联对象的id属性列赋值
     * @param obj
     * @return
     */
    <E>int save(E obj);
}
