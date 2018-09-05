import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//As a... prototype of multi-thread?
public class MyCrawl {
    private static Connection con4;

    //Personal Info
    private static final String usrname = "";
    private static final String pswd = "";
    private static final String command = "jx0404id";
    private static final String[] course_list = {};
    private static final String Useragent = "";

    //Web addresses
    private static final String cas_url = "<CAS url>";
    private static final String cs_url = "<Course_select url>";

    //Oper
    private static final String js_url = "<Oper url>";   // 跨专业选课
    //private static final String js_url = "<Oper Url>"; //公选课

    private static void getConfiguration() {
        try {
            Connection con1 = Jsoup.connect(cas_url);
            Connection.Response rs = con1.execute();
            Document dc = Jsoup.parse(rs.body());
            Element el = dc.getElementById("fm1");
            List<Element> list = el.getAllElements();
            Map<String, String> mp = new HashMap<>();
            for (Element e : list.get(0).getAllElements()) {
                if (e.attr("name").equals("username"))
                    e.attr("value", usrname);
                if (e.attr("name").equals("password"))
                    e.attr("value", pswd);
                if (e.attr("name").length() > 0)
                    mp.put(e.attr("name"), e.attr("value"));
            }
            System.out.println(">>Target configuration read successfully.");

            Connection con2 = Jsoup.connect(cas_url);
            Connection.Response login = con2.ignoreContentType(true).userAgent(Useragent)
                    .method(Connection.Method.POST).data(mp).cookies(rs.cookies()).execute();
            System.out.println(login.parse().text());
            System.out.println(">>Logged in successfully");
            mp.clear();

            Map<String, String> ck = login.cookies();
            Connection.Response sel;
            Connection con3 = Jsoup.connect(cs_url);
            sel = con3.cookies(ck).userAgent(Useragent).method(Connection.Method.GET).execute();

            select(ck);
        } catch (IOException e) {
            System.out.println("IOException");
        }
    }


    private static void select(Map<String, String> cookie) {
        short cnt = 0;
        List<Thread> threads = new ArrayList<>();
        for (String courseCode : course_list) {
            threads.add(new Thread(new Course_Sel(command, course_list[cnt],
                    Useragent, js_url, cookie, con4)));
            cnt++;
        }
        for (Thread thread : threads) {
            thread.start();
            System.out.println(thread.getName());
        }
    }

    public static void main(String[] args) {
        double start = System.currentTimeMillis();
        getConfiguration();
        System.out.println((System.currentTimeMillis() - start) / 1000);
    }
}