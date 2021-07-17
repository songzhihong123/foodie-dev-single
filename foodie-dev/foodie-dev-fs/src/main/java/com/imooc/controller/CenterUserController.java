package com.imooc.controller;

import com.imooc.pojo.Users;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.resource.FileResource;
import com.imooc.service.FdfsService;
import com.imooc.service.center.CenterUserService;
import com.imooc.utils.CookieUtils;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("fdfs")
public class CenterUserController extends BaseController{


    @Autowired
    private FdfsService fdfsService;

    @Autowired
    private FileResource fileResource;

    @Autowired
    private CenterUserService centerUserService;



    @PostMapping("/uploadFace")
    public IMOOCJSONResult uploadFace(String userId, MultipartFile file, HttpServletRequest request,HttpServletResponse response) throws Exception{
        String path = null;
        //开始文件上传
        if(file != null){
            //获得文件上传的文件路径
            String fileName = file.getOriginalFilename();
            if(StringUtils.isNotBlank(fileName)){
                String[] fileNameArr = fileName.split("\\.");
                //获取文件的后缀名
                String suffix = fileNameArr[fileNameArr.length - 1];

                if(!suffix.equalsIgnoreCase("png")
                        && !suffix.equalsIgnoreCase("jpg")
                        && !suffix.equalsIgnoreCase("jpeg")){
                    return IMOOCJSONResult.errorMap("图片格式不正确");
                }
                path = fdfsService.upload(file, suffix);
                System.out.println(path);
            }
        }else {
            return IMOOCJSONResult.errorMap("文件不能为空!");
        }

        if(StringUtils.isBlank(path)){

            String finalUserFaceUrl = fileResource.getHost() + path;
            Users userResult = centerUserService.updateUserFace(userId, finalUserFaceUrl);
            UsersVO usersVO = conventUsersVO(userResult);
            //把Users的信息放入到Cookie里面
            CookieUtils.setCookie(request,response,"user", JsonUtils.objectToJson(usersVO),true);
        }else {
            return IMOOCJSONResult.errorMsg("上传头像失败!");

        }

        return IMOOCJSONResult.ok();

    }



}
