package com.nullen.demo.tdengineorm.entity;

import com.nullen.tdengineorm.annotation.TdColumn;
import com.nullen.tdengineorm.annotation.TdTag;
import lombok.Data;

/**
 * 用于测试关联查询
 *
 * @author Nullen
 */
@Data
public class TestDeviceB {
    /**
     * TDengine要求每个表的第一个字段, 必须为ts, 表示数据的时间戳
     * Java可以使用Long或者Timestamp都行
     */
    private Long ts;
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
