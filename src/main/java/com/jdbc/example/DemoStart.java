package com.jdbc.example;

import com.alibaba.druid.pool.DruidDataSource;
import com.jdbc.example.model.DemoDTO;
import com.magician.jdbc.MagicianJDBC;
import com.magician.jdbc.core.constant.enums.TractionLevel;
import com.magician.jdbc.core.traction.TractionManager;
import com.magician.jdbc.core.util.JSONUtil;
import com.magician.jdbc.helper.templete.JdbcTemplate;
import com.mysql.cj.jdbc.Driver;

import java.util.Date;
import java.util.List;
import java.util.Properties;

public class DemoStart {

    public static void main(String[] args) throws Exception {

        TractionManager.beginTraction(TractionLevel.READ_COMMITTED);

        TractionManager.commit();

        TractionManager.rollback();

        /*
         * 首先需要创建数据源
         * 理论上支持任意 实现了 DataSource接口 的数据源
         * 这段代码在实战中 可以另起一个类去存放
         */
        DruidDataSource dataSource = new DruidDataSource();

        Properties properties = new Properties();
        properties.put("druid.name", "local");
        properties.put("druid.url", "jdbc:mysql://127.0.0.1:3306/martian-test?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf8&autoReconnect=true&rewriteBatchedStatements=true&useSSL=false");
        properties.put("druid.username", "root");
        properties.put("druid.password", "123456");
        properties.put("druid.driverClassName", Driver.class.getName());

        dataSource.setConnectProperties(properties);

        // 创建JDBC的资源，建议只在项目启动的时候执行一次
        MagicianJDBC.createJDBC()
                .addDataSource("a", dataSource)// 添加数据源，这个方法可以调用多次，添加多个数据源
                .defaultDataSourceName("a");// 设置默认数据源的名称


        /* ************** 操作数据库，这些代码 在实战中需要写到对应的DAO里面 ************ */
        // 从test表查询主键=102的数据
        DemoDTO demoDTO = JdbcTemplate.create().getOneByPrimaryKey("test","id", 102, DemoDTO.class);

        // 删除主键=104的数据
        JdbcTemplate.create().deleteByPrimaryKey("test","id", 104);

        // 将DEO保存到数据库
        DemoDTO demo = new DemoDTO();
        demo.setCreateTime(new Date());
        demo.setName("testName");
        JdbcTemplate.create().insert("test", demo);

        // 修改主键=100的数据的 name为testName，createTime为当前时间
        DemoDTO demo2 = new DemoDTO();
        demo2.setCreateTime(new Date());
        demo2.setName("testName");
        demo2.setId(103);
        JdbcTemplate.create().updateByPrimaryKey("test","id", demo2);

        // 查询name=testName的数据
        DemoDTO demo3 = new DemoDTO();
        demo3.setName("testName");
        List<DemoDTO> demoDTOList = JdbcTemplate.create().selectList("select * from test where name=#{name}", demo3, DemoDTO.class);
    }
}
