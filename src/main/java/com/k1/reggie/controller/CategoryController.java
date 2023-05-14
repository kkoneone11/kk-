package com.k1.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.k1.reggie.common.R;
import com.k1.reggie.entity.Category;
import com.k1.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品和套餐分类
     *
     * @param request
     * @param category
     * @return
     */

    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Category category) {
        log.info("category:{}", category);
        System.out.println(category);
        categoryService.save(category);
        return R.success("新增分类成功");
    }


    /**
     * 分页处理器
     *
     * @param page
     * @param pageSize
     * @return
     */

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize) {
        log.info("page:{},pageSize:{}", page, pageSize);

        Page pageInfo = new Page(page, pageSize);
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加排序条件，根据sort进行排序
        queryWrapper.orderByDesc(Category::getSort);

        categoryService.page(pageInfo, queryWrapper);return R.success(pageInfo);
    }


    /**
     * 根据id删除对象
     *
     * @param id
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long id) {
        log.info("删除分类,id为：{}", id);
//        categoryService.removeById(id);
        categoryService.removeById(id);
        return R.success("分类信息删除成功");
    }

    /**
     * 修改分类信息
     *
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category) {
        log.info("修改分类信息：{}", category);
        categoryService.updateById(category);
        return R.success("修改成功");
    }


    /**
     * 获取category列表给前端展示
     *
     * @return
     */
    //list中用Category，虽然后台前端传来的是数字但是封装到Category中方便以后接收其他参数
    @GetMapping("/list")
    public R<List<Category>> list(Category category) {
        //构造条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(category.getType() != null, Category::getType, category.getType());

        //添加排序条件
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }
}
