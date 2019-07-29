package com.ren.jdbc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface One {
    // 假设学生表 : value="teacher_id"
    String value();
    // @key(value=”数据库列name”, (可选：ref="id"))
    // 老师表中的id列,如果Teacher里面就只有一个@Id，可以不写
    String ref() default "";
    // 查询出主元素时，这个外键关联的属性是否也查出
    boolean cascade() default true;
}
