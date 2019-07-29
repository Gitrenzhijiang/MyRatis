package com.ren.test2;

import java.util.Date;

import org.junit.Test;

import com.ren.jdbc.core.SqlSession;

public class TestDemo2 {
    @Test
    public void select() {
        SqlSession sqlSession = SqlSessionFactoryUtil.getSessionFactory().openSession();
        User user = sqlSession.selectOne(User.class, 1);
        System.out.println(user);
    }
    
    @Test
    public void insert() {
        /**
         * 插入一个 Questionnaire 对象
         */
        Questionnaire qt = new Questionnaire();
        qt.setCtime(new Date());
        qt.setDtime(new Date());
        qt.setTitle("title");
        User puser = new User(); puser.setId(1);
        qt.setPuser(puser);
        qt.setPublish(1);
        
        SqlSession sqlSession = SqlSessionFactoryUtil.getSessionFactory().openSession();
        int k = sqlSession.save(qt);
        // 查询 出 刚刚插入的对象
        Questionnaire questionnaire = sqlSession.selectOne(Questionnaire.class, k);
        System.out.println(questionnaire);
        // 修改 
        questionnaire.setPublish(0); 
        questionnaire.setTitle("new title");
        System.out.println("start update");
//        sqlSession.update(questionnaire);
        System.out.println(questionnaire + ", end update");
    }
    @Test
    public void testDelete() {
        SqlSession sqlSession = SqlSessionFactoryUtil.getSessionFactory().openSession();
        for (int i = 0; i < 18; i++) {
            Questionnaire qt = new Questionnaire();
            qt.setId(i);
            sqlSession.delete(qt);
        }
    }
}
