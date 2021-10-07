package com.asc.downloadzip;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;


import java.util.List;

public class CreateZip {

      //Sandbox credentials
  protected static final String consumerKey = "3MVG9M6Iz6p_Vt2xTZYciaJsvx2C0hV6f3u3y6UB.fNzEVy.sc2ZDMj12b7wihlmHAtmVv7a7naMWo1cjqTy5";
  protected static final String consumerSecret = "F1869E3FA25ED1D3B3C8A0666DA0EAA7A525CDC27D549205F971A8971AA15F97";
  protected static final String username = "bakul@aspiresoftwareconsultancy.com1.loanfigure";
  protected static final String password = "yaya10Febyaya";
  protected static final String loginHost = "https://test.salesforce.com";

    public String[] urlAndToken = new String[2];
    public String logFileName,zipFilePath,DirName;

    public String getZipFilePath(String[] ids) {
        DirName = String.valueOf(LocalDateTime.now());
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
                get.setHeader("Authorization", "Bearer "+urlAndToken[1]);
                //get.setHeader("Authorization", "OAuth "+urlAndToken[1]);
                //get.setHeader("content-type","");
                HttpResponse queryResponse1 = client.execute(getBody);

                writeLogs("Response code from GET request is ---> "+ queryResponse1.getStatusLine().getStatusCode(),logFileName);
                HttpURLConnection connection = (HttpURLConnection) new URL(urlAndToken[0]+"/services/data/v53.0/sobjects/Attachment/"+id+"/Body").openConnection();

                StringBuilder stringBuilder = new StringBuilder();
                BufferedReader bufferedReader = null;
                try {
                    InputStream inputStream = connection.getInputStream();
                    if (inputStream != null) {
                        bufferedReader = new BufferedReader(new InputStreamReader(
                                inputStream));
                        char[] charBuffer = new char[BodyLength];
                        int bytesRead = -1;
                        while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                            stringBuilder.append(charBuffer, 0, bytesRead);
                        }
                    } else {
                        stringBuilder.append("");
                    }
                } catch (IOException ex) {
                    throw ex;
                }
                String body = stringBuilder.toString();

                byte[] bytes = body.getBytes();
                FileOutputStream fos = new FileOutputStream("C:\\DownloadZip\\"+DirName+"\\"+fileName);
                fos.write(bytes);
                fos.close();

            }
            catch(Exception e){
                writeLogs("inside catch block of getRootFolderID Method-->"+e,logFileName);
            }
        }
        return zipFilePath;
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
