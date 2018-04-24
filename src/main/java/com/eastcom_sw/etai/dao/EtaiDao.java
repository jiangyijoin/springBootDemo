package com.eastcom_sw.etai.dao;

import com.eastcom_sw.etai.StringList;
import com.eastcom_sw.frm.core.entity.Page;
import netscape.javascript.JSObject;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 数据接口
 *
 * @author Sunk
 * @create 2018-1-18-14:50
 **/
public interface EtaiDao {

    /**
     * 测试
     * @param testId
     * @return
     */
    List<JSObject> queryTest(String testId);


    /**
     * 流程列表
     * @param userName
     * @return
     */
    List<JSObject> queryProcessList(String userName);


    /**
     * 流程介绍
     * @param id
     * @return
     */
    List queryProcessById(String id);

    /**
     * 界面参数获取
     * @param flag model;project;dataSource
     * @param sceneId
     * @return
     */
    List<JSObject> queryParams(String flag,String sceneId,String userName);


    /**
     * 预测模型列表
     * @param sceneId
     * @param forecastId
     * @param startTime
     * @param endTime
     * @return
     */
    Page queryPredictModel(String sceneId,String forecastId,String startTime,String endTime,Integer pageNo, Integer limit);

    /**
     * 预测历史（具体模板预测信息）
     * @param forecastId
     * @return
     */
    Page queryPredictHistory(String forecastId, Integer pageNo, Integer limit);

    /**
     * 项目列表
     * @param sceneId
     * @param projectId
     * @return
     */
    Page queryOwnProjectList(String sceneId,String projectId,String userName,Integer pageNo, Integer limit);


    /**
     * 数据源列表
     * @param sceneId
     * @param sourceId
     * @param userName
     * @param pageNo
     * @param limit
     * @return
     */
    Page queryDataResourceList(String sceneId,String sourceId,String userName,String startTime,String endTime,
                               Integer pageNo, Integer limit);

    /**
     * 删除
     * @param flag project;dataResource
     * @param id 相关ID
     * @return
     */
    int[] deleteProjectById(String flag, String id);



    /**
     * 获取流程中所有组件关系
     * @param processId 流程ID
     * @return
     */
    List<Map<String, Object>> queryProcessCmptsRel(String processId);

    /**
     * 获取流程中组件接口
     * @param processId 流程ID
     * @return
     *//*
    List<Map<String, Object>> queryProcessCmptsIntf(String processId);
*/
    /**
     * 获取组件参数
     * @param pcmptId 组件ID
     * @return
     */
    List queryCmptParams(String pcmptId);


    /**
     * 查询父组件ID
     * @param pcmptId
     * @return
     */
    String queryParentCmpt(String pcmptId);

    /**
     * 查询组件输入设置及输出字段
     * @param pcmptId
     * @return
     */
    List queryOutputFields(String pcmptId);

    /**
     * 根据元数据ID 获取元数据字段
     * @param metadataId
     * @return
     */
    List<Map<String, Object>> queryMetadataCol(String metadataId);

    /**
     * 根据资源ID获取 资源参数
     * @param sourceId
     * @return
     */
    List<Map<String, Object>> querySourceParam(String sourceId);

    /**
     * 根据存储ID 获取存储数据
     * @param storageId
     * @return
     */
    List<Map<String, Object>> queryStorageParam(String storageId);

    /**
     * 获取组件数据源ID
     * @param processId
     * @return
     */
    String querySourceId(String processId);


    /**
     * 录入数据源
     * @param sceneId
     * @param sceneName
     * @param path
     * @param dataResourceName
     * @param separatorCol
     * @param separatorRow
     * @param isContent
     * @param userName
     * @return sourceId
     */
    String insertDataResource(String sceneId,String sceneName,String path,String dataResourceName,
                              String separatorCol,String separatorRow,String isContent,String userName,
                              String storageId);


    /**
     * 录入存储表数据（文件上传相关参数信息）
     * @param storageName 文件名称
     * @param storageType 存储类型
     * @return
     */
   /* String insertStorageData(String storageName,Integer storageType);*/


    /**
     * 获取存储ID
     * @return
     */
   String queryStorageId();


