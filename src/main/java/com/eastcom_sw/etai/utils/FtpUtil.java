package com.eastcom_sw.etai.utils;

import com.opencsv.CSVReader;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.util.Base64;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * FTP文件上传
 *
 * @author Sunk
 * @create 2018-2-02-14:01
 **/
public class FtpUtil {


    /** 本地字符编码 */
    private static String LOCAL_CHARSET = "GBK";

    // FTP协议里面，规定文件名编码为iso-8859-1
    private static String SERVER_CHARSET = "ISO-8859-1";

    /**
     * Description: 向FTP服务器上传文件
     * @param host FTP服务器hostname
     * @param port FTP服务器端口
     * @param username FTP登录账号
     * @param password FTP登录密码
     * @param basePath FTP服务器基础目录
     * @param filePath FTP服务器文件存放路径。例如分日期存放：/root/skai/2018/02/02。文件的路径为basePath+filePath
     * @param filename 上传到FTP服务器上的文件名
     * @param input 输入流
     * @return 成功返回true，否则返回false
     */
    public static boolean uploadFile(String host, int port, String username, String password, String basePath,
                                     String filePath, String filename, InputStream input) {
        boolean result = false;
        FTPClient ftp = new FTPClient();
        try {
            int reply;
            ftp.connect(host, port);// 连接FTP服务器
            // 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器
            ftp.login(username, password);// 登录
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return result;
            }
//            if (FTPReply.isPositiveCompletion(ftp.sendCommand(
//                    "OPTS UTF8", "ON"))) {// 开启服务器对UTF-8的支持，如果服务器支持就用UTF-8编码，否则就使用本地编码（GBK）.
//                LOCAL_CHARSET = "UTF-8";
//            }
//            ftp.setControlEncoding(LOCAL_CHARSET);
//            ftp.enterLocalPassiveMode();// 设置被动模式
////            ftp.setFileType(getTransforModule());// 设置传输的模式
//            filename = new String(filename.getBytes(LOCAL_CHARSET),
//                    SERVER_CHARSET);
            //切换到上传目录
            if (!ftp.changeWorkingDirectory(basePath+filePath)) {
                        //如果目录不存在创建目录
                        String[] dirs = filePath.split("/");
                        String tempPath = basePath;
                        for (String dir : dirs) {
                            if (null == dir || "".equals(dir)) continue;
                            tempPath += "/" + dir;
                            if (!ftp.changeWorkingDirectory(tempPath)) {//转移到FTP服务器目录
                                if (!ftp.makeDirectory(tempPath)) {//创建目录
                            return result;
                        } else {
                            ftp.changeWorkingDirectory(tempPath);
                        }
                    }
                }
            }
            //设置上传文件的类型为二进制类型
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            //上传文件
            ftp.enterLocalPassiveMode();
            if (!ftp.storeFile(new String(filename.getBytes("GBK"), "iso-8859-1") , input)) {
                return result;
            }
            input.close();
            ftp.logout();
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                }
            }
        }
        return result;
    }


    /**
     * 下载文件
     * @param path
     * @param fileName
     * @param response
     */
    public  static void downloadFile(String host,int port,String username,String password,
            String path, String fileName, HttpServletResponse response){

        FTPClient ftp = new FTPClient();
        try {
            int reply;
            ftp.connect(host, port);
            // 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器
            ftp.login(username, password);// 登录
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
            }
            ftp.changeWorkingDirectory(path);// 转移到FTP服务器目录
            FTPFile[] fs = ftp.listFiles();
            for (FTPFile ff : fs) {
                String name = new String(ff.getName().getBytes(SERVER_CHARSET), LOCAL_CHARSET);
                if (name.equals(fileName)) {
                    InputStream fis = ftp.retrieveFileStream(fileName);
                    byte[] buffer = new byte[fis.available()];
                    fis.read(buffer);
                    // 清空response
                    response.reset();
                    // 设置response的Header
                    response.addHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes()));
                    response.addHeader("Content-Length", "" + ff.getSize());
                    OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
                    response.setContentType("application/force-download");
                    toClient.write(buffer);
                    toClient.flush();
                    toClient.close();
                    fis.close();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    /**
     * 预览服务器文件数据
     * @param host
     * @param port
     * @param username
     * @param password
     * @param remotePath
     * @param fileName
     * @param separatorCol
     * @param separatorRow
     * @param readRows
     * @return
     */
    public static List readFileOnServer(String host, int port, String username, String password, String remotePath,
                                        String fileName,String separatorCol,String separatorRow,int readRows){

        FTPClient ftp = new FTPClient();
        List<String> list = new ArrayList<>();
        try {
            int reply;
            ftp.connect(host, port);
            // 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器
            ftp.login(username, password);// 登录
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
            }
            ftp.changeWorkingDirectory(remotePath);// 转移到FTP服务器目录
            FTPFile[] fs = ftp.listFiles();
            for (FTPFile ff : fs) {
                String name = new String(ff.getName().getBytes(SERVER_CHARSET),LOCAL_CHARSET);
                if (name.equals(fileName)) {
                    //从服务器上读取指定的文件
                    InputStream ins = ftp.retrieveFileStream(fileName);
                    Reader reader = new  InputStreamReader(ins,"GBK");

                    String arr="";
                    List<String> listStr = new ArrayList<>();
                    int tempchar;
                    int i = 0;
                    while ((tempchar = reader.read()) != -1) {
                        char separatorRowChar = '\r';
                        char separatorColChar = separatorCol.charAt(0);
                        if(StringUtils.equals("\\n",separatorRow)){
                            separatorRowChar = '\n';
                        }
                        if(StringUtils.equals("\\r\\n",separatorRow) && ((char) tempchar) == '\n'){//换行/r/n 去掉/n
                            arr="";
                        }else{
                            arr += String.valueOf((char) tempchar);
                            if(((char) tempchar) == separatorColChar){//列数据
                                listStr.add(arr.substring(0,arr.length()-1));
                                arr="";
                            }
                            if(((char) tempchar) == separatorRowChar){//行数据
                                listStr.add(arr.substring(0,arr.length()-1));
                                arr="";
                                String hang = "";
                                for(int k=0;k<listStr.size();k++){
                                    hang +=listStr.get(k).toString()+",";
                                }
                                list.add(hang);
//                                System.out.println("=========行数据:  "+list.toString());
                                listStr.clear();
                                i++;
                            }
                            if(i == readRows){
                                break;
                            }
                        }
                    }
                    reader.close();
                }
            }
            ftp.logout();
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }



    public static List readCSVFile(String ip, int port, String username, String password,String fileName,
                                   String filePath,String previewRows,String isContent){


        FTPClient ftp = new FTPClient();
        List<String> datalst = new ArrayList<>();
        int previewRowsInt = Integer.parseInt(previewRows);
        if(StringUtils.equals("true",isContent)){

            if(!StringUtils.isBlank(previewRows)){
                previewRowsInt = Integer.parseInt(previewRows) - 1;
            }
        }

        try {
            int reply;
            ftp.connect(ip, port);
            // 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器
            ftp.login(username, password);// 登录
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
            }
            ftp.changeWorkingDirectory(filePath);// 转移到FTP服务器目录
            FTPFile[] fs = ftp.listFiles();
            for (FTPFile ff : fs) {
                String name = new String(ff.getName().getBytes(SERVER_CHARSET),LOCAL_CHARSET);
                if (name.equals(fileName)) {

                    InputStream ins = ftp.retrieveFileStream(fileName);
                    CSVReader csvReader = new CSVReader(new InputStreamReader(ins,"GBK"));



                    StringBuffer defaultTitle = new StringBuffer();
                    StringBuffer titleRow = new StringBuffer();
                    String[] titles = csvReader.readNext();
                    if(titles != null && titles.length > 0){
                        int i = 0;
                        for(String title : titles){
                            i++;
                            if(title != null && !title.equals("")){
                                titleRow.append(title + ",");
                                defaultTitle.append("filed_"+i+",");
                            }
                        }
                        if(StringUtils.equals("false",isContent)){
                            datalst.add(defaultTitle.substring(0,defaultTitle.lastIndexOf(",")));
                            datalst.add(titleRow.substring(0,titleRow.lastIndexOf(",")));
                            --previewRowsInt;
                        }else{
                            datalst.add(titleRow.substring(0,titleRow.lastIndexOf(",")));
                        }
                    }

                    List<String[]> list = csvReader.readAll();

                    for(String[] ss : list){
                        if(--previewRowsInt < 0){
                            break;
                        }
                        StringBuffer dataRow = new StringBuffer();
                        for(String s : ss){
                            if(null != s && !s.equals("")){
                                dataRow.append(s + " , ");
                            }
                        }
                        datalst.add(dataRow.substring(0,dataRow.lastIndexOf(",")));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return datalst;
    }


    /**
     * 预览图片
     * @param path
     * @param fileName
     * @param response
     */
    public  static void previewPicture(String host,int port,String username,String password,
            String path, String fileName, HttpServletResponse response){

        FTPClient ftp = new FTPClient();
        try {
            int reply;
            ftp.connect(host, port);
            // 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器
            ftp.login(username, password);// 登录
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
            }
            ftp.changeWorkingDirectory(path);// 转移到FTP服务器目录
            FTPFile[] fs = ftp.listFiles();
            for (FTPFile ff : fs) {
                String name = new String(ff.getName().getBytes(SERVER_CHARSET), LOCAL_CHARSET);
                if (name.equals(fileName)) {
                    // 获得文件大小
                    int size = (int) fs[0].getSize();
                    byte[] bytes = new byte[size];
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    // 写入输出流
                    ftp.retrieveFile(fileName,os);
                    bytes = os.toByteArray();

                    // 清空response
                    response.reset();
                    // 设置response的Header
                    response.addHeader("Content-Disposition", "inline;filename=" + new String(fileName.getBytes()));
                    response.addHeader("Content-Length", "" + ff.getSize());
                    OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
                    response.setContentType("image/jpeg");
                    toClient.write(bytes);
                    toClient.flush();
                    toClient.close();
                    os.flush();
                    os.close();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }




    public static Workbook readExcel(String ip, int port, String username, String password,String fileName,
                                   String filePath){


        FTPClient ftp = new FTPClient();

        Workbook wb = null;
        try {
            int reply;
            ftp.connect(ip, port);
            // 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器
            ftp.login(username, password);// 登录
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
            }
            ftp.changeWorkingDirectory(filePath);// 转移到FTP服务器目录
            FTPFile[] fs = ftp.listFiles();
            for (FTPFile ff : fs) {
                String name = new String(ff.getName().getBytes(SERVER_CHARSET),LOCAL_CHARSET);
                if (name.equals(fileName)) {

                    String extString = fileName.substring(fileName.lastIndexOf("."));
                    InputStream ins = ftp.retrieveFileStream(fileName);
                    if(".xls".equals(extString)){
                        return wb = new HSSFWorkbook(ins);
                    }else if(".xlsx".equals(extString)){
                        return wb = new XSSFWorkbook(ins);
                    }else{
                        return wb = null;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wb;
    }


    public static Object getCellFormatValue(Cell cell){
        Object cellValue = null;
        if(cell!=null){
            //判断cell类型
            switch(cell.getCellType()){
                case Cell.CELL_TYPE_NUMERIC:{
                    cellValue = String.valueOf(cell.getNumericCellValue());
                    break;
                }
                case Cell.CELL_TYPE_FORMULA:{
                    //判断cell是否为日期格式
                    if(DateUtil.isCellDateFormatted(cell)){
                        //转换为日期格式YYYY-mm-dd
                        cellValue = cell.getDateCellValue();
                    }else{
                        //数字
                        cellValue = String.valueOf(cell.getNumericCellValue());
                    }
                    break;
                }
                case Cell.CELL_TYPE_STRING:{
                    cellValue = cell.getRichStringCellValue().getString();
                    break;
                }
                default:
                    cellValue = "";
            }
        }else{
            cellValue = "";
        }
        return cellValue;
    }
}
