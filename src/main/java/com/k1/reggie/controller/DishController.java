package com.k1.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.k1.reggie.common.R;
import com.k1.reggie.dto.DishDto;
import com.k1.reggie.entity.Category;
import com.k1.reggie.entity.Dish;
import com.k1.reggie.entity.DishFlavor;
import com.k1.reggie.service.CategoryService;
import com.k1.reggie.service.DishFlavorService;
import com.k1.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 新增菜品
 */
@RestController
@RequestMapping( "/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 新增菜品
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        System.out.println(dishDto);
        dishService.saveWithFlavor(dishDto);
        return R.success("新增成功");
    }


    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
//        //构造分页器
//        Page<Dish> pageInfo = new Page<>(page,pageSize);
//        //如果是仅用Dish类是展示不出来categoryName，要用Dto
//        Page<DishDto> dishDtoPage = new Page<>();
//
//        //条件构造条件器
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        //添加过滤条件
//        queryWrapper.like(name!=null,Dish::getName,name);
//        //添加排序条件
//        queryWrapper.orderByDesc(Dish::getUpdateTime);
//        //执行分页查询
//        dishService.page(pageInfo,queryWrapper);
//        //对象拷贝,但是忽略record列，因为里面是对每一条信息的整合，要另外对其进行处理
//        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
//        //对records处理
//        List<Dish> records = pageInfo.getRecords();
//        List<DishDto> list= records.stream().map((item) -> {
//            DishDto dishDto = new DishDto();
//            //将dish中原有的基本值赋给dishDto
//            BeanUtils.copyProperties(item, dishDto);
//            Long categoryid = item.getCategoryId();
//            //要根据id来查分类就得用到categoryservice，然后获取对应姓名
//            Category category = categoryService.getById(categoryid);
//
//            if(category!=null){
//                String categoryName = category.getName();
//                dishDto.setCategoryName(categoryName);
//            }
//
//            return dishDto;
//
//        }).collect(Collectors.toList());
//
//        dishDtoPage.setRecords(list);
//
//        return R.success(dishDtoPage);
//    }
        //构造分页构造器对象
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name != null, Dish::getName, name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        //执行分页查询
        dishService.page(pageInfo, queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");

        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * 修改数据，回显
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id) {
        DishDto dishDto = dishService.getByIdWithFlavor(id);

        return R.success(dishDto);
    }

    /**
     * 修改菜品
     *
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);
        //清除所有redis中缓存的菜品数据
        //redisTemplate.delete("dish_*");//dish_开头的全部清除

        //清理某个分类下面的菜品缓存
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);


        return R.success("更新菜品成功");
    }


    /**
     * 根据条件查询对应的菜品数据
     *
     * @param dish
     * @return
     */
//    @GetMapping("/list")
//    //此处参数用Dish不用id是方便以后接收其他参数可以复用。而因为前端返回的数据并不是完整json所以不用RequestBody
//    public R<List<Dish>> list(Dish dish){
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
//        queryWrapper.eq(Dish::getStatus,1);
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//        List<Dish> list = dishService.list(queryWrapper);
//        return R.success(list);
//    }
    @GetMapping("/list")
    //此处参数用Dish不用id是方便以后接收其他参数可以复用。而因为前端返回的数据并不是完整json所以不用RequestBody
    public R<List<DishDto>> list(Dish dish) {
        List<DishDto> dishDtoList = null;
        //拼凑key
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();
        //先查看redis中是否有缓存
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);

        if (dishDtoList != null) {
            //如果存在直接返回无需查询数据库
            return R.success(dishDtoList);
        }


        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus, 1);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);
        dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            //当前菜品的id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId, dishId);
            //SQL:select * from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());

        //如果没有就查询数据库,将查询到的菜品数据缓存到redis
        redisTemplate.opsForValue().set(key, dishDtoList, 60, TimeUnit.MINUTES);


        return R.success(dishDtoList);
    }

}
