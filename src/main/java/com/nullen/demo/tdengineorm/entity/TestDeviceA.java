package com.nullen.demo.tdengineorm.entity;

import com.nullen.tdengineorm.annotation.TdColumn;
import com.nullen.tdengineorm.annotation.TdTable;
import com.nullen.tdengineorm.annotation.TdTag;
import com.nullen.tdengineorm.entity.TdBaseEntity;
import com.nullen.tdengineorm.enums.TdFieldTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.sql.Timestamp;

/**
 * 设备A实体类
 * 随意写了各种类型各种名称的字段，用来测试字段解析
 *
 * @author Nullen
 * @date 2024/06/19
 */
@Data
@TdTable("tb_test_device_a")
@EqualsAndHashCode(callSuper = true)
public class TestDeviceA extends TdBaseEntity {
    /**
     * 使用 @TdField 注解指定字段类型以及长度, 未指定时默认为INT类型
     */
    @TdColumn(type = TdFieldTypeEnum.NCHAR, length = 20)
    private String name;
    private Integer age;
    @TdColumn(type = TdFieldTypeEnum.DOUBLE)
    private Double db1;
    @TdColumn(type = TdFieldTypeEnum.DOUBLE)
    private Double db2;
    @TdColumn(type = TdFieldTypeEnum.FLOAT)
    private Float fl1;
    @TdColumn(type = TdFieldTypeEnum.BIGINT)
    private Long id;
    @TdColumn(type = TdFieldTypeEnum.TIMESTAMP)
    private Timestamp createTime;
    @TdColumn(value = "device_b_id", type = TdFieldTypeEnum.BIGINT)
    private Long deviceBId;
    /**
     * 使用MP的 @TableField 注解来指定字段别名
     */
    @TdColumn(value = "a_b_cd_efgg_a", type = TdFieldTypeEnum.NCHAR, length = 20)
    private String aBCdEfggA;
    /**
     * 使用 @TdTag 注解标记字段为Tag字段
     */
    @TdTag
    @TdColumn(type = TdFieldTypeEnum.NCHAR, length = 20)
    private String tg1;

    @TdTag
    @TdColumn(type = TdFieldTypeEnum.NCHAR, length = 20)
    private Integer tg2;
}