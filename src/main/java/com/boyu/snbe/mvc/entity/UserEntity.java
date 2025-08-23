package com.boyu.snbe.mvc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("user")
public class UserEntity {
    /**
     * 主键ID，自增
     */
    @TableId(type = IdType.AUTO) // 声明自增主键
    private Integer id;
    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 盐值（用于密码加密）
     */
    private String salt;

    /**
     * 状态（例如：0-禁用，1-正常）
     */
    private Integer status;

    /**
     * 创建人ID
     */
    private Integer creator;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新人ID
     */
    private Integer updater;

    /**
     * 更新时间（默认当前时间戳）
     */
    private Date updateTime;

}
