package az.im.core.crawlers;

import az.im.core.Crawler;
import az.im.dao.MySQLUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Qianhz on 14-9-12.
 */
public class HudongBaikeCrawler implements Crawler {

    private String insertSQL = "insert into baike_pages(class, title, label, content) values(?, ?, ?, ?)";
    private String querySQL = "select class from baike_class where rec_id = ?";
    private String updateSQL = "update baike_class set tag = ? where rec_id = ?";

    // 上限为 34737
    private static int cnt = 0;

    @Override
    public void process() {

        PreparedStatement prestmtInsert = null;
        PreparedStatement prestmtQuery = null;
        PreparedStatement prestmtUpdate = null;

        doCrawler(prestmtInsert, prestmtQuery, prestmtUpdate);
    }

    private void doCrawler(PreparedStatement pstmtI, PreparedStatement pstmtQ, PreparedStatement pstmtU) {

        // 初始化
        pstmtI = MySQLUtils.getPreparedStatement("jdbc:mysql://10.15.62.235/hudong_baike", "root", "admin", insertSQL);
        pstmtQ = MySQLUtils.getPreparedStatement("jdbc:mysql://10.15.62.235/hudong_baike", "root", "admin", querySQL);
        pstmtU = MySQLUtils.getPreparedStatement("jdbc:mysql://10.15.62.235/hudong_baike", "root", "admin", updateSQL);

        int count = 0;

        while (count <= 34737) {

            // 获取全局 rec_id
            count = getCount();
            if (count > 34737) {
                return;
            }

            ResultSet rs = null;
            String key = null;


            // 这个key是必定可以取到的!
            boolean tag1 = true;
            while (tag1) {
                try {
                    pstmtQ.setInt(1, count);
                    rs = pstmtQ.executeQuery();
                    while (rs.next()) {
                        key = rs.getString(1);
                    }
                    tag1 = false;
                } catch (Exception e) {
                }
            }

            System.out.println(Thread.currentThread().getName() + " starts to parse " + count + " : " + key);

            // 构造list
            String url = "http://fenlei.baike.com/" + key + "/list/";

            Document doc = null;
            Elements eles = null;

            List<String> list = new ArrayList<String>();


            // 为了防止解析失败， 最多进行10次
            int tag2 = 1;

            while (tag2 < 10) {

                if(tag2 != 1) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                }

                System.out.println(Thread.currentThread().getName() + " try to get [" + key + "] lists. try " + tag2 + " times");

                try {
                    doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.120 Safari/537.36").referrer("http://www.baike.com/").timeout(10000).get();
                    // 取到后置位
                    if (doc != null) {
                        tag2 = 10;
                    }
                } catch (Exception e) {
                    tag2++;
                    // 如果重试失败，则进行下一轮循环
                    if(tag2 == 10) {
                        break;
                    }
                }
            }

            if(doc == null ) {
                continue;
            }

            eles = doc.select("dl>dd>a");

            if (eles == null) {
                // 如果获取元素为空，则进行下一轮循环
                continue;
            }

            for (Element e : eles) {
                list.add(e.text().trim());
            }

            // 开始抓取
            for (String str : list) {

                int time = 0;
                String targetURL = "http://www.baike.com/wiki/" + str;

                Document targetDoc = null;
                Elements targetEle = null;

                while (time < 10) {

                    if(time != 0) {
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println(Thread.currentThread().getName() + " is parsing : " + key + " : " + str);

                    try {
                        targetDoc = Jsoup.connect(targetURL).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.120 Safari/537.36").referrer("http://www.baike.com/").timeout(10000).get();
                        Thread.sleep(100);

                        targetEle = targetDoc.select("div.l.w-640");
                        String content = targetEle.text();
                        String tag = "";
                        targetEle = targetDoc.select("#openCatp a");
                        for (Element eee : targetEle) {
                            tag += eee.text() + ":";
                        }

                        pstmtI.setString(1, key);
                        pstmtI.setString(2, str);
                        pstmtI.setString(3, tag);
                        pstmtI.setString(4, content);
                        pstmtI.execute();

                        // 如果全部操作完成，则可以退出
                        if (targetDoc != null) {
                            time = 10;
                        }
                    } catch (Exception e) {
                        //e.printStackTrace();
                        ++time;
                        // 如果重试失败，则进行下一轮循环
                        if(time == 10) {
                            break;
                        }
                    }
                }

            }

            // 置位
            try{
                pstmtU.setInt(1, 1);
                pstmtU.setInt(2, count);
                pstmtU.execute();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized static int getCount() {
        return ++cnt;
    }
}
