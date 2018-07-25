package com.atguigu.gmall.manage.controller;

import org.apache.commons.lang3.StringUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class FileUpload {

    /*表示从配置文件中取得数据application.properties 还是服务器的地址 sprigmvc的注解！
    * 好处：实现软编码，可以随意更改服务器地址！
    * */
    @Value("${fileServer.url}")
    String fileUrl;

    @RequestMapping(value = "fileUpload",method = RequestMethod.POST)
    /*MultipartFile 通过拼接字符串：图片的路径 */
    public String fileUpload(@RequestParam("file") MultipartFile file) throws IOException, MyException {
        String imgUrl=fileUrl;
        if(file!=null){
            System.out.println("multipartFile = " + file.getName()+"|"+file.getSize());
            String configFile = this.getClass().getResource("/tracker.conf").getFile();
            ClientGlobal.init(configFile);
            TrackerClient trackerClient=new TrackerClient();
            TrackerServer trackerServer=trackerClient.getConnection();
            StorageClient storageClient=new StorageClient(trackerServer,null);
            String filename=    file.getOriginalFilename();
            String extName = StringUtils.substringAfterLast(filename, ".");

            String[] upload_file = storageClient.upload_file(file.getBytes(), extName, null);
            imgUrl=fileUrl ;
            for (int i = 0; i < upload_file.length; i++) {
                String path = upload_file[i];
                imgUrl+="/"+path;
            }
        }
        return imgUrl;
    }


    @Test
    public void textFileUpload() throws IOException, MyException {
        /*读取配置文件，取得服务器的ip地址*/
        String file = this.getClass().getResource("/tracker.conf").getFile();
        ClientGlobal.init(file);

        TrackerClient trackerClient=new TrackerClient();
        TrackerServer trackerServer=trackerClient.getConnection();

        StorageClient storageClient=new StorageClient(trackerServer,null);
        String orginalFilename="E:/img/zly.jpg";
        String[] upload_file = storageClient.upload_file(orginalFilename, "jpg", null);
        for (int i = 0; i < upload_file.length; i++) {
            String s = upload_file[i];
            System.out.println("s = " + s);
        }
    }
}
