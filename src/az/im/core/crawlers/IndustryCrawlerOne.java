package az.im.core.crawlers;

import az.im.core.Crawler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Created by Qianhz on 14-8-12.
 * 制药公司信息
 * url : http://app2.sfda.gov.cn/datasearchp/index1.do?tableId=25&tableName=TABLE25&company=company&tableView=%B9%FA%B2%FA%D2%A9%C6%B7&Id=【id】
 * example : http://app2.sfda.gov.cn/datasearchp/index1.do?tableId=25&tableName=TABLE25&company=company&tableView=%B9%FA%B2%FA%D2%A9%C6%B7&Id=1
 */
public class IndustryCrawlerOne implements Crawler{

    private static String url = "http://app2.sfda.gov.cn/datasearchp/index1.do?tableId=25&tableName=TABLE25&company=company&tableView=%B9%FA%B2%FA%D2%A9%C6%B7&Id=";
    private String industrySQL = "insert into industry(id, type, province, industry_name, industry_legal_person, industry_principal, industry_type, industry_address, production_address, production_scope, certificate_date, expiration_date, comment) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    //数据库相关
    private static Connection conn = null;
    private static PreparedStatement prestmt = null;


    @Override
    public void process() {

        int cnt = 1;

        while(true) {

            parseURL(cnt);
            cnt++;

            if(cnt == 1000) {
                break;
            }
        }
    }

    /**
     * 解析网页
     * @param cnt
     */
    private void parseURL(int cnt) {

        System.out.println("开始解析第 " + cnt + " 条企业信息");

        Document doc = null;

        try {
            doc = Jsoup.connect(url + cnt).get();
        } catch (IOException e) {
            System.out.println("解析第 " + cnt + " 条企业信息失败");
            return;
        }

        Elements elements = doc.select("body > center > table:nth-child(19) > tbody > tr > td > table:nth-child(3) > tbody > tr > td > table");

        if(elements.isEmpty() || elements.text().indexOf("编号") == -1) {
            System.out.println("企业信息不存在");
            return;
        }

        insertIndustryInfo(getIndustryInfo(elements));


        Element product  = getProductInfo(elements);

        System.out.println("解析第 " + cnt + " 条企业信息成功");

    }

    /**
     * 获取企业信息
     * @param ele
     * @return
     */
    private Element getIndustryInfo(Elements ele) {
        return ele.get(0);
    }

    /**
     * 获取产品信息
     * @param ele
     * @return
     */
    private Element getProductInfo(Elements ele) {

        int len = ele.size();
        for(int i = 0; i < len; i++) {
            if(ele.get(i).text().equals("产品列表")) {
                return ele.get(i + 1);
            }
        }
        return null;
    }

    /**
     * 企业信息入库
     * @param e
     */
    private void insertIndustryInfo(Element e) {
        Elements ele = e.select("td");
        for(int i = 0; i < ele.size(); i++) {
            System.out.println(ele.get(i).text());
        }

    }
}
