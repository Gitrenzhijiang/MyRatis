package com.ren.test2;

import com.ren.jdbc.core.SqlSession;
import org.junit.Test;

import javax.jws.soap.SOAPBinding;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

/**
 * 引入新特性, 新增对象User, 如果@One 为NULL， 则对应role_id 为NULL
 *  如果不为NULL， Role对象的ID必须是 逻辑正确的
 */
public class TestDemo3 {

    @Test
    public void insert(){
        Questionnaire questionnaire = new Questionnaire();

        questionnaire.setPublish(0);
        questionnaire.setTitle("haha");
        questionnaire.setCtime(new Date());
        questionnaire.setPuser(null);
        SqlSession sqlSession = SqlSessionFactoryUtil.getSessionFactory().openSession();
        int k = sqlSession.save(questionnaire);
        System.out.println("插入ID :" + k);
    }
    @Test
    public void update(){
        Questionnaire questionnaire = new Questionnaire();

        questionnaire.setPublish(0);
        questionnaire.setTitle("haha");
        questionnaire.setCtime(new Date());
        questionnaire.setPuser(null);
        SqlSession sqlSession = SqlSessionFactoryUtil.getSessionFactory().openSession();
        int k = sqlSession.save(questionnaire);
        System.out.println("k=" + k);
        questionnaire = sqlSession.selectOne(Questionnaire.class, k);
        System.out.println("插入后: "+ questionnaire);

        questionnaire.setDtime(new Date());

        sqlSession.update(questionnaire);
        System.out.println(questionnaire);
    }


    @Test
    public void ctest() throws BrokenBarrierException, InterruptedException {
        int num = 6;
        CyclicBarrier cb = new CyclicBarrier( 2 * num + 1);
        ThreadPoolExecutor exec = new ThreadPoolExecutor(190, 200, 0, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
        while (num > 0){

            num--;
            exec.submit(new Read(cb));
//            exec.submit(new Insert(cb));
            exec.submit(new Update(cb));
        }
        System.out.println("start : ");
        cb.await();

        cb.await(); // 等待结束
        exec.shutdown();

        Thread.sleep(30000);
    }

    class Read implements Runnable {
        Read(CyclicBarrier cb){
            this.cb = cb;
        }
        private  CyclicBarrier cb;
        @Override
        public void run() {
            try {
                cb.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            SqlSession sqlSession = SqlSessionFactoryUtil.getSessionFactory().openSession();
            List<User> users = sqlSession.selectList(User.class, "id > ?", 1);
            System.out.println("userList Size = " + users.size());

            try {
                cb.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }
    class Update implements Runnable {
        Update(CyclicBarrier cb){
            this.cb = cb;
        }
        private  CyclicBarrier cb;
        @Override
        public void run() {

            try {
                cb.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            SqlSession sqlSession = SqlSessionFactoryUtil.getSessionFactory().openSession();
            List<User> users = sqlSession.selectList(User.class, "id > ?", 1);
            for (int i = 0; i < users.size(); i++){
                User u = users.get(i);
                u.setRealName(new Date()+"");
                sqlSession.update(u);
            }

            try {
                cb.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }
    class Insert implements Runnable {
        Insert(CyclicBarrier cb){
            this.cb = cb;
        }
        private  CyclicBarrier cb;
        @Override
        public void run() {
            try {
                cb.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            SqlSession sqlSession = SqlSessionFactoryUtil.getSessionFactory().openSession();
            List<User> users = sqlSession.selectList(User.class, "id > ?", 1);
            for (int i = 0; i < users.size(); i++){
                User u = users.get(i);
                // 将这个user, 修改一顿之后，插入
                // 它使用了自增主键， 设置ID 无意义
                u.setRealName("i:" + new Date());
                u.setPassword("p:" + new Date());
                sqlSession.save(u);
            }

            try {
                cb.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }
}
