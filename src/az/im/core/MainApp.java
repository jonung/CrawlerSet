package az.im.core;

import az.im.core.crawlers.IndustryCrawlerOne;

/**
 * Created by Qianhz on 14-8-12.
 * 爬虫主程序
 */
public class MainApp {

    public static void main(String[] args) {
        new IndustryCrawlerOne().process();
    }
}
