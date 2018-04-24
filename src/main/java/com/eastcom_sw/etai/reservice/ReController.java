package com.eastcom_sw.etai.reservice;

import com.eastcom_sw.etai.bean.InputProcessBean;
import com.eastcom_sw.etai.response.BaseResponse;
import com.eastcom_sw.etai.service.EtaiService;
import com.eastcom_sw.etai.service.impl.EtaiServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import netscape.javascript.JSObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * 控制层
 *
 * @author Sunk
 * @create 2018-1-18-14:48
 **/

@Api(value = "etai",tags = {"AI"})
@RestController
@RequestMapping("/AI")
public class ReController {

    @Autowired
    private EtaiService etaiService;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value="/test",method = RequestMethod.GET)
    public List<String> queryTest(String testId){

//        return etaiService.queryTest(testId);
        List list = new ArrayList();
        list.add("1,2,3");
        list.add("2,3,4");
        list.add("rt,45,厌");
        return list;
    }


    /**
     * AI场景
     * @param userName 当前用户
     * @return
     */
    @ApiOperation(value = "AI场景列表",notes = "获取当前用户的ai场景列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userName",value = "用户名称",required = true,dataType = "String",paramType = "query")
    })
    @RequestMapping(value = "AIScenesList",method = RequestMethod.GET)
    public BaseResponse queryAlScenesList(@RequestParam(value = "userName",required = true) String userName){
        return  etaiService.queryAlScenesList("SYSTEM");
    }

    /**
     * 界面参数获取
     * @param flag 参数类别(model;project;dataSource)
     * @param sceneId 场景ID(ALL;ID)
     * @return
     */
    @ApiOperation(value = "界面参数",notes = "flag：model(模型名称);project(项目名称);dataSource(数据源文件名称);" +
            "sceneId:场景ID；ALL;ID;userName:登录用户(当flag为model时不需要该参数)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flag",value = "查询参数类别(model;project;dataSource)",required = true,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "sceneId",value ="场景ID(ALL;ID)",required =true,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "userName",value = "用户名称",required = false,dataType = "String",paramType = "query")
    })
    @RequestMapping(value = "params" ,method = RequestMethod.POST)
    public List<JSObject> queryParams(@RequestParam(value = "flag",required = true) String flag,
                                      @RequestParam(value = "sceneId",required = true)String sceneId,
                                      @RequestParam(value = "userName",required = false)String userName){
        return etaiService.queryParams(flag,sceneId,userName);
    }


    /**
     * 预测模型列表
     * @param sceneId 场景ID
     * @param forecastId 项目ID
     * @param startTime 起始时间
     * @param endTime 截止时间
     * @return
     */
    @ApiOperation(value = "预测模型列表",notes = "获取当前用户的所有预测模型列表（分页）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sceneId",value = "场景ID(ALL；模型ID)",required = true,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "forecastId",value ="项目ID",required =false,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "startTime",value = "发布起始时间14位（20180212000000）",required = false,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "endTime",value = "发布终止时间14位（20180212000000）",required = false,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "pageNo",value ="当前页",required =true,dataType = "int",paramType = "query"),
            @ApiImplicitParam(name = "limit",value = "每页数据数",required = true,dataType = "int",paramType = "query")
    })
    @RequestMapping(value = "predictModel",method = RequestMethod.POST)
    public BaseResponse queryPredictModel(String sceneId,String forecastId,String startTime,
                                          String endTime,Integer pageNo, Integer limit){
        return etaiService.queryPredictModel(sceneId,forecastId,startTime,endTime,pageNo,limit);
    }


    /**
     * 模型预测历史查看
     * @param forecastId
     * @param pageNo
     * @param limit
     * @return
     */
    @ApiOperation(value = "预测历史列表",notes = "预测模型劣势列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "forecastId",value = "预测模型ID",required = true,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "pageNo",value = "当前页数",required = true,dataType = "int",paramType = "query"),
            @ApiImplicitParam(name = "limit",value = "每页数据量",required = true,dataType = "int",paramType = "query")
    })

    @RequestMapping(value = "predictHistory",method = RequestMethod.POST)
    public BaseResponse queryPredictHistory(String forecastId, Integer pageNo,Integer limit){
        return etaiService.queryPredictHistory(forecastId,pageNo,limit);
    }

    /**
     * 我的项目列表
     * @param sceneId 场景ID
     * @param projectId 项目ID
     * @return
     */
    @ApiOperation(value = "项目列表",notes = "我的项目列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sceneId",value = "场景ID(ALL;ID)",required = true,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "projectId",value = "项目ID",required = false,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "userName",value = "用户",required = true,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "pageNo",value = "当前页数",required = true,dataType = "int",paramType = "query"),
            @ApiImplicitParam(name = "limit",value = "每页数据量",required = true,dataType = "int",paramType = "query")
    })
    @RequestMapping(value = "ownProjectList",method = RequestMethod.POST)
    public BaseResponse queryOwnProjectList(String sceneId,String projectId,String userName,
                                            Integer pageNo, Integer limit){
        return etaiService.queryOwnProjectList(sceneId,projectId,userName,pageNo,limit);
    }

    /**
     * 数据源列表
     * @param sceneId
     * @param sourceId
     * @param userName
     * @param pageNo
     * @param limit
     * @return
     */
    @ApiOperation(value = "数据源列表",notes = "根据条件查询数据源列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sceneId",value = "场景ID(ALL;ID)",required = true,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "sourceId",value ="数据源ID",required =false,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "userName",value = "用户名称",required = true,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "startTime",value = "导入起始时间",required = false,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "endTime",value ="导入结束时间",required =false,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "pageNo",value = "当前页",required = true,dataType = "int",paramType = "query"),
            @ApiImplicitParam(name = "limit",value = "每页数据量",required = true,dataType = "int",paramType = "query")
    })
    @RequestMapping(value = "dataResourceList",method = RequestMethod.POST)
    public BaseResponse queryDataResourceList(String sceneId,String sourceId,String userName,
                                              String startTime,String endTime,Integer pageNo, Integer limit){
        return  etaiService.queryDataResourceList(sceneId,sourceId,userName,startTime,endTime,pageNo,limit);
    }

    /**
     * 删除
     * @param flag project ：项目;dataResource ： 数据源
     * @param id
     * @return
     */
    @ApiOperation(value = "删除数据源",notes = "根据ID删除数据源")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flag",value = "删除标签（项目:project；数据源:dataResource）",required = true,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "id",value = "资源ID",required = true,dataType = "String",paramType = "query")
    })
    @RequestMapping(value = "deleteById",method = RequestMethod.POST)
    public BaseResponse deleteProjectById(String flag,String id){

        return etaiService.deleteProjectById(flag,id);
    }


    /**
     * 获取流程中所有组件
     * @param processId
     * @return
     */
    @ApiOperation(value = "流程展示",notes = "获取流程数据（流程参数；组件参数；组件之间关系）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processId",value = "流程ID",required = true,dataType = "String",paramType = "query")
    })
    @RequestMapping(value = "processCmpts",method = RequestMethod.GET)
    public  BaseResponse queryProcessCmpts(
            @RequestParam(value = "processId",required = true) String processId){
        return etaiService.queryProcessCmpts(processId);
    }

    /**
     * 获取组件参数
     * @param pcmptId 流程组件ID
     * @return
     */
    /*@ApiOperation(value = "流程组件参数",notes = "根据流程组件ID 获取其相应参数")
    @RequestMapping(value = "cmptParams",method = RequestMethod.GET)
    public BaseResponse queryCmptParams(String pcmptId){
        return etaiService.queryCmptParams(pcmptId);
    }
*/

    /**
     * 获取转换字段
     * @param pcmptId
     * @return
     */
    /*@RequestMapping(value = "cmptTransformCol",method = RequestMethod.POST)
    public BaseResponse queryCmptTransformCol(String pcmptId){ return etaiService.queryCmptTransformCol(pcmptId);}
*/
    /**
     * 上传文件到ftp
     * @param file
     * @return
     */
    @ApiOperation(value = "文件上传",notes = "上传文件")
    @RequestMapping(value = "uploadFile",method = RequestMethod.POST)
    public BaseResponse uploadFile(@RequestHeader("file") MultipartFile file){

        return etaiService.uploadFile(file);
    }



    /**
     * 文件下载
     * @param remotePath
     * @param response
     * @return
     */
    @ApiOperation(value = "文件下载",notes = "根据路径下载文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "remotePath",value = "文件路径",required = true,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "storageId",value = "存储ID",required = true,dataType = "String",paramType = "query")
    })
    @RequestMapping(value = "downloadFile",method = RequestMethod.POST)
    public BaseResponse downloadFile( String remotePath,String storageId,HttpServletResponse response){
        return  etaiService.downloadFile(remotePath,storageId,response);
    }

    /**
     * 预览
     * @param filePath
     * @param separatorCol
     * @param separatorRow
     * @param readRows
     * @param isContent
     */

    /*@RequestMapping(value = "previewFile",method = RequestMethod.POST)
    public BaseResponse  readFileByChars(String filePath,String separatorCol,String separatorRow,String readRows,
                                         String isContent){

        return etaiService.readFileByChars(filePath,separatorCol,separatorRow,readRows,isContent);
    }*/

    /**
     * 预览文件
     * @param filePath
     * @param separatorCol
     * @param separatorRow
     * @param previewRows
     * @param isContent
     * @param sourceId
     * @param storageId
     * @param metadataId
     * @param flag
     * @return
     */
    @ApiOperation(value = "文件预览",notes = "预览文件（数据源录入；列表处;流程运行结果处）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "filePath",value = "文件路径",required = false,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "separatorCol",value ="列分隔符",required =false,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "separatorRow",value = "行分隔符",required = false,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "previewRows",value = "预览行数",required = false,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "isContent",value ="是否包含表头",required =true,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "sourceId",value = "资源ID",required = false,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "storageId",value ="存储ID",required =false,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "metadataId",value ="元数据ID",required =false,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "flag",value = "预览文件类型(EXCELL;CSV;OTHER)",required = true,dataType = "String",paramType = "query")
    })
    @RequestMapping(value = "previewFiles",method = RequestMethod.POST)
    public BaseResponse previewFiews(String filePath,String separatorCol,String separatorRow,String previewRows,
                                     String isContent,String sourceId,String storageId,String metadataId,String flag){

        if(!StringUtils.isEmpty(filePath)){//数据源录入；运行结果中的预览
            if(StringUtils.isEmpty(previewRows)){
                previewRows = "10000";
            }

            if(org.apache.commons.lang3.StringUtils.equals("EXCELL",flag)){
                return  etaiService.previewExcellFile(storageId,filePath,isContent,previewRows);
            } else if (org.apache.commons.lang3.StringUtils.equals("CSV",flag)){
                return etaiService.previewCSVFile(filePath,storageId,previewRows,isContent);
            } else if (org.apache.commons.lang3.StringUtils.equals("OTHER",flag)){
                return etaiService.readFileByChars(filePath,separatorCol,separatorRow,previewRows,isContent,storageId);
            }

        }else{//列表中的预览
                return etaiService.readFileOnServer(sourceId,storageId,metadataId,flag);
        }
        return null;
    }
    /**
     * 录入数据源
     * @param sceneId
     * @param sceneName
     * @param urlPath
     * @param separatorCol
     * @param separatorRow
     * @param isContent
     * @param userName
     * @param listCol
     * @param flag
     * @param metadataId
     * @return
     */
    @ApiOperation(value = "录入数据源",notes = "创建新的数据源")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sceneId",value = "场景ID（ALL;ID）",required = false,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "sceneName",value ="场景名称",required =false,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "urlPath",value = "文件路径",required = false,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "separatorCol",value = "列分隔符",required = false,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "separatorRow",value ="行分隔符（只能是\r,\n或\r\n）",required =false,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "isContent",value = "是否包含表头",required = true,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "userName",value ="用户",required =true,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "listCol",value ="表头（每行以;分割 每列以,分割 自定义导入必填）",required =false,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "flag",value = "导入类型（场景导入：sceneImport；自定义导入：selfImport）",required = true,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "metadataId",value = "元数据ID",required = false,dataType = "String",paramType = "query")
    })

    @RequestMapping(value = "insertDataResource",method = RequestMethod.POST)
    public BaseResponse insertDataResource(String sceneId, String sceneName, String  urlPath,
                                           String separatorCol, String separatorRow, String isContent,
                                           String userName, String listCol, String flag,String metadataId){

        return etaiService.insertDataResource(sceneId,sceneName,urlPath,separatorCol,separatorRow,isContent,
                userName,listCol,flag,metadataId);
    }

    /**
     * 根据资源ID 和 存储ID 预览文件
     * @param sourceId
     * @param storageId
     * @return
     *//*
    @RequestMapping(value = "previewFileOnServer",method = RequestMethod.POST)
    public  BaseResponse previewFileOnServer(String sourceId,String storageId,String flag){
        return etaiService.readFileOnServer(sourceId,storageId,flag);
    }*/


    /**
     * 根据场景获取源文件路径
     * @param sceneId
     * @return
     */
    @ApiOperation(value = "获取文件路径",notes = "根据场景获取源文件路径")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sceneId",value = "场景ID",required = true,dataType = "String",paramType = "query")
    })
    @RequestMapping(value = "systemDataSourcePath",method = RequestMethod.POST)
    public  BaseResponse systemDataSource(String sceneId){
        return etaiService.systemDataSource(sceneId);
    }


    /**
     * 新建流程
     * @param inputProcessBean
     * @return
     */
    @ApiOperation(value = "新建流程",notes = "创建新流程")
    @RequestMapping(value = "saveProcess",method = RequestMethod.POST)
    public BaseResponse saveProcess(
            @RequestBody InputProcessBean inputProcessBean){

       return etaiService.saveProcess(inputProcessBean);
   }

    /**
     * 基础组件接口类型
     *
     * @return
     */
    @ApiOperation(value = "基础组件接口类型",notes = "前台需要获取基础组件类型（几进几出）")
    @RequestMapping(value = "baseCmptIntfType",method = RequestMethod.GET)
    public BaseResponse baseCmptIntfType(){

        return etaiService.baseCmptIntfType();
    }

    /**
     * 运行流程
     * @param processId
     * @return
     */
    @ApiOperation(value = "运行流程",notes = "运行流程")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processId",value = "流程ID",required = true,dataType = "String",paramType = "query")
    })
    @RequestMapping(value = "runProcess",method = RequestMethod.GET)
    public BaseResponse runProcess(String processId){

        return etaiService.runProcess(processId);
    }

    /**
     * 预览CSV文件
     * @param path
     * @param storageId
     * @return
     */
    /*@RequestMapping(value = "previewCSVFile",method = RequestMethod.GET)
    public  BaseResponse previewCSVFile(String path, String storageId,String previewRows,String isContent){
        return etaiService.previewCSVFile(path,storageId,previewRows,isContent);
    }*/

    /**
     * 发布模型
     * @param userName
     * @param processId
     * @param forecastName
     * @return
     */
    @ApiOperation(value = "发布模型",notes = "发布模型")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userName",value = "用户",required = true,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "processId",value = "模型ID",required = true,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "forecastName",value = "模型名称",required = true,dataType = "String",paramType = "query")
    })
    @RequestMapping(value = "publishProcess",method = RequestMethod.POST)
    public  BaseResponse publishProcess(String userName,String processId,String forecastName){

        String forecastId = etaiService.beforePublishProcess(userName,processId,forecastName);
        if(!StringUtils.isEmpty(forecastId)){

            return etaiService.publishProcess(userName,forecastId,forecastName);
        }else{
             BaseResponse b = new  BaseResponse();
             b.setMsg("发布前 录入数据到预测表出错！");
             return  b;
        }

    }

    /**
     * 预测模型
     * @param forecastId
     * @param userName
     * @param sourceId
     * @return
     */
    @ApiOperation(value = "预测模型",notes = "预测模型")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "forecastId",value = "模型ID",required = true,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "userName",value = "用户",required = true,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "sourceId",value = "资源ID",required = true,dataType = "String",paramType = "query")
    })
    @RequestMapping(value = "forecastProcess",method = RequestMethod.POST)
    public  BaseResponse forecastProcess(String forecastId,String userName,String sourceId){

        BaseResponse response = new BaseResponse();
        if(org.apache.commons.lang3.StringUtils.isBlank(forecastId) || org.apache.commons.lang3.StringUtils.isBlank(userName)
                || org.apache.commons.lang3.StringUtils.isBlank(sourceId)){

            response.setMsg("预测模型ID 或 资源ID 或 用户名 不能为空！");
            response.setSuccess(false);
            return response;
        }else{
            String id = etaiService.beforeforecastProcess(userName,forecastId,sourceId);

            return etaiService.forecastProcess(id);
        }


    }

    /**
     * 流程运行结果
     * @param processId
     * @return
     */
    @ApiOperation(value = "流程运行结果",notes = "获取流程运行结果")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processId",value = "流程ID",required = true,dataType = "String",paramType = "query")
    })
    @RequestMapping(value = "getProcessResult",method = RequestMethod.GET)
    public  BaseResponse getProcessResult(String processId){
        return etaiService.getProcessResult(processId);
    }


    /**
     * 流程预测结果
     * @param forecastId
     * @return
     */
    @ApiOperation(value = "流程预测结果",notes = "获取流程预测结果")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "forecastId",value = "模型ID",required = true,dataType = "String",paramType = "query")
    })
    @RequestMapping(value = "getForecastResult",method = RequestMethod.GET)
    public  BaseResponse getForecastResult(String forecastId){
        return etaiService.getForecastResult(forecastId);
    }

    /**
     * 修改保存流程
     * @param inputProcessBean
     * @return
     */
    @ApiOperation(value = "修改保存流程",notes = "修改保存")
    @RequestMapping(value = "updateProcess",method = RequestMethod.POST)
    public BaseResponse updateProcess(
            @RequestBody InputProcessBean inputProcessBean){

        return etaiService.updateProcess(inputProcessBean);
    }

    /**
     * 预览图片
     * @param remotePath
     * @param response
     * @return
     */
    @ApiOperation(value = "预览图片",notes = "预览图片")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "remotePath",value = "路径",required = true,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "storageId",value = "存储ID",required = true,dataType = "String",paramType = "query")
    })
    @RequestMapping(value = "previewPicture",method = RequestMethod.GET)
    public BaseResponse previewPicture( String remotePath,String storageId,HttpServletResponse response){
        return  etaiService.previewPicture(remotePath,storageId,response);
    }

    /**
     * 预览Excell
     * @param storageId
     * @param filePath
     * @param isContent
     * @param previewRows
     * @return
     */
    /*@RequestMapping(value = "previewExcellFile",method = RequestMethod.POST)
    public BaseResponse previewExcellFile(String storageId,String filePath,String isContent,String previewRows){
        return  etaiService.previewExcellFile(storageId,filePath,isContent,previewRows);
    }*/

    /**
     * 验证名称重复
     * @param flag
     * @param userName
     * @param forecastName
     * @return
     *//*
    @RequestMapping(value = "repeatProcessName",method = RequestMethod.POST)
    public BaseResponse repeatProcessName(String flag, String userName, String forecastName){
        return etaiService.repeatProcessName(flag,userName,forecastName);
    }*/


    /*@RequestMapping(value = "login",method = RequestMethod.POST)
    public BaseResponse login(String user, String pwd){

    }
*/
}
