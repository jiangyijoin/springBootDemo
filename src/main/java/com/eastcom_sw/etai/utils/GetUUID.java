package com.eastcom_sw.etai.utils;

import java.util.UUID;

/**
 * 获取32位UUID
 *
 * @author Sunk
 * @create 2018-2-08-15:51
 **/
public class GetUUID {

    public static String getUUID(){
        String uuid = UUID.randomUUID().toString().replace("-","");
        return uuid;
    }
}
