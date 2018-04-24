package com.eastcom_sw.etai.bean;

import io.swagger.annotations.ApiModelProperty;

/**
 * 目标组件
 *
 * @author Sunk
 * @create 2018-4-02-13:55
 **/
public class targetCmpt {
    @ApiModelProperty(value = "流程组件ID")
    private String pcmptId;

    @ApiModelProperty(value = "流程组件接口顺序")
    private int pcmptIntfOrder;

    @ApiModelProperty(value = "接口描述")
    private String intfDesc;

    public String getPcmptId() {
        return pcmptId;
    }

    public void setPcmptId(String pcmptId) {
        this.pcmptId = pcmptId;
    }

    public int getPcmptIntfOrder() {
        return pcmptIntfOrder;
    }

    public void setPcmptIntfOrder(int pcmptIntfOrder) {
        this.pcmptIntfOrder = pcmptIntfOrder;
    }

    public String getIntfDesc() {
        return intfDesc;
    }

    public void setIntfDesc(String intfDesc) {
        this.intfDesc = intfDesc;
    }
}
