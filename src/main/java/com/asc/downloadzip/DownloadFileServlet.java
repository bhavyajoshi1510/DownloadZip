package com.asc.downloadzip;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.*;
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
            CreateZip.writeLogs("***********-------------Request Came---------------***************",logFileName);
            String filePath = new CreateZip().getZipFilePath(listOfId, zipFileName, logFileName);
            CreateZip.writeLogs("***********-------------Zip is created.---------------***************",logFileName);
            File downloadFile = new File(filePath);
            //FileInputStream inStream = new FileInputStream(downloadFile);
            ServletContext context = getServletContext();
            String mimeType = context.getMimeType(filePath);
            if (mimeType == null) {
                mimeType = "application/zip";
            }
            long fileLength = downloadFile.length();
            CreateZip.writeLogs("***********-------------Length of the zip file---------------***************"+fileLength,logFileName);
            CreateZip.writeLogs("***********-------------Length of the zip file in Integer ---------------***************"+(int)fileLength,logFileName);
            response.setContentLength((int) downloadFile.length());
            response.setHeader("Content-Disposition","attachment; filename=\"" + downloadFile.getName() + "\"");
            response.setContentType(mimeType);

            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte bytes[] = new byte[(int) fileLength];

                FileInputStream fis = new FileInputStream(downloadFile);
                int bytesRead;
                while ((bytesRead = fis.read(bytes)) != -1)
                {
                    baos.write(bytes, 0, bytesRead);
                }

                fis.close();
                baos.flush();
                baos.close();

                ServletOutputStream op = response.getOutputStream();
                op.write(baos.toByteArray());
                op.flush();

            }catch(IOException ioe) {
                CreateZip.writeLogs("***********-------------Error while flushing ---------------***************"+ioe,logFileName);
            }
            FileUtils.forceDelete(new File(filePath.split(downloadFile.getName())[0]));
            CreateZip.writeLogs("***********-------------Dir is deleted.---------------***************",logFileName);
            CreateZip.deleteOldFiles();
        }catch(Exception e){
            CreateZip.writeLogs("catch block of doPost method -->"+e,logFileName);
        }
    }
}
