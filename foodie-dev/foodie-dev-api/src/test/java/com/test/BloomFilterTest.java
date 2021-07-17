package com.test;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.junit.Test;

import java.nio.charset.Charset;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

public class BloomFilterTest {


    @Test
    public void myTest(){
        BloomFilter bf = BloomFilter.create(Funnels.stringFunnel(Charset.forName("utf-8")),100000,0.0001);
//        bf.put("1001");
//        bf.mightContain("1001");
        for (int i = 0; i < 100000; i++) {
            bf.put(String.valueOf(i));
        }
        int count = 0;
        for (int i = 0; i < 10000; i++) {
            boolean isExist = bf.mightContain("imooc" + i);
            if(isExist){
                count ++;
            }
        }
        System.out.println("误判率 : "+count);
    }


}
