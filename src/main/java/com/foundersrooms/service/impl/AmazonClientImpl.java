package com.foundersrooms.service.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.foundersrooms.service.AmazonClient;


@Service
public class AmazonClientImpl implements AmazonClient {

    private AmazonS3 s3client;

    @Value("${aws_end_point}")
    private String endpointUrl;
    @Value("${aws_region}")
    private String region;
    @Value("${aws_namecard_bucket}")
    private String bucketName;
    @Value("${aws_access_key_id}")
    private String accessKey;
    @Value("${aws_secret_access_key}")
    private String secretKey;	
    @Value("${aws_attachment_bucket}")
    private String attachmentsBucketName;    
	
	@Override
	public String uploadFile(MultipartFile multipartFile,String fileName) {
        String fileUrl = "";
        try {
            File file = convertMultiPartToFile(multipartFile);            
            fileUrl = endpointUrl + "/" + bucketName + "/" + fileName;
            uploadFileTos3bucket(fileName, file);
            file.delete();
        } catch (Exception e) {
           e.printStackTrace();
        }
        return fileUrl;

	}

	@Override
	public byte[] downloadFile(String fileId)  {
		// TODO Auto-generated method stub
		S3Object s3object = s3client.getObject(new GetObjectRequest(bucketName, fileId/*+".jpg"*/));
		InputStream in = s3object.getObjectContent();
		try {
		return IOUtils.toByteArray(in);
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
    @PostConstruct
    private void initializeAmazon() {
       AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
		this.s3client = AmazonS3ClientBuilder.standard()
				.withRegion(Regions.fromName(this.region))
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }	
    
    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }    
    
    private void uploadFileTos3bucket(String fileName, File file) throws IOException {
    	ObjectMetadata md = new ObjectMetadata();
    	md.setContentType("image/jpg");
    	md.setContentLength(file.length());
    	/*
        s3client.putObject(new PutObjectRequest(bucketName, fileName+".jpg", file)
                .withCannedAcl(CannedAccessControlList.PublicRead));*/
    	ByteArrayInputStream contentsAsStream=new ByteArrayInputStream(FileUtils.readFileToByteArray(file));
    	s3client.putObject(new PutObjectRequest(bucketName, fileName, contentsAsStream,md)
                .withCannedAcl(CannedAccessControlList.PublicRead));    	
    	
    }

	@Override
	public boolean doesBucketExists(String bucketName, String objectKey) {
		// TODO Auto-generated method stub
		return this.s3client.doesObjectExist(bucketName, objectKey);
	}

	@Override
	public void createFileInsideBucket(String prefix, MultipartFile multipartFile) {
		File file=null;
		try {
			file = convertMultiPartToFile(multipartFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		// TODO Auto-generated method stub
		this.s3client.putObject(bucketName, prefix, file);
	}

	@Override
	public void deleteObject(String objectKey) {
		// TODO Auto-generated method stub
		this.s3client.deleteObject(bucketName, objectKey);
	}    
  

}
