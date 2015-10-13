package com.app.jiaxiaotong.data;


import android.support.v4.util.ArrayMap;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * okHttp访问网络获取数据基类
 * Created by ekfans-com on 2015/6/16.
 */
public class OkHttpUtil {
    private static int CONNECTION_TIMEOUT = 20 * 1000;//连接超时
    private static int SO_TIMEOUT = 20 * 1000;//读取超时

    public static final String HEADER = "token";

    public static final MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("text/x-markdown; charset=utf-8");

    private static final OkHttpClient client = new OkHttpClient();

    static {
        client.setConnectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS);
    }

    /**
     * post不开启异步线程请求服务器
     * @param map
     * @return
     */
    public static String post(Map<String,Object> map,Map<String,Object> bodyMap){
        String body = null;
        try {
            Response response = execute(request(map,bodyMap));
            if (!response.isSuccessful())
                throw new IOException("Unexpected code " + response);
            body = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return body;
    }

    /**
     * post不开启异步线程请求服务器
     * @param map
     * @return
     */
    public static String post(ArrayMap<String,Object> map,Map<String,Object> bodyMap){


        String body = null;
        try {
            Response response = execute(request(map,bodyMap));
            if (!response.isSuccessful())
                throw new IOException("Unexpected code " + response);
            body = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return body;
    }

    /**
     * 以流方式post文件
     * @param fileUri
     * @return
     * @throws Exception
     */
    public static String postFile(URI fileUri) throws Exception{
        String url = ServiceConst.SERVICE_URL;
        File file = new File(fileUri);
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, file))
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!response.isSuccessful())
            try {
                throw new IOException("Unexpected code " + response);
            } catch (IOException e) {
                e.printStackTrace();
            }

        return response.body().string();
    }
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    /**
     * 生成request
     * @param params
     * @return
     */
    private static Request request(Map<String,Object> params,Map<String,Object> bodyMap){
        Request.Builder builder = new Request.Builder();
        Set<String> keySet = params.keySet();
        for (String key : keySet){
            builder.addHeader(key, (String) params.get(key));
        }
        String url = (String) params.get("url");
        Request request = null;
        //添加body
        if(bodyMap != null){
            JSONObject jsonObject = new JSONObject();
//            FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();
            Set<String> bodyKeySet = bodyMap.keySet();
            for (String key : bodyKeySet){
//                formEncodingBuilder.add(key, (String) bodyMap.get(key));
                try {
                    jsonObject.put(key,bodyMap.get(key));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            RequestBody formBody = RequestBody.create(JSON,jsonObject.toString());
            request = builder.url(url).post(formBody).build();
        }else{
            FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();
            formEncodingBuilder.add("post","");
            request = builder.url(url).post(formEncodingBuilder.build()).build();
        }
        return  request;
//        return new Request.Builder()
//                .url(url)
//                .addHeader(HEADER,header)
//                .post(formBody)
//                .build();
    }
    /**
     * 生成request
     * @param params
     * @return
     */
    private static Request request(ArrayMap<String,String> params,String header){
        String url = ServiceConst.SERVICE_URL;
        FormEncodingBuilder builder = new FormEncodingBuilder();
        Set<String> keySet = params.keySet();
        for (String key : keySet){
            builder.add(key,params.get(key));
        }
        RequestBody formBody = builder.build();
        return new Request.Builder()
                .url(url)
                .addHeader(HEADER, header)
                .post(formBody)
                .build();
    }


    /**
     * 该不会开启异步线程。
     * @param request
     * @return
     * @throws IOException
     */
    public static Response execute(Request request) throws IOException{
        return client.newCall(request).execute();
    }
    /**
     * 开启异步线程访问网络
     * @param request
     * @param responseCallback
     */
    public static void enqueue(Request request, Callback responseCallback){
        client.newCall(request).enqueue(responseCallback);
    }
    /**
     * 开启异步线程访问网络, 且不在意返回结果（实现空callback）
     * @param request
     */
    public static void enqueue(Request request){
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(Response arg0) throws IOException {

            }

            @Override
            public void onFailure(Request arg0, IOException arg1) {

            }
        });
    }


    public static String getStringFromServer(String url,String header) throws IOException {
        Request request = new Request.Builder().url(url).addHeader("token", header).build();
        Response response = execute(request);
        if (response.isSuccessful()) {
            String responseUrl = response.body().string();
            return responseUrl;
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }
    public static String getStringFromServerWithMoreHeader(Map<String, Object> params,Map<String, Object> bodyMap) throws IOException {
        Request.Builder builder = new Request.Builder();
        Set<String> keySet = params.keySet();
        for (String key : keySet){
            builder.addHeader(key, (String) params.get(key));
        }
        String url = (String) params.get("url");
        Request request = null;
        //添加body
        if(bodyMap != null){

            FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();
            Set<String> bodyKeySet = bodyMap.keySet();
            for (String key : bodyKeySet){
                formEncodingBuilder.add(key, (String) bodyMap.get(key));
            }
            RequestBody formBody = formEncodingBuilder.build();
            request = builder.url(url).post(formBody).build();
        }else{
            request = builder.url(url).build();
        }
        //end
        Response response = execute(request);
        if (response.isSuccessful()) {
            String responseUrl = response.body().string();
            return responseUrl;
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    public static String postStringFromServerWithMoreHeader(Map<String, Object> headerMap,Map<String,Object> bodyMap) throws IOException {
        Request.Builder builder = new Request.Builder();
        Set<String> keySet = headerMap.keySet();
        for (String key : keySet){
            builder.addHeader(key, (String) headerMap.get(key));
        }
        String url = (String) headerMap.get("url");
        FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();
        if(bodyMap != null){
            Set<String> bodyKeySet = bodyMap.keySet();
            for (String key : bodyKeySet){
                formEncodingBuilder.add(key, (String) bodyMap.get(key));
            }
        }
        RequestBody formBody = formEncodingBuilder.build();

        Request request = builder.url(url).post(formBody).build();
        Response response = execute(request);
        if (response.isSuccessful()) {
            String responseUrl = response.body().string();
            return responseUrl;
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }
    private static final String CHARSET_NAME = "UTF-8";
//    /**
//     * 这里使用了HttpClinet的API。只是为了方便
//     * @param params
//     * @return
//     */
//    public static String formatParams(List<BasicNameValuePair> params){
//        return URLEncodedUtils.format(params, CHARSET_NAME);
//    }
//    /**
//     * 为HttpGet 的 url 方便的添加多个name value 参数。
//     * @param url
//     * @param params
//     * @return
//     */
//    public static String attachHttpGetParams(String url, List<BasicNameValuePair> params){
//        return url + "?" + formatParams(params);
//    }
    /**
     * 为HttpGet 的 url 方便的添加1个name value 参数。
     * @param url
     * @param name
     * @param value
     * @return
     */
    public static String attachHttpGetParam(String url, String name, String value){
        return url + "?" + name + "=" + value;
    }
}
