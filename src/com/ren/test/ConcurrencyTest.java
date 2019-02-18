package com.ren.test;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.ren.jdbc.core.SqlSession;
import com.ren.jdbc.core.SqlSessionFactory;
import com.ren.jdbc.core.SqlSessionFactoryBuilder;

/**
 * 并发测试
 * @author REN
 *
 */
public class ConcurrencyTest {
    // ssf 在应用程序中应当只有一个实例
    SqlSessionFactory ssf = SqlSessionFactoryBuilder.build("MyRatis.properties");
    public static void main(String[] args) {
        ConcurrencyTest ct = new ConcurrencyTest();
        
        ct.startTest();
        
    }
    // 那两个runner 都运行到数据库操作时
    private CyclicBarrier cb = new CyclicBarrier(4);
    private AtomicInteger id = new AtomicInteger(new Random().nextInt());
    GetRunner gr = new GetRunner();
    AddRunner ar = new AddRunner();
    public void startTest() {
        new Thread(ar).start();
        new Thread(ar).start();
        new Thread(gr).start();
        new Thread(gr).start();
    }
    class GetRunner implements Runnable{
        @Override
        public void run()  {
            SqlSession session = ssf.openSession();
            try {
                cb.await();
//                Thread.sleep(10); // 如果这里没有延迟，两个线程一个insert,一个select,select永远都查不到
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }//等待下面的兄弟一起到，如果有必要
            Person p =  session.selectOne(Person.class, id.decrementAndGet());
            System.out.println("get:"+p);
        }
        
    }
    class AddRunner implements Runnable{
        @Override
        public void run()  {
            SqlSession session = ssf.openSession();
            
            Person person = new Person(); 
            person.setId(id.getAndIncrement()); // 需要关闭person的 自增主键设置 才能在添加person时自己设置主键
            person.setAge(222);
            person.setName("addRunner" + System.currentTimeMillis());
            try {
                cb.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            // 一起
            int p = session.save(person);
            
            System.out.println("save:" + p);
        }
        
    }
}

