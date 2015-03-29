package az.im.core.crawlers;

import az.im.core.Crawler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;

/**
 * Created by Qianhz on 2015/1/6.
 */
public class TCMKDCrawler implements Crawler {

    private static int cnt = 0;

    private String basicUrl = "http://www.tcmkd.com/herbnet/entity.php?tab=basic&db_name=herbnet&id=";
    private String detailUrl = "http://www.tcmkd.com/herbnet/entity.php?tab=detail&db_name=herbnet&id=";
    private String relationUrl = "http://www.tcmkd.com/herbnet/entity.php?tab=relation&db_name=herbnet&id=";

    private BufferedReader reader;
    private BufferedWriter writer;
    private StringBuffer buffer;

    @Override
    public void process() {

        while(true) {

            int ccnt = getCount();
            System.out.println(Thread.currentThread().getName() + " is parsing " + ccnt);
            parseURL(ccnt);

            if(ccnt >= 9999) {
                return;
            }
        }
    }

    void parseURL(int cnt) {

        Document basicDoc = null;
        Document detailDoc = null;
        Document relationDoc = null;

        try {
            basicDoc = Jsoup.connect(basicUrl + cnt).get();
            detailDoc = Jsoup.connect(detailUrl + cnt).get();
            relationDoc = Jsoup.connect(relationUrl + cnt).get();
        } catch (IOException e) {
            System.out.println(Thread.currentThread().getName() + " parse basic " + cnt + " failed");
            return;
        }

        // 获取名称和描述信息
        String title = basicDoc.select("body>div:nth-child(3)>h1").text().trim();
        String description = basicDoc.select("body>div:nth-child(3)>div").text().trim();
        String detail = basicDoc.select("body>div:nth-child(3)>div.well").text().trim();

        int index = title.indexOf("(");
        if(index == -1) {
            return;
        }
        String name = title.substring(0, index).trim();
        String type = title.substring(index + 1, title.indexOf(")")).trim();
        buffer = new StringBuffer();
        try {
                String types = nameSwitch(type);
            if(types.equals("ops")){
                return;
            }
                buffer.append("名称:" + name).append("\r\n");
                buffer.append("简介:" + detail).append("\r\n");
                Elements ele = basicDoc.select("#title tr");
                if(ele.size() != 0) {
                    buffer.append("#基本信息").append("\r\n");
                    for(Element e : ele) {
                        String key = e.select("td").get(0).text();
                        String value = e.select("td").get(1).text();
                        buffer.append(key + value).append("\r\n");
                    }
                }
                ele = detailDoc.select(".container > .panel.panel-default");
                if(ele.size() != 0) {
                    buffer.append("#详细信息").append("\r\n");
                    for(Element e : ele) {
                        if(e.select(".panel-heading").size() == 0) {
                            continue;
                        }else{
                            String key = e.select(".panel-heading > strong").get(0).text().trim();
                            String value = e.select(".panel-body").get(0).html().trim();
                            buffer.append(key + ":").append("\r\n").append(value).append("\r\n");
                        }
                    }
                }
                ele = relationDoc.select(".container > .panel.panel-default");
                if(ele.size() != 0) {
                    buffer.append("#相关关系").append("\r\n");
                    for(Element e : ele) {
                        String values = "";
                        String key = e.select(".panel-heading > h3").get(0).text().trim();
                        if(key.equals("被分析")) {
                            Elements eles = e.select("tbody tr");
                            for(Element eee : eles) {
                                values += eee.text() + "\r\n";
                            }
                        }else {
                            Elements eles = e.select("ul > li > a");
                            for(Element es : eles) {
                                values += es.text().trim() + "\r\n";
                            }
                        }
                        String from = e.select(".panel-footer").get(0).text().trim();
                        buffer.append(key + ":").append("\r\n").append(values).append("\r\n").append(from).append("\r\n");
                    }

                }
                writer = new BufferedWriter(new FileWriter(new File("./resources/tcmkd/" + types + "/" + cnt + "-" + name + ".txt")));
                writer.write(buffer.toString());
                writer.close();

                try {
                    Thread.sleep(1000 * 3);
                }catch (Exception e) {
                    e.printStackTrace();
                }

        }catch (Exception e) {
            e.printStackTrace();
        }


    }

    private synchronized static int getCount() {
        return ++cnt;
    }

    private static String nameSwitch(String str) {
        if(str.equals("疾病")){
            return "jb";
        }else if(str.equals("中药")){
            return "zy";
        }else if(str.equals("方剂")){
            return "fj";
        }else if(str.equals("药理作用")){
            return "ylzy";
        }else if(str.equals("中药化学成分")){
            return "zyhxcf";
        }else if(str.equals("化学实验方法")){
            return "hxsyff";
        }else {
            System.out.println("OVER");
            return "ops";
        }
    }
}
