package com.eastcom_sw.etai.dao.impl;

import com.eastcom_sw.etai.StringList;
import com.eastcom_sw.etai.dao.EtaiDao;
import com.eastcom_sw.etai.utils.GetUUID;
import com.eastcom_sw.etai.utils.ToolsUtill;
import com.eastcom_sw.frm.common.utils.ParseJSONObject;
import com.eastcom_sw.frm.core.dao.jpa.DaoImpl;
import com.eastcom_sw.frm.core.entity.Page;
import net.sf.json.JSONObject;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.bouncycastle.asn1.misc.MiscObjectIdentifiers.netscape;

/**
 * 数据接口实现层
 *
 * @author Sunk
 * @create 2018-1-18-14:52
 **/
@Component
@Transactional
public class EtaiDaoImpl extends DaoImpl implements EtaiDao {

    protected org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<JSObject> queryTest(String testId) {

        String sql = "SELECT " +
                " BCMPT_ID,BCMPT_NAME,IS_NEED_RESOURCE,BCMPT_DESC,BCMPT_ICON,BCMPT_CLASS " +
                " FROM ETAI_BASIC_CMPT ";


        log.info("=========================测试sql:"+sql);

        String[] arr = new String[] {"BCMPT_ID","BCMPT_NAME","IS_NEED_RESOURCE","BCMPT_DESC","BCMPT_ICON","BCMPT_CLASS" };
        return getList(sql,arr);
    }

    @Override
    public List<JSObject> queryProcessList(String userName) {

        String whereSql=null;
        if(StringUtils.isNoneEmpty(userName)){

            whereSql = " WHERE USERNAME = '"+userName+"'";
        }

        String sql = " SELECT " +
                " PROCESS_ID, PROCESS_NAME, USERNAME, PROCESS_DESC, DATA_DESC, SOURCE_ID, PROCESS_ICON "+
                " FROM  ETAI_PROCESS";

        sql+=whereSql;

        log.info("============流程列表sql:========="+sql);

        String[] arr = new String[] {"PROCESS_ID", "PROCESS_NAME", "USERNAME", "PROCESS_DESC", "DATA_DESC",
                "SOURCE_ID", "PROCESS_ICON"};

        return getList(sql,arr);
    }


    @Override
    public List queryProcessById(String id) {

        String whereSql = " WHERE PROCESS_ID = '"+id+"'";
        String sql = " SELECT " +
                " PROCESS_ID,SCENE_ID,SCENE_NAME, PROCESS_NAME, USERNAME, PROCESS_DESC, DATA_DESC, SOURCE_ID, PROCESS_ICON "+
                " FROM  ETAI_PROCESS ";
        sql+=whereSql;

        log.info("============根据流程ID获取该流程sql:========="+sql);
        return jdbcTemplate.queryForList(sql);
    }


    @Override
    public List<JSObject> queryParams(String flag, String sceneId,String userName) {

        String whereSql = "";
        String sql = "  ";
        String[] arr=null;
        if(StringUtils.isNoneBlank(sceneId) && !StringUtils.equalsIgnoreCase("ALL",sceneId)){
            whereSql = " AND SCENE_ID = '"+sceneId+"'";
        }
        if(StringUtils.equals("model",flag)){

            sql="SELECT F.FORECAST_ID,F.FORECAST_NAME" +
                    " FROM ETAI_PROCESS_FORECAST F WHERE PROCESS_ID IN(" +
                    "SELECT PROCESS_ID FROM ETAI_PROCESS  WHERE 1=1 "+whereSql+")";
            arr = new String[] {"FORECAST_ID","FORECAST_NAME"};
        }else if(StringUtils.equals("project",flag)){

            sql="SELECT PROCESS_ID,PROCESS_NAME FROM ETAI_PROCESS WHERE USERNAME='"+userName+"'"+whereSql+"" ;
            arr = new String[] {"PROCESS_ID","PROCESS_NAME"};
        }else if(StringUtils.equals("dataSource",flag)){

            sql="SELECT SOURCE_ID,SOURCE_NAME,STORAGE_ID FROM ETAI_SOURCE WHERE 1=1 "+whereSql+ " AND USERNAME='" +userName+"'";
            arr = new String[] {"SOURCE_ID","SOURCE_NAME","STORAGE_ID"};
        }

        log.info("============参数"+flag+"获取sql:========="+sql);
        return getList(sql,arr);
    }


