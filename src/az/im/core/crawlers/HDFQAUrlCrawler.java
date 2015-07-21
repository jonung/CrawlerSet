package az.im.core.crawlers;

import az.im.core.Crawler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by azurexsyl on 2015/6/14.
 */
public class HDFQAUrlCrawler implements Crawler {

    String urlPrefix = "http://so.haodf.com/index/search?type=flow&p=";
    String urlSuffix = "&kw=%D6%D0%D2%BD";



    @Override
    public void process() {

        int page = 1;
        while(true) {
            Document doc;
            try {
                doc = Jsoup.connect(urlPrefix + page++ + urlSuffix).get();
            } catch (IOException e) {
                e.printStackTrace();
                page--;
                continue;
            }
            Elements contents = doc.select("table.list td");
        }
    }
}
