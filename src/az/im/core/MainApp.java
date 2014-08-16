package az.im.core;

import az.im.core.crawlers.IndustryCrawlerOne;

/**
 * Created by Qianhz on 14-8-12.
 * 爬虫主程序
 */
public class MainApp {

    public static void main(String[] args) {

        final Crawler crawler = new IndustryCrawlerOne();


        Thread thread1 = new Thread() {
            @Override
            public void run() {
                crawler.process();
            }
        };

        Thread thread2 = new Thread() {
            @Override
            public void run() {
                crawler.process();
            }
        };

        Thread thread3 = new Thread() {
            @Override
            public void run() {
                crawler.process();
            }
        };

        Thread thread4 = new Thread() {
            @Override
            public void run() {
                crawler.process();
            }
        };

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();

    }
}
