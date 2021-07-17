package com.imooc.controller;

import com.imooc.enums.YesOrNo;
import com.imooc.pojo.Carousel;
import com.imooc.pojo.Category;
import com.imooc.service.CarouselService;
import com.imooc.service.CategoryService;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.pojo.vo.CategoryVO;
import com.imooc.pojo.vo.NewItemsVO;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Api(value = "首页",tags = {"首页展示的相关接口"})
@RestController
@RequestMapping("index")
public class IndexController {

    @Autowired
    private CarouselService carouselService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedisOperator redisOperator;


    @ApiOperation(value = "获取首页轮播图列表",notes = "获取首页轮播图列表",httpMethod = "GET")
    @GetMapping("/carousel")
    public IMOOCJSONResult carousel(){
        List<Carousel> result = new ArrayList<>();
        String carouselStr = redisOperator.get("carousel");
        if(StringUtils.isBlank(carouselStr)){
            result = carouselService.queryAll(YesOrNo.YES.type);
            redisOperator.set("carousel", JsonUtils.objectToJson(result));
        }else{
            result = JsonUtils.jsonToList(carouselStr,Carousel.class);
        }
        return IMOOCJSONResult.ok(result);
    }

    /**
     * 1.后台运营系统，一旦广告（轮播图） 发生更改，就可以删除缓存，然后重置
     * 2.定时重置，比如每天凌晨三点重置
     * 3.每个轮播图都有可能是一个广告，每个广告都会有一个过期时间，过期了，再重置
     */

    /**
     * 首页分类展示需求：
     * 1.第一次刷新主页查询大分类，渲染展示到首页
     * 2.如果鼠标上移到大分类，则加载器子分类，如果已经存在了子分类，则不需要加载(懒加载)
     */

    @ApiOperation(value = "获取商品分类(一级分类)",notes = "获取商品分类(一级分类)",httpMethod = "GET")
    @GetMapping("/cats")
    public IMOOCJSONResult cats(){
        String catsStr = redisOperator.get("cats");
        List<Category> result = new ArrayList<>();
        if(StringUtils.isBlank(catsStr)){
            result = categoryService.queryAllRootLevelCat();
            redisOperator.set("cats",JsonUtils.objectToJson(result));
        }else{
            result = JsonUtils.jsonToList(catsStr,Category.class);
        }
        return IMOOCJSONResult.ok(result);
    }

    @ApiOperation(value = "获取商品子分类",notes = "获取商品子分类",httpMethod = "GET")
    @GetMapping("/subCat/{rootCatId}")
    public IMOOCJSONResult subCat(
            @ApiParam(name = "rootCatId",value = "一级分类ID",required = true)
            @PathVariable Integer rootCatId){
        if(rootCatId == null){
            return IMOOCJSONResult.errorMsg("分类不存在");
        }
        String subCatStr = redisOperator.get("subCat:" + rootCatId);
        List<CategoryVO> result = new ArrayList<>();
        if(StringUtils.isBlank(subCatStr)){
            result = categoryService.getSubCatList(rootCatId);
            //这种操作会引起内缓存穿透
            /**
             * 查询的key在redis中不存在
             * 对应的id在数据库中也不存在
             * 此时被非法用户进行攻击，大量的请求会直接请求在db上
             * 造成宕机，从而影响整个系统，
             * 这种现象称为缓存穿透。
             * 解决方案：把空的数据也缓存起来，比如空字符串，空对象，空数据或者list
             */
            if(result != null && result.size() > 0){
                redisOperator.set("subCat:" + rootCatId,JsonUtils.objectToJson(result));
            }else {
                redisOperator.set("subCat:" + rootCatId,JsonUtils.objectToJson(result),5*60);
            }
        }else{
            result = JsonUtils.jsonToList(subCatStr,CategoryVO.class);
        }

        return IMOOCJSONResult.ok(result);
    }

    @ApiOperation(value = "查询每个一级分类下的最新六条商品数据",notes = "查询每个一级分类下的最新六条商品数据",httpMethod = "GET")
    @GetMapping("/sixNewItems/{rootCatId}")
    public IMOOCJSONResult sixNewItems(
            @ApiParam(name = "rootCatId",value = "一级分类ID",required = true)
            @PathVariable Integer rootCatId){
        if(rootCatId == null){
            return IMOOCJSONResult.errorMsg("分类不存在");
        }
        List<NewItemsVO> result = categoryService.getSixNewItemsLazy(rootCatId);
        return IMOOCJSONResult.ok(result);
    }



}
