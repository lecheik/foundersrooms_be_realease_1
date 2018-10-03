package com.foundersrooms.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.activation.FileTypeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foundersrooms.service.AmazonClient;

@RestController
@RequestMapping("/imag")
public class ImageResource {

	@Autowired
	private AmazonClient amazonS3Client;
/*	
	  @RequestMapping(value = "/user/{id}") public ResponseEntity<byte[]>
	  getImage(@PathVariable("id") String userId) throws IOException { try { File
	  img = new File("src/main/resources/static/image/user/" + userId + ".jpg");
	  return ResponseEntity.ok()
	  .contentType(MediaType.valueOf(FileTypeMap.getDefaultFileTypeMap().
	  getContentType(img))) .body(Files.readAllBytes(img.toPath())); } catch
	  (Exception e) { File img = new
	  File("src/main/resources/static/image/user/user.jpg"); return
	  ResponseEntity.ok()
	  .contentType(MediaType.valueOf(FileTypeMap.getDefaultFileTypeMap().
	  getContentType(img))) .body(Files.readAllBytes(img.toPath())); } }
	*/

	@RequestMapping(value = "/user/{id}")
	public ResponseEntity<byte[]> getImage(@PathVariable("id") String userId) throws IOException {
		final HttpHeaders headers = new HttpHeaders();
		try {
			byte[] bytes = amazonS3Client.downloadFile(userId);
			headers.setContentType(MediaType.IMAGE_JPEG);
			return new ResponseEntity<byte[]>(bytes, headers, HttpStatus.OK);
		} catch (Exception e) {
			byte[] bytes = amazonS3Client.downloadFile("user.jpg");
			return new ResponseEntity<byte[]>(bytes, headers, HttpStatus.OK);
		}

	}

	/*
	 @RequestMapping(value = "/user/default") public ResponseEntity<byte[]>
	  getDefaultImage() throws IOException { File img = new
	  File("src/main/resources/static/image/user/user.jpg"); return
	  ResponseEntity.ok()
	  .contentType(MediaType.valueOf(FileTypeMap.getDefaultFileTypeMap().
	  getContentType(img))) .body(Files.readAllBytes(img.toPath())); }
	  
	 */

	@RequestMapping(value = "/user/default")
	public ResponseEntity<byte[]> getDefaultImage() throws IOException {
		final HttpHeaders headers = new HttpHeaders();
		byte[] bytes = amazonS3Client.downloadFile("user.jpg");
		return new ResponseEntity<byte[]>(bytes, headers, HttpStatus.OK);
	}

}
