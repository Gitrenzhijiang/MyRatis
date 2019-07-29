package com.ren.test2;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.ren.jdbc.core.SqlSession;

public class TestDemo {
    @Test
    public void testfindByRoleId() {
        SqlSession sqlSession = SqlSessionFactoryUtil.getSessionFactory().openSession();
        
        Role role = sqlSession.selectOne(Role.class, 1);
        
        System.out.println(role);
    }
    /**
     * 根据ID查询User
     */
    @Test
    public void testfindById() {
        SqlSession sqlSession = SqlSessionFactoryUtil.getSessionFactory().openSession();
        
        User findUser = sqlSession.selectOne(User.class, 1);
        System.out.println(findUser);
    }
    /**
     * 根据给定条件 查询User
     */
    @Test
    public void testfindByName() {
        SqlSession sqlSession = SqlSessionFactoryUtil.getSessionFactory().openSession();
        // 中间的条件，格式是:
        // tb_user 的字段名称 = ? and 下一个字段 = ? 
        List<User> findUserList = sqlSession.selectList(User.class, "name= ? or password = ?", "root", "sa");
        System.out.println(Arrays.toString(findUserList.toArray()));
    }
    @Test
    public void testFindMyPermissionList() {
        SqlSession sqlSession = SqlSessionFactoryUtil.getSessionFactory().openSession();
        // 查询所有用户
        List<User> uList = sqlSession.selectList(User.class, "limit ?", 1);
        System.out.println("size = " + uList.size());
        for (User u : uList) {
            Role role = u.getMyRole();
            List<RolePermission> rpList = sqlSession.selectList(RolePermission.class, 
                    "role_id = ? ", role.getId());
            System.out.print(u.getName() + "的所有权限: ");
            for (RolePermission rp : rpList) {
                System.out.print(rp.getPermission() + " ");
            }
            System.out.println();
        }
    }
    @Test
    public void testUpdate() {
        SqlSession sqlSession = SqlSessionFactoryUtil.getSessionFactory().openSession();
        User selectUser = sqlSession.selectOne(User.class, 1);
        selectUser.setRealName("Name_updateOn_" + LocalDateTime.now().toString());
        sqlSession.update(selectUser); // 不能更新的字段不能修改, 比如id
    }
    
    @Test
    public void testDelete() {
        // 先插入一条
        SqlSession sqlSession = SqlSessionFactoryUtil.getSessionFactory().openSession();
        /*
         * 插入一个User, 它的角色是 管理员 
         */
        User willInsert = new User();
        willInsert.setName("willdelete");
        willInsert.setPassword("password");
        willInsert.setRealName("GGbo");
//        Role role = new Role(); role.setId(1);
//        willInsert.setMyRole(role);
        
        int id = sqlSession.save(willInsert);
        System.out.println("插入的记录 的 id = " + id);
        // 删除对象的 主键属性必须有, 否则更改的记录数=0, 即没有删除任何记录,
//        willInsert.setId(id);
//        int count = sqlSession.delete(willInsert);
//        System.out.println("删除 记录数count = " + count);
    }
}
