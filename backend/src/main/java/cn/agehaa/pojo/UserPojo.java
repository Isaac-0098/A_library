package cn.agehaa.pojo;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "usertable")
public class UserPojo {
    
    @TableId(value = "id")
    private Integer id;
    
    @TableField(value = "username")
    private String username;
    
    @TableField(value = "password")
    private String password;
    
    @TableField(value = "email")
    private String email;
    
    @TableField(value = "phone")
    private String phone;
    
    @TableField(value = "role")
    private Integer role;  // 0:普通用户 1:管理员 2:超级用户
    
    @TableField(value = "create_time")
    private LocalDateTime createTime;
    
    @TableField(value = "update_time")
    private LocalDateTime updateTime;
    
    @TableField(value = "status")
    private Integer status;  // 0:禁用 1:正常
    
    @TableField(value = "last_login")
    private LocalDateTime lastLogin;
} 