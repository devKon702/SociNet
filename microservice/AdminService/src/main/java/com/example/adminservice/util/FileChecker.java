package com.example.adminservice.util;

import org.springframework.web.multipart.MultipartFile;

public class FileChecker {
    public static long MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB
    public static long MAX_VIDEO_SIZE = 15 * 1024 * 1024; //15MB

    public static boolean isValidImage(MultipartFile file, long maxSize) throws Exception{
        if(file != null && file.getContentType().startsWith("image")){
            if(file.getSize() > maxSize) throw new Exception("OVERSIZE IMAGE");
            else return true;
        } else return false;
    }

    public static boolean isValidVideo(MultipartFile file, long maxSize) throws Exception{
        if(file != null && file.getContentType().startsWith("video")){
            if(file.getSize() > maxSize) throw new Exception("OVERSIZE VIDEO");
            else return true;
        } else return false;}
}
