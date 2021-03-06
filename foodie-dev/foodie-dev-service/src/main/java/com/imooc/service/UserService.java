package com.imooc.service;

import com.imooc.pojo.bo.UserBO;
import com.imooc.pojo.Users;

public interface UserService {


    /**
     * 判断用户名是否存在.
     * @param username
     * @return
     */
    public boolean queryUsernameIsExist(String username);

    /**
     * 创建用户.
     * @param userBO
     * @return
     */
    public Users createUser(UserBO userBO);


    /**
     * 检索用户名和密码是否匹配，用于登录.
     * @param username
     * @param password
     * @return
     */
    public Users queryUsersForLogin(String username,String password);


}
