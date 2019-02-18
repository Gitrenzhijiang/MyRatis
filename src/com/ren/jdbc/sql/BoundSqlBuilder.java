package com.ren.jdbc.sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ren.jdbc.config.Configuration;
import com.ren.jdbc.config.PJConfig;

/**
 * 根据配置的一些内容生成一些半成品的sql
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
        Set<Class> cset = configuration.getPojoConfigMap().keySet();
        for (Iterator iterator = cset.iterator(); iterator.hasNext();) {
            Class cz = (Class) iterator.next();
            PJConfig pjconfig = configuration.getPojoConfigMap().get(cz);
            Map<SqlType, BoundSql> sqlMap = new HashMap<>();
            sqlMap.put(SqlType.INSERT, createInsertSql(cz, pjconfig));
            sqlMap.put(SqlType.UPDATE, createUpdateSql(cz, pjconfig));
            sqlMap.put(SqlType.DELETE, createDeleteSql(cz, pjconfig));
            sqlMap.put(SqlType.SELECT, createSelectSql(cz, pjconfig));
            configuration.registerSqlMap(cz, sqlMap);
        }
        try {
            init();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }
        return this.configuration;
    }
    private void init() throws ClassNotFoundException {
        for (Iterator iterator = configuration.getPojoConfigMap().keySet().iterator(); iterator.hasNext();) {
            Class clazz = (Class) iterator.next();
            initOneSql(configuration.getPojoConfigMap().get(clazz));
            initManySql(configuration.getPojoConfigMap().get(clazz), clazz);
        }
    }
 // 在boundSql生成之后调用
    //初始化某些具体的sql(级联)
    //以onesJavaName为顺序
    //在生成object[]时亦是如此 no顺序
    private void initOneSql(PJConfig pjconfig) throws ClassNotFoundException {
        Map<String, String> onesJavaName = pjconfig.getOnesJavaName();
        // 生成onesSql,
        Set<String> keys = onesJavaName.keySet();
        for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
            String key = (String) iterator.next(); // teacher
            String cname = onesJavaName.get(key); // teacher_id
            String t = pjconfig.getOnesMap().get(cname); // com.ren.test.Teacher#teacher表中的某个列名
            String rsclass = null;
            String id = null;
            int kindex = t.indexOf("#");
            if (kindex == -1) {
                rsclass = t;
            }else {
                rsclass = t.substring(0, kindex);
                id = t.substring(kindex + 1);
            }
            Class clazz = Class.forName(rsclass);
            // // 去查teacher唯一的id
            PJConfig teacherPJconfig = this.configuration.getPojoConfigMap().get(clazz);
            if (id == null) {
                List<String> tids = teacherPJconfig.getIds();
                if (tids.size() != 1) {
                    throw  new RuntimeException("@One 没有指定所关联的" + clazz.getSimpleName() + "属性名称");
                }
                id = tids.get(0);
            }
            // 拿到了teacher的boundsql for select
            BoundSql bs = this.configuration.getSqlsByClass(clazz).get(SqlType.SELECT);
            bs = new BoundSql(bs);
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
            sb.append(teacherPJconfig.getTableName() + BoundSql.DOT +id + BoundSql.EQUAL + BoundSql.QM);
            bs.setCommons(sb.toString());
            pjconfig.getOnesSql().put(key, bs.getSql());
        }
    }
    // person ,teacher, teacher_id, id
    private String oneCommons(String table, String reftable, String cname, String ref) {
        StringBuilder sb = new StringBuilder();
        sb.append(table + BoundSql.DOT + cname);
        sb.append(BoundSql.EQUAL);
        sb.append(reftable + BoundSql.DOT + ref);
        return sb.toString();
    }
    private void initManySql(PJConfig pjconfig, Class teacherClass) throws ClassNotFoundException {
        Map<String, Class<?>> manys = pjconfig.getManys();
        // 生成onesSql
        Set<String> keys = manys.keySet();
        for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
            String key = (String) iterator.next(); // myStudents
            Class clazz = manys.get(key); // student的class
            PJConfig studentPJ = configuration.getPojoConfigMap().get(clazz);
            String id = null; //teacher_id
            String id2 = null;//teacher 表的id
            for (String s :studentPJ.getOnesMap().keySet()) {
                String czn = studentPJ.getOnesMap().get(s);
                String czncopy = czn;
                int k = czncopy.lastIndexOf("#");
                if (k != -1) {
                    czncopy = czncopy.substring(0, k);
                }
                if (czncopy.equals(teacherClass.getName())) {
                    id = s;
                    // id2
                    if (k != -1) {
                        id2 = czn.substring(k);
                    }else {
                        if (pjconfig.getIds().size()!=1) {
                            throw new RuntimeException("@One 没有指定一个@Id属性," + teacherClass.getSimpleName() + "存在多个@Id");
                        }
                        id2 = pjconfig.getIds().get(0);
                    }
                    break;
                }
            }
            // // 去查
            BoundSql bs = this.configuration.getSqlsByClass(clazz).get(SqlType.SELECT);
            bs = new BoundSql(bs);
            // 加一个table
            bs.getTables().add(pjconfig.getTableName());
            // 开始设置查询条件
            StringBuilder sb = new StringBuilder();
            sb.append(oneCommons(pjconfig.getTableName(), studentPJ.getTableName(), 
                    id2, id));
            for (String idstr:pjconfig.getIds()) {
                sb.append(BoundSql.SPACE);
                sb.append(BoundSql.AND + BoundSql.SPACE);
                sb.append(pjconfig.getTableName() + BoundSql.DOT + idstr + BoundSql.EQUAL + BoundSql.QM);
            }
            bs.setCommons(sb.toString());
            pjconfig.getManySql().put(key, bs.getSql());
        }
    }
    
    private BoundSql createInsertSql(Class cz, PJConfig config) {
        BoundSql bs = new BoundSql();
        bs.setTables(Arrays.asList(config.getTableName()));
        List<String> view = new ArrayList<>();
        bs.setView(view);
        // 暂且不生成id的视图 以最小的改动为基准
        view.addAll(config.getColumns());
        bs.setType(SqlType.INSERT);
        return bs;
    }
    private BoundSql createUpdateSql(Class cz, PJConfig config) {
        BoundSql bs = new BoundSql();
        bs.setTables(Arrays.asList(config.getTableName()));
        List<String> view = new ArrayList<>();
        bs.setView(view);
        view.addAll(config.getColumns());
        view.addAll(config.getOnesMap().keySet()); //关联的外键
        bs.setType(SqlType.UPDATE);
        return bs;
    }
    private BoundSql createDeleteSql(Class cz, PJConfig config) {
        BoundSql bs = new BoundSql();
        bs.setTables(Arrays.asList(config.getTableName()));
        bs.setType(SqlType.DELETE);
        return bs;
    }
    private BoundSql createSelectSql(Class cz, PJConfig config) {
        BoundSql bs = new BoundSql();
        List<String> tbs = new ArrayList<>();
        tbs.add(config.getTableName());
        bs.setTables(tbs);
        List<String> view = new ArrayList<>();
        bs.setView(view);
        addTable2Column(config.getIds(), view, config.getTableName());
        addTable2Column(config.getColumns(), view, config.getTableName());
        addTable2Column(config.getOnesMap().keySet(), view, config.getTableName());
        bs.setType(SqlType.SELECT);
        return bs;
    }
    // tableName.name
    private void addTable2Column(Collection<String> list, List<String> view, String tb) {
        for (String str : list)
            view.add(tb + BoundSql.DOT + str);
    }
}
