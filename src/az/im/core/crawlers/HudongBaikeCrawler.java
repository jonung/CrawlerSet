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

    private String del1 = "飞信 0";
    private String del2 = "互动百科的词条（含所附图片）系由网友上传，如果涉嫌侵权，请与客服联系，我们将按照法律之相关规定及时进行处理。";

    // 上限为 34737
    private static int cnt = 0;

    @Override
    public void process() {

        PreparedStatement prestmtInsert = null;
        PreparedStatement prestmtQuery = null;
        doCrawler(prestmtInsert, prestmtQuery);
    }

    private void doCrawler(PreparedStatement pstmtI, PreparedStatement pstmtQ) {

        // 初始化
        pstmtI = MySQLUtils.getPreparedStatement("jdbc:mysql://10.15.62.235/hudong_baike", "root", "admin", insertSQL);
        pstmtQ = MySQLUtils.getPreparedStatement("jdbc:mysql://10.15.62.235/hudong_baike", "root", "admin", querySQL);

        int count = 0;

        while (count <= 34737) {

            // 获取全局 rec_id
            count = getCount();
            if (count > 34737) {
                return;
            }

            ResultSet rs = null;
            String key = null;

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
                    System.out.println("Get key failed, try again!");
                }
            }

            System.out.println(Thread.currentThread().getName() + " starts to parse " + count + " : " + key);

            String url = "http://fenlei.baike.com/" + key + "/list/";

            Document doc = null;
            Elements eles = null;

            List<String> list = new ArrayList<String>();

            int tag2 = 0;
            while (tag2 < 10) {

                try {
                    doc = Jsoup.connect(url).get();
                    if (doc != null) {
                        tag2 = 10;
                    }
                } catch (Exception e) {
                }
            }

            eles = doc.select("dl > dd > a");

            if (eles == null) {
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
                    System.out.println(Thread.currentThread().getName() + " is parsing : " + key + " : " + str);
                    try {
                        targetDoc = Jsoup.connect(targetURL).get();

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

                        if (targetDoc != null) {
                            time = 10;
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    private synchronized static int getCount() {
        return ++cnt;
    }
}
