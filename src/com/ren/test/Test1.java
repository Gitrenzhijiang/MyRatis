package com.ren.test;

import java.util.List;

import com.ren.jdbc.core.SqlSession;
import com.ren.jdbc.core.SqlSessionFactory;
import com.ren.jdbc.core.SqlSessionFactoryBuilder;

public class Test1 {
    /**
     * SqlSession 接口 
     */
    public static void main(String[] args) {
//        testGetAll();
        testGetAll2();
//        testDelete();
//        testUpdate();
//        testInsert();
//        testGetOne();
    }
    public static void testGetOne() {
        SqlSession sqlSession = SqlSessionFactoryBuilder.build("MyRatis.properties").openSession();
        Person person = sqlSession.selectOne(Person.class, 6);
        System.out.println(person);
        Person person2 = sqlSession.selectOne(Person.class, 6);
        System.out.println(person2);
        Person person3 = sqlSession.selectOne(Person.class, 6);
        System.out.println(person3);
    }
    public static void testInsert() {
        SqlSession sqlSession = SqlSessionFactoryBuilder.build("MyRatis.properties").openSession();
        Person person = new Person();person.setAge(28);person.setName("JOIB");
        Teacher teacher = new Teacher();teacher.setId(2);
        person.setTeacher(teacher);
        System.out.println(sqlSession.save(person));
    }
    
    public static void testUpdate() {
        SqlSession sqlSession = SqlSessionFactoryBuilder.build("MyRatis.properties").openSession();
        Person person = sqlSession.selectOne(Person.class, 2);
        System.out.println(person);
        
        person.getTeacher().setId(2);
        sqlSession.update(person);
        
    }
    
    public static void testDelete() {
        SqlSession sqlSession = SqlSessionFactoryBuilder.build("MyRatis.properties").openSession();
        Teacher get = sqlSession.selectOne(Teacher.class, 3);
        System.out.println(get);
        sqlSession.delete(get);
        
        Teacher t = sqlSession.selectOne(Teacher.class, 3);
        System.out.println(t);
    }
    public static void testGetAll2() {
        SqlSession sqlSession = SqlSessionFactoryBuilder.build("MyRatis.properties").openSession();
        List<Person> persons = sqlSession.selectList(Person.class, "age>? limit ?,?", 125, 1, 2);
        System.out.println(persons);
        
        
    }
    public static void testGetAll() {
        SqlSession sqlSession = SqlSessionFactoryBuilder.build("MyRatis.properties").openSession();
        // 这样是非法的
//        List<Person> persons = sqlSession.selectList(Person.class, "teacher_id=? ", new Object[] {null});
//        System.out.println(persons);
//        for (Person p : persons) {
//            System.out.println(p);
//        }
        System.out.println("====================");
        Teacher teacher = sqlSession.selectOne(Teacher.class, 2);
        //System.out.println(teacher);
        System.out.println(teacher.getMyStudents());
        // 
    }

}


