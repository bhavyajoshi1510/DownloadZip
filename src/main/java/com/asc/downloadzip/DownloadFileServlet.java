package com.asc.downloadzip;


import org.apache.commons.io.FileUtils;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;

@WebServlet(name = "DownloadFileServlet", value = "/DownloadFile")
public class DownloadFileServlet extends HttpServlet {

    String zipFileName, logFileName;
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            zipFileName = request.getParameter("fileName");
            String[] listOfId = request.getParameter("Id").split(",");
            logFileName = String.valueOf(LocalDateTime.now()).replace(":","-")+"__"+zipFileName+"_log";

            String filePath = new CreateZip().getZipFilePath(listOfId, zipFileName, logFileName);

            File downloadFile = new File(filePath);
            FileInputStream inStream = new FileInputStream(downloadFile);
            ServletContext context = getServletContext();
            String mimeType = context.getMimeType(filePath);
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }
            long fileLength = downloadFile.length();
            CreateZip.writeLogs("***********-------------Length of the zip file---------------***************"+fileLength,logFileName);
            CreateZip.writeLogs("***********-------------Length of the zip file in Integer ---------------***************"+(int)fileLength,logFileName);
            response.setContentType(mimeType);
            response.setContentLength((int) downloadFile.length());
            String headerKey = "Content-Disposition";
            String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
            response.setHeader(headerKey, headerValue);

            OutputStream outStream = response.getOutputStream();
            byte[] buffer = new byte[(int) fileLength];
            int bytesRead;

            while ((bytesRead = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
            inStream.close();
            outStream.close();
            FileUtils.forceDelete(new File(filePath.split(downloadFile.getName())[0]));
            CreateZip.writeLogs("***********-------------Dir is deleted.---------------***************",logFileName);
            CreateZip.deleteOldFiles();
        }catch(Exception e){
            CreateZip.writeLogs("catch block of doPost method -->"+e,logFileName);
        }
    }
}
