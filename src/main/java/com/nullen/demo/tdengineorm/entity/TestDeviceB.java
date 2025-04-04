package com.nullen.demo.tdengineorm.entity;

import com.nullen.tdengineorm.annotation.TdColumn;
import com.nullen.tdengineorm.annotation.TdTag;
import com.nullen.tdengineorm.entity.TdBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用于测试关联查询
 *
 * @author Nullen
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TestDeviceB extends TdBaseEntity {
    /**
     * 关联设备TestDeviceA.deviceBId字段
     */
    private Long id;
    @TdColumn(length = 128)
    private String name;
    private Integer age;

    @TdTag
    @TdColumn(length = 128)
    private String deviceCode;
}
