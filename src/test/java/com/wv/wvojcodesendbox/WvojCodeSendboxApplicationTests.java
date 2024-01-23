package com.wv.wvojcodesendbox;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WvojCodeSendboxApplicationTests {

    @Test
    void contextLoads() {

        String str = "aaabbbccc";
        System.out.println(str.substring(0, 3));
    }

}