    /**
     * 录入元数据
     * @param sourceId
     * @param metaName
     * @param listCol COL_NAME;COL_TYPE;COL_DESC
     * @return
     */
    String insertMetadata(String sourceId,String metaName,StringList listCol);


    /**
     * 根据场景ID 获取模板源文件路径
     * @param sceneId
     * @return
     */
    List<JSObject> systemDatasource(String sceneId);


    /**
     * 录入流程表数据
     * @param PROCESS_NAME
     * @param USERNAME
     * @param PROCESS_DESC
     * @param DATA_DESC
     * @param SOURCE_ID
     * @param PROCESS_ICON
     * @param SCENE_ID
     * @param SCENE_NAME
     * @return
     */
    String saveProcessOnProcess(String PROCESS_NAME,String USERNAME,String PROCESS_DESC,String DATA_DESC,
                       String SOURCE_ID,String PROCESS_ICON,String SCENE_ID,String SCENE_NAME);


    /**
     * 录入流程组件（包含参数）数据
     * @param PCMPT_ID
     * @param PCMPT_NAME
     * @param BCMPT_ID
     * @param SOURCE_ID
     * @param LAYOUT_X
     * @param LAYOUT_Y
     * @param IS_PROCESS_ENTRANCE
     * @param list
     * @return
     */
    int saveProcessOnCmpt(final String PCMPT_ID, String processId, String PCMPT_NAME, String BCMPT_ID, String SOURCE_ID,
                          BigDecimal LAYOUT_X, BigDecimal LAYOUT_Y, int IS_PROCESS_ENTRANCE,
                          final List<Map<String,Object>> list, final List<Map<String,Object>> lst);


    /**
     * 录入流程组件关系数据
     * @param list
     * @return
     */
    int saveProcessOnRelation(final List<Map<String,Object>> list);


    /**
     * 基础组件类型
     * （满足前台对组件类型查询）
     * 针对组件接口出入个数
     * @return
     */
    List baseCmptIntfType();


    /**
     * 查询流程结果表
     * @param processId
     * @return
     */
    List processResult(String processId);

    /**
     * 重名验证
     * @param flag processForecast:发布模型；流程
     * @param username
     * @param processName
     * @return
     */
    int repeatProcessName(String flag,String username,String processName);


    /**
     * 发布模型
     * @param map
     * @return
     */
    String publicProcess(Map<String,Object> map);

    /**
     * 预测模型
     * @param map
     * @return
     */
    String forecastProcess(Map<String,Object> map);


    /**
     * 获取预测模型结果
     * @param forecastId
     * @return
     */
    List forecastProcessResult(String forecastId);


    /**
     * 删除流程组件关系
     * @param list
     * @return
     */
    int deleteProcessOnRelation(final List<String> list);

    /**
     * 删除流程中组件
     * @param processId
     * @return
     */
    int deleteProcessOnCmpt(String processId);

    /**
     * 删除组件参数
     * @param list
     * @return
     */
    int deleteProcessOnCmptParams(final List<String> list);

    int deleteRrocessOnTranseformFields(final  List<String> list);

    /**
     * 修改流程信息
     * @param PROCESS_NAME
     * @param USERNAME
     * @param PROCESS_DESC
     * @param DATA_DESC
     * @param SOURCE_ID
     * @param PROCESS_ICON
     * @param SCENE_ID
     * @param SCENE_NAME
     * @return
     */
    int updateProcessOnProcess(String PROCESS_ID,String PROCESS_NAME,String USERNAME,String PROCESS_DESC,String DATA_DESC,
                               String SOURCE_ID,String PROCESS_ICON,String SCENE_ID,String SCENE_NAME);

    /**
     * 获取流程中所有组件ID;基础组件ID
     * @param processId
     * @param flag processCmptId;baseCmptId
     * @return
     */
    List pcmptIdsOnProcess(String processId,String flag);

    /**
     * 转换字段
     * @param pcmptId
     * @return
     */
    List transformField(String pcmptId,String bcmptId);

    /**
     * 转换字段规则
     * @param baseCmptId
     * @return
     */
    List transformRules(String baseCmptId);


    /**
     * 录入 资源元数据关系表
     * @param sourceId
     * @param metadataId
     * @return
     */
    int insertSourceMetadataRelation(String sourceId,String metadataId);



}
