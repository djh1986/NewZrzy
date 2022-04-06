package com.zrzyyzt.runtimeviewer.DBO;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class DBUtils {

    private static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver"); //加载驱动
            String ip = "61.178.245.189";
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + ip + ":9095/" + "zrzysjb",
                    "zrzyyd", "sa123jkl");
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return conn;
    }

    public static int getCount() {
        Connection connection = getConnection();
        HashMap<String, String> map = new HashMap<>();
        int count = 0;
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet=statement.executeQuery("select * from zrzy_user");
            if(resultSet!=null) {
                int cut=resultSet.getMetaData().getColumnCount();
                Log.e("数据库连接测试:",String.valueOf(cut));
                //resultSet.next();
            }

        } catch (Exception e) {

        }
        return count;
    }
}
