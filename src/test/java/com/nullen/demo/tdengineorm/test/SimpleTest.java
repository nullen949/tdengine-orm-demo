package com.nullen.demo.tdengineorm.test;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.nullen.demo.tdengineorm.DemoApplication;
import com.nullen.demo.tdengineorm.entity.TestDeviceA;
import com.nullen.demo.tdengineorm.entity.TestDeviceB;
import com.nullen.tdengineorm.enums.JoinTypeEnum;
import com.nullen.tdengineorm.enums.SelectJoinSymbolEnum;
import com.nullen.tdengineorm.enums.TdSelectFuncEnum;
import com.nullen.tdengineorm.repository.TDengineRepository;
import com.nullen.tdengineorm.wrapper.AbstractTdQueryWrapper;
import com.nullen.tdengineorm.wrapper.TdQueryWrapper;
import com.nullen.tdengineorm.wrapper.TdWrappers;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Nullen
 */
@Slf4j
@SpringBootTest(classes = DemoApplication.class)
public class SimpleTest {

    @Resource
    private TDengineRepository tdengineRepository;


    @Test
    void testCreateStableTable() {
        int testDeviceASTableResult = tdengineRepository.createStableTable(TestDeviceA.class);
        int testDeviceBSTableResult = tdengineRepository.createStableTable(TestDeviceB.class);
        log.info("testCreateStableTable result =====> testDeviceASTableResult:{}, testDeviceBSTableResult:{}", testDeviceASTableResult, testDeviceBSTableResult);
    }

    /**
     * 使用INSERT USING插入数据（缺少子表则自动创建）
     */
    @Test
    void insertUsing() {
        long time = System.currentTimeMillis();
        String deviceCode = "0";
        TestDeviceB testDeviceB = buildDeviceB(time, deviceCode);
        tdengineRepository.insertUsing(testDeviceB, s -> s + "_" + deviceCode);

        TestDeviceA testDeviceA = buildDeviceA(time, testDeviceB.getId());

        tdengineRepository.insertUsing(testDeviceA, s -> s + "_" + deviceCode);
    }


    /**
     * 直接插入数据，缺少子表时会报错
     */
    @Test
    void insert() {
        long time = getTestTs();
        String deviceCode = "0";
        TestDeviceB testDeviceB = buildDeviceB(time, deviceCode);
        TestDeviceA testDeviceA = buildDeviceA(time, testDeviceB.getId());

        tdengineRepository.insert(testDeviceA, s -> s + "_0");
        tdengineRepository.insert(testDeviceB, s -> s + "_0");
    }


    /**
     * 查询最新一条数据
     */
    @Test
    void getLastOne() {
        System.out.println(JSONUtil.toJsonStr(tdengineRepository.getLastOneByTs(TestDeviceA.class)));
        System.out.println(JSONUtil.toJsonStr(tdengineRepository.getLastOneByTs(TestDeviceB.class)));
    }


    /**
     * 按照指定条件查询单个数据(如果有多个则自动获取列表第一个)
     */
    @Test
    void getOne() {
        System.out.println(tdengineRepository.getOne(
                TdWrappers.queryWrapper(TestDeviceA.class)
                        .selectAll()
                        .eq(TestDeviceA::getTs, getTestTs())));
    }


