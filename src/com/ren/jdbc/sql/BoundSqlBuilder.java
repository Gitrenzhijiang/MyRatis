package com.ren.jdbc.sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.ren.jdbc.config.ColumnConfig;
import com.ren.jdbc.config.Configuration;
import com.ren.jdbc.config.TableConfig;
import com.ren.jdbc.exception.ConfigException;

/**
 * 根据配置的一些内容生成一些半成品的sql
 * 根据 POJO类 的class 对象 和 tableConfig 对象, 生成对应的BoundSql对象.
 * 这个BoundSql 只是一个初步的具有相同共性的SQL. 用这个BoundSQL 方便创建更加具体的BoundSQL语句.
 * @author REN
 *
 */
public class BoundSqlBuilder {
    private Configuration configuration;
    /**
     * @param configuration
     * @return
     */
    public BoundSqlBuilder(Configuration configuration) {
        this.configuration = configuration;
    }
    public Configuration buildConfig() {
        Set<Class> cset = configuration.getTableConfigMap().keySet();
        for (Iterator iterator = cset.iterator(); iterator.hasNext();) {
            Class cz = (Class) iterator.next(); // POJO class 对象
            TableConfig tableConfig = configuration.getTableConfigMap().get(cz); // 单个Table 配置对象
            Map<SqlType, BoundSql> sqlMap = new ConcurrentHashMap<>();
            sqlMap.put(SqlType.INSERT, createInsertSql(cz, tableConfig));
            sqlMap.put(SqlType.UPDATE, createUpdateSql(cz, tableConfig));
            sqlMap.put(SqlType.DELETE, createDeleteSql(cz, tableConfig));
            sqlMap.put(SqlType.SELECT, createSelectSql(cz, tableConfig));
            configuration.registerSqlMap(cz, sqlMap);
        }
        try {
            init();
            System.out.println(configuration);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }
        return this.configuration;
    }
    private void init() throws ClassNotFoundException {
        for (Iterator iterator = configuration.getTableConfigMap().keySet().iterator(); iterator.hasNext();) {
            Class clazz = (Class) iterator.next();
            initOneSql(configuration.getTableConfigMap().get(clazz));
            initManySql(configuration.getTableConfigMap().get(clazz));
        }
    }

    /**
     * 当有@one 属性时, 查询主表一行记录, 顺带把从表{配置了级联查询的@One}也查出来，不要分为两个SQL 。
     * 比如有User表， Role表.
     * user{ id, name, desc, role_id, address_id}
     * role {id, name, desc} 级联= true
     * Address{id, name, desc, shi_id[ignore this]} 级联= false
     * shi{id, name}
     *
     * 查询List<User> 时, 应该写的SQL 是
     * select *** from user, role where user.role_id = role.id  [and ( 其他条件 )]
     *
     * 具体： user.*   role.*   address.id  这个级联是false
     * select user.id, user.name, user.desc, user.role_id, role.id, role.name, role.desc from
     *
     * user, role, address where user.role_id = role.id and user.address_id = address.id  [...]
     *
     * 先不管, 先把Pjconfig 替换掉.
     * @param tableConfig
     * @throws ClassNotFoundException
     */
    private void initOneSql(TableConfig tableConfig) throws ClassNotFoundException {
        tableConfig.columnConfigList(e -> {
            if (e.isOne()){
                return true;
            }
            return false;
        }).forEach(e -> {
            final String key = e.getAttrName(); // myRole 在User类中
//            String cname = e.getLabelName(); // role_id 在user表

            Class clazz = e.getRef();// One 字段 对应java 类型.
            // // 去查teacher唯一的id
            TableConfig refTableConfig = this.configuration.getTableConfigMap().get(clazz);
            String id = e.getRefIdColumnConfig().getLabelName();

            // 拿到了teacher的boundsql for select
            BoundSql selectbs = this.configuration.getSqlsByClass(clazz).get(SqlType.SELECT);
            final BoundSql bs = new BoundSql(selectbs);
//            // 加一个table
//            bs.getTables().add(pjconfig.getTableName());
            // 开始设置查询条件
            StringBuilder sb = new StringBuilder();
//            sb.append(oneCommons(pjconfig.getTableName(), teacherPJconfig.getTableName(),
//                    cname, id));
//            for (String idstr:pjconfig.getIds()) {
//                sb.append(BoundSql.SPACE);
//                sb.append(BoundSql.AND + BoundSql.SPACE);
//                sb.append(pjconfig.getTableName() + BoundSql.DOT + idstr + BoundSql.EQUAL + BoundSql.QM);
//            }
            sb.append(refTableConfig.getTableName() + BoundSql.DOT +id + BoundSql.EQUAL + BoundSql.QM);
            bs.setCommons(sb.toString());
            tableConfig.columnConfigList(columnConfig -> {
                return columnConfig.isOne() &&
                        columnConfig.getAttrName().equals(key);
            }).forEach(ele -> {
                ele.setSql(bs.getSql());
            });
        });

    }
    // person ,teacher, teacher_id, id
    private String oneCommons(String table, String reftable, String cname, String ref) {
        StringBuilder sb = new StringBuilder();
        sb.append(table + BoundSql.DOT + cname);
        sb.append(BoundSql.EQUAL);
        sb.append(reftable + BoundSql.DOT + ref);
        return sb.toString();
    }

