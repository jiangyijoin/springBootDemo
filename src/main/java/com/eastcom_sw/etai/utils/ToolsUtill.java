package com.eastcom_sw.etai.utils;

import com.google.gson.Gson;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 工具
 *
 * @author Sunk
 * @create 2018-2-08-17:23
 **/
public class ToolsUtill {

    /**
     * 获取当前服务器时间
     * @return
     */
    public static String getNow(){

        Date date = new Date();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddhhmmss");

        return fmt.format(date);
    }


    /**
     * 将Javabean转换成JSON字符串
     * @param obj
     * @return
     */
    public static String converJavaBeanToJson(Object obj){

        if(obj == null ){
            return "";
        }

        Gson gson = new Gson();

        String beanstr = gson.toJson(obj);

        if(!StringUtils.isEmpty(beanstr)){

            return beanstr;
        }
        return  "";
    }


    /**
     * 将JSON字符串转换成javabean
     * @param json
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T parsr(String json ,Class<T> tClass){
        //判读字符串是否为空
        if(StringUtils.isEmpty(json)){
            return null;
        }

        Gson gson = new Gson();
        return gson.fromJson(json,tClass);
    }
}
