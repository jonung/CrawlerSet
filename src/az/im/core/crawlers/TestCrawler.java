package az.im.core.crawlers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Created by azurexsyl on 2015/6/16.
 */
public class TestCrawler implements Runnable{

    // 计数
    private static int count = 0;

    String urlPrefix = "http://so.haodf.com/index/search?type=flow&p=";
    String urlSuffix = "&kw=%D6%D0%D2%BD";

    @Override
    public void run() {
        while(true) {
            int page = getId();
            System.out.println(Thread.currentThread() + " is parsing page " + page);
            Document doc;
            try {
                doc = Jsoup.connect(urlPrefix + page  + urlSuffix).timeout(10000).get();
                // 成功
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(Thread.currentThread() + " terminated at page " + page);
                break;
            }
        }
    }


    private synchronized int getId() {
        return ++count;
    }
}
