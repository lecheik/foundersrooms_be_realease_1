package com.foundersrooms.service;

import java.io.File;

import org.springframework.web.multipart.MultipartFile;

public interface AmazonClient {
	public String uploadFile(MultipartFile multipartFile,String fileName);
	public byte[] downloadFile(String fileId);
	public boolean doesBucketExists(String bucketName, String objectKey);
	public void createFileInsideBucket(String prefix, MultipartFile multipartFile);
	public void deleteObject(String objectKey);
}
