package com.asc.downloadzip;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;


import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CreateZip {

      //Sandbox credentials
  protected static final String consumerKey = "3MVG9M6Iz6p_Vt2xTZYciaJsvx2C0hV6f3u3y6UB.fNzEVy.sc2ZDMj12b7wihlmHAtmVv7a7naMWo1cjqTy5";
  protected static final String consumerSecret = "F1869E3FA25ED1D3B3C8A0666DA0EAA7A525CDC27D549205F971A8971AA15F97";
  protected static final String username = "bakul@aspiresoftwareconsultancy.com1.loanfigure";
  protected static final String password = "yaya10Febyaya";
  protected static final String loginHost = "https://test.salesforce.com";

    public String[] urlAndToken = new String[2];
    public String logFileName,zipFilePath,DirName;

    public String getZipFilePath(String[] ids) throws IOException {
        DirName = String.valueOf(LocalDateTime.now()).replace(":","-");
        Path path = Paths.get("C:\\DownloadZip\\"+DirName);
        Files.createDirectories(path);
        zipFilePath ="C:\\DownloadZip\\"+DirName;

        for (String id: ids) {
            logFileName = id+"_log";
            CloseableHttpClient client = HttpClients.createDefault();
            try {
                urlAndToken = getAccessToken(client);
                HttpGet get = new HttpGet(urlAndToken[0]+"/services/data/v53.0/sobjects/Attachment/"+id);
                get.setHeader("Authorization", "OAuth " + urlAndToken[1]);
                HttpResponse queryResponse = client.execute(get);
                writeLogs("Response code from GET request is ---> "+ queryResponse.getStatusLine().getStatusCode(),logFileName);
                JSONObject object = new JSONObject(EntityUtils.toString(queryResponse.getEntity()));
                String fileName = object.get("Name").toString();
                int BodyLength = (int) object.get("BodyLength");

                HttpGet getBody = new HttpGet(urlAndToken[0]+"/services/data/v53.0/sobjects/Attachment/"+id+"/Body");
                getBody.setHeader("Authorization", "Bearer "+urlAndToken[1]);
                HttpResponse queryResponse1 = client.execute(getBody);

                writeLogs("Response code from GET request is ---> "+ queryResponse1.getStatusLine().getStatusCode(),logFileName);
                File file = new File(zipFilePath+"\\"+fileName);


                copyInputStreamToFile(queryResponse1.getEntity().getContent(),file,BodyLength);
            }
            catch(Exception e){
                writeLogs("inside catch block of getRootFolderID Method-->"+e,logFileName);
            }
        }

        return zipFiles(zipFilePath);
    }

    private  String zipFiles(String zipFilePath) {
        try {
            FileOutputStream fos = new FileOutputStream(zipFilePath+"\\Attachments.zip");
            ZipOutputStream zos = new ZipOutputStream(fos);
            File[] files = new File(zipFilePath).listFiles();
            String[] filePaths = new String[files.length];
            for (int i = 0; i < files.length; i++) {
                filePaths[i] = files[i].getAbsolutePath();
            }

            for (String aFile : filePaths) {
                zos.putNextEntry(new ZipEntry(new File(aFile).getName()));

                byte[] bytes = Files.readAllBytes(Paths.get(aFile));
                zos.write(bytes, 0, bytes.length);
                zos.closeEntry();
            }

            zos.close();

        } catch (FileNotFoundException ex) {
            System.err.println("A file does not exist: " + ex);
        } catch (IOException ex) {
            System.err.println("I/O error: " + ex);
        }
        return zipFilePath+"\\Attachments.zip";
    }

    private static void copyInputStreamToFile(InputStream inputStream, File file, int BufferSize)
            throws IOException {

        // append = false
        try (FileOutputStream outputStream = new FileOutputStream(file, false)) {
            int read;
            byte[] bytes = new byte[BufferSize];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        }
    }

    public String[] getAccessToken (CloseableHttpClient client) throws IOException {

        String[] data = new String[2];
        String baseUrl = loginHost+"/services/oauth2/token";
        // Send a post request to the OAuth URL.
        HttpPost oauthPost = new HttpPost(baseUrl);
        // The request body must contain these 5 values.
        List<BasicNameValuePair> parametersBody = new ArrayList<>();
        parametersBody.add(new BasicNameValuePair("grant_type", "password"));
        parametersBody.add(new BasicNameValuePair("username", username));
        parametersBody.add(new BasicNameValuePair("password", password));
        parametersBody.add(new BasicNameValuePair("client_id", consumerKey));
        parametersBody.add(new BasicNameValuePair("client_secret", consumerSecret));
        oauthPost.setEntity(new UrlEncodedFormEntity(parametersBody, HTTP.UTF_8));
        // Execute the request.
        HttpResponse response = client.execute(oauthPost);
        String res = EntityUtils.toString(response.getEntity());

        JSONObject jo = new JSONObject(res);
        data[1] = jo.getString("access_token");
        data[0] = jo.getString("instance_url");

        return data;
    }

    public static void writeLogs(String line, String logFileName){

        try(FileWriter fw = new FileWriter("C:\\DownloadZip\\DownloadZipLogs\\"+logFileName+".txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.println(LocalDateTime.now() + "---- " + line);
        } catch (IOException e) {
            writeLogs("inside catch block of writeLogs Method-->"+e,logFileName);
        }
    }
}
