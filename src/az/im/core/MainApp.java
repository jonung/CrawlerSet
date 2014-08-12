package az.im.core;

import az.im.core.crawlers.CrawlerOne;

/**
 * Created by Qianhz on 14-8-12.
 * 爬虫主程序
 */
public class MainApp {

    public static void main(String[] args) {
        new CrawlerOne().process();
    }
}
