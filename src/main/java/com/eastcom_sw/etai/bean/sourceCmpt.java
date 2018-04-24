package com.eastcom_sw.etai.bean;

import io.swagger.annotations.ApiModelProperty;

/**
 * 目标组件
 *
 * @author Sunk
 * @create 2018-4-02-13:56
 **/
public class sourceCmpt {

    @ApiModelProperty(value = "父流程组件ID")
    private String fatherPcmptId;

    @ApiModelProperty(value = "父流程组件的接口顺序")
    private int fatherPcmptIntfOrder;

    public String getFatherPcmptId() {
        return fatherPcmptId;
    }

    public void setFatherPcmptId(String fatherPcmptId) {
        this.fatherPcmptId = fatherPcmptId;
    }

    public int getFatherPcmptIntfOrder() {
        return fatherPcmptIntfOrder;
    }

    public void setFatherPcmptIntfOrder(int fatherPcmptIntfOrder) {
        this.fatherPcmptIntfOrder = fatherPcmptIntfOrder;
    }
}
