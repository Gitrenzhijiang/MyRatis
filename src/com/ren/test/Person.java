package com.ren.test;

import com.ren.jdbc.annotation.Column;
import com.ren.jdbc.annotation.Generatekey;
import com.ren.jdbc.annotation.Id;
import com.ren.jdbc.annotation.One;
import com.ren.jdbc.annotation.POJO;

@POJO("person")
@Generatekey(false) //the default is false
public class Person {
    @Id(value="id")
    private Integer id;
    @Column("person_name")
    private String name;
    @Column
    private int age;
    
    @One(value="teacher_id", cascade=true)
    private Teacher teacher;
    
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
    public Teacher getTeacher() {
        return teacher;
    }
    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }
    @Override
    public String toString() {
        return "Person [id=" + id + ", name=" + name + ", age=" + age + ", teacher=" + teacher + "]";
    }
}