    /**
     * 初始化@Many 的查询SQL ， 这个SQL 查询的视图正是需要查询的table, 只需把当前 role 的ID 列，作为条件.
     * @param tableConfig
     * @throws ClassNotFoundException
     */
    private void initManySql(TableConfig tableConfig) throws ClassNotFoundException {
        tableConfig.columnConfigList(e->{
            return e.isMany();
        }).forEach(e -> {
            // e 代表的是 Role 的 userList 配置
            String key = e.getAttrName(); // userList
            Class clazz = e.getManyListType(); // User的class
            TableConfig refTableConfig = configuration.getTableConfigMap().get(clazz);
            String id = null; //role_id 在  refTable [user]
            String id2 = null;//role 表的id
            // 遍历 用户表的@One, 判断
            for (ColumnConfig cc : refTableConfig.columnConfigList(a->{return  a.isOne();})) {
                if (cc.getRef().equals(tableConfig.getPojoClass())){ //
                    // 唯一确定后.
                    // 找自身的@Id
                    List<String> tempList = tableConfig.columnList(ele->{return ele.isId();});
                    if (tempList.isEmpty() || tempList.size() > 1){
                        throw  new RuntimeException("@Id 在" + tableConfig.getPojoClass() + " 中存在多个或不存在!");
                    }
                    id2 = tempList.get(0);
                    id = cc.getLabelName();
                    break;
                }
            }
            // // 去查
            BoundSql bs = this.configuration.getSqlsByClass(clazz).get(SqlType.SELECT);
            bs = new BoundSql(bs);
            // 加一个table
            bs.getTables().add(tableConfig.getTableName());
            // 开始设置查询条件
            StringBuilder sb = new StringBuilder();
            // role     user
            sb.append(oneCommons(tableConfig.getTableName(), refTableConfig.getTableName(),
                    id2, id));
            // 加上 role.id = ?
            for (String idstr:tableConfig.columnList(e2->{return e2.isId();})) {
                sb.append(BoundSql.SPACE);
                sb.append(BoundSql.AND + BoundSql.SPACE);
                sb.append(tableConfig.getTableName() + BoundSql.DOT + idstr + BoundSql.EQUAL + BoundSql.QM);
            }
            bs.setCommons(sb.toString());
            e.setSql(bs.getSql());
        });

    }

    /**
     *
     * 当前生成的 BoundSql view 只包含@Column 的字段. 如果是没有使用自增主键, 还需要放入@Id 的属性.
     *
     * @param cz
     * @param tableConfig
     * @return
     */
    private BoundSql createInsertSql(Class cz, TableConfig tableConfig) {
        BoundSql bs = new BoundSql();
        bs.setTables(Arrays.asList(tableConfig.getTableName()));
        List<String> view = new ArrayList<>();
        bs.setView(view);
        // 暂且不生成id的视图 以最小的改动为基准
        view.addAll(tableConfig.columnList(e -> {
            if (e.isColumn()){
                return true;
            }
            return false;
        }));
        bs.setType(SqlType.INSERT);
        return bs;
    }

    /**
     * 更新 半成品 BoundSQL, 视图部分包含 @Column 和 @One 的字段
     * update tableName set field1 = ? and field2 = ? where ...
     * update tableName1, tableName2 set f1 = ? and f2 = ? where ...  (可以更新 连接表), 但是不使用
     * 这里把 关联的外键包含入内, 所以, 在update方法中, 对于关联的对象 一定不能为空.
     * @param cz
     * @param tableConfig
     * @return
     */
    private BoundSql createUpdateSql(Class cz, TableConfig tableConfig) {
        BoundSql bs = new BoundSql();
        bs.setTables(Arrays.asList(tableConfig.getTableName()));
        List<String> view = new ArrayList<>();
        bs.setView(view);
        view.addAll(tableConfig.columnList(e -> {
            if (e.isColumn() || e.isOne()){
                return true;
            }
            return false;
        }));
        bs.setType(SqlType.UPDATE);
        return bs;
    }

    /**
     * 删除半成品BoundSQL 只包含 type 和 tableName
     * @param cz
     * @param config
     * @return
     */
    private BoundSql createDeleteSql(Class cz, TableConfig config) {
        BoundSql bs = new BoundSql();
        bs.setTables(Arrays.asList(config.getTableName()));
        bs.setType(SqlType.DELETE);
        return bs;
    }

    /**
     * 查询半成品BoundSQL, 包含表名, 表名+字段 的视图， @Id @Column @One 都包含
     * @param cz
     * @param config
     * @return
     */
    private BoundSql createSelectSql(Class cz, TableConfig config) {
        BoundSql bs = new BoundSql();
        List<String> tbs = new ArrayList<>();
        tbs.add(config.getTableName());
        bs.setTables(tbs);

        List<String> view = config.columnList(e -> {
            if (!e.isMany()){
                return true;
            }
            return false;
        });
        view = view.stream().map(e -> {return config.getTableName() + BoundSql.DOT +e;})
                .collect(Collectors.toList());
        bs.setView(view);
        bs.setType(SqlType.SELECT);
        return bs;
    }

}
