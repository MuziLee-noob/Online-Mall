package com.pinyougou.manager.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import entity.Result;
import util.FastDFSClient;

@RestController
public class UploadController {

	@Value("${FILE_SERVER_URL}")
	private String FILE_SERVER_URL;

	@RequestMapping("/upload")
	public Result upload(MultipartFile file) {

		String originalFilename = file.getOriginalFilename();// 获取文件名
		String extName = originalFilename.substring(originalFilename.lastIndexOf('.') + 1);// 得到扩展名

		try {
			FastDFSClient client = new FastDFSClient("classpath:config/fdfs_client.conf");
			String fieldId = client.uploadFile(file.getBytes(), extName);
			String url = FILE_SERVER_URL + fieldId;
			return new Result(true, url);
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "上传失败");
		}
	}
}