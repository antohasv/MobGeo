package authorization;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.params.*;;




public class VK_Authorization {

    private static final int client_id = 3187335;
    private static final int scope = 4100;


    private String email;//email or telephone
    private String pass;

    private String token;
    private String user_id;
    private int error;

    public String getToken() {
        return this.token;
    }

    public String getUserID() {
        return this.user_id;
    }
    
    public int getError() {
    	return this.error;
    }

    public VK_Authorization(String email, String pass) {
        try {
            this.email = email;
            this.pass = pass;
            this.error = 0;
            //First Request
            String url = "https://oauth.vk.com/authorize?client_id="
                    + client_id + "&scope="
                    + scope + "&redirect_uri=http://oauth.vk.com/blank.html&display=page&response_type=token";
            String line = "";
            String _origin = null;
            String ip_h = null;
            String to = null;

            HttpResponse res = getReq(url);
            BufferedReader reader = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));

            while((line = reader.readLine()) != null) {
                if (line.trim().contains("<input type=\"hidden\"")) {
                    if(_origin == null)
                        _origin = parseLine(line, "hidden", "_origin");

                    if(ip_h == null)
                        ip_h = parseLine(line, "hidden", "ip_h");

                    if(to == null)
                        to = parseLine(line, "hidden", "to");
                }
            }
            
            //Second Request
            Matcher mt = null;
            String cookie = null;
            int start = 0;
            int end = 0;
            url = "https://login.vk.com/?act=login&soft=1&utf8=1";
            //Post-data
            List<NameValuePair> nameValuePairs  = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("_origin", _origin));
            nameValuePairs.add(new BasicNameValuePair("ip_h", ip_h));
            nameValuePairs.add(new BasicNameValuePair("to", to));
            nameValuePairs.add(new BasicNameValuePair("email", email));
            nameValuePairs.add(new BasicNameValuePair("pass", pass));

            //Execute Second 
            res = postReq(url, null, nameValuePairs);
            

            String redirect = Arrays.toString(res.getHeaders("Location"));
            String loc = "[Location:";
            start = redirect.indexOf(loc) + loc.length();
            end = redirect.length() - 1;
            redirect = redirect.substring(start, end).trim();

            cookie = Arrays.toString(res.getHeaders("Set-Cookie"));

            if(!redirect.contains("_hash")) {
                token = null;
                user_id = null;
                error = 1;
            } else {
                //Third Request
                String h = null;
                String s = null;
                String l = null;
                String p = null;

                mt = Pattern.compile("h=([0-9]{0,3});").matcher(cookie);
                if(mt.find())
                    h = mt.group(1);

                mt = Pattern.compile("s=([0-9]{0,3});").matcher(cookie);
                if(mt.find())
                    s = mt.group(1);

                mt = Pattern.compile("l=([0-9]+);").matcher(cookie);
                if(mt.find())
                    l = mt.group(1);

                mt = Pattern.compile("p=([a-z0-9]+);").matcher(cookie);
                if(mt.find())
                    p = mt.group(1);

                cookie = "h="+ h + "; s=" + s + "; l=" + l + "; p=" + p + ";";
                //Execute Third
                String action = "action=\"";
                res = postReq(redirect, cookie, nameValuePairs);

                Pattern pat = Pattern.compile("\\[Location:[^\\]]{20,}\\]");
                Matcher m = pat.matcher(Arrays.toString(res.getHeaders("Location")));
                if(!m.matches()) {
                    //Первый раз зашел
                    BufferedReader r = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
                    while((line = r.readLine()) != null) {
                        if(line.contains("<form")) {
                            start = line.indexOf(action) + action.length();
                            end = line.indexOf("\">", start);
                            redirect = line.substring(start, end);
                        }
                    }
                } else {
                    redirect = Arrays.toString(res.getHeaders("Location"));
                    loc = "[Location:";
                    start = redirect.indexOf(loc) + loc.length();
                    end = redirect.length() - 1;
                    redirect = redirect.substring(start, end).trim();
                }

                //Fourth request
                res = postReq(redirect, cookie, nameValuePairs);
                redirect = Arrays.toString(res.getHeaders("Location"));

                mt = Pattern.compile("access_token=([a-zA-Z0-9]+)").matcher(redirect);
                if(mt.find())
                    token = mt.group(1);

                mt = Pattern.compile("user_id=([0-9]+)").matcher(redirect);
                if(mt.find())
                    user_id = mt.group(1);

            }
        } catch (Exception e) {
        	error = 2;
        }
    }


    private String parseLine(String line, String m_type, String m_name) {
        String type = "";
        String name = "";
        String val = "";
        int start = 0;
        int end = 0;
        int k = 0;

        if ((k = line.indexOf("type")) != -1)
        {
            start = line.indexOf('\"', k) + 1;
            end = line.indexOf("\"", start);
            type = line.substring(start, end);

            if (!type.equals(m_type))
                return null;

            if ((k = line.indexOf("name")) != -1)
            {
                start = line.indexOf('\"', k) + 1;
                end = line.indexOf("\"", start);
                name = line.substring(start, end);

                if (!name.equals(m_name))
                    return null;

                if ((k = line.indexOf("value")) != -1)
                {
                    start = line.indexOf('\"', k) + 1;
                    end = line.indexOf("\"", start);
                    val = line.substring(start, end);
                    return val;
                }
            }
        }
        return null;
    }

    private HttpResponse getReq(String url) throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, false);
        HttpGet req = new HttpGet(url);
        req.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        HttpResponse res = httpclient.execute(req);
        return res;
    }

    private HttpResponse postReq(String url, String cookie, List<NameValuePair> list_data) throws IOException {
        if(cookie == null) {
            HttpClient httpclient = new DefaultHttpClient();
            httpclient.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, false);
            HttpPost post = new HttpPost(url);
            post.setEntity(new UrlEncodedFormEntity(list_data));
            HttpResponse res = httpclient.execute(post);
            return res;
        } else {
            HttpClient httpclient = new DefaultHttpClient();
            httpclient.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, false);
            HttpPost post = new HttpPost(url);
            post.setHeader("Cookie:", cookie);
            post.setEntity(new UrlEncodedFormEntity(list_data));
            HttpResponse res = httpclient.execute(post);
            return res;
        }
    }
}
