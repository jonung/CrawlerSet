package az.im.comp;

import az.im.dao.MySQLUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Qianhz on 14-10-19.
 */
public class Industry {

    private static String industrySQL = "select province, industry_name from industry";
    private static String productSQL = "select no from product where product_type = '中药' and production_industry = ? limit 1";


    private static PreparedStatement prestmtIndustry = null;
    private static PreparedStatement prestmtProduct = null;

    public static void main(String[] args) throws Exception{

        Map<String, Integer> m_count = new HashMap<String, Integer>();
        Map<String, Integer> m_count_t = new HashMap<String, Integer>();

        String pro = "";
        String ind = "";
        int count = 1;
        prestmtIndustry = MySQLUtils.getPreparedStatement("jdbc:mysql://localhost/cfda", "root", "admin", industrySQL);
        prestmtProduct = MySQLUtils.getPreparedStatement("jdbc:mysql://localhost/cfda", "root", "admin", productSQL);

        ResultSet rs = prestmtIndustry.executeQuery();

        /*while(rs.next()) {
            pro = rs.getString(1);
            ind = rs.getString(2);
            //System.out.println(count++ + " " + pro + "  " + ind);
            if(m_count.containsKey(pro)) {
                Integer c = m_count.get(pro);
                c++;
                m_count.put(pro, c);
            }else{
                m_count.put(pro, 1);
            }
        }

        for(String s :m_count.keySet()) {
            System.out.println(s + "\t" + m_count.get(s));
        }*/

        while(rs.next()) {
            pro = rs.getString(1);
            ind = rs.getString(2);
            prestmtProduct.setString(1, ind);
            ResultSet rss = prestmtProduct.executeQuery();
            if(rss.next()) {
                if(m_count.containsKey(pro)) {
                    Integer c = m_count.get(pro);
                    System.out.println(pro + "  " + c);
                    c++;
                    m_count.put(pro, c);
                }else{
                    m_count.put(pro, 1);
                }
            }

        }

        System.out.println("RESULT!!!");
        for(String s :m_count.keySet()) {
            System.out.println(s + "\t" + m_count.get(s));
        }
    }
}
