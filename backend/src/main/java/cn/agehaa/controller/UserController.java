package cn.agehaa.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.agehaa.pojo.UserPojo;
import cn.agehaa.service.impl.UserServiceImpl;
import cn.agehaa.utils.Result;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    // 用户注册
    @PostMapping("/register")
    public String register(@RequestParam("username") String username,
                           @RequestParam("password") String password,
                           @RequestParam(value = "email", required = false) String email,
                           @RequestParam(value = "phone", required = false) String phone) {
        if (userService.isUsernameExists(username)) {
            return Result.errorGetString("用户名已存在");
        }
        try {
            userService.register(username, password, email, phone);
            return Result.okGetString("注册成功");
        } catch (Exception e) {
            return Result.errorGetString("注册失败：" + e.getMessage());
        }
    }

    // 用户登录
    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                    @RequestParam("password") String password) {
        UserPojo user = userService.login(username, password);
        if (user != null) {
            // 创建返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("username", user.getUsername());
            data.put("role", user.getRole());
            data.put("lastLogin", user.getLastLogin());
            return Result.okGetData("登录成功", data);
        }
        return Result.errorGetString("用户名或密码错误");
    }

    // 修改密码
    @PutMapping("/password")
    public String updatePassword(@RequestParam("username") String username,
                           @RequestParam("oldPassword") String oldPassword,
                           @RequestParam("newPassword") String newPassword) {
        try {
            UserPojo user = userService.login(username, oldPassword);
            if (user != null) {
                userService.updatePassword(user.getId(), newPassword);
                return Result.okGetString("密码修改成功");
            }
            return Result.errorGetString("原密码错误");
        } catch (Exception e) {
            return Result.errorGetString("密码修改失败：" + e.getMessage());
        }
    }

    // 获取所有用户列表
    @GetMapping("/list/all")
    public String getAllUsers() {
        try {
            List<UserPojo> users = userService.getAllUsers();
            return Result.okGetList("获取用户列表成功", users);
        } catch (Exception e) {
            return Result.errorGetString("获取用户列表失败：" + e.getMessage());
        }
    }

    // 修改原有的获取用户列表方法
    @GetMapping("/list/{role}")
    public String getUserList(@PathVariable("role") Integer role) {
        try {
            List<UserPojo> users = userService.getUsersByRole(role);
            return Result.okGetList("获取用户列表成功", users);
        } catch (Exception e) {
            return Result.errorGetString("获取用户列表失败：" + e.getMessage());
        }
    }

    // 更新用户信息（需要相应权限）
    @PutMapping("/update")
    public String updateUser(@RequestParam("id") Integer id,
                             @RequestParam("username") String username,
                             @RequestParam("password") String password,
                             @RequestParam(value = "email", required = false) String email,
                             @RequestParam(value = "phone", required = false) String phone,
                             @RequestParam("role") Integer role,
                             @RequestParam("status") Integer status) {
        try {
            UserPojo user = new UserPojo();
            user.setId(id);
            user.setUsername(username);
            user.setPassword(password);
            user.setEmail(email);
            user.setPhone(phone);
            user.setRole(role);
            user.setStatus(status);
            userService.updateUser(user);
            return Result.okGetString("更新成功");
        } catch (Exception e) {
            return Result.errorGetString("更新失败：" + e.getMessage());
        }
    }

    // 删除用户（需要相应权限）
    @DeleteMapping("/delete/{userId}")
    public String deleteUser(@PathVariable("userId") Integer userId) {
        try {
            userService.deleteUser(userId);
            return Result.okGetString("删除成功");
        } catch (Exception e) {
            return Result.errorGetString("删除失败：" + e.getMessage());
        }
    }

    // 修改用户状态（启用/禁用）
    @PutMapping("/status")
    public String updateUserStatus(@RequestParam("userId") Integer userId,
                                   @RequestParam("status") Integer status) {
        try {
            userService.updateUserStatus(userId, status);
            return Result.okGetString("状态修改成功");
        } catch (Exception e) {
            return Result.errorGetString("状态修改失败：" + e.getMessage());
        }
    }
} 