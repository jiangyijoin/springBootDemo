package com.eastcom_sw.etai.bean;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 流程数据
 *
 * @author Sunk
 * @create 2018-3-30-16:54
 **/
public class InputProcessBean {

    @ApiModelProperty(value = "流程ID")
    private String processId;

    @ApiModelProperty(value = "流程图标")
    private String processIcon;

    @ApiModelProperty(value = "流程描述")
    private String processDesc;

    @ApiModelProperty(value = "流程名称")
    private String processName;

    @ApiModelProperty(value = "资源ID")
    private String sourceId;

    @ApiModelProperty(value = "场景ID")
    private String sceneId;

    @ApiModelProperty(value = "场景名称")
    private String sceneName;

    @ApiModelProperty(value = "所属用户")
    private String username;

    @ApiModelProperty(value = "数据集描述")
    private String dataDesc;

    @ApiModelProperty(value = "组件关系列表")
    private List<relation> relationlst;

    @ApiModelProperty(value = "组件信息列表")
    private List<info> infolst;

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getProcessIcon() {
        return processIcon;
    }

    public void setProcessIcon(String processIcon) {
        this.processIcon = processIcon;
    }

    public String getProcessDesc() {
        return processDesc;
    }

    public void setProcessDesc(String processDesc) {
        this.processDesc = processDesc;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getSceneId() {
        return sceneId;
    }

    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }

    public String getSceneName() {
        return sceneName;
    }

    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDataDesc() {
        return dataDesc;
    }

    public void setDataDesc(String dataDesc) {
        this.dataDesc = dataDesc;
    }

    public List<relation> getRelationlst() {
        return relationlst;
    }

    public void setRelationlst(List<relation> relationlst) {
        this.relationlst = relationlst;
    }

    public List<info> getInfolst() {
        return infolst;
    }

    public void setInfolst(List<info> infolst) {
        this.infolst = infolst;
    }

    public InputProcessBean(){};

}

