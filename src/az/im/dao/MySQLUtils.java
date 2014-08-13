package az.im.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

/**
 * Created by Qianhz on 14-8-13.
 */
public class MySQLUtils {

    public static PreparedStatement getPreparedStatement(String db, String user, String pwd, String sql) {

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(db, user, pwd);
            return conn.prepareStatement(sql);
        } catch (Exception e) {
            System.out.println("数据库初始化失败");
            return null;
        }
    }
}
