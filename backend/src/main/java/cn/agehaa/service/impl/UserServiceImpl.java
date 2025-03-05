package cn.agehaa.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.agehaa.dao.UserDao;
import cn.agehaa.pojo.UserPojo;

@Service
public class UserServiceImpl {

    @Autowired
    private UserDao userDao;

    // 用户注册
    public void register(String username, String password, String email, String phone) {
        UserPojo user = new UserPojo();
        user.setUsername(username);
        user.setPassword(password); // 实际应用中应该加密
        user.setEmail(email);
        user.setPhone(phone);
        user.setRole(0); // 默认为普通用户
        user.setStatus(1); // 默认状态为正常
        user.setCreateTime(LocalDateTime.now());
        userDao.insert(user);
    }

    // 用户登录
    public UserPojo login(String username, String password) {
        QueryWrapper<UserPojo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username)
                   .eq("password", password)
                   .eq("status", 1);
        UserPojo user = userDao.selectOne(queryWrapper);
        if (user != null) {
            user.setLastLogin(LocalDateTime.now());
            userDao.updateById(user);
        }
        return user;
    }

    // 修改密码
    public void updatePassword(Integer userId, String newPassword) {
        UserPojo user = userDao.selectById(userId);
        if (user != null) {
            user.setPassword(newPassword);
            userDao.updateById(user);
        }
    }

    // 获取用户列表（根据角色）
    public List<UserPojo> getUsersByRole(Integer role) {
        QueryWrapper<UserPojo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role", role);
        return userDao.selectList(queryWrapper);
    }

    // 更新用户信息
    public void updateUser(UserPojo user) {
        user.setUpdateTime(LocalDateTime.now());
        userDao.updateById(user);
    }

    // 删除用户
    public void deleteUser(Integer userId) {
        userDao.deleteById(userId);
    }

    // 检查用户名是否存在
    public boolean isUsernameExists(String username) {
        QueryWrapper<UserPojo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return userDao.selectCount(queryWrapper) > 0;
    }

    // 修改用户状态
    public void updateUserStatus(Integer userId, Integer status) {
        UserPojo user = userDao.selectById(userId);
        if (user != null) {
            user.setStatus(status);
            userDao.updateById(user);
        }
    }

    // 根据ID获取用户
    public UserPojo getUserById(Integer id) {
        return userDao.selectById(id);
    }

    // 根据用户名搜索用户
    public List<UserPojo> searchUserByName(String username) {
        QueryWrapper<UserPojo> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("username", username);
        return userDao.selectList(queryWrapper);
    }

    // 获取所有用户
    public List<UserPojo> getAllUsers() {
        return userDao.selectList(null);
    }
} 