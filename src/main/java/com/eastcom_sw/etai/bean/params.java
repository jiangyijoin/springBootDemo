package com.eastcom_sw.etai.bean;

import io.swagger.annotations.ApiModelProperty;

/**
 * 组件参数
 *
 * @author Sunk
 * @create 2018-4-02-13:55
 **/
public class params {

    @ApiModelProperty(value = "基础组件ID")
    private String bcmptParamId;

    @ApiModelProperty(value = "组件参数key")
    private String pcmptParamKey;

    @ApiModelProperty(value = "参数Value")
    private String pcmptParamValue;

    public String getBcmptParamId() {
        return bcmptParamId;
    }

    public void setBcmptParamId(String bcmptParamId) {
        this.bcmptParamId = bcmptParamId;
    }

    public String getPcmptParamKey() {
        return pcmptParamKey;
    }

    public void setPcmptParamKey(String pcmptParamKey) {
        this.pcmptParamKey = pcmptParamKey;
    }

    public String getPcmptParamValue() {
        return pcmptParamValue;
    }

    public void setPcmptParamValue(String pcmptParamValue) {
        this.pcmptParamValue = pcmptParamValue;
    }
}
