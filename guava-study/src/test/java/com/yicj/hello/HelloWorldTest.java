package com.yicj.hello;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import java.util.Objects;

@Slf4j
public class HelloWorldTest {

    @Test
    public void testEquals(){
        boolean eq1 = Objects.equals("a", "a");
        boolean eq2 = Objects.equals(null, "a");
        boolean eq3 = Objects.equals("a", null);
        boolean eq4 = Objects.equals(null, null);
        log.info("eq1: {}", eq1);
        log.info("eq2: {}", eq2);
        log.info("eq3: {}", eq3);
        log.info("eq4: {}", eq4);
    }

    @Test
    public void testToString(){
       // Objects.toStringHelper(this).add("x", 1).toString();
    }

}
