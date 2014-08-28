package az.im.core.crawlers;

import az.im.core.Crawler;
import az.im.dao.MySQLUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by Qianhz on 14-8-28.
 * 百度百科爬虫
 */
public class BaiduBaikeCrawler implements Crawler {

    private String querySQL = "select id, title, category, retitle, url from baike_dic where rec_id = ?";
    private String insertSQL = "insert into baike_pages(id, title, category, retitle, url, page) values(?, ?, ?, ?, ?, ?)";

    /* 上限是 5011167 */
    private static int cnt = 0;

    @Override
    public void process() {
        /* 初始化 */
        PreparedStatement prestmtQuery = null;
        PreparedStatement prestmtInsert = null;

        doCrawler(prestmtQuery, prestmtInsert);
    }

    private void doCrawler(PreparedStatement prestmtQuery, PreparedStatement prestmtInsert) {
        prestmtQuery = MySQLUtils.getPreparedStatement("jdbc:mysql://localhost/baidu_baike", "root", "admin", querySQL);
        prestmtInsert = MySQLUtils.getPreparedStatement("jdbc:mysql://localhost/baidu_baike", "root", "admin", insertSQL);

        boolean tag = true;
        int count = 1;
        int cnnt = 0;

        while(cnt <= 5011167) {

            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(tag == true){
                cnnt = getCount();
            }
            System.out.println(Thread.currentThread().getName() + " is getting " + cnnt + " count:" + count);

            ResultSet rs = null;
            try{
                prestmtQuery.setInt(1, cnnt);
                rs = prestmtQuery.executeQuery();
            } catch(Exception e) {
                System.out.println("get rec failed!");
                continue;
            }

            String id = "";
            String title = "";
            String category = "";
            String retitle = "";
            String url = "";

            try {
                while(rs.next()){
                    id = rs.getString(1);
                    title = rs.getString(2);
                    category = rs.getString(3);
                    retitle = rs.getString(4);
                    url = rs.getString(5);
                }
            }catch(Exception e) {
                System.out.println("get content failed");
                if(count <= 3) {
                    count++;
                    tag = false;
                }else {
                    count = 0;
                    tag = true;
                }

                continue;
            }

            Document doc = null;
            Elements ele = null;


            try {
                doc = Jsoup.connect(url).get();
            }catch(Exception e) {
                System.out.println("parse pages failed");
                continue;
            }
            try {
                ele = doc.select("#main-wrap > div");
            }   catch(Exception e) {

            }

            try{
                prestmtInsert.setString(1, id);
                prestmtInsert.setString(2, title);
                prestmtInsert.setString(3, category);
                prestmtInsert.setString(4, retitle);
                prestmtInsert.setString(5, url);
                prestmtInsert.setString(6, ele.text());
                prestmtInsert.execute();
                count = 0;
                tag = true;
            }catch(Exception e) {
                e.printStackTrace();
                System.out.println("insert failed");
            }

        }
    }

    private synchronized static int getCount() {
        return ++cnt;
    }
}
