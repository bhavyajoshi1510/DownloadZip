package com.asc.downloadzip;


import org.apache.commons.io.FileUtils;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@WebServlet(name = "DownloadFileServlet", value = "/DownloadFile")
public class DownloadFileServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String[] listOfId = request.getParameter("Id").split(",");

        String filePath = new CreateZip().getZipFilePath(listOfId);

        File downloadFile = new File(filePath);
        FileInputStream inStream = new FileInputStream(downloadFile);
        ServletContext context = getServletContext();
        String mimeType = context.getMimeType(filePath);
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }
        long fileLength = downloadFile.length();
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
        FileUtils.deleteDirectory(new File(filePath.split( downloadFile.getName())[0]));
    }
}
