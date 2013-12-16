package requests;


import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;

public class PostGetRequests {
	
	public static HttpResponse getReq(String url) throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, false);
        HttpGet req = new HttpGet(url);
        HttpResponse res = httpclient.execute(req);
        return res;
    }

    public static HttpResponse postReq(String url, String cookie, List<NameValuePair> list_data) throws IOException {
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
