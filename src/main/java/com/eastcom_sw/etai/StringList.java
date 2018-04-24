package com.eastcom_sw.etai;

import net.bytebuddy.agent.builder.AgentBuilder;

import java.util.List;

/**
 * 元数据bean
 *
 * @author Sunk
 * @create 2018-2-08-10:52
 **/
public class StringList {

    private  List<String> listStr;

    public List<String> getListStr(){
        return listStr;
    }

    public void setListStr(List<String> listStr) {
        this.listStr = listStr;
    }
}
