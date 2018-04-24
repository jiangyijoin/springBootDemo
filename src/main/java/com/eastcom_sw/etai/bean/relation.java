package com.eastcom_sw.etai.bean;

import io.swagger.annotations.ApiModelProperty;

/**
 * 组件关系
 *
 * @author Sunk
 * @create 2018-4-02-13:57
 **/
public class relation {

//    @ApiModelProperty(value = "组件关系ID")
//    private String relationId;

    @ApiModelProperty(value = "目标组件（子组件）")
    private targetCmpt targetCmpt;

    @ApiModelProperty(value = "源组件（父组件）")
    private sourceCmpt sourceCmpt;

//    public String getRelationId() {
//        return relationId;
//    }
//
//    public void setRelationId(String relationId) {
//        this.relationId = relationId;
//    }

    public targetCmpt getTargetCmpt() {
        return targetCmpt;
    }

    public void setTargetCmpt(targetCmpt targetCmpt) {
        this.targetCmpt = targetCmpt;
    }

    public sourceCmpt getSourceCmpt() {
        return sourceCmpt;
    }

    public void setSourceCmpt(sourceCmpt sourceCmpt) {
        this.sourceCmpt = sourceCmpt;
    }
}
