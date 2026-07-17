package com.xiyuguopu.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 字段自动填充
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createdAt", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 用 setFieldValByName 强制覆盖，不用 strictUpdateFill
        // 因为 strictUpdateFill 只在字段为 null 时才填充，
        // 但 updateById 传的是从 DB 查出来的对象，updatedAt 已有旧值，会被跳过
        this.setFieldValByName("updatedAt", LocalDateTime.now(), metaObject);
    }
}
