package com.imooc.interceptor;

import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public class UserTokenInterceptor implements HandlerInterceptor {

    public static final String REDIS_USER_TOKEN = "redis_user_token";

    @Autowired
    private RedisOperator redisOperator;

    /**
     * 拦截请求在访问controller之前
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

//        System.out.println("进入的拦截器被拦截.........");

        String userId = request.getHeader("headerUserId");
        String userToken = request.getHeader("headerUserToken");
        if(StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(userToken)){
            String uniqueToken = redisOperator.get(REDIS_USER_TOKEN + ":" + userId);
            if(StringUtils.isBlank(uniqueToken)){
                returnErrorResponse(response,IMOOCJSONResult.errorMsg("请登录....."));
//                System.out.println("请登录.....");
                return false;
            }else {
                if(!uniqueToken.equals(userToken)){
                    returnErrorResponse(response,IMOOCJSONResult.errorMsg("账号在异地登录........"));
//                    System.out.println("账号在异地登录........");
                    return false;
                }
            }
        }else{
            returnErrorResponse(response,IMOOCJSONResult.errorMsg("请登录......."));
//            System.out.println("请登录.......");
            return false;
        }

        /**
         * false : 请求被拦截，被驳回，验证出现问题
         * true : 请求在经过验证校验之后，是OK的，是可以放行的
         */
        return true;
    }

    public void returnErrorResponse(HttpServletResponse response, IMOOCJSONResult result){
        OutputStream outputStream = null;
        try {
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/json");
            outputStream = response.getOutputStream();
            outputStream.write(JsonUtils.objectToJson(result).getBytes("utf-8"));
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(outputStream != null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    /**
     * 请求访问controller之后渲染视图之前
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 请求访问controller和视图之后
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
