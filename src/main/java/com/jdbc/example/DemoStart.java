package com.jdbc.example;

import com.alibaba.druid.pool.DruidDataSource;
import com.jdbc.example.model.DemoDTO;
import com.magician.jdbc.MagicianJDBC;
import com.magician.jdbc.core.constant.enums.TractionLevel;
import com.magician.jdbc.core.transaction.TransactionManager;
import com.magician.jdbc.helper.templete.JdbcTemplate;
import com.magician.jdbc.sqlbuild.SqlBuilder;
import com.mysql.cj.jdbc.Driver;

import java.util.Date;
import java.util.List;
import java.util.Properties;

public class DemoStart {

    public static void main(String[] args) throws Exception {

//        TransactionManager.beginTraction(TractionLevel.READ_COMMITTED);
//
//        TransactionManager.commit();
//
//        TransactionManager.rollback();

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
        String sql = SqlBuilder.select("test").byPrimaryKey("id").builder();
        DemoDTO param = new DemoDTO();
        param.setId(102);
        DemoDTO demoDTO = JdbcTemplate.create().selectOne(sql, param, DemoDTO.class);

        // 删除主键=107的数据
        String sql2 = SqlBuilder.delete("test").byPrimaryKey("id").builder();
        DemoDTO param2 = new DemoDTO();
        param2.setId(107);
        JdbcTemplate.create().update(sql2, param2);

        // 将DEO保存到数据库
        DemoDTO demo = new DemoDTO();
        demo.setCreateTime(new Date());
        demo.setName("testName");

        String sql3 = SqlBuilder.insert("test").column(DemoDTO.class).builder();
        JdbcTemplate.create().update(sql3, demo);

        // 修改主键=105的数据的 name为testName，createTime为当前时间
        DemoDTO demo2 = new DemoDTO();
        demo2.setCreateTime(new Date());
        demo2.setName("testName");
        demo2.setId(105);

        String sql5 = SqlBuilder.update("test").column(DemoDTO.class).where("id = #{id}").builder();
        JdbcTemplate.create().update(sql5, demo2);

        // 查询name=testName的数据
        DemoDTO demo3 = new DemoDTO();
        demo3.setName("testName");
        List<DemoDTO> demoDTOList = JdbcTemplate.create().selectList("select * from test where name=#{name}", demo3, DemoDTO.class);
    }
}
