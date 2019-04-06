package com.smart.smart.wordSegment;

import android.util.Log;

import com.smart.smart.speechRecog.CallBackSegment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.smart.smart.wordSegment.AuthService.getAuth;

//
// Created by dingying on 2019/3/6.
//
public class RequestSegment {
    private static final String TAG = "RequestSegment";

    /**
     *创建一个子线程处理分词请求，并回调接口
     */
    public static void performWithAnswer(final String words, final CallBackSegment callBackSegment){

            if (null==callBackSegment)
                return ;
        new Thread(new Runnable() {
            @Override
            public void run() {
                //处理分词请求
                String originalJSON= null;
                try {
                    originalJSON = sendBody(words);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (originalJSON==null){
                    Log.i(TAG,"originalJSON segment is null");
                }else{
                    SegmentResult segmentResult = SegmentResult.parseJson(originalJSON);
                    if(segmentResult!=null&&callBackSegment!=null){
                        //接口回调，返回给Message
                    callBackSegment.callBackSegmentResult(segmentResult);
                }
                }
            }
        }).start();
    }


    public static String sendBody(String words) throws IOException, JSONException {
        JSONObject obj = new JSONObject();
        obj.put("text",words);
        //   String path = "https://aip.baidubce.com/rpc/2.0/nlp/v1/lexer?charset=UTF-8&access_token=";
        String path="https://aip.baidubce.com/rpc/2.0/nlp/v1/lexer_custom?charset=UTF-8&access_token=";
        String getPath = path + getAuth();
        URL url = new URL(getPath);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Charset","UTF-8");

        //转换为字节数组
        byte[] data = (obj.toString()).getBytes("UTF-8");

        // 设置文件长度
        conn.setRequestProperty("Content-Length", String.valueOf(data.length));
        // 设置文件类型:
        conn.setRequestProperty("Content-Type", "application/json");
        conn.connect();
        OutputStream out = conn.getOutputStream();
        // 写入请求的字符串
        out.write((obj.toString()).getBytes());
        out.flush();
        out.close();

        Log.i(TAG,conn.getResponseMessage());
        if (conn.getResponseCode() == 200) {
            // System.out.println("连接成功");
            // 请求返回的数据
            Log.d(TAG,"分词API请求连接成功");
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String result = "";
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            //  System.err.println(result);
            Log.i(TAG,"分词结果"+result);
            return result;

        }else  {
            // System.out.println("返回代码："+conn.getResponseCode());
            Log.e(TAG,"连接失败，返回代码："+conn.getResponseCode());
            return null;
        }
    }

}
