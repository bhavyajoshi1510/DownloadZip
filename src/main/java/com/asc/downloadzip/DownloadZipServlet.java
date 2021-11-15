package com.asc.downloadzip;

import org.apache.commons.io.FileUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@WebServlet(name = "DownloadZipServlet", value = "/DownloadZip")
public class DownloadZipServlet extends HttpServlet {
    public String zipFileName, logFileName, zipFilePath, downloadFilePath;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            logFileName= request.getParameter("logFileName");
            zipFileName =request.getParameter("zipFileName");
            zipFilePath =request.getParameter("zipFilePath");

            downloadFilePath = "C:\\DownloadZip\\"+zipFilePath+"\\"+zipFileName+".zip";

            ServletContext context = getServletContext();
            String mimeType = context.getMimeType(downloadFilePath);
            if (mimeType == null) {
                mimeType = "application/zip";
            }

            File downloadFile = new File(downloadFilePath);
            long fileLength = downloadFile.length();
            CreateZip.writeLogs("\n\n\n\n\n",logFileName);
            CreateZip.writeLogs("***********-------------Length of the zip file---------------***************"+fileLength,logFileName);
            CreateZip.writeLogs("***********-------------Length of the zip file in Integer ---------------***************"+(int)fileLength,logFileName);
            response.setContentLength((int) downloadFile.length());
            response.setHeader("Content-Disposition","attachment; filename=\"" + downloadFile.getName() + "\"");
            response.setContentType(mimeType);
            CreateZip.writeLogs("***********------------- zip file path  ---------------***************"+downloadFile.getName(),logFileName);
            CreateZip.writeLogs("***********------------- zip file path  ---------------***************"+downloadFile.getAbsolutePath(),logFileName);
            CreateZip.writeLogs("***********------------- zip download Start  ---------------***************",logFileName);

            try (ServletOutputStream out = response.getOutputStream(); InputStream in = new FileInputStream(downloadFile)) {
                byte[] bytes = new byte[(int) fileLength];
                int bytesRead;
                CreateZip.writeLogs("***********------------- MIME TYPE ---------------***************" + mimeType, logFileName);
                response.setContentType(mimeType);

                while ((bytesRead = in.read(bytes)) != -1) {
                    out.write(bytes, 0, bytesRead);
                }

            } catch (Exception ioe) {
                CreateZip.writeLogs("***********-------------Error while flushing ---------------***************" + ioe, logFileName);
            }
            CreateZip.writeLogs("***********------------- zip download end  ---------------***************",logFileName);
            FileUtils.forceDelete(new File(downloadFilePath.split(downloadFile.getName())[0]));
            CreateZip.writeLogs("***********------------- Dir is deleted ---------------***************",logFileName);

        } catch (Exception e) {
            CreateZip.writeLogs("***********------------- error while downloading---------------***************"+e,logFileName);
        }
    }
}
