import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated   //Although this one may be tidier.
public class MyCrawl_legacy {
    private static final String Useragent = "";
    private static final String cas_url = "";
    private static final String cs_url = "";
    private static final String js_url = "";

    static String[] course_list = {};

    public static void login(String usrname, String pswd) {
        try {
            Connection con = Jsoup.connect(cas_url);
            Connection.Response rs = con.execute();
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
            //-----Get configuration over------//

            Connection con2 = Jsoup.connect(cas_url);
            Connection.Response login = con2.ignoreContentType(true).userAgent(Useragent)
                    .method(Connection.Method.POST).data(mp).cookies(rs.cookies()).execute();
            System.out.println(">>Logged in successfully");
            mp.clear();
            //-------Cas login over-------------//

            Map<String, String> ck = login.cookies();
            Map<String, String> course = new HashMap<>();
            Connection con3 = Jsoup.connect(cs_url);
            Connection.Response sel;
            sel = con3.cookies(ck).userAgent(Useragent).method(Connection.Method.GET).execute();
            for (String str : course_list) {
                course.put("jx0404id", str);
                Connection con4 = Jsoup.connect(js_url);
                sel = con4.cookies(ck).userAgent(Useragent).method(Connection.Method.GET)
                        .data(course).execute();

                Document doc = sel.parse();
                System.out.println(doc.text());
            }
        } catch (IOException e) {
            System.out.println("IOException when getting response.");
        }
    }

    public static void main(String[] args) {
        double start = System.currentTimeMillis();
        login("", "");
        System.out.println((System.currentTimeMillis() - start) / 1000);
    }

}
