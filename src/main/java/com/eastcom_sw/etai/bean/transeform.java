package com.eastcom_sw.etai.bean;

import io.swagger.annotations.ApiModelProperty;

/**
 * 字段转换
 *
 * @author Sunk
 * @create 2018-4-02-13:53
 **/
public class transeform {

    @ApiModelProperty(value = "基础转换定义ID")
    private String transformId;

    @ApiModelProperty(value = "是否保留原字段")
    private int retainOrcol;

    @ApiModelProperty(value = "输入字段KEY")
    private String inputColKey;

    @ApiModelProperty(value = "输入字段value")
    private String inputColValue;

    @ApiModelProperty(value = "输出字段名称")
    private String outputName;

    public String getTransformId() {
        return transformId;
    }

    public void setTransformId(String transformId) {
        this.transformId = transformId;
    }

    public int getRetainOrcol() {
        return retainOrcol;
    }

    public void setRetainOrcol(int retainOrcol) {
        this.retainOrcol = retainOrcol;
    }

    public String getInputColKey() {
        return inputColKey;
    }

    public void setInputColKey(String inputColKey) {
        this.inputColKey = inputColKey;
    }

    public String getInputColValue() {
        return inputColValue;
    }

    public void setInputColValue(String inputColValue) {
        this.inputColValue = inputColValue;
    }

    public String getOutputName() {
        return outputName;
    }

    public void setOutputName(String outputName) {
        this.outputName = outputName;
    }

}
