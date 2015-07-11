package com.nannansea.apk.Laugh4EveryDay;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author yindongyang
 * @Created 14/12/13
 * @Version 1.0
 */
public class HttpUtils {
    // 默认的连接池等待时间
    public static final int DEFAULT_CONNECTION_MANAGER_TIME_OUT = 8000;
    // 默认的连接等待时间
    public static final int DEFAULT_CONNECTION_TIME_OUT = 5000;
    // 默认的数据等待时间
    public static final int DEFAULT_SOCKET_TIME_OUT = 15000;

    public static String doHttpGet(String url) throws Exception{
        HttpGet get = new HttpGet(url);
        return doExcute(get);
    }

    public static String doHttpPost(String url, Map<String, String> body) throws Exception{
        HttpPost post = new HttpPost(url);
        List<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
        for (String key : body.keySet()) {
            String value = body.get(key);
            if (StringUtils.isBlank(value)) {
                continue;
            }
            pairs.add(new BasicNameValuePair(key, value));
        }
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs, "UTF-8");
        post.setEntity(entity);
        return doExcute(post);
    }

    public static String doExcute(HttpUriRequest request) throws Exception{
        DefaultHttpClient client = new DefaultHttpClient();
        HttpParams clientParams = client.getParams();
        clientParams.setParameter("http.connection-manager.timeout", DEFAULT_CONNECTION_MANAGER_TIME_OUT); // 从连接池取连接超时
        clientParams.setParameter("http.connection.timeout", DEFAULT_CONNECTION_TIME_OUT); // 连接建立超时
        clientParams.setParameter("http.socket.timeout", DEFAULT_SOCKET_TIME_OUT); // 数据等待超时
        HttpResponse response = client.execute(request);
        return EntityUtils.toString(response.getEntity());
    }
}
