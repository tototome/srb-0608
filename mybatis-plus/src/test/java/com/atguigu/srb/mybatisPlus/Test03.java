package com.atguigu.srb.mybatisPlus;

import com.atguigu.srb.mybatisPlus.mapper.ProductMapper;
import com.atguigu.srb.mybatisPlus.pojo.entity.Product;
import com.atguigu.srb.mybatisPlus.pojo.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class Test03 {
    @Autowired
    private ProductMapper productMapper;
    @Test
    public void  test01(){
        //模拟 读操作的时候读到还未更新的数据 最后造成库存的超卖
        Product li = productMapper.selectById(1);
        Product wang = productMapper.selectById(1);
        li.setPrice(li.getPrice()+50);
        productMapper.updateById(li);
        wang.setPrice(wang.getPrice()-30);
        productMapper.updateById(wang);
        System.out.println(productMapper.selectById(1).getPrice());
    }

    @Test
    public  void test02(){
        //使用乐观锁的方式解决上面问题
        // 乐观锁修改数据的时候会用当前的版本号与要修改的数据版本好做一个比较
        //版本号相同时候则修改数据 不同的时候则不修改数据。
        Product li = productMapper.selectById(1);
        Product wang = productMapper.selectById(1);
        li.setPrice(li.getPrice()+50);
        productMapper.updateById(li);
        wang.setPrice(wang.getPrice()-30);
        int i = productMapper.updateById(wang);
        if(i==0){
            System.out.println("wang 更新失败 重新获取 数据进行更改");
            wang = productMapper.selectById(1);
            wang.setPrice(wang.getPrice()-30);
            productMapper.updateById(wang);
            System.out.println("更新成功");
        }
        System.out.println(productMapper.selectById(1).getPrice());
    }

}
