package com.eastcom_sw.etai.bean;

import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.List;

/**
 * 组件信息
 *
 * @author Sunk
 * @create 2018-4-02-13:57
 **/
public class info {

    @ApiModelProperty(value = "流程组件ID")
    private String pcmptId;

    @ApiModelProperty(value = "流程组件名称")
    private String pcmptName;

    @ApiModelProperty(value = "基础组件ID")
    private String  bcmptId;

    @ApiModelProperty(value = "资源ID")
    private String sourceId;

    @ApiModelProperty(value = "是否为流程入口")
    private int isProcessEntrance;

    @ApiModelProperty(value = "布局X轴")
    private BigDecimal layoutX;

    @ApiModelProperty(value = "布局Y轴")
    private BigDecimal layoutY;

    @ApiModelProperty(value = "组件参数列表")
    private List<params> paramslst;

    @ApiModelProperty(value = "组件选择字段列表")
    private List<transeform> transeformlst;

    public String getPcmptId() {
        return pcmptId;
    }

    public void setPcmptId(String pcmptId) {
        this.pcmptId = pcmptId;
    }

    public String getPcmptName() {
        return pcmptName;
    }

    public void setPcmptName(String pcmptName) {
        this.pcmptName = pcmptName;
    }

    public String getBcmptId() {
        return bcmptId;
    }

    public void setBcmptId(String bcmptId) {
        this.bcmptId = bcmptId;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public int getIsProcessEntrance() {
        return isProcessEntrance;
    }

    public void setIsProcessEntrance(int isProcessEntrance) {
        this.isProcessEntrance = isProcessEntrance;
    }

    public BigDecimal getLayoutX() {
        return layoutX;
    }

    public void setLayoutX(BigDecimal layoutX) {
        this.layoutX = layoutX;
    }

    public BigDecimal getLayoutY() {
        return layoutY;
    }

    public void setLayoutY(BigDecimal layoutY) {
        this.layoutY = layoutY;
    }

    public List<params> getParamslst() {
        return paramslst;
    }

    public void setParamslst(List<params> paramslst) {
        this.paramslst = paramslst;
    }

    public List<transeform> getTranseformlst() {
        return transeformlst;
    }

    public void setTranseformlst(List<transeform> transeformlst) {
        this.transeformlst = transeformlst;
    }

}
