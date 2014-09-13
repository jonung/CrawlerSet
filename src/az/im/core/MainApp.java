package az.im.core;

import az.im.core.crawlers.HudongBaikeCrawler;

/**
 * Created by Qianhz on 14-8-12.
 * 爬虫主程序
 */
public class MainApp {

    public static void main(String[] args) {
        //new HudongBaikeSeedCrawler().process();

        System.setProperty("proxySet", "true");
        System.setProperty("http.proxyHost", "10.15.62.235");
        System.setProperty("http.proxyPort", "808");

        final Crawler crawler = new HudongBaikeCrawler();

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

        Thread thread5 = new Thread() {
            @Override
            public void run() {
                crawler.process();
            }
        };

        Thread thread6 = new Thread() {
            @Override
            public void run() {
                crawler.process();
            }
        };

        Thread thread7 = new Thread() {
            @Override
            public void run() {
                crawler.process();
            }
        };

        Thread thread8 = new Thread() {
            @Override
            public void run() {
                crawler.process();
            }
        };

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        //thread5.start();
        //thread6.start();
        //thread7.start();
        //thread8.start();

    }
}
