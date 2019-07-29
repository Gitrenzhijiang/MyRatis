package com.ren.jdbc.core;

import java.util.List;

public interface SqlSession {
    /**
     * 根据给定的唯一id查询, 如果数据库不存在返回null
     * @param clazz
     * @param id
     * @return
     */
    <T> T selectOne(Class<T> clazz, Object id);
    /**
     * @param clazz
     * @param condition 属性名1=?, 属性名2=? limit ?,? ;condition 就是写在where 后面的条件,
     * 属性名必须是和数据库表字段相同的名称, 如果有limit, 只能加在最后.
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
     * 修改的对象内普通 @Column 属性将导致数据库变化。如果@One 属性为NULL，对应数据库也是NULL。
     * 如果@One 属性关联对象ID 有值, 将会同步到数据库.
     * 对于对象内的List关联，改动对于数据库没有任何意义
     * 如果obj==null,不会有任何变化
     * @param obj
     * @return
     */
    <E>int update(E obj);
    /**
     * 保存该对象,如果@one 属性为NULL， 数据库中数据也是NULL。否则和 用给定关联对象作为外键的值
     * 
     * 如果保存的对象使用自增主键, 那么它将返回新插入 记录的 主键。 
     * @param obj
     * @return
     */
    <E>int save(E obj);
}
