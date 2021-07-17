package com.imooc.service.impl;

import com.imooc.mapper.StuMapper;
import com.imooc.pojo.Stu;
import com.imooc.service.StuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StuServiceImpl implements StuService {

    @Autowired
    private StuMapper stuMapper;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Stu getStuInfo(int id) {
        Stu stu = stuMapper.selectByPrimaryKey(id);
        return stu;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveStu() {
        Stu stu = new Stu();
        stu.setName("jack");
        stu.setAge(19);
        stuMapper.insert(stu);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateStu(int id) {
        Stu stu = new Stu();
        stu.setId(id);
        stu.setName("lucy");
        stu.setAge(20);
        stuMapper.updateByPrimaryKey(stu);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void deleteStu(int id) {
        stuMapper.deleteByPrimaryKey(id);

    }

    @Override
    public void saveParent(){
        Stu stu = new Stu();
        stu.setName("parent");
        stu.setAge(19);
        stuMapper.insert(stu);
    }

    @Transactional(propagation = Propagation.NESTED)
    @Override
    public void saveChildren(){
        saveChildren1();
        int i = 10 / 0;
        saveChildren2();
    }


    public void saveChildren1(){
        Stu stu1 = new Stu();
        stu1.setName("child-1");
        stu1.setAge(11);
        stuMapper.insert(stu1);
    }

    public void saveChildren2(){
        Stu stu2 = new Stu();
        stu2.setName("child-2");
        stu2.setAge(12);
        stuMapper.insert(stu2);
    }


}
