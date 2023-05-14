package com.k1.Test;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.k1.reggie.common.R;
import com.k1.reggie.config.MybatisPlusConfig;
import com.k1.reggie.config.RedisConfig;
import com.k1.reggie.config.WebMvcConfig;
import com.k1.reggie.entity.Category;
import com.k1.reggie.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;



@SpringBootTest(classes = {MybatisPlusConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class FileUploadTest {

    @Autowired
    @SuppressWarnings("all")
    private CategoryService categoryService;

    @Test
    public static void main(String[] args) {

        String fileName = UUID.randomUUID().toString();
        System.out.println(fileName);
    }

    @Test
    public void page() {
        Page pageInfo = new Page(1, 10);
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加排序条件，根据sort进行排序
        queryWrapper.orderByDesc(Category::getSort);
        categoryService.page(pageInfo, queryWrapper);
        List<Category> records = pageInfo.getRecords();
        for (Category record : records) {
            byte[] bytes = record.getName().getBytes(Charset.forName("GBK"));
//            String s = new String(bytes, StandardCharsets.UTF_8);
//            System.out.println(s);
            for (byte aByte : bytes) {
                System.out.print(aByte);
            }
            System.out.println("");
        }
    }
}
