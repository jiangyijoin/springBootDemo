package com.eastcom_sw.etai.utils;

import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

/**
 * 调用ETAI后台接口
 *
 * @author Sunk
 * @create 2018-3-20-9:41
 **/
public class RestfulUtil {


    private static String urlstr = "http://10.8.132.223:5000/etai/job";


    /**
     * 调用后台restful接口
     * @param obj
     * @return
     */
    public static JSONObject restfulEtai(JSONObject obj){

        JSONObject data = new JSONObject();

        try{
            URL url = new URL(urlstr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type","application/json;charset=UTF-8");
            conn.setRequestProperty("accept","application/json");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // POST请求
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(true);

            String jsonstr= ToolsUtill.converJavaBeanToJson(obj);
            if(jsonstr != null && !StringUtils.isEmpty(jsonstr)){

                byte[] writebytes = jsonstr.getBytes();
                //设置文件长度
                conn.setRequestProperty("Content-Length",String.valueOf(writebytes.length));
                OutputStream out = conn.getOutputStream();
                out.write(writebytes);
                out.flush();
                out.close();
            }
//            logger.info("===============HttpURLConnection json========="+jsonstr);
            int k = conn.getResponseCode();
            if(conn.getResponseCode() == 200) {

                // 读取响应
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String lines;
                StringBuffer sb = new StringBuffer("");
                while ((lines = reader.readLine()) != null) {
                    lines = URLDecoder.decode(lines, "utf-8");
                    sb.append(lines);
                }

                data = ToolsUtill.parsr(sb.toString(), JSONObject.class);
            }


        }catch (MalformedURLException e){
            e.printStackTrace();

        }catch (IOException io){

            io.printStackTrace();
        }
        return  data;
    }

    /**
     * 运行
     * @param processId
     * @return
     */
    public static JSONObject run(String processId){
        JSONObject obj = new JSONObject();
        obj.put("id",processId);
        obj.put("type",0);
        return obj;
    }

    /**
     * 发布
     * @param processId
     * @return
     */
    public static JSONObject publish(String processId){
        JSONObject obj = new JSONObject();
        obj.put("id",processId);
        obj.put("type",1);
        return obj;
    }


    /**
     * 预测
     * @param forecastId
     * @return
     */
    public static JSONObject forecast(String forecastId){
        JSONObject obj = new JSONObject();
        obj.put("id",forecastId);
        obj.put("type",2);
        return obj;
    }
}