    @Override
    public Page queryPredictModel(String sceneId, String forecastId, String startTime,
                                  String endTime,Integer pageNo, Integer limit) {

        String whereSql = " ";
        if(StringUtils.isNoneBlank(sceneId) && !StringUtils.equals("ALL",sceneId)){
            whereSql = " AND S.SCENE_ID = '"+sceneId+"' ";
        }
        if(StringUtils.isNoneBlank(forecastId)){
            whereSql +=" AND F.FORECAST_ID = '" +forecastId+"' ";
        }
        if(StringUtils.isNoneBlank(startTime) && StringUtils.isNoneBlank(endTime)){
            whereSql +=" AND F.RELEASE_TIME >"+startTime+" AND F.RELEASE_TIME < "+endTime;
        } else if(StringUtils.isBlank(startTime) && StringUtils.isNotBlank(endTime)){
            whereSql += " AND F.RELEASE_TIME <" +endTime;
        }

        String sql = " SELECT F.FORECAST_ID, F.PROCESS_ID, F.FORECAST_NAME, S.PROCESS_NAME, S.SCENE_ID, F.USERNAME," +
                " F.RELEASE_TIME " +
                " FROM ETAI_PROCESS_FORECAST F LEFT JOIN ETAI_PROCESS S ON S.PROCESS_ID = F.PROCESS_ID " +
                " WHERE 1=1" +whereSql;
//        String[] arr = new String[] {"FORECAST_ID","PROCESS_ID","FORECAST_NAME","PROCESS_NAME","SCENE_ID",
// "USERNAME","RELEASE_TIME"};
        log.info("============预测模型列表sql:=========="+sql);

        String str ="FORECAST_ID,PROCESS_ID,FORECAST_NAME,PROCESS_NAME,SCENE_ID,USERNAME,RELEASE_TIME";

        Page page = pagedSQLQuery(sql, pageNo, limit);
        List rellist = page.getElements();
        List<JSONObject> jsonList = ParseJSONObject.parse(str, rellist);
        page.setElements(jsonList);
        return page;
    }

    @Override
    public Page queryPredictHistory(String forecastId,Integer pageNo,Integer limit) {

//        List<Object> list = new ArrayList<Object>();

        String whereSql = " ";
        if(StringUtils.isNoneBlank(forecastId)){
            whereSql = " FORECAST_ID = '"+forecastId+"' ";
        }
        String sql=" SELECT * FROM (" +
                " SELECT P.SCENE_NAME,E.FORECAST_NAME " +
                " FROM ETAI_PROCESS_FORECAST E " +
                " LEFT JOIN ETAI_PROCESS P ON P.PROCESS_ID = E.PROCESS_ID " +
                " WHERE E."+whereSql+") B " +
                " LEFT JOIN ( " +
                " SELECT S.SOURCE_NAME,S.STORAGE_ID,S.SOURCE_ID,F.USERNAME,F.FORECAST_RESULT_TIME, F.FORECAST_RESULT_INFO, " +
                " P1.SOURCE_PARAM_VALUE AS PATH "+
                " FROM ETAI_PROCESS_FORECAST_RESULT F LEFT JOIN ETAI_SOURCE S ON F.SOURCE_ID = S.SOURCE_ID" +
                " LEFT JOIN (SELECT * FROM ETAI_SOURCE_PARAM P where P.SOURCE_PARAM_KEY='path') P1 ON F.SOURCE_ID = P1.SOURCE_ID " +
                " WHERE " +
                " F.FORECAST_RESULT_STATUS =1 " +
                " AND F."+whereSql+
                " ) A ON 1=1";

        String str="SCENE_NAME,FORECAST_NAME,SOURCE_NAME,STORAGE_ID,SOURCE_ID,USERNAME,FORECAST_RESULT_TIME," +
                "FORECAST_RESULT_INFO,PATH";

        log.info("==============模型预测历史sql:========="+sql);
//        Page page = pagedSQLQuery(sql, pageNo, limit,list.toArray());
        Page page = pagedSQLQuery(sql, pageNo, limit);
        List rellist = page.getElements();
        List<JSONObject> jsonList = ParseJSONObject.parse(str, rellist);
        page.setElements(jsonList);
        return page;
    }

    @Override
    public Page queryOwnProjectList(String sceneId, String projectId,String userName,
                                              Integer pageNo, Integer limit) {

        String whereSql = " ";
        if(StringUtils.isNoneBlank(sceneId) && !StringUtils.equals("ALL",sceneId)){
            whereSql = " AND SCENE_ID = '"+sceneId+"' ";
        }
        if(StringUtils.isNoneBlank(projectId)){
            whereSql+= " AND PROCESS_ID = '"+projectId+"' ";
        }
        if(StringUtils.isNoneBlank(userName)){
            whereSql+=" AND USERNAME='"+userName+"' ";
        }
        String sql = " SELECT  PROCESS_ID,PROCESS_NAME,SCENE_NAME,UPDATE_TIME,USERNAME FROM ETAI_PROCESS " +
                " WHERE 1=1 " +whereSql;
//        String[] arr = new String[] {"PROCESS_ID","PROCESS_NAME","SCENE_NAME","UPDATE_TIME","USERNAME"};
        String str="PROCESS_ID,PROCESS_NAME,SCENE_NAME,UPDATE_TIME,USERNAME";
        log.info("============我的项目列表sql:=========="+sql);
        Page page = pagedSQLQuery(sql, pageNo, limit);
        List rellist = page.getElements();
        List<JSONObject> jsonList = ParseJSONObject.parse(str, rellist);
        page.setElements(jsonList);
        return page;
    }

