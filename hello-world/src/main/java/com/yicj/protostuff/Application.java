package com.yicj.protostuff;

import com.yicj.protostuff.model.Group;
import com.yicj.protostuff.model.User;
import com.yicj.protostuff.utils.ProtostuffUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Application {
    public static void main(String[] args) {
        List<User> users = new ArrayList<>() ;
        for(int i = 0 ; i < 100 ; i++){
            //创建一个user对象
            User user = User.builder().id("1").age(20).name("张三" + i).desc("programmer").build();
            users.add(user) ;
        }
        //创建一个Group对象
        Group group = Group.builder().id("1").name("分组1").users(users).build() ;
        //使用ProtostuffUtils序列化
        byte[] data = ProtostuffUtils.serialize(group);
        System.out.println("序列化后：" + Arrays.toString(data));
        Group result = ProtostuffUtils.deserialize(data, Group.class);
        System.out.println("反序列化后：" + result.toString());
    }
}
