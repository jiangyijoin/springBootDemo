package com.eastcom_sw.etai.service.impl;

import com.eastcom_sw.etai.StringList;
import com.eastcom_sw.etai.bean.*;
import com.eastcom_sw.etai.dao.EtaiDao;
import com.eastcom_sw.etai.response.BaseResponse;
import com.eastcom_sw.etai.service.EtaiService;
import com.eastcom_sw.etai.utils.FtpUtil;
import com.eastcom_sw.etai.utils.RestfulUtil;
import com.eastcom_sw.frm.core.entity.Page;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import netscape.javascript.JSObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.eastcom_sw.etai.utils.FtpUtil.getCellFormatValue;

/**
 * @author Sunk
 * @create 2018-1-18-16:00
 **/
@Component
@Transactional
public class EtaiServiceImpl implements EtaiService {

    //基本路径
    private String FTP_BASEPATH = "/home/etai/skai";

    @Autowired
    private EtaiDao etaiDao;

    private org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());



    @Override
    public List<JSObject> queryTest(String testId) {

        List<JSObject> list = new ArrayList<>();

        try{
            list = etaiDao.queryTest(testId);
        }catch(Exception e){
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public BaseResponse queryAlScenesList(String userName) {

        List<JSObject> list = new ArrayList<>();
        BaseResponse response = new BaseResponse();

        boolean success = true;
        String msg = "数据查询成功";
        try{
            list = etaiDao.queryProcessList(userName);
        }catch(Exception e){
            e.printStackTrace();
            success = false;
            msg = e.getMessage();
        }
        response.setSuccess(success);
        response.setMsg(msg);
        response.setData(list);
        return response;
    }

    @Override
    public List<JSObject> queryAIScenesById(String id) {
        return null;
    }

    @Override
    public List<JSObject> queryParams(String flag, String sceneId,String userName) {
        List<JSObject> list = new ArrayList<>();

        try{
            list = etaiDao.queryParams(flag,sceneId,userName);
        }catch(Exception e){
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public BaseResponse queryPredictModel(String sceneId, String forecastId, String startTime,
                                          String endTime,Integer pageNo, Integer limit) {
        Page page=null;
        BaseResponse response = new BaseResponse();
        boolean success = true;
        String msg = "数据查询成功";
        if(StringUtils.isNotBlank(startTime) && StringUtils.isBlank(endTime)){
            Date date = new Date();
            SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
            endTime = fmt.format(date);
        }
        if(StringUtils.isNoneBlank(sceneId)){
            try{
                page  = etaiDao.queryPredictModel(sceneId,forecastId,startTime,endTime,pageNo, limit);
            }catch(Exception e){
                e.printStackTrace();
                success = false;
                msg = e.getMessage();
            }
        } else{
            success = false;
            msg = "参数不能为空！";
        }
        response.setSuccess(success);
        response.setMsg(msg);
        response.setData(page);
        return response;
    }

    @Override
    public BaseResponse queryOwnProjectList(String sceneId, String projectId,String userName,
                                            Integer pageNo, Integer limit) {
        Page page = null;
        BaseResponse response = new BaseResponse();
        boolean success = true;
        String msg = "数据查询成功";
        try{
            page = etaiDao.queryOwnProjectList(sceneId,projectId,userName,pageNo,limit);
        }catch(Exception e){
            e.printStackTrace();
            success = false;
            msg = e.getMessage();
        }
        response.setSuccess(success);
        response.setMsg(msg);
        response.setData(page);
        return response;
    }

    @Override
    public BaseResponse queryPredictHistory(String forecastId, Integer pageNo,Integer limit) {
        Page page=null;
        BaseResponse response = new BaseResponse();
        boolean success = true;
        String msg = "数据查询成功";

        try{
            if(pageNo != null){
                page = etaiDao.queryPredictHistory(forecastId,pageNo,limit == null? 10:limit);
            }else{
                success = false;
                msg = "分页参数PageNo不能为空！";
            }
        }catch(Exception e){
            e.printStackTrace();
            success = false;
            msg = e.getMessage();
        }
        response.setSuccess(success);
        response.setMsg(msg);
        response.setData(page);
        return response;
    }

    @Override
    public BaseResponse queryDataResourceList(String sceneId, String sourceId, String userName,String startTime,
                                              String endTime,Integer pageNo, Integer limit) {
        Page page=null;
        BaseResponse response = new BaseResponse();
        boolean success = true;
        String msg = "数据查询成功";



        if(StringUtils.isNotBlank(startTime) && StringUtils.isBlank(endTime)){
            Date date = new Date();
            SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
            endTime = fmt.format(date);
        }
        try{
            if(pageNo != null){
                page = etaiDao.queryDataResourceList(sceneId,sourceId,userName,startTime,endTime,
                        pageNo,limit==null?10:limit);
            }else{
                success = false;
                msg = "分页参数PageNo不能为空！";
            }
        }catch(Exception e){
            e.printStackTrace();
            success = false;
            msg = e.getMessage();
        }
        response.setSuccess(success);
        response.setMsg(msg);
        response.setData(page);
        return response;
    }

    @Override
    public BaseResponse deleteProjectById(String flag, String id) {
        BaseResponse response = new BaseResponse();
        boolean success = true;
        String msg = "数据删除成功";

        try{
            int[] is = etaiDao.deleteProjectById(flag,id);
            if(is.length<0){msg = "无相应数据被删除,请查看有无对应数据存在";}
//            for(int i: is){if(i < 0){ msg = "无相应数据被删除";}}
        }catch(Exception e){
            e.printStackTrace();
            success = false;
            msg = e.getMessage();
        }
        response.setSuccess(success);
        response.setMsg(msg);
        return response;
    }

    @Override
    public BaseResponse queryProcessCmpts(String processId) {

        BaseResponse response = new BaseResponse();
        boolean success = true;
        String msg = "数据查询成功";
        Map<String,Object> process = new HashMap<>();
        List<Object> cmptsList = new ArrayList<>();
        List<Object> cmptRelationlst = new ArrayList<>();


        Set<String> cmptIds = new HashSet<>();

        List<Map<String,Object>> list = new ArrayList<>();
        try{

            List processInfo = etaiDao.queryProcessById(processId);
            Iterator pinfo = processInfo.iterator();
            if(pinfo.hasNext()){
                Map map = (Map) pinfo.next();
                for(Object entry : map.keySet()){
                    process.put((String) entry,map.get(entry));
                }
                //元数据字段
                List collst = etaiDao.queryMetadataCol((String) map.get("SOURCE_ID"));
                List metaDataCollst = new ArrayList();
                for(Iterator col = collst.iterator(); col.hasNext();){
                    Map mapcol = (Map) col.next();
                    Map metaDataCol = new HashMap();
                    for(Object entry : mapcol.keySet()){
                        metaDataCol.put(entry,mapcol.get(entry));
                    }
                    metaDataCollst.add(metaDataCol);
                }
                process.put("metaDataCollst",metaDataCollst);
            }else{
                response.setSuccess(false);
                response.setMsg("数据库中无流程"+processId+"相应数据");
                return response;

            }

            list = etaiDao.queryProcessCmptsRel(processId);

            //获取流程中所有组件ID
            String pcmptId =(String) list.get(0).get("PCMPT_ID");
            for(int i=0;i<list.size();i++){
                cmptIds.add((String) list.get(i).get("PCMPT_ID"));
            }
            for (Iterator<String> it = cmptIds.iterator(); it.hasNext(); ) {
                String id = it.next();
                Map<String, Object> cmpt = new HashMap<>();
                List repeatCmptlst = new ArrayList();
                List lst = new ArrayList<>();
                Map<String, Object> cmptInfo = new HashMap<>();
                List<Map<String, Object>> cmptIntflst = new ArrayList<>();


                for (int i = 0; i < list.size(); i++) {

                    Map<String, Object> targetCmpt = new HashMap<>();
                    Map<String, Object> sourceCmpt = new HashMap<>();
                    Map<String, Object> cmptRelation = new HashMap<>();
                    if(StringUtils.equals(id,(String)list.get(i).get("PCMPT_ID"))){

                        //组件基本信息
                        if(!repeatCmptlst.contains(list.get(i).get("PCMPT_ID"))){
                            repeatCmptlst.add(list.get(i).get("PCMPT_ID"));
                            //组件信息
                            cmptInfo.put("PCMPT_ID", (String) list.get(i).get("PCMPT_ID"));
                            cmptInfo.put("BCMPT_ID", list.get(i).get("BCMPT_ID"));
                            cmptInfo.put("PCMPT_NAME", list.get(i).get("PCMPT_NAME"));
                            cmptInfo.put("SOURCE_ID", list.get(i).get("SOURCE_ID"));
                            cmptInfo.put("LAYOUT_X", list.get(i).get("LAYOUT_X"));
                            cmptInfo.put("LAYOUT_Y", list.get(i).get("LAYOUT_Y"));
                            cmptInfo.put("IS_PROCESS_ENTRANCE", list.get(i).get("IS_PROCESS_ENTRANCE"));
                            cmptInfo.put("BCMPT_ICON", list.get(i).get("BCMPT_ICON"));
                            cmptInfo.put("BCMPT_CLASS", list.get(i).get("BCMPT_CLASS"));
                            cmptInfo.put("IS_NEED_SOURCE",list.get(i).get("IS_NEED_SOURCE"));
                            cmptInfo.put("INTF_DIRECTION", list.get(i).get("INTF_DIRECTION"));

                            //组件选取字段转换规则
                            /*List<Map<String,Object>> transeformRulelst = new ArrayList<>();
                            List transeformRules = etaiDao.transformRules((String) list.get(i).get("BCMPT_ID"));
                            for(Iterator rules = transeformRules.iterator(); rules.hasNext();){
                                Map map = (Map) rules.next();
                                Map<String,Object> outMap = new HashMap<>();
                                for(Object entry : map.keySet()){
                                    outMap.put((String) entry,map.get(entry));
                                }
                                transeformRulelst.add(outMap);
                            }
                            cmptInfo.put("transeformRulelst",transeformRulelst);*/

                            //转换字段
                            List<Map<String,Object>> transeformlst = new ArrayList<>();
                            List transformField = etaiDao.transformField((String) list.get(i).get("PCMPT_ID"),
                                    (String) list.get(i).get("BCMPT_ID"));
                            for(Iterator field = transformField.iterator(); field.hasNext();){
                                Map map = (Map) field.next();
                                Map outMap = new HashMap();
                                for(Object entry : map.keySet()){
                                    outMap.put(entry,map.get(entry));
                                }
                                transeformlst.add(outMap);
                            }
                            cmptInfo.put("transeformlst",transeformlst);

                            //参数信息
                            List  plst = etaiDao.queryCmptParams((String) list.get(i).get("PCMPT_ID"));
                            List<Map<String,Object>> paramslst = new ArrayList<>();
                            for(Iterator<JSObject> pit=plst.iterator();pit.hasNext();){
                                Map  map = (Map) pit.next();
                                Map<String,Object> outmap = new HashMap<>();
                                for(Object entry : map.keySet()){
                                    outmap.put((String) entry,map.get(entry));
                                }
                                outmap.put("PCMPT_PARAM_KEY",map.get("BCMPT_PARAM_KEY"));
                                outmap.remove("PCMPT_ID");
                                paramslst.add(outmap);
                            }
                            cmptInfo.put("paramslst",paramslst);
                        }

                        //组件接口信息
                        String valueIntf = String.valueOf(list.get(i).get("INTF_DIRECTION"))
                                +String.valueOf(list.get(i).get("INTF_ORDER"));
                        if(!lst.contains(valueIntf) && list.get(i).get("INTF_DIRECTION")!=null){
                            Map<String, Object> cmptIntf = new HashMap<>();
                            cmptIntf.put("INTF_DIRECTION", list.get(i).get("INTF_DIRECTION"));
                            cmptIntf.put("INTF_ORDER", list.get(i).get("INTF_ORDER"));
                            cmptIntf.put("INTF_DESC", list.get(i).get("INTF_DESC"));
                            cmptIntf.put("IS_MUST", list.get(i).get("IS_MUST"));
                            cmptIntflst.add(cmptIntf);
                            lst.add(valueIntf);
                        }

                        //组件关系
                        if(list.get(i).get("FATHER_PCMPT_ID") == null){
                            continue;
                        }
                        targetCmpt.put("PCMPT_ID",list.get(i).get("PCMPT_ID"));
                        targetCmpt.put("PCMPT_INTF_ORDER", list.get(i).get("PCMPT_INTF_ORDER"));
                        targetCmpt.put("INTF_DESC", list.get(i).get("INTF_DESC"));

                        //父组件
                        sourceCmpt.put("FATHER_PCMPT_ID", list.get(i).get("FATHER_PCMPT_ID"));
                        sourceCmpt.put("FATHER_PCMPT_INTF_ORDER", list.get(i).get("FATHER_PCMPT_INTF_ORDER"));
                        //relationId 此数据暂无作用
//                        cmptRelation.put("relationId",list.get(i).get("RELATION_ID"));
                        cmptRelation.put("targetCmpt", targetCmpt);
                        cmptRelation.put("sourceCmpt", sourceCmpt);

                        cmptRelationlst.add(cmptRelation);
                    }
                }

                cmptInfo.put("cmptIntf", cmptIntflst);
                cmpt.put("info", cmptInfo);
                cmptsList.add(cmpt);

            }
            process.put("relationlst",cmptRelationlst);
            process.put("cmptlst",cmptsList);
        }catch(Exception e){

            e.printStackTrace();
            success = false;
            msg = e.getMessage();
        }

        response.setSuccess(success);
        response.setMsg(msg);
        response.setData(process);
        return response;
    }

    @Override
    public BaseResponse queryCmptParams(String pcmptId) {
        BaseResponse response = new BaseResponse();
        boolean success = true;
        String msg = "数据查询成功";
        List<JSObject> list = new ArrayList<>();
        try{

            list = etaiDao.queryCmptParams(pcmptId);
        }catch(Exception e){

            e.printStackTrace();
            success = false;
            msg = e.getMessage();
        }

        response.setSuccess(success);
        response.setMsg(msg);
        response.setData(list);
        return response;
    }

    @Override
    public BaseResponse queryCmptTransformCol(String pcmptId) {
        BaseResponse response = new BaseResponse();
        boolean success = true;
        String msg = "数据查询成功";
        String parentId = null;
//        List<JSObject> listParentCmpt = new ArrayList<>();
        List listOutputFields ;
        List<JSObject> list = new ArrayList<>();
        List<Map<String,Object>> metadataColList = new ArrayList<>();
        List<Map<String,Object>> outPutFields = new ArrayList<>();
        String sourceId=null;

        try{
            do{
                parentId = etaiDao.queryParentCmpt(pcmptId);
                listOutputFields =etaiDao.queryOutputFields(parentId);
                sourceId = etaiDao.querySourceId(parentId);
                if(StringUtils.isNoneBlank(sourceId)){break;}
            }while ( listOutputFields.size()<0);

            if(listOutputFields.size()<0){//输入为元数据
                metadataColList = etaiDao.queryMetadataCol(sourceId);//输入字段
                listOutputFields =etaiDao.queryOutputFields(pcmptId);//组件字段转换设置
                Iterator it = listOutputFields.iterator();
                while (it.hasNext()){
                    Map<String,Object> outMap = new HashMap<>();
                    Map map = (Map) it.next();
                    outMap.put("isMust",map.get("IS_MUST"));//是否必须
                    outMap.put("retainOrcol",map.get("RETAIN_ORCOL"));//是否保留原字段
                    outMap.put("inputColKey",map.get("INPUT_COL_KEY"));//输入字段key
                    outMap.put("transformDesc",map.get("TRANSFORM_DESC"));//转换定义描述
                    outMap.put("chooseFileds",metadataColList);
                    if(0 == map.get("OUTPUT_TAG")){//输出字段个数标签(多输入1输出)

                        outMap.put("nameKey","目标列");
                        outMap.put("nameValue",map.get("OUTPUT_NAME_BASE"));
                    }
                    outPutFields.add(outMap);
                }

            } else{//输入字段为parentId 组件输出字段

                //推测出parentId输出字段  作为当前组件输入字段
                listOutputFields =etaiDao.queryOutputFields(parentId);//获取父组件输入字段根据输出规则推算输出字段

                //查询当前组件转换设置  展示
                Iterator it = listOutputFields.iterator();
                while (it.hasNext()){

                    Map map = (Map) it.next();
                    map.get("");

                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        response.setSuccess(success);
        response.setMsg(msg);
        response.setData(list);
        return response;
    }


    @Override
    public BaseResponse uploadFile(MultipartFile uploadFile) {


        BaseResponse response = new BaseResponse();
        boolean success = true;
        String msg = "文件上传成功";
        String urlPath="";

        try {
            // 获取旧的名字
            String oldName = uploadFile.getOriginalFilename();
            Date now = new Date( );
            SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd");
            String uploadPath = ft.format(now);

            //上传服务器信息
            String storageId = etaiDao.queryStorageId();
            Map serviceInfoMap = getServiceInfo(storageId);
            String ip =(String) serviceInfoMap.get("ip");
            String user = (String) serviceInfoMap.get("user");
            String password = (String) serviceInfoMap.get("password");
            int port = (Integer)serviceInfoMap.get("port");

            //调用方法，上传文件
            boolean result = FtpUtil.uploadFile(ip, port,
                    user, password, FTP_BASEPATH, uploadPath,
                    oldName, uploadFile.getInputStream());
            //判断是否上传成功
            if (!result) {
                success = false;
                msg = "上传失败!";
            }
            urlPath = FTP_BASEPATH+"/"+uploadPath+"/"+oldName;

        } catch (IOException e) {
            success = false;
            msg = "上传发生异常!";
        }

        response.setMsg(msg);
        response.setSuccess(success);
        response.setData(urlPath);
        return response;
    }


    @Override
    public BaseResponse downloadFile(String path,String storageId, HttpServletResponse response) {


        BaseResponse responseBase = new BaseResponse();
        boolean success = true;
        String msg = "文件下载成功";
        String fileName = path.substring(path.lastIndexOf("/")+1);
        path = path.substring(0,path.lastIndexOf("/"));

        Map<String,Object> serviceInfoMap = getServiceInfo(storageId);

        FtpUtil.downloadFile((String) serviceInfoMap.get("ip"),(Integer)serviceInfoMap.get("port"),
                (String) serviceInfoMap.get("user"),(String) serviceInfoMap.get("password"),path,fileName,response);
        responseBase.setMsg(msg);
        responseBase.setSuccess(success);
        return responseBase;
    }

    @Override
    public BaseResponse readFileByChars(String filePath, String separatorCol, String separatorRow, String readRows,
                                        String isContent,String storageId) {
        BaseResponse response = new BaseResponse();
        boolean success = true;
        String msg = "预览";
        Map<String,Object> serviceInfoMap = getServiceInfo(storageId);
        int previewRows=Integer.parseInt(readRows);
        if(StringUtils.equals("true",isContent)){previewRows = Integer.parseInt(readRows)+1;}
        String fileName = filePath.substring(filePath.lastIndexOf("/")+1);
        filePath = filePath.substring(0,filePath.lastIndexOf("/"));
        List<String> list = FtpUtil.readFileOnServer((String) serviceInfoMap.get("ip"),(Integer)serviceInfoMap.get("port"),
                (String) serviceInfoMap.get("user"),(String) serviceInfoMap.get("password"),filePath,
                fileName, separatorCol, separatorRow, previewRows);


        List<Map<String,Object>> returnData = new ArrayList<>();


        if(StringUtils.equals("true",isContent)){//包含表头字段

            String[] titleArr = list.get(0).split(",");
            for(int i=1; i<list.size(); i++){
                String[] arr = list.get(i).split(",");
                Map<String,Object> map = new LinkedHashMap<>();
                for(int j =0;j<titleArr.length;j++){
                    map.put(titleArr[j],arr[j]);
                }
                returnData.add(map);
            }

        }else{
            for(int i=0; i<list.size(); i++){
                String[] arr = list.get(i).split(",");
                Map<String,Object> map = new LinkedHashMap<>();
                for(int j =0;j<arr.length;j++){
                    map.put("Filed_"+j,arr[j]);
                }
                returnData.add(map);
            }
        }
        response.setMsg(msg);
        response.setSuccess(success);
        response.setData(returnData);
        return response;
    }

    @Override
    public BaseResponse insertDataResource(String sceneId, String sceneName, String urlPath,
                                           String separatorCol, String separatorRow, String isContent,
                                           String userName, String listCol, String flag,String metadataId) {

        BaseResponse response = new BaseResponse();
        boolean success = true;
        String msg = "录入数据源文件成功!";
        String fileName = urlPath.substring(urlPath.lastIndexOf("/")+1);
//        urlPath = urlPath.substring(0,urlPath.lastIndexOf("/"));

        try{
            //获取存储表默认存储ID
            String storageId = etaiDao.queryStorageId();

            //录入数据源表相关参数
            String sourceId = etaiDao.insertDataResource(sceneId,sceneName,urlPath,fileName,separatorCol,separatorRow,
                    isContent,userName,storageId);
            if(StringUtils.equals("selfImport",flag) ){//自定义导入

                if(listCol.isEmpty()){msg = "自定义导入没有表头数据导致录入数据失败!";}else{

                    StringList list = new StringList();
                    List<String> str= new ArrayList<>();
                    String[] arrs = listCol.split(";");
                    for(int i=0;i<arrs.length;i++){
                        str.add(arrs[i]);
                    }
                    list.setListStr(str);

                    //录入元数据
                    etaiDao.insertMetadata(sourceId,fileName,list);
                }
            }else{//场景导入
                if(StringUtils.isNotBlank(metadataId)){
                    //录入关系表
                    etaiDao.insertSourceMetadataRelation(sourceId,metadataId);
                }else{
                    response.setMsg("未获得元数据ID ！场景导入 必须要有元数据ID");
                    response.setSuccess(false);
                    return response;
                }
            }
        }catch(Exception e){
            success = false;
            msg = "录入数据失败!";
        }
        response.setMsg(msg);
        response.setSuccess(success);
        return response;
    }

    @Override
    public BaseResponse readFileOnServer(String sourceId,String storageId,String metadataId,String flag) {


        BaseResponse response = new BaseResponse();
        boolean success = true;
        String msg = "预览!";

        String remotePath = null,
                fileName = null,separatorCol = null,separatorRow = null,
                readRows="10",isContent = null;

        List<String> listCol = new ArrayList<>();

        //获取表头(资源保存时保存过表头信息)
        if(StringUtils.isNotBlank(metadataId)){
            listCol = getMetadataCol(metadataId);
        }

        //资源ID 获取分隔符,url信息
        List<Map<String, Object>> listParam = etaiDao.querySourceParam(sourceId);
        for (Map<String,Object> map : listParam) {
            if(StringUtils.equals("path",(String)map.get("SOURCE_PARAM_KEY"))){
                remotePath = (String) (String)map.get("SOURCE_PARAM_VALUE");
            }
            if(StringUtils.equals("separatorRow",(String)map.get("SOURCE_PARAM_KEY"))){
                separatorRow = (String) (String)map.get("SOURCE_PARAM_VALUE");
            }
            if(StringUtils.equals("separatorCol",(String)map.get("SOURCE_PARAM_KEY"))){
                separatorCol = (String) (String)map.get("SOURCE_PARAM_VALUE");
            }
            if(StringUtils.equals("isContent",(String)map.get("SOURCE_PARAM_KEY"))){
                isContent = (String) (String)map.get("SOURCE_PARAM_VALUE");
            }
        }

        //文件服务器信息
        Map serviceInfoMap = getServiceInfo(storageId);
        String ip =(String) serviceInfoMap.get("ip");
        String user = (String) serviceInfoMap.get("user");
        String password = (String) serviceInfoMap.get("password");
        int port = (Integer)serviceInfoMap.get("port");

        int previewRows=Integer.parseInt(readRows);
        if(StringUtils.equals("true",isContent)){previewRows = Integer.parseInt(readRows)+1;}
        fileName = remotePath.substring(remotePath.lastIndexOf("/")+1);
        remotePath = remotePath.substring(0,remotePath.lastIndexOf("/"));

        List<Map<String,Object>> returnData = new ArrayList<>();

        if(StringUtils.equals("EXCELL",flag)){

            Workbook wb = FtpUtil.readExcel(ip,port,user,password,fileName,remotePath);
            if(wb != null){
                //获取第一个sheet
                Sheet sheet = wb.getSheetAt(0);
                //获取最大行数
                int rownum = sheet.getPhysicalNumberOfRows();

                //获取第一行
                Row row = sheet.getRow(0);
                //获取最大列数
                int colnum = row.getPhysicalNumberOfCells();

                int i = 0;
                if(StringUtils.equals("true",isContent)){//包含表头

                    i = 1;
                    if(!StringUtils.isBlank(readRows)){
                        rownum = Integer.parseInt(readRows)+1;
                    }
                    if( listCol==null || listCol.size()<1){//以元数据字段为准
                        for(int j=0;j<colnum;j++){
                            listCol.add((String) getCellFormatValue(row.getCell(j)));
                        }
                    }

                }else{
                    if(!StringUtils.isBlank(readRows)){
                        rownum = Integer.parseInt(readRows);
                    }
                }
                for (; i<rownum; i++) {
                    Map<String,Object> map = new LinkedHashMap<String,Object>();
                    row = sheet.getRow(i);
                    if(row !=null){
                        for (int j=0;j<colnum;j++){
                            String cellData = (String) getCellFormatValue(row.getCell(j));
                            map.put(listCol.get(j), cellData);
                        }
                    }else{
                        break;
                    }
                    returnData.add(map);
                }
            }

        }else if (StringUtils.equals("CSV",flag)){

            List<String> list = FtpUtil.readCSVFile(ip,port,user,password,fileName,remotePath,
                    readRows,isContent);
            if(StringUtils.equals("false",isContent)){//文件不含表头
                //去掉添加表头
                list.remove(0);
            }else{
                if(listCol==null || listCol.size()<1){//以元数据字段为准
                    String[] title = list.get(0).split(",");
                    for(String str : title){
                        listCol.add(str);
                    }
                }
            }

            getPreviewDatas(readRows, listCol, returnData, list);
        }else if (StringUtils.equals("OTHER",flag)){

            List<String> list = FtpUtil.readFileOnServer(ip,port,user,password,remotePath,
                    fileName, separatorCol, separatorRow, previewRows);

            if(StringUtils.equals("true",isContent)){//包含表头字段
                if(listCol == null ||listCol.size()<1){//以元数据字段为准
                    String[] title = list.get(0).split(separatorCol);
                    for(String str:title){
                        listCol.add(str);
                    }
                }
                list.remove(0);//去掉原表头
            }
            getPreviewDatas(readRows, listCol, returnData, list);
        }

        response.setMsg(msg);
        response.setData(returnData);
        response.setSuccess(success);
        return response;
    }

    private void getPreviewDatas(String readRows, List<String> listCol, List<Map<String, Object>> returnData,
                                 List<String> list) {
        if(list.size() > Integer.parseInt(readRows)){//读取预览行数
            for(int i=0;i<Integer.parseInt(readRows);i++){
                String[] arr = list.get(i).split(",");
                Map<String,Object> map = new LinkedHashMap<>();
                for(int j = 0; j < listCol.size();j++){
                    map.put(listCol.get(j),arr[j]);
                }
                returnData.add(map);
            }

        }else{//读取所有
            for(int i=0; i<list.size();i++){
                String[] arr = list.get(i).split(",");
                Map<String,Object> map = new LinkedHashMap<>();
                for(int j =0;j<listCol.size();j++){
                    map.put(listCol.get(j),arr[j]);
                }
                returnData.add(map);
            }
        }
    }

    /**
     *  根据资源ID 获取元数据(表头)
     * @param metadataId
     * @return
     */
    private List<String> getMetadataCol(String metadataId) {
        List<String> listCol = new ArrayList<>();

        List<Map<String, Object>> listTitle = etaiDao.queryMetadataCol(metadataId);
        for(Map<String,Object> map : listTitle){
            if(map.containsKey("COL_NAME")){
                listCol.add((String) map.get("COL_NAME"));
            }
        }
        return listCol;
    }

    @Override
    public BaseResponse systemDataSource(String sceneId) {

        BaseResponse response = new BaseResponse();
        boolean success = true;
        String msg = "模板文件路径查询成功!";
        List list = new ArrayList();
        try{
            list = etaiDao.systemDatasource(sceneId);
        }catch (Exception e){
            success = false;
            msg = "模板文件路径查询失败!";
        }

        response.setMsg(msg);
        response.setData(list);
        response.setSuccess(success);
        return response;
    }

    @Override
    public BaseResponse saveProcess(InputProcessBean inputProcessBean) {


        BaseResponse response = new BaseResponse();
        boolean success = true;
        String msg = "数据录入成功！";

        try{


            /*String data = request.getParameter("data");
            Enumeration eunm = request.getParameterNames();
            for(Enumeration e = eunm;eunm.hasMoreElements(); ){
                String name = e.nextElement().toString();
                String value = request.getParameter(name);
                System.out.print("Parameters:   name="+name+";value="+value);

            }*/
            /*JSONObject  dataObject1 = JSONObject.fromObject(data);
            JSONObject  dataObject = dataObject1.getJSONObject("data");
            String PROCESS_NAME = dataObject.getString("PROCESS_NAME");
            String USERNAME = dataObject.getString("USERNAME");
            String PROCESS_DESC = dataObject.getString("PROCESS_DESC");
            String DATA_DESC = dataObject.getString("DATA_DESC");
            String SOURCE_ID = dataObject.getString("SOURCE_ID");
            String PROCESS_ICON = dataObject.getString("PROCESS_ICON");
            String SCENE_ID = dataObject.getString("SCENE_ID");
            String SCENE_NAME = dataObject.getString("SCENE_NAME");*/

            String PROCESS_NAME = inputProcessBean.getProcessName();
            String USERNAME = inputProcessBean.getUsername();
            String PROCESS_DESC = inputProcessBean.getProcessDesc();
            String DATA_DESC = inputProcessBean.getDataDesc();
            String SOURCE_ID = inputProcessBean.getSourceId();
            String PROCESS_ICON = inputProcessBean.getProcessIcon();
            String SCENE_ID = inputProcessBean.getSceneId();
            String SCENE_NAME = inputProcessBean.getSceneName();
            int i = etaiDao.repeatProcessName("process",SCENE_NAME,PROCESS_NAME);
            if(i > 0){
                response.setMsg("流程名称"+PROCESS_NAME+"已经存在！");
                response.setSuccess(false);
                return response;
            }

            //录入流程基础数据
            String processId = etaiDao.saveProcessOnProcess(PROCESS_NAME,USERNAME,PROCESS_DESC,DATA_DESC,
                    SOURCE_ID,PROCESS_ICON,SCENE_ID,SCENE_NAME);

            insertProcessRelationDatas(inputProcessBean, processId);

        }catch (Exception e){
            msg = "数据录入异常！";
            success = false;
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }

        response.setMsg(msg);
        response.setSuccess(success);
        return response;
    }

    @Override
    public BaseResponse baseCmptIntfType() {

        BaseResponse response = new BaseResponse();
        boolean success = true;
        String msg = "数据查询成功！";
        List<Map<String,Object>> cmptIntfLis = new ArrayList<>();

        try{

            List list = etaiDao.baseCmptIntfType();
            Set<String> set = new HashSet<>();
            for(Iterator cmpt=list.iterator();cmpt.hasNext();){
                Map cmptMap = (Map) cmpt.next();
                set.add((String) cmptMap.get("BCMPT_ID"));
            }

            for(String bcmptIdSet : set){

                int inIntf = 0;
                int outIntf = 0;
                Map<String,Object> outMap = new HashMap<>();
                for(Iterator cmpt=list.iterator();cmpt.hasNext();){

                    Map cmptMap = (Map) cmpt.next();
                    String bcmptId = (String) cmptMap.get("BCMPT_ID");

                    if(StringUtils.equals(bcmptId,bcmptIdSet)){
                        if(0 == (Integer) cmptMap.get("INTF_DIRECTION")){//传入接口
                            ++inIntf;
                        }else if(1 == (Integer) cmptMap.get("INTF_DIRECTION")){//输出接口
                            ++outIntf;
                        }
                    }
                }
                outMap.put(bcmptIdSet,inIntf+"进"+outIntf+"出");
                cmptIntfLis.add(outMap);
            }

        }catch (Exception e){
            msg = "数据查询异常！";
            success = false;
            e.printStackTrace();
        }
        response.setMsg(msg);
        response.setData(cmptIntfLis);
        response.setSuccess(success);
        return response;
    }

    @Override
    public BaseResponse runProcess(String processId) {

        List<Map<String,Object>> processResultList = new ArrayList<>();

        BaseResponse response = new BaseResponse();
        boolean success = true;
        String msg = "运行成功！";
        JSONObject data = new JSONObject();

        if(StringUtils.isBlank(processId)){
            response.setMsg("流程ID不能为空！");
            response.setSuccess(false);
            return response;
        }

        try {
            data = RestfulUtil.restfulEtai(RestfulUtil.run(processId));
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
            msg="运行异常原因：调用restful后台接口失败！"+e.getMessage();
        }

        if(200 !=  data.getInt("status")){
            success = false;
            msg="restful后台接口运行异常："+data.getString("msg");
        }

        response.setMsg(msg);
        response.setData(processResultList);
        response.setSuccess(success);
        return response;
    }


    @Override
    public BaseResponse previewCSVFile(String filePath, String storageId,String previewRows,String isContnt) {

        BaseResponse response = new BaseResponse();
        boolean success = true;
        String msg = "预览数据成功！";
        List listDatas = new ArrayList();
        if(StringUtils.isBlank(filePath)){
            success = false;
            msg = "文件路径为空 ！";
            response.setMsg(msg);
            response.setSuccess(success);
            return response;
        }
        try{

            Map serviceInfoMap = getServiceInfo(storageId);
            String fileName = filePath.substring(filePath.lastIndexOf("/")+1);
            filePath = filePath.substring(0,filePath.lastIndexOf("/"));
            List list  = FtpUtil.readCSVFile((String) serviceInfoMap.get("ip"),(Integer)serviceInfoMap.get("port"),
                    (String) serviceInfoMap.get("user"),(String) serviceInfoMap.get("password"),fileName,
                    filePath,previewRows,isContnt);

            Set<String> set = new HashSet<>();
            if(list.size()>0){
                String[] title = list.get(0).toString().split(",");
                for(String t:title){
                    set.add(t);
                }
                for(int i = 1;i < list.size();i++){
                    Map<String,Object> map = new HashMap<>();
                    int j = 0;
                    for(String s:set){
                        map.put(s,list.get(i).toString().split(",")[j++]);
                    }
                    listDatas.add(map);
                }
            }



        }catch (Exception e){
            success = false;
            msg=e.getMessage();
        }

        response.setMsg(msg);
        response.setData(listDatas);
        response.setSuccess(success);
        return response;

    }

    @Override
    public String beforePublishProcess(String userName, String processId, String forecastName) {


        String  forecastId="";
        int c = etaiDao.repeatProcessName("processForecast",userName,forecastName);
        if(c<1){
            Map<String,Object> map = new HashMap<>();
            map.put("PROCESS_ID",processId);

            map.put("FORECAST_NAME",forecastName);
            map.put("USERNAME",userName);

            map.put("RELEASE_STATUS",0);
            forecastId = etaiDao.publicProcess(map);
        }
        return forecastId;
    }

    @Override
    public BaseResponse publishProcess(String userName, String forecastId,String forecastName) {

        BaseResponse response = new BaseResponse();
        boolean success = true;
        String msg = "发布成功！";
        JSONObject data = new JSONObject();

        try {

            data = RestfulUtil.restfulEtai(RestfulUtil.publish(forecastId));
            if(data != null){
                if(data.getInt("status") != 200){
                    success = false;
                    msg="restful后台接口运行异常："+data.getString("msg");
                }
            }else {
                success = false;
                msg="restful后台接口运行异常："+data.getString("msg");
            }

        } catch (Exception e) {
            e.printStackTrace();
            success = false;
            msg="发布异常原因：调用restful后台接口失败！"+e.getMessage();
        }

        response.setMsg(msg);
        response.setSuccess(success);
        return response;
    }

    @Override
    public String beforeforecastProcess(String userName, String forecastId, String sourceId) {


        Map<String,Object> map = new HashMap<>();
        map.put("FORECAST_ID",forecastId);

        map.put("USERNAME",userName);
        map.put("SOURCE_ID",sourceId);

        map.put("FORECAST_RESULT_STATUS",0);
        String forecastResultId = etaiDao.forecastProcess(map);

        return  forecastResultId;
    }

    @Override
    public BaseResponse forecastProcess(String forecastResultId) {

        BaseResponse response = new BaseResponse();
        boolean success = true;
        String msg = "预测成功！";
        JSONObject data = new JSONObject();

        try {

            data = RestfulUtil.restfulEtai(RestfulUtil.forecast(forecastResultId));

            if(200 != data.getInt("status")){
                success = false;
                msg="预测调用restful后台接口运行异常："+data.getString("msg");
            }

        } catch (Exception e) {
            e.printStackTrace();
            success = false;
            msg="预测异常原因：调用restful后台接口失败！"+e.getMessage();
        }

        response.setMsg(msg);
        response.setSuccess(success);
        return response;
    }

    @Override
    public BaseResponse getProcessResult(String processId) {

        BaseResponse response = new BaseResponse();
        boolean success = true;
        String msg = "查询成功！";
        List<Map<String,Object>> list = new ArrayList<>();

        if(StringUtils.isBlank(processId)){
            response.setMsg("流程ID 不能为空！");
            response.setSuccess(false);
            return response;
        }
        try{
            list = processResult(processId);
        }catch (Exception e){
            msg="查询流程运行结果失败！"+e.getMessage();
            success = false;
        }

        response.setMsg(msg);
        response.setData(list);
        response.setSuccess(success);
        return response;
    }

    @Override
    public BaseResponse getForecastResult(String forecastId) {

        BaseResponse response = new BaseResponse();
        boolean success = true;
        String msg = "查询成功！";
        List<Map<String,Object>> list = new ArrayList<>();

        if(StringUtils.isBlank(forecastId)){
            response.setMsg("预测模型ID 不能为空！");
            response.setSuccess(false);
            return response;
        }

        try{
            List listResult = etaiDao.forecastProcessResult(forecastId);
            for(Iterator result = listResult.iterator();result.hasNext();){
                Map map = (Map) result.next();
                Map<String,Object> outMap = new HashMap<>();
                outMap.put("forecastId",forecastId);
                outMap.put("resultStatus",map.get("FORECAST_RESULT_STATUS"));
                JSONObject obj = JSONObject.fromObject(map.get("FORECAST_RESULT_INFO"));
                if(obj.containsKey("exception")){
                    outMap.put("exception",obj.get("exception"));
                }
                if(obj.containsKey("path")){
                    outMap.put("path",obj.get("path"));
                }
                if(obj.containsKey("type")){
                    outMap.put("type",obj.get("type"));
                }
                if(obj.containsKey("storage_id")){
                    outMap.put("storageId",obj.get("storage_id"));
                }
                list.add(outMap);
            }
        }catch (Exception e){

        }
        response.setMsg(msg);
        response.setData(list);
        response.setSuccess(success);
        return response;
    }

    @Override
    public BaseResponse updateProcess(/*HttpServletRequest request*/InputProcessBean inputProcessBean) {

        BaseResponse response = new BaseResponse();
        boolean success = true;
        String msg = "修改保存成功！";

        try{
            /*String data = request.getParameter("data");
            JSONObject  dataObject1 = JSONObject.fromObject(data);
            JSONObject  dataObject = dataObject1.getJSONObject("data");
            String PROCESS_NAME = dataObject.getString("PROCESS_NAME");
            String USERNAME = dataObject.getString("USERNAME");
            String PROCESS_DESC = dataObject.getString("PROCESS_DESC");
            String DATA_DESC = dataObject.getString("DATA_DESC");
            String SOURCE_ID = dataObject.getString("SOURCE_ID");
            String PROCESS_ICON = dataObject.getString("PROCESS_ICON");
            String SCENE_ID = dataObject.getString("SCENE_ID");
            String SCENE_NAME = dataObject.getString("SCENE_NAME");
            String PROCESS_ID = dataObject.getString("PROCESS_ID");*/

            String PROCESS_NAME = inputProcessBean.getProcessName();
            String USERNAME = inputProcessBean.getUsername();
            String PROCESS_DESC = inputProcessBean.getProcessDesc();
            String DATA_DESC = inputProcessBean.getDataDesc();
            String SOURCE_ID = inputProcessBean.getSourceId();
            String PROCESS_ICON = inputProcessBean.getProcessIcon();
            String SCENE_ID = inputProcessBean.getSceneId();
            String SCENE_NAME = inputProcessBean.getSceneName();
            String PROCESS_ID = inputProcessBean.getProcessId();


            etaiDao.updateProcessOnProcess(PROCESS_ID,PROCESS_NAME,USERNAME,PROCESS_DESC,DATA_DESC,
                    SOURCE_ID,PROCESS_ICON,SCENE_ID,SCENE_NAME);

            List list = etaiDao.pcmptIdsOnProcess(PROCESS_ID,"processCmptId");
            List<String> cmptIdlist = new ArrayList<>();
            for(Iterator cmptIds = list.iterator();cmptIds.hasNext();){
                String cmptId = (String) cmptIds.next();
                cmptIdlist.add(cmptId);
            }
            etaiDao.deleteProcessOnRelation(cmptIdlist);
            etaiDao.deleteProcessOnCmptParams(cmptIdlist);
            etaiDao.deleteProcessOnCmpt(PROCESS_ID);
            etaiDao.deleteRrocessOnTranseformFields(cmptIdlist);


            insertProcessRelationDatas(inputProcessBean, PROCESS_ID);

        }catch (Exception e){

            success = false;
            msg = "修改保存失败！"+e.getMessage();
            e.printStackTrace();
        }

        response.setMsg(msg);
        response.setSuccess(success);
        return response;
    }

    @Override
    public BaseResponse previewPicture(String path,String storageId, HttpServletResponse response) {
        BaseResponse responseBase = new BaseResponse();
        boolean success = true;
        String msg = "预览！";

        String fileName = path.substring(path.lastIndexOf("/")+1);
        path = path.substring(0,path.lastIndexOf("/"));

        Map serviceInfoMap = getServiceInfo(storageId);
        FtpUtil.previewPicture((String) serviceInfoMap.get("ip"),(Integer)serviceInfoMap.get("port"),
                (String) serviceInfoMap.get("user"),(String) serviceInfoMap.get("password"),path,fileName,response);
        responseBase.setMsg(msg);
        responseBase.setSuccess(success);
        return responseBase;
    }

    @Override
    public BaseResponse previewExcellFile(String storageId, String path,String isContent,String previewRows) {

        BaseResponse responseBase = new BaseResponse();
        boolean success = true;
        String msg = "修改保存成功！";

        List<String> titleLst = new ArrayList<>();
        Map serviceMap =  getServiceInfo(storageId);
        Workbook wb =null;
        Sheet sheet = null;
        Row row = null;
        List<Map<String,String>> list = null;
        String cellData = null;
        String fileName = path.substring(path.lastIndexOf("/")+1);
        path = path.substring(0,path.lastIndexOf("/"));
        wb = FtpUtil.readExcel((String) serviceMap.get("ip"),(Integer)serviceMap.get("port"),
                (String) serviceMap.get("user"),(String) serviceMap.get("password"),fileName,path);
        if(wb != null){
            //用来存放表中数据
            list = new ArrayList<Map<String,String>>();
            //获取第一个sheet
            sheet = wb.getSheetAt(0);
            //获取最大行数
            int rownum = sheet.getPhysicalNumberOfRows();

            //获取第一行
            row = sheet.getRow(0);
            //获取最大列数
            int colnum = row.getPhysicalNumberOfCells();

            int i = 0;
            if(StringUtils.equals("true",isContent)){//包含表头
                i = 1;
                for(int j=0;j<colnum;j++){
                    titleLst.add((String) getCellFormatValue(row.getCell(j)));
                }

                if(!StringUtils.isBlank(previewRows)){
                    rownum = Integer.parseInt(previewRows)+1;
                }
            }else{
                if(!StringUtils.isBlank(previewRows)){
                    rownum = Integer.parseInt(previewRows);
                }
                for (int j = 0; j < colnum; j++)
                titleLst.add("Filed_"+j);
            }
            for (; i<rownum; i++) {
                Map<String,String> map = new LinkedHashMap<String,String>();
                row = sheet.getRow(i);
                if(row !=null){
                    for (int j=0;j<colnum;j++){
                        cellData = (String) getCellFormatValue(row.getCell(j));
                        map.put(titleLst.get(j), cellData);
                    }
                }else{
                    break;
                }
                list.add(map);
            }
        }

        responseBase.setData(list);
        responseBase.setMsg(msg);
        responseBase.setSuccess(success);
        return responseBase;

    }


    @Override
    public BaseResponse repeatProcessName(String flag, String userName, String forecastName) {

        BaseResponse response = new BaseResponse();
        boolean success = true;
        String msg = "";
        int c = etaiDao.repeatProcessName("processForecast",userName,forecastName);
        if(c > 0 ){
            response.setMsg("模型名称 重复！");
            response.setSuccess(false);
            return response;
        }
        response.setMsg(msg);
        response.setSuccess(success);
        return response;
    }

    /**
     * 组件关系数据录入（参数；接口关系）
     * @param inputProcessBean
     * @param PROCESS_ID
     */
    private void insertProcessRelationDatas(/*JSONObject dataObject,*/InputProcessBean inputProcessBean, String PROCESS_ID) {

        /*JSONArray infolst = JSONArray.fromObject(dataObject.getString("infolst"));
        JSONArray relationlst = JSONArray.fromObject(dataObject.getString("relationlst"));*/

        List<info> infolst = inputProcessBean.getInfolst();
        for(info info:infolst){

            String PCMPT_ID = info.getPcmptId();
            String PCMPT_NAME = info.getPcmptName();
            String BCMPT_ID = info.getBcmptId();
            String CMPT_SOURCE_ID = info.getSourceId();
            BigDecimal LAYOUT_X = info.getLayoutX();
            BigDecimal LAYOUT_Y = info.getLayoutY();
            int IS_PROCESS_ENTRANCE = info.getIsProcessEntrance();


            List<Map<String,Object>> paramsList = new ArrayList<>();

            List<params> paramslst = info.getParamslst();
            for(params param:paramslst){
                Map<String,Object> map = new HashMap<>();
                map.put("BCMPT_PARAM_ID",param.getBcmptParamId());
                map.put("PCMPT_PARAM_KEY",param.getPcmptParamKey());
                map.put("PCMPT_PARAM_VALUE",param.getPcmptParamValue());
                paramsList.add(map);
            }


            List<transeform> transeformlst = info.getTranseformlst();
            List<Map<String,Object>> transeformList = new ArrayList<>();
            for(transeform transeform:transeformlst){
                Map<String,Object> map = new HashMap<>();
                map.put("TRANSFORM_ID",transeform.getTransformId());
                map.put("RETAIN_ORCOL",transeform.getRetainOrcol());
                map.put("INPUT_COL_KEY",transeform.getInputColKey());
                map.put("INPUT_COL_VALUE",transeform.getInputColValue());
                map.put("OUTPUT_NAME",transeform.getOutputName());
                transeformList.add(map);
            }

            //录入流程组件数据（包含组件参数）
            etaiDao.saveProcessOnCmpt(PCMPT_ID,PROCESS_ID,PCMPT_NAME,BCMPT_ID,CMPT_SOURCE_ID,LAYOUT_X,LAYOUT_Y,
                    IS_PROCESS_ENTRANCE,paramsList,transeformList);
        }

        List<Map<String,Object>> relationList = new ArrayList<>();

        List<relation> relationlst = inputProcessBean.getRelationlst();
        for(relation relation:relationlst){

            targetCmpt targetCmpt = relation.getTargetCmpt();
            sourceCmpt sourceCmpt = relation.getSourceCmpt();

            Map<String,Object> map = new HashMap<>();
            map.put("PCMPT_ID",targetCmpt.getPcmptId());
            map.put("PCMPT_INTF_ORDER",targetCmpt.getPcmptIntfOrder());
            map.put("FATHER_PCMPT_INTF_ORDER",sourceCmpt.getFatherPcmptIntfOrder());
            map.put("FATHER_PCMPT_ID",sourceCmpt.getFatherPcmptId());

            relationList.add(map);

        }
        //录入组件关系
        etaiDao.saveProcessOnRelation(relationList);




       /* for(Iterator<JSONObject> infoIterator = infolst.iterator(); infoIterator.hasNext();){

            JSONObject infoJson = JSONObject.fromObject(infoIterator.next());
            String PCMPT_ID = infoJson.getString("PCMPT_ID");
            String PCMPT_NAME = infoJson.getString("PCMPT_NAME");
            String BCMPT_ID = infoJson.getString("BCMPT_ID");
            String CMPT_SOURCE_ID = infoJson.getString("SOURCE_ID");
            String LAYOUT_X = infoJson.getString("LAYOUT_X");
            String LAYOUT_Y = infoJson.getString("LAYOUT_Y");
            String IS_PROCESS_ENTRANCE = infoJson.getString("IS_PROCESS_ENTRANCE");

            JSONArray paramslst = JSONArray.fromObject(infoJson.getString("paramslst"));

            List<Map<String,Object>> paramsList = new ArrayList<>();
            for(Iterator<JSONObject> paramsIterator = paramslst.iterator(); paramsIterator.hasNext();){

                JSONObject params = JSONObject.fromObject(paramsIterator.next());
                Map<String,Object> map = new HashMap<>();
                map.put("BCMPT_PARAM_ID",params.getString("BCMPT_PARAM_ID"));
                map.put("PCMPT_PARAM_KEY",params.getString("PCMPT_PARAM_KEY"));
                map.put("PCMPT_PARAM_VALUE",params.getString("PCMPT_PARAM_VALUE"));
                paramsList.add(map);
            }

            JSONArray transeformlst = JSONArray.fromObject(infoJson.getString("transeformlst"));
            List<Map<String,Object>> transeformList = new ArrayList<>();
            for(Iterator<JSONObject> transeformIterator = transeformlst.iterator();transeformIterator.hasNext();){

                JSONObject transeform = JSONObject.fromObject(transeformIterator.next());
                Map<String,Object> map = new HashMap<>();
                map.put("TRANSFORM_ID",transeform.getString("TRANSFORM_ID"));
                map.put("RETAIN_ORCOL",transeform.getString("RETAIN_ORCOL"));
                map.put("INPUT_COL_KEY",transeform.getString("INPUT_COL_KEY"));
                map.put("INPUT_COL_VALUE",transeform.getString("INPUT_COL_VALUE"));
                map.put("OUTPUT_NAME",transeform.getString("OUTPUT_NAME"));
                transeformList.add(map);
            }


            //录入流程组件数据（包含组件参数）
            etaiDao.saveProcessOnCmpt(PCMPT_ID,PROCESS_ID,PCMPT_NAME,BCMPT_ID,CMPT_SOURCE_ID,LAYOUT_X,LAYOUT_Y,
                    IS_PROCESS_ENTRANCE,paramsList,transeformList);
        }

        List<Map<String,Object>> relationList = new ArrayList<>();
        for(Iterator<JSONObject> relationItreator = relationlst.iterator();relationItreator.hasNext();){

            Map<String,Object> map = new HashMap<>();
            JSONObject relationJson = JSONObject.fromObject(relationItreator.next());
            JSONObject targetCmpt = JSONObject.fromObject(relationJson.getString("targetCmpt"));
            JSONObject sourceCmpt = JSONObject.fromObject(relationJson.getString("sourceCmpt"));

            map.put("PCMPT_ID",targetCmpt.getString("PCMPT_ID"));
            map.put("PCMPT_INTF_ORDER",targetCmpt.getString("PCMPT_INTF_ORDER"));
            map.put("FATHER_PCMPT_INTF_ORDER",sourceCmpt.getString("FATHER_PCMPT_INTF_ORDER"));
            map.put("FATHER_PCMPT_ID",sourceCmpt.getString("FATHER_PCMPT_ID"));

            relationList.add(map);
        }
        //录入组件关系
        etaiDao.saveProcessOnRelation(relationList);*/
    }

    private List processResult(String processId){

        List<Map<String,Object>> processResultList = new ArrayList<>();

        List list = etaiDao.processResult(processId);
        for(Iterator it = list.iterator();it.hasNext();){
            Map map = (Map) it.next();
            Map<String,Object> outMap = new HashMap<>();
            outMap.put("pcmptId",map.get("PCMPT_ID"));
            outMap.put("resultCode",map.get("RESULT_CODE"));
            if(1 != (Integer)map.get("RESULT_CODE")){//运行中
                JSONObject resultInfoJSONObject = JSONObject.fromObject(map.get("RESULT_INFO"));
                if(resultInfoJSONObject.containsKey("moduleCsvPreview")){
                    outMap.put("csvPath",resultInfoJSONObject.getString("moduleCsvPreview"));
                }
                if(resultInfoJSONObject.containsKey("moduleTxtPreview")){
                    outMap.put("txtPath",resultInfoJSONObject.getString("moduleTxtPreview"));
                }
                if(resultInfoJSONObject.containsKey("modulePicPreview")){
                    outMap.put("picPath",resultInfoJSONObject.getString("modulePicPreview"));
                }
                if(resultInfoJSONObject.containsKey("storage_id")){
                    outMap.put("storageId",resultInfoJSONObject.getString("storage_id"));
                }
                if(resultInfoJSONObject.containsKey("exception")){
                    outMap.put("exception",resultInfoJSONObject.getString("exception"));
                }
            }

            processResultList.add(outMap);
        }

        return processResultList;
    }


    /**
     * 获取服务器相关信息
     * @param storageId
     * @return
     */
    private Map getServiceInfo(String storageId){

        String ip ="10.8.132.223";
        String username = "root";
        String password = "eastcom";
        int port =21;

        //根据存储ID 获取服务器相关信息数据
        List<Map<String, Object>> listStorageParam = etaiDao.queryStorageParam(storageId);
        for (Map<String,Object> map : listStorageParam) {
            if(StringUtils.equals("ip",(String)map.get("STORAGE_PARAM_KEY"))){
                ip = (String) map.get("STORAGE_PARAM_VALUE");
            }
            if(StringUtils.equals("user",(String)map.get("STORAGE_PARAM_KEY"))){
                username = (String) map.get("STORAGE_PARAM_VALUE");
            }
            if(StringUtils.equals("password",(String)map.get("STORAGE_PARAM_KEY"))){
                password = (String) map.get("STORAGE_PARAM_VALUE");
            }
            if(StringUtils.equals("port",(String)map.get("STORAGE_PARAM_KEY"))){
                port = Integer.parseInt((String)map.get("STORAGE_PARAM_VALUE"));
            }
        }

        Map<String,Object> map = new HashMap<>();
        map.put("ip",ip);
        map.put("user",username);
        map.put("password",password);
        map.put("port",port);

        return map;
    }

}