    @Override
    public Page queryDataResourceList(String sceneId, String sourceId, String userName,
                                      String startTime,String endTime,Integer pageNo, Integer limit) {

        List<Object> list = new ArrayList<Object>();
        String whereSql="";
        if(!StringUtils.equals("ALL",sceneId)){
            whereSql = " AND R.SCENE_ID = '"+sceneId+"' ";
        }
        if(StringUtils.isNotBlank(sourceId)){
            whereSql += " AND R.SOURCE_ID = '"+sourceId+"' ";
        }
        if(StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)){
            whereSql += " AND R.CREATE_TIME BETWEEN "+startTime+" AND "+endTime;
        } else if(StringUtils.isBlank(startTime) && StringUtils.isNotBlank(endTime)){
            whereSql += " AND R.CREATE_TIME <" +endTime;
        }
        whereSql += " AND R.USERNAME = '"+userName+"' ";
        String sql =" SELECT R.SOURCE_ID,R.SOURCE_NAME,R.STORAGE_ID,R.SCENE_NAME,R.USERNAME,R.CREATE_TIME,M.METADATA_ID" +
                " FROM ETAI_SOURCE  R LEFT JOIN ETAI_SOURCE_METADATA_RELATION M ON R.SOURCE_ID = M.SOURCE_ID " +
                " WHERE 1=1" +whereSql;
        String str = "SOURCE_ID,SOURCE_NAME,STORAGE_ID,SCENE_NAME,USERNAME,CREATE_TIME,METADATA_ID";

