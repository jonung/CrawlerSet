package az.im.core;

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

        for(int i = 1; i <= 46; i++) {
            Document document = Jsoup.connect("http://www.importnew.com/all-posts/page/" + i).get();
            Elements ele = document.select(".post.floated-thumb > .post-meta > p > .meta-title");
            System.out.println(ele.html());
            for(Element e : ele) {
                System.out.println(e.text());
            }
        }
    }
}
