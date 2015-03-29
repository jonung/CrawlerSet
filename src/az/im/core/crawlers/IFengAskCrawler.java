package az.im.core.crawlers;

import az.im.core.Crawler;
import az.im.dao.MySQLUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.PreparedStatement;

/**
 * Created by Qianhz on 2015/3/15.
 * 凤凰中医问答数据
 */
public class IFengAskCrawler implements Crawler {

    private String sql = "insert into ifengaskf(title, question, answer, class, subclass) values(?, ?, ?, ?, ?)";

    private String baseUrl = "http://zhongyi.ifeng.com/";
    private String url;
    private PreparedStatement pstmt;
    public IFengAskCrawler(String url) {
        this.url = url;
        pstmt = MySQLUtils.getPreparedStatement("jdbc:mysql://10.15.62.235/ask", "root", "admin", sql);
    }

    @Override
    public void process() {
        int n = 1;
        while(true){
            try {
                Thread.sleep(5000);
            }catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("抓取第" + n + "页");
            try {
                Document doc = Jsoup.connect(url + n + ".html").timeout(5000).get();

                Elements elements = doc.select(".ask_list > p > a");
                if(elements.size() == 0) {
                    return;
                }
                for(Element e : elements) {
                    String askUrl = baseUrl + e.attr("href");

                    Document qas = Jsoup.connect(askUrl).timeout(5000).get();

                    // 开始获取具体问答数据
                    Element classes = qas.select("#ak-subnav > h1").first();
                    String classname = classes.select("a").get(2).text();
                    // 综合时要注释下列语句
                    //String subclassname = classes.select("a").get(3).text();

                    //System.out.println(classname + " " + subclassname);

                    String title = qas.select("#content > div.ak-wall > div.ak-wall-left > div.ak-wall05 > div:nth-child(1) > div.ak-pdg1 > div.ak-picright > div.ak-title3 > h1").text();
                    String question = qas.select("#content > div.ak-wall > div.ak-wall-left > div.ak-wall05 > div:nth-child(1) > div.ak-pdg1 > div.ak-picright > p").text();
                    //String question2 = qas.select("#div_questionAppend").text();
                    //String question = question1 + "\n" + question2;
                    Element qae = qas.select("#div_questionBestAnswer > div.ak-pdg1 > div.ak-picright > p").first();
                    String answer = "";
                    if(qae != null) {
                        answer = qae.text();
                    }

                    //System.out.println("title : " + title);
                    //System.out.println("question : " + question);
                    //System.out.println("answer : " + answer);
                    /*if(!answer.equals("")) {
                        System.out.println("good");
                    }*/

                        pstmt.setString(1, title);
                        pstmt.setString(2, question);
                        pstmt.setString(3, answer);
                        pstmt.setString(4, classname);
                        pstmt.setString(5, "");
                        pstmt.execute();
                }

            }catch (Exception e) {
                e.printStackTrace();
                return;
            }



            // 全部完成
            n++;
        }
    }
}
