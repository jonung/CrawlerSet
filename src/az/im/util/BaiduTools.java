package az.im.util;

import az.im.dao.MySQLUtils;

import java.io.*;
import java.sql.PreparedStatement;

/**
 * Created by Qianhz on 14-8-25.
 */
public class BaiduTools {


    public static void main(String[] args) {
        ReadFile();
    }

    public static void ReadFile() {

        String sql1 = "insert into baike_dic(id, title, category, retitle, url) values(?, ?, ?, ?, ?)";
        PreparedStatement prestmt1 = MySQLUtils.getPreparedStatement("jdbc:mysql://localhost/baidu_baike", "root", "admin", sql1);
        BufferedReader bufferedReader = null;
        String line = null;
        String newline = null;
        String name = "";

        try {

            File file = new File("E:\\IdeaProject\\CrawlerSet\\resources\\dic\\baike.txt");

            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "GBK"));
            line = bufferedReader.readLine();

            int cnt = 0;
            while(null != line) {

                newline = line.substring(line.indexOf("=") + 1, line.length());

                if(cnt == 0) {

                } else if (cnt == 1) {
                    prestmt1.setString(1, newline);
                } else if (cnt == 2) {
                    prestmt1.setString(2, newline);
                    name = newline;
                } else if (cnt == 3) {
                    prestmt1.setString(3, newline);
                } else if (cnt == 4) {
                    prestmt1.setString(4, newline);
                } else {
                    prestmt1.setString(5, newline);
                    System.out.println("insert " + name);
                    try {
                        prestmt1.execute();
                    } catch(Exception e) {
                        System.out.println("insert" + name + "failed");
                        e.printStackTrace();
                    }
                    cnt = -1;
                }

                cnt++;

                //System.out.println(newline);
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
