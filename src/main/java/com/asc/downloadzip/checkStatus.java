package com.asc.downloadzip;

import javax.servlet.http.HttpServlet;


public class checkStatus extends HttpServlet {

/*   public void initiateDownload(HttpServletResponse response,String filePath,String logFileName) throws IOException, InterruptedException {
       File downloadFile = new File(filePath);
       long fileLength = downloadFile.length();
       CreateZip.writeLogs("***********-------------Length of the zip file---------------***************"+fileLength,logFileName);
       CreateZip.writeLogs("***********-------------Length of the zip file in Integer ---------------***************"+(int)fileLength,logFileName);
       response.setContentLength((int)downloadFile.length());
       response.setHeader("Content-Disposition","attachment; filename=\"" + downloadFile.getName() + "\"");
       response.setContentType("application/zip");
       CreateZip.writeLogs("***********------------- zip file path  ---------------***************"+filePath,logFileName);
       CreateZip.writeLogs("***********------------- zip download start  ---------------***************",logFileName);
       try {
           ByteArrayOutputStream baos = new ByteArrayOutputStream();
           byte bytes[] = new byte[(int) fileLength];
           CreateZip.writeLogs("***********------------- step 1 ---------------***************",logFileName);
           FileInputStream fis = new FileInputStream(downloadFile);
           int bytesRead;
           while ((bytesRead = fis.read(bytes)) != -1)
           {
               baos.write(bytes, 0, bytesRead);
           }
           CreateZip.writeLogs("***********------------- step 2 ---------------***************",logFileName);
           fis.close();
           baos.flush();
           baos.close();
           CreateZip.writeLogs("***********------------- step 3 ---------------***************",logFileName);
           ServletOutputStream op = response.getOutputStream();
           op.write(baos.toByteArray());
           op.flush();
           CreateZip.writeLogs("***********------------- zip download end  ---------------***************",logFileName);
       }catch(Exception ioe) {
           CreateZip.writeLogs("***********-------------Error while flushing ---------------***************"+ioe,logFileName);
       }
       Thread.sleep(60000);
       FileUtils.forceDelete(new File(filePath.split(downloadFile.getName())[0]));
       CreateZip.writeLogs("***********------------- Dir is deleted ---------------***************",logFileName);
   }*/
    public Thread getThreadByName(String threadName) {
       Thread threadReturn = null;
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if (t.getName().equals(threadName)) {
                threadReturn = t;
            }
        }
        return threadReturn;
    }
}
