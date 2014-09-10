package az.im.core.crawlers;

import az.im.core.Crawler;
import az.im.dao.MySQLUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.sql.PreparedStatement;
import java.util.*;

/**
 * Created by Qianhz on 14-9-10.
 */
public class HudongBaikeSeedCrawler implements Crawler {

    private String insertClassTree = "insert into baike_class_tree(class, super_class, sub_class, relative_class) values(?, ?, ?, ?)";
    private String insertClass = "insert into baike_class(class) values(?)";

    private String baseUrl = "http://fenlei.baike.com/";

    private Stack<String> remainWordStack = new Stack<String>();
    private Set<String> allWordSet = new HashSet<String>();

    @Override
    public void process() {

        // 数据库初始化
        PreparedStatement insertClassTreePstmt = MySQLUtils.getPreparedStatement("jdbc:mysql://localhost/hudong_baike", "root", "admin", insertClassTree);
        PreparedStatement insertClassPstmt = MySQLUtils.getPreparedStatement("jdbc:mysql://localhost/hudong_baike", "root", "admin", insertClass);


        Document doc = null;
        try {
            doc = Jsoup.connect(baseUrl).get();
        }catch(Exception e) {
            System.out.println("Init Failed!");
        }

        // 初始化关键词列表
        Elements ele = doc.select("#f-a dl a");
        for(Element e : ele) {
            remainWordStack.push(e.text().trim());
        }

        int count = 1;
        String word = null;
        String url = null;
        while(!remainWordStack.isEmpty()) {

            int times = 1;
            String c1 = "";
            String c2 = "";
            String c3 = "";

            // 取出一个分类
            word = remainWordStack.pop();
            url = baseUrl + word;

            // 如果在以爬名单中，则跳过步骤，否则加入已爬名单
            if(allWordSet.contains(word)) {
                continue;
            }else{
                allWordSet.add(word);
            }

            // 解析URL
            System.out.println("Parsing " + count++ + " : " + url);

            while(times == 1) {
            try {
                doc = Jsoup.connect(url).get();
                if(doc != null) {
                    times++;
                }
            }catch(Exception e) {
                System.out.println("Parse Failed!");
            }
            }


            Elements eleh = doc.select("div.sort h3");
            Elements elep = doc.select("div.sort p");
            for(int i = 0; i < eleh.size(); i++) {
                if(eleh.get(i).text().equals("上一级微百科")){
                    Elements elea = elep.get(i).select("a");
                    c1 = elea.text().trim();
                    for(Element e : elea) {
                        remainWordStack.push(e.text().trim());
                    }
                } else if(eleh.get(i).text().equals("下一级微百科")) {
                    Elements elea = elep.get(i).select("a");
                    c2 = elea.text().trim();
                    for(Element e : elea) {
                        remainWordStack.push(e.text().trim());
                    }
                } else {
                    Elements elea = elep.get(i).select("a");
                    c3 = elea.text().trim();
                    for(Element e : elea) {
                        remainWordStack.push(e.text().trim());
                    }
                }

            }

            // 插入分类树
            try {
                insertClassTreePstmt.setString(1, word);
                insertClassTreePstmt.setString(2, c1);
                insertClassTreePstmt.setString(3, c2);
                insertClassTreePstmt.setString(4, c3);
                insertClassTreePstmt.execute();
            }catch (Exception e) {
                System.out.println("插入分类树失败！");
                e.printStackTrace();
            }

            // 插入分类
            try {
                insertClassPstmt.setString(1, word);
                insertClassPstmt.execute();
            }catch (Exception e) {
                System.out.println("插入分类失败！");
                e.printStackTrace();
            }
        }
    }
}
