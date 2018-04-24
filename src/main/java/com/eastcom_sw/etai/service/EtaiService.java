package com.eastcom_sw.etai.service;

import com.eastcom_sw.etai.StringList;
import com.eastcom_sw.etai.bean.InputProcessBean;
import com.eastcom_sw.etai.response.BaseResponse;
import com.eastcom_sw.frm.core.entity.Page;
import netscape.javascript.JSObject;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author Sunk
 * @create 2018-1-18-15:59
 **/
public interface EtaiService {

    List<JSObject> queryTest(String testId);

    BaseResponse queryAlScenesList(String userName);

    List<JSObject> queryAIScenesById(String id);

    List<JSObject> queryParams(String flag,String sceneId,String userName);

    BaseResponse queryPredictModel(String sceneId,String forecastId,String startTime,String endTime,Integer pageNo,
                                   Integer limit);

    BaseResponse queryOwnProjectList(String sceneId,String projectId,String userName,Integer pageNo, Integer limit);

    BaseResponse queryPredictHistory(String forecastId, Integer page,Integer limit);

    BaseResponse queryDataResourceList(String sceneId,String sourceId,String userName,String startTime,String endTime,
                                       Integer pageNo, Integer limit);

    BaseResponse deleteProjectById(String flag, String id);

    BaseResponse queryProcessCmpts(String processId);

    BaseResponse queryCmptParams(String pcmptId);

    BaseResponse queryCmptTransformCol(String pcmptId);

    BaseResponse uploadFile(MultipartFile file);

    BaseResponse readFileByChars(String filePath,String separatorCol,String separatorRow,String readRows,
                                 String isContent,String storageId);

    BaseResponse insertDataResource(String sceneId, String sceneName, String urlPath,
                                    String separatorCol, String separatorRow, String isContent,
                                    String userName, String listCol, String flag,String metadataId);

    BaseResponse readFileOnServer(String sourceId,String storageId,String metadataId,String flag);

    BaseResponse downloadFile(String path,String storageId,HttpServletResponse response);

    BaseResponse systemDataSource(String sceneId);

    BaseResponse saveProcess(InputProcessBean inputProcessBean);

    BaseResponse baseCmptIntfType();

    BaseResponse runProcess(String processId);

    BaseResponse previewCSVFile(String path,String storageId,String previewRows,String isContnt);

    BaseResponse publishProcess(String userName,String processId,String forecastName);

    BaseResponse forecastProcess(String forecastId);

    BaseResponse getProcessResult(String processId);

    BaseResponse getForecastResult(String forecastId);

    BaseResponse updateProcess(InputProcessBean inputProcessBean);

    BaseResponse previewPicture(String path,String storageId,HttpServletResponse response);

    BaseResponse previewExcellFile(String storageId,String path,String isContent,String previewRows);

    String beforePublishProcess(String userName, String processId, String forecastName);

    String beforeforecastProcess(String userName, String forecastId, String sourceId);

    BaseResponse repeatProcessName(String flag,String userName,String forecastName);
}