    /**
     * 批量插入使用
     */
    @Test
    void batchInsertUsing() {
        List<TestDeviceA> listA = new ArrayList<>();
        List<TestDeviceB> listB = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            long time = System.currentTimeMillis();
            TestDeviceB testDeviceB = buildDeviceB(time, "0");
            TestDeviceA testDeviceA = buildDeviceA(time, testDeviceB.getId());

            listA.add(testDeviceA);
            listB.add(testDeviceB);
        }
        tdengineRepository.batchInsertUsing(TestDeviceA.class, listA, s -> s + "_0");
        tdengineRepository.batchInsertUsing(TestDeviceB.class, listB, s -> s + "_0");
    }


    @Test
    void testLambdaList() {
        AbstractTdQueryWrapper<TestDeviceA> wrapper =
                TdWrappers.queryWrapper(TestDeviceA.class)
                        .selectAll()
                        .eq(TestDeviceA::getAge, 12)
                        .limit(5000);

        List<TestDeviceA> list = tdengineRepository.list(wrapper);
        log.info("{}", JSONUtil.toJsonStr(list));
    }

    @Test
    void testStrList() {
        TdQueryWrapper<TestDeviceA> wrapper = TdWrappers.queryWrapper(TestDeviceA.class)
                .selectAll()
                .eq("tg1", 12)
                .limit(2, 3000);
        System.out.println(tdengineRepository.list(wrapper));
    }

    @Test
    void simpleWindowFuncLambdaQuery() {
        TdQueryWrapper<TestDeviceA> wrapper = TdWrappers.queryWrapper(TestDeviceA.class)
                .selectFunc(TdSelectFuncEnum.FIRST, TestDeviceA::getTs)
                .selectFunc(TdSelectFuncEnum.FIRST, TestDeviceA::getName)
                .selectFunc(TdSelectFuncEnum.FIRST, TestDeviceA::getAge)
                .selectFunc(TdSelectFuncEnum.LAST, TestDeviceA::getId)
                .eq(TestDeviceA::getTg1, "12")
                .intervalWindow("10m");
        System.out.println(tdengineRepository.list(wrapper));
    }


    /**
     * 简单窗口函数使用
     */
    @Test
    void simpleWindowFuncQuery() {
        TdQueryWrapper<TestDeviceA> wrapper = TdWrappers.queryWrapper(TestDeviceA.class)
                .selectFunc(TdSelectFuncEnum.FIRST, "ts", "name", "age")
                .selectFunc(TdSelectFuncEnum.LAST, "id")
                .eq("tg1", 12)
                .intervalWindow("10m");
        System.out.println(tdengineRepository.list(wrapper));
    }


    @Test
    void complexWindowFunc() {
        // TODO 复杂窗口函数使用
        TdQueryWrapper<TestDeviceA> wrapper = TdWrappers.queryWrapper(TestDeviceA.class);
    }


    /**
     * 嵌套lambda查询
     */
    @Test
    void nestingLambdaQuery() {
        TdQueryWrapper<TestDeviceA> wrapper = TdWrappers.queryWrapper(TestDeviceA.class)
                .innerQueryWrapper(innerWrapper ->
                        innerWrapper
                                .selectFunc(TdSelectFuncEnum.FIRST, TestDeviceA::getTs)
                                .eq(TestDeviceA::getTg1, 12)
                                .intervalWindow("30m")
                )
                .selectAll()
                .orderByDesc(TestDeviceA::getTs);

        System.out.println(tdengineRepository.list(wrapper));
    }

    @Test
    void selectJoinQuery() {
        TdQueryWrapper<TestDeviceA> wrapper = TdWrappers.queryWrapper(TestDeviceA.class)
                .selectAll()
                .innerQueryWrapper(innerWrapper -> innerWrapper
                        .selectCalc(TestDeviceA::getAge, consumer -> {
                            consumer.select(TdSelectFuncEnum.FIRST, TestDeviceA::getAge)
                                    .operate(SelectJoinSymbolEnum.PLUS)
                                    .select(TdSelectFuncEnum.LAST, TestDeviceA::getAge);
                        })
                        .eq(TestDeviceA::getTg1, 12)
                        .intervalWindow("10m")
                )
                .orderByDesc(TestDeviceA::getAge)
                .limit(12);

        System.out.println(tdengineRepository.list(wrapper));
    }


    @Test
    void simpleJoinQueryTest() {
        AbstractTdQueryWrapper<TestDeviceA> wrapper =
                TdWrappers.queryWrapper(TestDeviceA.class)
                        .select(TestDeviceA::getAge)
                        .select(TestDeviceB.class, TestDeviceB::getName)
                        .join(JoinTypeEnum.LEFT_JOIN, TestDeviceB.class)
                        .eq("test_device_b.id", 1803270034955735040L)
                        .limit(5000);

        List<TestDeviceA> list = tdengineRepository.list(wrapper);
        log.info("{}", JSONUtil.toJsonStr(list));
    }


    private static TestDeviceA buildDeviceA(long time, Long testDeviceBId) {
        TestDeviceA testDeviceA = new TestDeviceA();
        testDeviceA.setTs(time);
        testDeviceA.setAge(12);
        testDeviceA.setFl1(12F);
        testDeviceA.setName("12");
        testDeviceA.setId(12L);
        testDeviceA.setDb2(12D);
        testDeviceA.setDb2(22D);
        testDeviceA.setABCdEfggA("12");
        testDeviceA.setTg1("12");
        testDeviceA.setTg2(12);
        testDeviceA.setDeviceBId(testDeviceBId);
        testDeviceA.setCreateTime(new Timestamp(time));
        return testDeviceA;
    }

    private static TestDeviceB buildDeviceB(long time, String deviceBCode) {
        TestDeviceB testDeviceB = new TestDeviceB();
        testDeviceB.setTs(time);
        testDeviceB.setDeviceCode(deviceBCode);
        testDeviceB.setName("aaa");
        testDeviceB.setAge(11);
        testDeviceB.setId(IdUtil.getSnowflake().nextId());
        return testDeviceB;
    }

    private static long getTestTs() {
        return LocalDateTime.parse("2024-06-19T19:34:08") // 注意格式中的 'T'
                .toInstant(ZoneOffset.UTC) // 转换为 UTC 时间戳
                .toEpochMilli();
    }
}
