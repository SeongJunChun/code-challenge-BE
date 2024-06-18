 package goorm.code_challenge.global.utils;
//
// import java.io.File;
// import java.io.FileOutputStream;
// import java.io.IOException;
// import java.util.Optional;
// import java.util.UUID;
//
// import com.amazonaws.AmazonServiceException;
// import com.amazonaws.services.s3.AmazonS3Client;
// import com.amazonaws.services.s3.model.CannedAccessControlList;
// import com.amazonaws.services.s3.model.PutObjectRequest;
//
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Component;
// import org.springframework.stereotype.Service;
// import org.springframework.web.multipart.MultipartFile;
//
// import goorm.code_challenge.global.exception.CustomException;
// import goorm.code_challenge.global.exception.ErrorCode;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
//
// @Slf4j
// @RequiredArgsConstructor
// @Component
// @Service
// public class S3Upload {
// 	private final AmazonS3Client amazonS3Client;
//
// 	@Value("${cloud.aws.s3.bucket}")
// 	private String bucket;
//
// 	public String upload(MultipartFile multipartFile, String dirName) throws IOException {
// 		File uploadFile = convert(multipartFile)
// 			.orElseThrow(() -> new CustomException(ErrorCode.INTERNAL_SERVER_ERROR,"파일 변환 실패"));
//
// 		return upload(uploadFile, dirName);
// 	}
// 	private String upload(File uploadFile, String dirName) {
// 		String fileName = dirName + "/" + UUID.randomUUID()+"."+ uploadFile.getName();
// 		String uploadImageUrl = putS3(uploadFile, fileName);
// 		removeNewFile(uploadFile);
// 		return uploadImageUrl;
// 	}
//
// 	private String putS3(File uploadFile, String fileName) {
// 		amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(
// 			CannedAccessControlList.PublicRead));
// 		return amazonS3Client.getUrl(bucket, fileName).toString();
// 	}
//
// 	private void removeNewFile(File targetFile) {
// 		if (targetFile.delete()) {
// 			log.info("파일이 삭제되었습니다.");
// 		} else {
// 			log.info("파일이 삭제되지 못했습니다.");
// 		}
// 	}
// 	public void fileDelete(String fileName){
// 		log.info("file name : " + fileName);
// 		log.info("File : " + fileName.substring(51));
// 		try {
// 			amazonS3Client.deleteObject(this.bucket,fileName.substring(51));
// 		}catch (AmazonServiceException e){
// 			System.err.println(e.getErrorMessage());
// 		}
// 	}
//
// 	private Optional<File> convert(MultipartFile file) throws IOException {
// 		File convertFile = new File(file.getOriginalFilename());
// 		if(convertFile.createNewFile()) {
// 			try (FileOutputStream fos = new FileOutputStream(convertFile)) {
// 				fos.write(file.getBytes());
// 			}
// 			return Optional.of(convertFile);
// 		}
//
// 		return Optional.empty();
// 	}
// }
