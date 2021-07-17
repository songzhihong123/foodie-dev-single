package com.test;

import com.imooc.Application;
import com.imooc.controller.HelloController;
import com.imooc.service.StuService;
import com.imooc.service.TestTransService;
import com.imooc.service.impl.TestTransServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class TransTest {


        private MockMvc mockMvc;

        @Autowired
        private Sid sid;

//        @Before
        public void setUp() throws Exception{
            mockMvc = MockMvcBuilders.standaloneSetup(new HelloController()).build();
        }

//        @Test
        public void getHello() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/hello1?name=Imooc")
                    .accept(MediaType.APPLICATION_JSON_UTF8)).andDo(print());
        }
    @Autowired
    private StuService stuService;

    @Autowired
    private TestTransService testTransService;

//    @Test
    public void myTest(){
        testTransService.testPropagationTrans();
    }

    @Test
    public void testId(){
        System.out.println(sid.nextShort());

    }

}
