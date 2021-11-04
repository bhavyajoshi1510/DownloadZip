package com.asc.downloadzip;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class checkStatus extends HttpServlet {

   public void initiateDownload(HttpServletResponse response,String filePath,String logFileName) throws IOException {
       File downloadFile = new File(filePath);

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
       CreateZip.writeLogs("***********------------- Dir is deleted ---------------***************",logFileName);
   }

    public Thread getThreadByName(String threadName) {
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if (t.getName().equals(threadName)) {
                return t;
            }
        }
        return null;
    }
}
