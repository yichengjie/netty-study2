package com.yicj.study.field;

import java.lang.reflect.Field;

public class UserTest {
    public static void main(String[] args) throws Exception {
        Field field = User.class.getDeclaredField("username");
        field.setAccessible(true);
        Object o = field.get(null);
        System.out.println(o);
    }
}