        log.info("==============数据源列表 sql:========="+sql);
        Page page = pagedSQLQuery(sql, pageNo, limit,list.toArray());
        List rellist = page.getElements();
        List<JSONObject> jsonList = ParseJSONObject.parse(str, rellist);
        page.setElements(jsonList);
        return page;
    }

    @Override
    public int[] deleteProjectById(String flag, String id) {

        String sql = " ";
        int[] i = null;

        if(StringUtils.equals("project",flag)){

            sql = "DELETE FROM ETAI_PROCESS WHERE PROCESS_ID ='"+id+"' ";
            i = jdbcTemplate.batchUpdate(sql);

        }
        if(StringUtils.equals("dataResource",flag)){

            i = jdbcTemplate.batchUpdate("DELETE FROM ETAI_SOURCE_PARAM WHERE SOURCE_ID = '"+id+"'",
                        "DELETE FROM ETAI_SOURCE WHERE SOURCE_ID = '"+id+"'");
        }
        return i;
    }

    @Override
    public List<Map<String, Object>> queryProcessCmptsRel(String processId) {

        String sql = " SELECT A.*,R.RELATION_ID,R.PCMPT_INTF_ORDER,R.FATHER_PCMPT_ID,R.FATHER_PCMPT_INTF_ORDER  FROM " +
                " (SELECT  " +
                " E.BCMPT_ID,E.PCMPT_NAME,E.PCMPT_ID,E.SOURCE_ID,E.PROCESS_ID,E.LAYOUT_X,E.LAYOUT_Y,E.IS_PROCESS_ENTRANCE," +
                " B.BCMPT_ICON,B.BCMPT_CLASS,B.IS_NEED_SOURCE,C.INTF_DIRECTION,C.INTF_ORDER,C.INTF_DESC,C.IS_MUST" +
                " FROM (" +
                " SELECT * FROM ETAI_PROCESS_CMPT WHERE PROCESS_ID = '"+processId+"')E" +
                " LEFT JOIN ETAI_BASIC_CMPT B ON E.BCMPT_ID = B.BCMPT_ID" +
                " LEFT JOIN ETAI_BASIC_CMPT_INTF C ON E.BCMPT_ID = C.BCMPT_ID AND  C.INTF_DIRECTION = 0) A" +
                " LEFT JOIN ETAI_PROCESS_CMPT_RELATION R ON A.PCMPT_ID = R.PCMPT_ID AND  A.INTF_ORDER = R.PCMPT_INTF_ORDER";


        log.info("=============获取流程中所有组件======"+sql);
        return jdbcTemplate.queryForList(sql);
    }

   /* @Override
    public List<Map<String, Object>> queryProcessCmptsIntf(String processId) {

    }*/

    @Override
    public List queryCmptParams(String pcmptId) {

        String sql = "SELECT " +
                "    P.PCMPT_ID," +
                "P.PCMPT_PARAM_KEY," +
                "P.PCMPT_PARAM_VALUE," +
                "B.BCMPT_PARAM_ID," +
                "    B.BCMPT_PARAM_NAME," +
                "B.BCMPT_PARAM_ORDER," +
                "B.BCMPT_PARAM_KEY," +
                "B.BCMPT_PARAM_DVALUE," +
                "B.IS_MUST," +
                "B.BCMPT_PARAM_DESC," +
                "B.BCMPT_PARAM_TYPE," +
                "B.BCMPT_PARAM_VALUE_UP," +
                "B.BCMPT_PARAM_VALUE_DOWN," +
                "B.BCMPT_PARAM_VALUE_ENUM FROM ETAI_BASIC_CMPT_PARAM B " +
                "LEFT JOIN ETAI_PROCESS_CMPT_PARAM P ON P.BCMPT_PARAM_ID = B.BCMPT_PARAM_ID AND P.PCMPT_ID = '"+pcmptId+"'"+
                "WHERE BCMPT_ID = (SELECT BCMPT_ID FROM ETAI_PROCESS_CMPT WHERE PCMPT_ID = '"+pcmptId+"') ";


        log.info("=============获取组件参数======"+sql);
        return jdbcTemplate.queryForList(sql);
    }

    @Override
    public String  queryParentCmpt(String pcmptId) {

        String sql =" SELECT * FROM queryCmptParams WHERE PCMPT_ID = '"+pcmptId+"' ";
//        String[] arr = new String[]{"RELATION_ID","PCMPT_ID","PCMPT_INTF_ORDER","FATHER_PCMPT_ID","FATHER_PCMPT_INTF_ORDER"};

        log.info("===========查询父组件======" + sql);
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);
        String id = null;
        while (sqlRowSet.next()){
            id = sqlRowSet.getString("FATHER_PCMPT_ID");
        }
        return id;
    }

    @Override
    public List queryOutputFields(String pcmptId) {
        String sql =" SELECT P.TRANSFORM_ID," +
                " P.BCMPT_ID," +
                " P.INPUT_COL_KEY," +
                " P.INPUT_COL_TYPE," +
                " P.INPUT_COL_ORDER," +
                " P.IS_MUST," +
                " P.TRANSFORM_DESC," +
                " P.OUTPUT_TAG," +
                " P.OUTPUT_TYPE," +
                " P.OUTPUT_NAME AS OUTPUT_NAME_BASE," +
                " T.PCMPT_ID," +
                " T.INPUT_COL_VALUE," +
                " T.RETAIN_ORCOL," +
                " T.OUTPUT_NAME" +
                " FROM  ETAI_PROCESS_CMPT_TRANSFORM T " +
                " LEFT JOIN ETAI_CMPT_TRANSFORM P ON T.TRANSFORM_ID = P.TRANSFORM_ID WHERE T.PCMPT_ID = '"+pcmptId+"' "+
                " ORDER BY P.INPUT_COL_ORDER ASC ";

// String[] arr = new String[]{"TRANSFORM_ID","PCMPT_ID","INPUT_COL_KEY",
//        "IS_MUST","TRANSFORM_DESC","OUTPUT_TAG","OUTPUT_TYPE","OUTPUT_NAME","PCMPT_ID",
//        "INPUT_COL_VALUE","RETAIN_ORCOL"};

        log.info("===========查询组件输入设置及输出字段======" + sql);
        List list = jdbcTemplate.queryForList(sql);
        return list;
    }


    @Override
    public String querySourceId(String processId) {

        String sql = " SELECT * FROM ETAI_PROCESS_CMPT WHERE IS_PROCESS_ENTRANCE = 1";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);
        String id = null;
        while (sqlRowSet.next()){
            id = sqlRowSet.getString("SOURCE_ID");
        }
        return id;
    }

    @Override
    public List<Map<String, Object>> queryMetadataCol(String metadataId) {

        String sql = " SELECT H.METADATA_ID,H.METADATA_NAME,C.COL_ID,C.COL_NAME,C.COL_ORDER,C.COL_DESC,C.COL_TYPE " +
                " FROM ETAI_METADATA_COL C RIGHT JOIN( " +
                " SELECT E.METADATA_NAME,E.METADATA_ID FROM  ETAI_METADATA E WHERE E.METADATA_ID ='"+metadataId+"') " +
                "  H ON C.METADATA_ID = H.METADATA_ID ORDER BY C.COL_ORDER";

        log.info("===========根据资源ID 获取元数据字段" + sql);
        return jdbcTemplate.queryForList(sql);
    }

    @Override
    public List<Map<String, Object>> querySourceParam(String sourceId) {
        String sql=" SELECT SOURCE_PARAM_NAME,SOURCE_PARAM_KEY, SOURCE_PARAM_VALUE FROM ETAI_SOURCE_PARAM WHERE SOURCE_ID = '"+sourceId+"' ";

        String[] arr = new String[] {"SOURCE_PARAM_NAME","SOURCE_PARAM_KEY","SOURCE_PARAM_VALUE"};

        log.info("===========根据资源ID 获取资源参数" + sql);
        return jdbcTemplate.queryForList(sql);
    }

    @Override
    public List<Map<String, Object>> queryStorageParam(String storageId) {
        String sql=" SELECT" +
                " P.STORAGE_ID," +
                " P.STORAGE_PARAM_NAME," +
                " P.STORAGE_PARAM_KEY," +
                " P.STORAGE_PARAM_VALUE," +
                " P.STORAGE_PARAM_DESC," +
                " S.STORAGE_NAME," +
                " S.STORAGE_TYPE," +
                " S.STORAGE_DESC" +
                " FROM" +
                " ETAI_STORAGE_PARAM P" +
                " LEFT JOIN ETAI_STORAGE S ON S.STORAGE_ID = P.STORAGE_ID WHERE" +
                " P.STORAGE_ID = '"+storageId+"' ";

        String[] arr = new String[] {"STORAGE_ID","STORAGE_PARAM_NAME","STORAGE_PARAM_KEY","STORAGE_PARAM_VALUE",
        "STORAGE_PARAM_DESC","STORAGE_NAME","STORAGE_TYPE","STORAGE_DESC"};

        log.info("===========根据存储ID 获取存储参数" + sql);
        return jdbcTemplate.queryForList(sql);
    }

    @Override
    public String insertDataResource(String sceneId, String sceneName, String path, String sourceName,
                                     String separatorCol, String separatorRow, String isContent, String userName,
                                     String storageId) {

        String sourceId = GetUUID.getUUID();


        String sql = " INSERT INTO ETAI_SOURCE VALUE('"+sourceId+"','"+sourceName+"','"+storageId+"','"+sceneId+"','"+sceneName+"','" +
                "数据描述','"+userName+"','"+ToolsUtill.getNow()+"')";

        String sqlParam1 = " INSERT INTO ETAI_SOURCE_PARAM VALUE('"+GetUUID.getUUID()+"','导入文件路径','"+sourceId+"'" +
                ",'path','"+path+"')";
        String sqlParam2 = " INSERT INTO ETAI_SOURCE_PARAM VALUE('"+GetUUID.getUUID()+"','行分隔符','"+sourceId+"'" +
                ",'separatorRow','"+separatorRow+"')";
        String sqlParam3 = " INSERT INTO ETAI_SOURCE_PARAM VALUE('"+GetUUID.getUUID()+"','列分隔符','"+sourceId+"'" +
                ",'separatorCol','"+separatorCol+"')";
        String sqlParam4 = " INSERT INTO ETAI_SOURCE_PARAM VALUE('"+GetUUID.getUUID()+"','是否包含列头行','"+sourceId+"'" +
                ",'isContent','"+isContent+"')";
        log.info("================sql==========="+sql);
        log.info("================sqlParam1==========="+sqlParam1);
        log.info("================sqlParam2==========="+sqlParam2);
        log.info("================sqlParam3==========="+sqlParam3);
        log.info("================sqlParam4==========="+sqlParam4);
        jdbcTemplate.batchUpdate(sql,sqlParam1,sqlParam2,sqlParam3,sqlParam4);

        return sourceId;
    }


    /*@Override
    public String insertStorageData(String storageName, Integer storageType) {

        String storageId = GetUUID.getUUID();
        String sql="INSERT INTO ETAI_STORAGE VALUE('"+storageId+"','"+storageName+"',"+storageType+",'存储描述')";

        log.info("============录入存储表数据sql==="+ sql);
        jdbcTemplate.update(sql);
        return storageId;
    }*/

    @Override
    public String queryStorageId() {
        String  sql = " SELECT STORAGE_ID FROM ETAI_STORAGE  WHERE STORAGE_FLAG=1";
        return  jdbcTemplate.queryForObject(sql,String.class);
    }

    @Override
    public String insertMetadata(String sourceId, String metaName, StringList listCol) {


        String metadataId = GetUUID.getUUID();
        String sql =" INSERT INTO ETAI_METADATA VALUE('"+metadataId+"','"+metaName+"')";
        String sqlRel = "INSERT INTO ETAI_SOURCE_METADATA_RELATION VALUE('"+GetUUID.getUUID()
                +"','"+sourceId+"','"+metadataId+"')";
        String sqlMeta_ = " INSERT INTO ETAI_METADATA_COL(COL_ID,METADATA_ID,COL_ORDER,COL_NAME,COL_DESC,COL_TYPE) " +
                "VALUES(?,?,?,?,?,?) ";

        for(int i=0; i<listCol.getListStr().size();i++){

            String[] arrCol = listCol.getListStr().get(i).split(",");
            int k= jdbcTemplate.update(sqlMeta_,new Object[]{GetUUID.getUUID(),metadataId,(i+1),arrCol[0],arrCol[2],arrCol[1]});
            log.info("=========元数据字段录入======="+k);
        }

        log.info("===============元数据录入sql==========："+sql);
        log.info("===============元数据关系数据录入sqlRel==========："+sqlRel);

        jdbcTemplate.batchUpdate(sql,sqlRel);
        return null;
    }

    @Override
    public List<JSObject> systemDatasource(String sceneId) {
        String sql = "SELECT P.*,R.METADATA_ID FROM (SELECT A.SOURCE_ID,A.STORAGE_ID,M.SOURCE_PARAM_KEY," +
                "M.SOURCE_PARAM_VALUE FROM " +
                " (SELECT * FROM ETAI_SOURCE WHERE SCENE_ID = '"+sceneId+"' AND USERNAME ='SYSTEM' ) A" +
                " LEFT JOIN ETAI_SOURCE_PARAM M ON A.SOURCE_ID = M.SOURCE_ID WHERE M.SOURCE_PARAM_KEY='path'"+
                " ) P LEFT JOIN  ETAI_SOURCE_METADATA_RELATION R ON P.SOURCE_ID = R.SOURCE_ID";

        log.info("=============根据场景ID 获取模板源文件路径:========"+sql);

        String[] arr = new String[] {"SOURCE_ID","STORAGE_ID","SOURCE_PARAM_KEY","SOURCE_PARAM_VALUE","METADATA_ID"};
        return getList(sql,arr);

    }

    @Override
    public String saveProcessOnProcess(String PROCESS_NAME, String USERNAME, String PROCESS_DESC, String DATA_DESC,
                              String SOURCE_ID, String PROCESS_ICON, String SCENE_ID, String SCENE_NAME) {

        String processId = GetUUID.getUUID();
        String sql = " INSERT INTO ETAI_PROCESS VALUE('"+processId+"','"+PROCESS_NAME+"','"+SCENE_ID+"'" +
                ",'"+SCENE_NAME+"','"+USERNAME+"','"+PROCESS_DESC+"','"+DATA_DESC+"','"+SOURCE_ID+"','"+PROCESS_ICON+"" +
                "','"+ToolsUtill.getNow()+"')";
        log.info("===============流程录入（流程表）sql:======="+sql);

        jdbcTemplate.update(sql);
        return processId;
    }


    @Override
    public int saveProcessOnCmpt(final String pcmptId, String PROCESS_ID, String PCMPT_NAME, String BCMPT_ID, String SOURCE_ID,
                                 BigDecimal LAYOUT_X, BigDecimal LAYOUT_Y, int IS_PROCESS_ENTRANCE,
                                 final List<Map<String,Object>> list, final List<Map<String,Object>> lst) {

//        final String pcmptId = GetUUID.getUUID();
        String sql = " INSERT INTO ETAI_PROCESS_CMPT VALUE(' "+pcmptId+"','"+PROCESS_ID+"','"+PCMPT_NAME+"','"+BCMPT_ID+"','" +
                SOURCE_ID+"','"+LAYOUT_X+"','"+LAYOUT_Y+"','"+IS_PROCESS_ENTRANCE+"')";
        log.info("===============流程组件数据录入（流程组件表）sql:============="+sql);

        int i = jdbcTemplate.update(sql);
        String sqlParam = "  INSERT INTO ETAI_PROCESS_CMPT_PARAM(PCMPT_PARAM_ID,BCMPT_PARAM_ID,PCMPT_ID,PCMPT_PARAM_KEY," +
                "PCMPT_PARAM_VALUE) VALUES(?,?,?,?,?)";

        String sqlTranseform = " INSERT INTO ETAI_PROCESS_CMPT_TRANSFORM(PTRANSFORM_ID,TRANSFORM_ID,PCMPT_ID," +
                "INPUT_COL_KEY,INPUT_COL_VALUE,OUTPUT_NAME,RETAIN_ORCOL) VALUES(?,?,?,?,?,?,?)";

        jdbcTemplate.batchUpdate(sqlTranseform, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Map<String,Object> lstset =  lst.get(i);
                ps.setString(1, GetUUID.getUUID());
                ps.setString(2, (String) lstset.get("TRANSFORM_ID"));
                ps.setString(3, (String) lstset.get("PCMPT_ID"));
                ps.setString(4, (String) lstset.get("INPUT_COL_KEY"));
                ps.setString(5, (String) lstset.get("INPUT_COL_VALUE"));
                ps.setString(4, (String) lstset.get("OUTPUT_NAME"));
                ps.setString(5, (String) lstset.get("RETAIN_ORCOL"));

            }

            @Override
            public int getBatchSize() {
                return 0;
            }
        });

        jdbcTemplate.batchUpdate(sqlParam,new BatchPreparedStatementSetter(){
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Map<String,Object> linkset =  list.get(i);
                ps.setString(1, GetUUID.getUUID());
                ps.setString(2, (String) linkset.get("BCMPT_PARAM_ID"));
                ps.setString(3, pcmptId);
                ps.setString(4, (String) linkset.get("PCMPT_PARAM_KEY"));
                ps.setString(5, (String) linkset.get("PCMPT_PARAM_VALUE"));
            }

            public int getBatchSize() {
                return list.size();
                //这个方法设定更新记录数，通常List里面存放的都是我们要更新的，所以返回list.size();
            }
        });
        return i;
    }

    @Override
    public int saveProcessOnRelation(final List<Map<String,Object>> list) {

        String sql = "INSERT INTO ETAI_PROCESS_CMPT_RELATION(RELATION_ID,PCMPT_ID,PCMPT_INTF_ORDER," +
                "FATHER_PCMPT_ID,FATHER_PCMPT_INTF_ORDER) VALUE(?,?,?,?,?) ";

        int[] i = jdbcTemplate.batchUpdate(sql,new BatchPreparedStatementSetter(){
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Map<String,Object> linkset =  list.get(i);
                ps.setString(1, GetUUID.getUUID());
                ps.setString(2, (String) linkset.get("PCMPT_ID"));
                ps.setInt(3, (int)linkset.get("PCMPT_INTF_ORDER"));
                ps.setString(4, (String) linkset.get("FATHER_PCMPT_ID"));
                ps.setInt(5,(int) linkset.get("FATHER_PCMPT_INTF_ORDER"));
            }
            public int getBatchSize() {
                return list.size();
                //这个方法设定更新记录数，通常List里面存放的都是我们要更新的，所以返回list.size();
            }
        });
        return i.length > 0 ? i[0] : -1;
    }


    @Override
    public List baseCmptIntfType() {

        String sql = " Select BCMPT_ID,INTF_DIRECTION,INTF_ORDER,IS_MUST FROM ETAI_BASIC_CMPT_INTF";

        log.info("=============基础组件信息============"+sql);
        return jdbcTemplate.queryForList(sql);
    }

    @Override
    public List processResult(String processId) {
        String sql = "SELECT * FROM ETAI_PROCESS_RESULT WHERE PROCESS_ID='"+processId+"'";
        log.info("=============流程结果信息============"+sql);
        return jdbcTemplate.queryForList(sql);
    }

    @Override
    public int repeatProcessName(String flag,String username, String projectName) {

        String tableName = " ETAI_PROCESS ";
        String where = " AND PROCESS_NAME ='"+projectName+"'";
        if(StringUtils.equals("processForecast",flag)){
            tableName = " ETAI_PROCESS_FORECAST ";
            where = " AND FORECAST_NAME = '"+projectName+"'";
        }
        String sql = "Select count(*) from "+tableName+" WHERE  USERNAME='"+username+"'"+where;

        log.info(flag+"=========重名验证sql========="+sql);
        int count = jdbcTemplate.queryForObject(sql,Integer.class);
        return count;
    }

    @Override
    public String publicProcess(Map<String, Object> map) {

        String FORECAST_ID = GetUUID.getUUID();

        String sql = " INSERT INTO  ETAI_PROCESS_FORECAST(FORECAST_ID,PROCESS_ID,FORECAST_NAME," +
                "USERNAME,RELEASE_STATUS,RELEASE_TIME) VALUES('"+FORECAST_ID+"','"+map.get("PROCESS_ID")+"','"+
                map.get("FORECAST_NAME")+"','"+map.get("USERNAME")+"',"+map.get("RELEASE_STATUS")+",'"+ToolsUtill.getNow()+"')";

        log.info("===========模型发布(录入流程预测模型表) sql======="+sql);

        int i = jdbcTemplate.update(sql);

        return i>0?FORECAST_ID:"";
    }

    @Override
    public String forecastProcess(Map<String, Object> map) {

        String FORECAST_RESULT_ID = GetUUID.getUUID();
        String sql = " INSERT INTO ETAI_PROCESS_FORECAST_RESULT(FORECAST_RESULT_ID,FORECAST_ID,USERNAME,SOURCE_ID," +
                "FORECAST_RESULT_STATUS,FORECAST_RESULT_TIME) VALUES('"+FORECAST_RESULT_ID+"','"+map.get("FORECAST_ID")
                +"','"+map.get("USERNAME")+"','"+map.get("SOURCE_ID")+"',"+map.get("FORECAST_RESULT_STATUS")+",'"
                +ToolsUtill.getNow()+"')";

        log.info("===========模型预测(录入流程预测结果表) sql======="+sql);
        int i = jdbcTemplate.update(sql);
        return i>0?FORECAST_RESULT_ID:"";
    }

    @Override
    public List forecastProcessResult(String forecastId) {
        String sql = "SELECT * FROM ETAI_PROCESS_FORECAST_RESULT WHERE FORECAST_ID = '"+forecastId+"'";

        log.info("=======流程预测结果sql==========="+sql);
        return jdbcTemplate.queryForList(sql);

    }

    @Override
    public int deleteProcessOnRelation(final List<String> list) {

        String sql = " DELECT FROM ETAI_PROCESS_CMPT_RELATION WHERE PCMPT_ID = ?";
        int[] i = jdbcTemplate.batchUpdate(sql,new BatchPreparedStatementSetter(){
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, list.get(i));
            }
            public int getBatchSize() {
                return list.size();
                //这个方法设定更新记录数，通常List里面存放的都是我们要更新的，所以返回list.size();
            }
        });
        return i.length > 0 ? i[0] : -1;
    }

    @Override
    public int deleteProcessOnCmpt(String processId) {
        String sql = " DELECT FROM ETAI_PROCESS_CMPT WHERE PROCESS_ID = '"+processId+"'";
        log.info("=============删除"+processId+"流程中所有组件============sql"+sql);
        int i= jdbcTemplate.update(sql);
        return i;
    }

    @Override
    public int deleteProcessOnCmptParams(final List<String> list) {
        String sql = " DELECT FROM ETAI_PROCESS_CMPT_PARAM WHERE PCMPT_ID = ?";
        int[] i = jdbcTemplate.batchUpdate(sql,new BatchPreparedStatementSetter(){
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, list.get(i));
            }
            public int getBatchSize() {
                return list.size();
                //这个方法设定更新记录数，通常List里面存放的都是我们要更新的，所以返回list.size();
            }
        });
        return i.length > 0 ? i[0] : -1;
    }

    @Override
    public int deleteRrocessOnTranseformFields(final List<String> list) {
        String sql = " DELECT FROM ETAI_PROCESS_CMPT_TRANSFORM WHERE PCMPT_ID = ?";
        int[] i = jdbcTemplate.batchUpdate(sql,new BatchPreparedStatementSetter(){
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, list.get(i));
            }
            public int getBatchSize() {
                return list.size();
                //这个方法设定更新记录数，通常List里面存放的都是我们要更新的，所以返回list.size();
            }
        });
        return i.length > 0 ? i[0] : -1;
    }

    @Override
    public int updateProcessOnProcess(String PROCESS_ID,String PROCESS_NAME, String USERNAME, String PROCESS_DESC, String DATA_DESC,
                                      String SOURCE_ID, String PROCESS_ICON, String SCENE_ID, String SCENE_NAME) {

        String sql = "UPDATE ETAI_PROCESS SET PROCESS_NAME='"+PROCESS_NAME+"',SCENE_ID='"+SCENE_ID+"',SCENE_NAME='"+
                SCENE_NAME+"',USERNAME='"+USERNAME+"',PROCESS_DESC='"+PROCESS_DESC+"',DATA_DESC='"+DATA_DESC+"'," +
                "SOURCE_ID='"+SOURCE_ID+"',PROCESS_ICON='"+PROCESS_ICON+"',UPDATE_TIME='"+ToolsUtill.getNow()+"' WHERE" +
                " PROCESS_ID='"+PROCESS_ID+"'";
        return 0;
    }

    @Override
    public List pcmptIdsOnProcess(String processId,String flag) {
        String col = " PCMPT_ID ";
        if(StringUtils.equals("baseCmptId",flag)){
            col = " BCMPT_ID ";
        }
        String sql = " SELECT "+col+" FROM ETAI_PROCESS_CMPT WHERE PROCESS_ID='"+processId+"'";
        log.info("====获取流程中所有"+col+"ID sql=="+sql);
        return jdbcTemplate.queryForList(sql);
    }

    @Override
    public List transformField(String pcmptId,String bcmptId) {

        String sql = " SELECT B.*,P.PCMPT_ID,P.INPUT_COL_VALUE FROM ETAI_CMPT_TRANSFORM B" +
                " LEFT JOIN ETAI_PROCESS_CMPT_TRANSFORM  P ON B.TRANSFORM_ID = P.TRANSFORM_ID " +
                " AND P.PCMPT_ID = '"+pcmptId+"' WHERE  B.BCMPT_ID = '"+bcmptId+"'";

        log.info("====获取流程组件转换字段ql=="+sql);
        return jdbcTemplate.queryForList(sql);
    }

    @Override
    public List transformRules(String baseCmptId) {
        String sql = " SELECT * FROM ETAI_CMPT_TRANSFORM WHERE BCMPT_ID = '"+baseCmptId+"'";
        log.info("===========流程转换字段规则========sql"+sql);
        return jdbcTemplate.queryForList(sql);
    }

    @Override
    public int insertSourceMetadataRelation(String sourceId, String metadataId) {

        String sql = " INSERT INTO ETAI_SOURCE_METADATA_RELATION(RELATION_ID,SOURCE_ID,METADATA_ID) VALUES('"
                +GetUUID.getUUID()+"','"+sourceId+"','"+metadataId+"')";
        log.info("============录入 数据源与元数据关系表==========sql:"+sql);
        return jdbcTemplate.update(sql);
    }

    private List getList(String sql, final String[] arr) {
        List<JSONObject> list = jdbcTemplate.query(sql.toString(), new Object[]{} , new RowMapper<JSONObject>() {
            @Override
            public JSONObject mapRow(ResultSet resultSet, int i) throws SQLException {
                JSONObject json = new JSONObject();
                for(int j=0;j<arr.length;j++) {
                    String field = arr[j];
                    String value = resultSet.getString(field+"");
                    if(value == null) {
                        json.accumulate(arr[j], "");
                    }else {
                        json.accumulate(arr[j], value);
                    }
                }

                return json;
            }
        });
        return list;
    }


    private List getListJson(String sql, final String[] arr) {
        List<JSObject> list = jdbcTemplate.query(sql.toString(), new Object[]{} , new RowMapper<JSObject>() {
            @Override
            public JSObject mapRow(ResultSet resultSet, int i) throws SQLException {
                JSObject json = new JSObject() {
                    @Override
                    public Object call(String s, Object... objects) throws JSException {
                        return null;
                    }

                    @Override
                    public Object eval(String s) throws JSException {
                        return null;
                    }

                    @Override
                    public Object getMember(String s) throws JSException {
                        return null;
                    }

                    @Override
                    public void setMember(String s, Object o) throws JSException {

                    }

                    @Override
                    public void removeMember(String s) throws JSException {

                    }

                    @Override
                    public Object getSlot(int i) throws JSException {
                        return null;
                    }

                    @Override
                    public void setSlot(int i, Object o) throws JSException {

                    }
                };
                for(int j=0;j<arr.length;j++) {
                    String field = arr[j];
                    String value = resultSet.getString(field+"");
                    if(value == null) {
                        json.setMember(field, "");
//                        json.accumulate(arr[j], "");
                    }else {
                        json.setMember(field, value);
//                        json.accumulate(arr[j], value);
                    }
                }

                return json;
            }
        });
        return list;
    }

}
