package com.imooc.controller;

import com.imooc.pojo.Users;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.service.UserService;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.MD5Utils;
import com.imooc.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

@Controller
public class SSOController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisOperator redisOperator;

    private static final String REDIS_USER_TOKEN = "redis_user_token";

    private static final String REDIS_USER_TICKET = "redis_user_ticket";

    private static final String REDIS_TEMP_TICKET = "redis_temp_ticket";

    private static final String COOKIE_USER_TIKET = "cookie_user_tiket";

    @GetMapping("/login")
    public String login(String returnUrl, Model model, HttpServletRequest request, HttpServletResponse response){
        model.addAttribute("returnUrl",returnUrl);
        // 1 .获取userTicket门票，如果cookie中能够获取到，证明用户登录过CAS系统，tmpTicket此时签发一个一次性的临时票据
        String userTicket = getCookie(request,COOKIE_USER_TIKET);
        boolean isVerified = verifyUserTicket(userTicket);
        if(isVerified){
            String tmpTicket = createTempTicket();
            return "redirect:" + returnUrl + "?tmpTicket="+tmpTicket;
        }
        //2.用户从未登陆过，第一次进入则跳转到CAS的统一登录页面
        return "login";
    }

    /**
     * 校验CAS全局用户门票
     * @param userTicket
     * @return
     */
    private boolean verifyUserTicket(String userTicket){
        //0.验证CAS门票不能为空
        if(StringUtils.isBlank(userTicket))
            return false;
        //1.验证CAS门票是否有效
        String userId = redisOperator.get(REDIS_USER_TICKET + ":" + userTicket);
        if(StringUtils.isBlank(userId))
            return false;
        //2.验证门票对应的user会话是否存在
        String userRedis = redisOperator.get(REDIS_USER_TOKEN + ":" + userId);
        if(StringUtils.isBlank(userRedis)){
            return false;
        }
        return true;
    }

    /**
     * CAS 的统一登录接口
     *  目的：
     *      1.登录后创建用户的全局会话                        ->  uniqueToken
     *      2.创建用户的全局门票，用以表示在CAS端是否登录过   -> userTicket
     *      3.创建用户的临时票据，用于回跳回传                -> tempTicket
     */
    @PostMapping("/doLogin")
    public String doLogin(String username, String password, String returnUrl,
                          Model model, HttpServletRequest request, HttpServletResponse response) throws Exception{
        model.addAttribute("returnUrl",returnUrl);

        //0.判断用户名和密码不为空
        if(StringUtils.isBlank(username)
                || StringUtils.isBlank(password)){
            model.addAttribute("errmsg","用户名或密码不能为空");
            return "login";
        }
        //1.实现登录
        Users userResult = userService.queryUsersForLogin(username, MD5Utils.getMD5Str(password));
        if(userResult == null){
            model.addAttribute("errmsg","用户名或者密码不正确");
            return "login";
        }

        //2.实现用户的redis会话
        String uniqueToken = UUID.randomUUID().toString().trim();
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(userResult,usersVO);
        usersVO.setUserUniqueToken(uniqueToken);
        redisOperator.set(REDIS_USER_TOKEN + ":" + userResult.getId(), JsonUtils.objectToJson(usersVO));

        //3.生成ticket门票，全局门票，代表用户在CAS端登录过
        String userTicket = UUID.randomUUID().toString().trim();

        //3.1  用户全局门票需要放入CAS的cookie中
        setCookie(COOKIE_USER_TIKET,userTicket,response);

        //4.userTicket 关联用户ID，并且放入到Redis中，代表这个用户有门票了，可以在各个景区游玩
        redisOperator.set(REDIS_USER_TICKET + ":" + userTicket , userResult.getId());

        //5.生成临时票据，回跳到调用端网站，是由CAS端所签发的一个一次性的临时的ticket。

        String tmpTicket = createTempTicket();

        /**
         * userTicket : 用于表示用户在CAS端的一个登录状态：已经登录
         * tempTicket : 用于颁发给用户进行一次性的验证的票据，有时效性
         */

        /**
         * 举例：
         *  我们去动物园游玩，在大门口需要买一张门票，这个就是CAS系统的全局门票和用户会话
         *  动物园里面有一些小的景点，需要凭你的门票去换一次性的票据，有了这张票据以后就能去一些小景点游玩
         *  这样一个个的小景点其实就是我们这里对应的一个个的站点
         *  当我们使用完这张临时票据以后，就需要销毁。
         */


//        return "login";
        return "redirect:" + returnUrl + "?tmpTicket="+tmpTicket;
    }


    @PostMapping("/verifyTmpTicket")
    @ResponseBody
    public IMOOCJSONResult verifyTmpTicket(String tmpTicket, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 使用一次性临时票据来验证用户是否登录，如果登录过，把会话信息返回给站点
        //使用完毕后，需要销毁临时票据
        String tmpTicketValue  = redisOperator.get(REDIS_TEMP_TICKET + ":" + tmpTicket);
        if(StringUtils.isBlank(tmpTicketValue)){
            return IMOOCJSONResult.errorUserTicket("用户的票据异常！");
        }

        //0.如果临时票据ok，这需要销毁，并且拿到CAS端cookie中全局userTicket,以此在获取用户会话
        if(!tmpTicketValue.equals(MD5Utils.getMD5Str(tmpTicket))){
            return IMOOCJSONResult.errorUserTicket("用户的票据异常！");
        }else{
            //销毁临时票据
            redisOperator.del(REDIS_TEMP_TICKET + ":" + tmpTicket);
        }

        //1.验证并且获取用户的userTicket
        String userTicket = getCookie(request,COOKIE_USER_TIKET);
        String userId = redisOperator.get(REDIS_USER_TICKET + ":" + userTicket);
        if(StringUtils.isBlank(userId)){
            return IMOOCJSONResult.errorUserTicket("用户的票据异常！");
        }
        //2.验证门票对应的user会话是否存在
        String userRedis = redisOperator.get(REDIS_USER_TOKEN + ":" + userId);
        if(StringUtils.isBlank(userRedis)){
            return IMOOCJSONResult.errorUserTicket("用户的票据异常！");
        }
        //验证成功，返回OK，携带用户会话
        return IMOOCJSONResult.ok(JsonUtils.jsonToPojo(userRedis,UsersVO.class));

    }


    @PostMapping("/logout")
    @ResponseBody
    public IMOOCJSONResult logout(String userId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if(StringUtils.isBlank(userId)){
            return IMOOCJSONResult.errorMsg("用户ID不能为空");
        }
        //0.获取CAS的全局门票
        String userTicket = getCookie(request,COOKIE_USER_TIKET);
        //1.清除userTicket票据  redis/cookie
        deleteCookie(COOKIE_USER_TIKET,response);
        redisOperator.del(REDIS_USER_TICKET + ":" + userTicket);

        //2.清除用户全局会话（分布式会话）
        redisOperator.del(REDIS_USER_TOKEN + ":" + userId);

        return IMOOCJSONResult.ok();
    }


    /**
     * 创建临时票据
     * @return
     */
    private String createTempTicket(){
        String tempTicket = UUID.randomUUID().toString().trim();
        try {
            redisOperator.set(REDIS_TEMP_TICKET + ":" + tempTicket,MD5Utils.getMD5Str(tempTicket) , 600);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tempTicket;
    }

    private void setCookie(String key , String value , HttpServletResponse response){
        Cookie cookie = new Cookie(key,value);
        cookie.setDomain("sso.com");
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    private void deleteCookie(String key , HttpServletResponse response){
        Cookie cookie = new Cookie(key,null);
        cookie.setDomain("sso.com");
        cookie.setPath("/");
        cookie.setMaxAge(-1);
        response.addCookie(cookie);
    }

    private String getCookie(HttpServletRequest request, String key){
        Cookie[] cookies = request.getCookies();
        if(cookies == null || StringUtils.isBlank(key)){
            return null;
        }
        String cookieValue = "";
        for (int i = 0 ; i < cookies.length ; i ++ ){
            if(cookies[i].getName().equals(key)){
                cookieValue = cookies[i].getValue();
                break;
            }
        }
        return cookieValue;
    }


}
