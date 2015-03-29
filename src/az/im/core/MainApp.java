package az.im.core;

import az.im.core.crawlers.HudongBaikeCrawler;
import az.im.core.crawlers.IFengAskCrawler;
import az.im.core.crawlers.TCMKDCrawler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by Qianhz on 14-8-12.
 * 爬虫主程序
 */
public class MainApp {

    public static void main(String[] args) throws IOException {

        new TCMKDCrawler().process();
    }
}
