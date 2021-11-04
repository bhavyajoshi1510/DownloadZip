package com.asc.downloadzip;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.*;
import java.time.LocalDateTime;

@WebServlet(name = "DownloadFileServlet", value = "/DownloadFile")
public class DownloadFileServlet extends HttpServlet {
    public String zipFileName, logFileName, filePath, DirName, threadName;
    public Thread thread;

   @Override

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            if(request.getParameter("logFileName")==null && request.getParameter("zipFileName")==null && request.getParameter("zipFilePath")==null){
                zipFileName = request.getParameter("fileName");
                String[] listOfId = request.getParameter("Id").split(",");
                logFileName = String.valueOf(LocalDateTime.now()).replace(":","-")+"__"+zipFileName+"_log";
                CreateZip.writeLogs("***********------------- Initial Request Came---------------***************",logFileName);
                DirName = String.valueOf(LocalDateTime.now()).replace(":","-");
                filePath = new CreateZip().getZipFilePath(DirName);
                thread = new Thread(() -> {
                    try {
                       new CreateZip().getZipFile(listOfId, zipFileName, logFileName,filePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                thread.setName(DirName);
                thread.start();
                response.addHeader("Access-Control-Allow-Origin", "*");
                response.addHeader("Access-Control-Allow-Methods","GET, OPTIONS, HEAD, PUT, POST");
                PrintWriter out = response.getWriter();
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                out.print("{\n" +
                        "  \"logFileName\": \""+logFileName+"\",\n" +
                        "  \"zipFileName\": \""+zipFileName+"\",\n" +
                        "  \"zipFilePath\": \""+DirName+"\"\n" +
                        "}");
                out.flush();

            }else{
                threadName = request.getParameter("zipFilePath");
                logFileName = request.getParameter("logFileName");
                zipFileName = request.getParameter("zipFileName");
                CreateZip.writeLogs("***********-------------Followup Request came---------------***************",logFileName);
                CreateZip.writeLogs("***********-------------Followup thread name--------------***************"+threadName,logFileName);
                CreateZip.writeLogs("***********-------------Followup logFileName name--------------***************"+logFileName,logFileName);
                CreateZip.writeLogs("***********-------------Followup zipFileName name--------------***************"+zipFileName,logFileName);
                filePath = "C:\\DownloadZip\\"+threadName+"\\"+zipFileName+".zip";
                checkStatus obj = new checkStatus();
                thread = obj.getThreadByName(threadName);
                if(thread.getState() == Thread.State.TERMINATED){
                    CreateZip.writeLogs("***********-------------Thread is terminated------ Download initiated--------***************",logFileName);
                    obj.initiateDownload(response,filePath,logFileName);
                }else{
                    PrintWriter out = response.getWriter();
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    out.print("{\n" +
                            "  \"logFileName\": \""+logFileName+"\",\n" +
                            "  \"zipFileName\": \""+zipFileName+"\",\n" +
                            "  \"zipFilePath\": \""+threadName+"\"\n" +
                            "}");
                    out.flush();
                }
            }
        }catch(Exception e){
            CreateZip.writeLogs("catch block of doPost method -->"+e,logFileName);
        }
    }
}
