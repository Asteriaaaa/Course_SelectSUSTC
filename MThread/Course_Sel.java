import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

class Course_Sel implements Runnable {
    private Map<String, String> course;
    private Map<String, String> cookie;
    private String UserAgent;
    private String js_url;
    private Connection con4;

    Course_Sel(String command, String cse, String UserAgent, String js_url,
               Map<String, String> cookie, Connection con4) {
        this.cookie = cookie;
        this.UserAgent = UserAgent;
        this.con4 = con4;
        this.js_url = js_url;
        course = new HashMap<>();
        course.put(command, cse);
    }

    @Override
    public void run() {
        try {
            con4 = Jsoup.connect(js_url);
            Connection.Response sel;
            sel = con4.cookies(cookie).userAgent(UserAgent).method(Connection.Method.GET)
                    .data(course).execute();
            Document doc = sel.parse();
            System.out.println(doc.text());
        } catch (IOException e) {
            System.out.println("Problem occur when sending js requests");
        }
    }
}