package com.ren.jdbc.config;

/**
 * 对应POJO 一行的所有配置,
 * @Column  or @One  or @Id  or @Many
 *
 */
public class ColumnConfig {
    /**
     * 是否是普通的 column 字段
     */
    private boolean isColumn = false;
    /**
     * 对应 表中字段 名称
     */
    private String labelName;
    /**
     * java类中属性名称
     */
    private String attrName;
    /**
     * 当前字段是否是主键
     */
    private boolean isId = false;
    /**
     * 当前是否使用级联
     */
    private boolean cascade = false;
    /**
     * 是否是 对应的Many List.
     */
    private boolean isMany = false;
    /**
     * 如果一对多, 在一的一方可以声明 一个List<@manyListType>
     *     用于存储所有的多的一方
     */
    private Class manyListType;
    /**
     * 是否 one
     */
    private boolean isOne = false;
    /**
     * one 内的属性, 被关联的类
     */
    private Class<?> ref;
    /**
     * @One 时 有效的属性, 指向关联的表的@Id 列配置.
     */
    private ColumnConfig refIdColumnConfig;

    /**
     * 这个字段为SQL语句;
     * 如果 @One, 查询关联对象的sql
     * 如果 @Many, 查询关联List的SQL
     */
    private String sql;

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public String getAttrName() {
        return attrName;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    public boolean isId() {
        return isId;
    }

    public void setId(boolean id) {
        isId = id;
    }

    public boolean isCascade() {
        return cascade;
    }

    public void setCascade(boolean cascade) {
        this.cascade = cascade;
    }

    public boolean isMany() {
        return isMany;
    }

    public void setMany(boolean many) {
        isMany = many;
    }

    public Class getManyListType() {
        return manyListType;
    }

    public void setManyListType(Class manyListType) {
        this.manyListType = manyListType;
    }

    public boolean isOne() {
        return isOne;
    }

    public void setOne(boolean one) {
        isOne = one;
    }
    public Class<?> getRef() {
        return ref;
    }
    public void setRef(Class<?> ref) {
        this.ref = ref;
    }

    public boolean isColumn() {
        return isColumn;
    }

    public void setColumn(boolean column) {
        isColumn = column;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public ColumnConfig getRefIdColumnConfig() {
        return refIdColumnConfig;
    }
    public void setRefIdColumnConfig(ColumnConfig refIdColumnConfig) {
        this.refIdColumnConfig = refIdColumnConfig;
    }
    @Override
    public String toString() {
        return "ColumnConfig{" +
                "isColumn=" + isColumn +
                ", labelName='" + labelName + '\'' +
                ", attrName='" + attrName + '\'' +
                ", isId=" + isId +
                ", cascade=" + cascade +
                ", isMany=" + isMany +
                ", manyListType=" + manyListType +
                ", isOne=" + isOne +
                ", ref=" + ref +
                ", refIdColumnConfig=" + refIdColumnConfig +
                ", sql='" + sql + '\'' +
                '}';
    }
}
