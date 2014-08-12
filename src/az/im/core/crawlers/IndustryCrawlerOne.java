package az.im.core.crawlers;

import az.im.core.Crawler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by Qianhz on 14-8-12.
 * 制药公司信息
 * url : http://app2.sfda.gov.cn/datasearchp/index1.do?tableId=25&tableName=TABLE25&company=company&tableView=%B9%FA%B2%FA%D2%A9%C6%B7&Id=【id】
 * example : http://app2.sfda.gov.cn/datasearchp/index1.do?tableId=25&tableName=TABLE25&company=company&tableView=%B9%FA%B2%FA%D2%A9%C6%B7&Id=1
 */
public class IndustryCrawlerOne implements Crawler{

    private static String url = "http://app2.sfda.gov.cn/datasearchp/index1.do?tableId=25&tableName=TABLE25&company=company&tableView=%B9%FA%B2%FA%D2%A9%C6%B7&Id=";

    @Override
    public void process() {

        int cnt = 1;

        while(true) {

            parseIndustry(cnt);
            cnt++;

            if(cnt == 1000) {
                break;
            }
        }
    }

    private static void parseIndustry(int cnt) {

        System.out.println("开始解析第 " + cnt + " 条企业信息");

        Document doc = null;

        try {
            doc = Jsoup.connect(url + cnt).get();
        } catch (IOException e) {
            System.out.println("<-- 解析第 " + cnt + " 条企业信息失败");
        }

        Elements elements = doc.select(".msgtab");

        if(elements.isEmpty() || elements.text().indexOf("企业名称") == -1) {
            System.out.println("企业信息不存在");
            return ;
        }

        Elements industry = doc.select("body>center>table:nth-child(19)>tbody>tr>td>table:nth-child(3)>tbody>tr>td>table:nth-child(1)");
        System.out.println(industry.text());

        /*Elements drugs = doc.select("body>center>table:nth-child(19)>tbody>tr>td>table:nth-child(3)>tbody>tr>td>table:nth-child(7)");
        System.out.println(drugs.text());*/

        System.out.println("解析第 " + cnt + " 条企业信息成功 -->");

    }
}
