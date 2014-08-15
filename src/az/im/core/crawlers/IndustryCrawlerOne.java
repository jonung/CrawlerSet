package az.im.core.crawlers;

import az.im.core.Crawler;
import az.im.dao.MySQLUtils;
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

    private String url = "http://app2.sfda.gov.cn/datasearchp/index1.do?tableId=25&tableName=TABLE25&company=company&tableView=%B9%FA%B2%FA%D2%A9%C6%B7&Id=";
    private String industrySQL = "insert into industry(id, type, province, industry_name, industry_legal_person, industry_principal, industry_type, industry_address, production_address, product_scope, certificate_date, expiration_date, comment) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private String productSQL = "insert into product(no, approval_number, product_name, english_name, commodity_name, dosage_form, specification, production_industry, production_address, product_type, original_approval_number, approval_date, drug_standard_code, drug_standard_code_comment) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private PreparedStatement prestmtIndustry = null;
    private PreparedStatement prestmtProduct = null;

    private int cnt = 1;
    private String no = "";

    @Override
    public void process() {

        /* 初始化 */
        prestmtIndustry = MySQLUtils.getPreparedStatement("jdbc:mysql://localhost/cfda", "root", "admin", industrySQL);
        prestmtProduct = MySQLUtils.getPreparedStatement("jdbc:mysql://localhost/cfda", "root", "admin", productSQL);

        while(true) {

                System.out.println(Thread.currentThread().getName() + " is parsing " + cnt);
                parseURL(url + cnt, cnt);
                cnt++;

            if(cnt == 10000000) {
                System.out.println("Finished~");
                break;
            }
        }
    }

    /**
     * 解析网页
     * @param industryURL
     */
    private void parseURL(String industryURL, int count) {

        System.out.println("开始解析第 " + count + " 条企业信息");

        Document doc = null;

        try {
            doc = Jsoup.connect(industryURL).get();
        } catch (IOException e) {
            System.out.println("解析第 " + count + " 条企业信息失败");
            return;
        }

        Elements elements = doc.select("body > center > table:nth-child(19) > tbody > tr > td > table:nth-child(3) > tbody > tr > td > table");

        if(elements.isEmpty() || elements.text().indexOf("编号") == -1) {
            System.out.println("企业信息不存在");
            return;
        }

        if(!insertIndustryInfo(getIndustryInfo(elements))){
            System.out.println("插入企业信息失败");
            return;
        }


        Element product  = getProductInfo(elements);
        Elements links = product.select("a");
        for(int i = 0; i < links.size(); i++) {
            parseProductURL("http://app2.sfda.gov.cn/" + links.get(i).attr("href"), count);
        }

        System.out.println("解析第 " + count + " 条企业信息成功");

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
    private boolean insertIndustryInfo(Element e) {
        Elements ele = e.select("td");

        no = ele.get(0).text();

        try {
        for(int i = 0; i < ele.size(); i++) {
                prestmtIndustry.setString(i + 1, ele.get(i).text());
        }
            prestmtIndustry.execute();
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
        return true;
    }

    private void parseProductURL(String productURL, int count) {

        //System.out.println("    开始解析第 " + count + " 条产品信息");
        Document doc = null;

        try {
            doc = Jsoup.connect(productURL).get();
        } catch (IOException e) {
            //System.out.println("解析第 " + count + " 条产品信息失败");
            return;
        }

        Elements elements = doc.select("body > center > table:nth-child(19) > tbody > tr > td > table:nth-child(2) > tbody > tr > td > table");

        if(!insertProductInfo(elements, count)){
            //System.out.println("    插入产品信息失败");
            return;
        }

        //System.out.println("    解析第 " + count + " 条产品信息成功");
    }

    /**
     * 产品信息入库
     * @param e
     */
    private boolean insertProductInfo(Elements e, int count) {

        Elements ele = e.select("td");

        try {
            prestmtProduct.setString(1, "" + count);
            for(int i = 0; i < ele.size() - 1; i++) {
                prestmtProduct.setString(i + 2, ele.get(i).text());
                //System.out.println(ele.get(i).text());
            }
            prestmtProduct.execute();
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
        return true;
    }
}
