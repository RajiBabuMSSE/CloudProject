package com.java.spring;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.java.beans.FileUpload;
import com.java.beans.Login;
import com.java.jdbc.JDBCDBConnection;

import org.apache.commons.io.FilenameUtils;


@Controller
public class FileController {
	
	private static final Logger logger = LogManager.getLogger(FileController.class);

	@Value("${Access.Key.ID}")
	private String accessKey;
	@Value("${Secret.Access.Key}")
	private String secretKey;
	@Value("${welcome.message:test}")
	private String message = "Hello World";
		
	@RequestMapping("/getUserContentById")
	public String getUserContentById(Map<String, List<FileUpload>> model, @ModelAttribute Login login, @ModelAttribute FileUpload fileUpload,HttpServletRequest request) {
		
		String userName = (String)request.getSession().getAttribute("user");
		 logger.info(userName);
		 Connection conn = null;
		  Statement setupStatement = null;
		  Statement readStatement = null;
		  ResultSet resultSet = null;
		  String results = "";
		  int numresults = 0;
		  String statement = null;
		  fileUpload = new FileUpload();
		  List<FileUpload> fileUploadList = new ArrayList<FileUpload>();
		  try{
		  conn = JDBCDBConnection.getRemoteConnection();
		    
		  // Create connection to RDS DB instance
		    conn = JDBCDBConnection.getRemoteConnection();
		    // Create a table and write two rows
		    setupStatement = conn.createStatement();
		    String selectQuery =  "SELECT user_id,file_name, file_description, uploaded_on, updated_on FROM user_content where user_id = ? ;";
		    
				    PreparedStatement prepStmt = conn.prepareStatement(selectQuery);
				    prepStmt.setString(1, userName);
				    			   
				    logger.info(selectQuery);
				    resultSet = prepStmt.executeQuery();
				    logger.info(prepStmt.getFetchSize());
				   // String insertRow1 = "INSERT INTO Beanstalk (Resource) VALUES ('EC2 Instance');";
				   // String
				    
				    //logger.info(resultSet.first());
				         
		    while (resultSet.next()) {              
		    	fileUpload = new FileUpload();
		            System.out.println(resultSet.getString("user_id"));
		            System.out.println(resultSet.getString("file_name"));
		            System.out.println(resultSet.getString("file_description"));
		            System.out.println(resultSet.getDate("uploaded_on").toString());
		            System.out.println(resultSet.getDate("updated_on").toString());
		            fileUpload.setDescription(resultSet.getString("file_description"));
		            fileUpload.setFileName(resultSet.getString("file_name"));
		            fileUpload.setUpdatedOn(resultSet.getString("updated_on"));
		            fileUpload.setUploadedOn(resultSet.getString("uploaded_on"));
		            fileUploadList.add(fileUpload);
		           /* System.out.println(resultSet.getString("Col 3"));                    
		            System.out.println(resultSet.getString("Col n"));*/
		    }
		    
		    resultSet.close();
		    conn.close();

		  } catch (SQLException ex) {
		    // Handle any errors
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		  } finally {
		       System.out.println("Closing the connection.");
		      if (conn != null) try { conn.close(); } catch (SQLException ignore) {}
		  }
		  model.put("fileUploadList", fileUploadList);
		  return "FileUpload";	
	}
	
	 @RequestMapping(value = "/fileUpload", method = RequestMethod.POST)	 
	
	 /*
	 public String uploadFile(@RequestParam("file") MultipartFile multipartile, @ModelAttribute FileUpload fileUpload,HttpServletRequest request) throws InterruptedException, IOException {
		 
		 
		 AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
		                         .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
		                         .build();
		 s3Client.setRegion(Region.getRegion(Regions.US_WEST_1));
		 
		 BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
		 AmazonS3 s3Client = AmazonS3Client.builder().withRegion("us-west-1").withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                 .build();
		 
		 AmazonS3Client s3Client = new AmazonS3Client(new ProfileCredentialsProvider());
	        s3Client.configureRegion(Regions.US_WEST_1);
		  //AmazonS3Client s3Client = new AmazonS3Client(new ProfileCredentialsProvider());
	       // s3Client.configureRegion(Regions.US_WEST_1);
	        String existingBucketName  = "myapp-content"; 
		    String keyName             = multipartile.getOriginalFilename();

	        
	      
	        File file = convertFromMultiPart(multipartile);
	       	        // Use Amazon S3 Transfer Acceleration endpoint.           
	       // s3Client.setS3ClientOptions(S3ClientOptions.builder().setAccelerateModeEnabled(true).build());
	       
	    	TransferManager tm = new TransferManager(s3Client);        
	        System.out.println("TransferManager");
	        // TransferManager processes all transfers asynchronously, 
	        // so this call will return immediately.
	        Upload upload = tm.upload(
	        		existingBucketName, keyName, file);
	        System.out.println("Upload");

	        try {
	        	// Or you can block and wait for the upload to finish
	        	upload.waitForCompletion();
	        	System.out.println("Upload complete");
	        	  updateUserContentDB(request, multipartile,  fileUpload);
	        } catch (AmazonClientException amazonClientException) {
	        	System.out.println("Unable to upload file, upload was aborted.");
	        	amazonClientException.printStackTrace();
	        }
	        return "forward:/getUserContentById";
	 }
	 
	 */
	 
	 public String uploadFileHandler(@RequestParam("file") MultipartFile multipartile, @ModelAttribute FileUpload fileUpload,HttpServletRequest request) throws Exception {   
		 updateUserContentDB(request, multipartile,  fileUpload);
			System.out.println(multipartile.getOriginalFilename());
			System.out.println(multipartile.getName());
		String existingBucketName  = "myapp-content"; 
	    String keyName             = multipartile.getOriginalFilename();
	//    String filePath            = "C:\\raji\\MSSE\\Fall 2017\\Cloud Computing\\Term Project\\Project1\\S3FileUpload.txt";   
	    
	    AmazonS3 s3Client = new AmazonS3Client(new ProfileCredentialsProvider());        

	    // Create a list of UploadPartResponse objects. You get one of these
	    // for each part upload.	
	    List<PartETag> partETags = new ArrayList<PartETag>();

	    // Step 1: Initialize.
	    InitiateMultipartUploadRequest initRequest = new 
	         InitiateMultipartUploadRequest(existingBucketName, keyName);
	    InitiateMultipartUploadResult initResponse = 
	    	                   s3Client.initiateMultipartUpload(initRequest);

	    File file = convertFromMultiPart(multipartile);
//	    File file = new File(filePath);
	    
	//    File file = new File(multipartile.getOriginalFilename());
	    long contentLength = file.length();
	    long partSize = 5242880; // Set part size to 5 MB.

	    try {
	        // Step 2: Upload parts.
	        long filePosition = 0;
	        for (int i = 1; filePosition < contentLength; i++) {
	            // Last part can be less than 5 MB. Adjust part size.
	        	partSize = Math.min(partSize, (contentLength - filePosition));
	        	
	            // Create request to upload a part.
	            UploadPartRequest uploadRequest = new UploadPartRequest()
	                .withBucketName(existingBucketName).withKey(keyName)
	                .withUploadId(initResponse.getUploadId()).withPartNumber(i)
	                .withFileOffset(filePosition)
	                .withFile(file)
	                .withPartSize(partSize);

	            // Upload part and add response to our list.
	            partETags.add(
	            		s3Client.uploadPart(uploadRequest).getPartETag());

	            filePosition += partSize;
	        }

	        // Step 3: Complete.
	        CompleteMultipartUploadRequest compRequest = new 
	                     CompleteMultipartUploadRequest(
	                                existingBucketName, 
	                                keyName, 
	                                initResponse.getUploadId(), 
	                                partETags);

	        s3Client.completeMultipartUpload(compRequest);
	       
	        
	       
	        
	    } catch (Exception e) {
	        s3Client.abortMultipartUpload(new AbortMultipartUploadRequest(
	                existingBucketName, keyName, initResponse.getUploadId()));
	    }
	    return "forward:/getUserContentById";
}
	 
	 private void updateUserContentDB(HttpServletRequest request, MultipartFile multipartile, FileUpload fileUpload) {
		 
		 Connection conn = null;
		 String userName = (String)request.getSession().getAttribute("user");

		  try{
		  conn = JDBCDBConnection.getRemoteConnection();
		  
		/*   INSERT INTO user_content(user_id,file_name,file_description,uploaded_on,updated_on) 
		   VALUES ('RAJI','S3FileUpload - Copy.txt','copy',sysdate(),current_timestamp()) 
		ON DUPLICATE KEY UPDATE updated_on = current_timestamp(); 
		   
		   */
		   
		 PreparedStatement ps = conn.prepareStatement(
			        "INSERT INTO user_content (user_id,file_name,file_description,uploaded_on,updated_on)" +
			        " VALUES (?, ?,?, ?,?) " + "ON DUPLICATE KEY UPDATE updated_on = ? ");
			ps.setString(1, userName);
			ps.setString(2,   multipartile.getOriginalFilename());
			
			ps.setString(3, fileUpload.getDescription());
			ps.setTimestamp(4, new java.sql.Timestamp(new java.util.Date().getTime()));
			ps.setTimestamp(5, new java.sql.Timestamp(new java.util.Date().getTime()));
			ps.setTimestamp(6, new java.sql.Timestamp(new java.util.Date().getTime()));
			int euReturnValue = ps.executeUpdate();
			System.out.println(String.format("executeUpdate returned %d", euReturnValue));

		  } catch (SQLException ex) {
			    // Handle any errors
			    System.out.println("SQLException " + ex.getMessage());
			    System.out.println("SQLState: " + ex.getSQLState());
			    System.out.println("VendorError: " + ex.getErrorCode());
			  } finally {
			       System.out.println("Closing the connection.");
			      if (conn != null) try { conn.close(); } catch (SQLException ignore) {}
			  }
		 // model.put("message", this.message);
		 
	 }
	 private File convertFromMultiPart(MultipartFile multipartFile) throws IOException {

		 File file = new File(multipartFile.getOriginalFilename());
			file.createNewFile(); 
			FileOutputStream fos = new FileOutputStream(file); 
			fos.write(multipartFile.getBytes());
			fos.close(); 

			return file;
		}
	 
	 
	 @RequestMapping(value = "/fileDownload", method = RequestMethod.GET)
	 public  String downloadFile(@ModelAttribute FileUpload fileUpload, final HttpServletRequest request, final HttpServletResponse response) {
		 	
			String existingBucketName  = "myapp-content"; 
		    String keyName             = fileUpload.getFileName();
		    BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
			 AmazonS3 s3Client = AmazonS3Client.builder().withRegion("us-west-1").withCredentials(new AWSStaticCredentialsProvider(awsCreds))
	                 .build();
			 String fileContentType = null;
			 String extension = FilenameUtils.getExtension(keyName);
	        try {
	        	 S3Object o = s3Client.getObject(existingBucketName, keyName);
	        	    S3ObjectInputStream s3is = o.getObjectContent();
	        	    
	        	    fileContentType =  o.getObjectMetadata().getContentType();
		        	   
   	    		 logger.info("Content-Type: "  + 
   	    				 fileContentType);
   	    		OutputStream fos = response.getOutputStream();
   	    byte[] read_buf = new byte[1024];
   	    int read_len = 0;
   	    while ((read_len = s3is.read(read_buf)) > 0) { 
   	        fos.write(read_buf, 0, read_len);
   	        
   	    }
   	    
			System.out.println("Extension " + extension);
			if (extension.equals("pdf"))
				response.setContentType("application/pdf");
			else if (extension.equals("txt"))
				response.setContentType("application/text");
			else if (extension.equals("jpeg") || extension.equals("jpg") || extension.equals("png"))
				response.setContentType("application/jpg");
			else if (extension.equals("zip"))
				response.setContentType("application/zip");
			else if (extension.equals("docx")|| extension.equals("doc"))
				response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
			else if (extension.equals("docx")|| extension.equals("doc"))
				response.setContentType("application/msword");
			
			
			
			response.setHeader("Content-Disposition", "filename=" + keyName);
			response.setContentLength(read_buf.length);

   	 
   	    s3is.close();
   	    fos.flush();
   	    fos.close();
	        	    

	    
	        	       
	        	    } catch (IOException e) {
	        	        e.printStackTrace();
	        	    
	        } catch (AmazonServiceException ase) {
	            System.out.println("Caught an AmazonServiceException, which" +
	            		" means your request made it " +
	                    "to Amazon S3, but was rejected with an error response" +
	                    " for some reason.");
	            System.out.println("Error Message:    " + ase.getMessage());
	            System.out.println("HTTP Status Code: " + ase.getStatusCode());
	            System.out.println("AWS Error Code:   " + ase.getErrorCode());
	            System.out.println("Error Type:       " + ase.getErrorType());
	            System.out.println("Request ID:       " + ase.getRequestId());
	        } catch (AmazonClientException ace) {
	            System.out.println("Caught an AmazonClientException, which means"+
	            		" the client encountered " +
	                    "an internal error while trying to " +
	                    "communicate with S3, " +
	                    "such as not being able to access the network.");
	            System.out.println("Error Message: " + ace.getMessage());
	        }
	        
	        return "FileUpload";
	    
	 }
 
	 @RequestMapping(value = "/fileDelete", method = RequestMethod.GET)
	 private String fileDelete (@ModelAttribute FileUpload fileUpload, final HttpServletRequest request, final HttpServletResponse response) {
		 String existingBucketName  = "myapp-content"; 
         String keyName             = fileUpload.getFileNameToDelete();
         String userName = (String)request.getSession().getAttribute("user");
		 System.out.println("Delete file name " + keyName);
         //AmazonS3 s3Client = new AmazonS3Client(new ProfileCredentialsProvider());
         
         BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
 		/* AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
 		                         .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
 		                         .build();
 		 s3Client.setRegion(Region.getRegion(Regions.US_WEST_1));*/
 		 
 		 
 		 AmazonS3 s3Client = AmazonS3Client.builder().withRegion("us-west-1").withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                  .build();
         
         try {
         s3Client.deleteObject(new DeleteObjectRequest(existingBucketName, keyName));
         System.out.println("Deleted");
         deleteDBContent(userName,keyName);
         System.out.println("Delete Completed");
         }catch(Exception e) {
        	 e.printStackTrace();
         }      
         return "forward:/getUserContentById";
	 }
	 
	 private void deleteDBContent(String userName, String fileName) throws UnsupportedEncodingException {
		 
		 Connection conn = null;
		  try{
			  conn = JDBCDBConnection.getRemoteConnection();
	
		
			  Statement readStatement = null;
			 PreparedStatement ps = conn.prepareStatement(
				        "DELETE FROM user_content where file_name =? and user_id =?");
				//ps.setString(1, userName);
				ps.setString(1, fileName);
				ps.setString(2, userName);

				ps.executeUpdate();


			  } catch (SQLException ex) {
				    // Handle any errors
				    System.out.println("SQLException: " + ex.getMessage());
				    System.out.println("SQLState: " + ex.getSQLState());
				    System.out.println("VendorError: " + ex.getErrorCode());
				  } finally {
				       System.out.println("Closing the connection.");
				      if (conn != null) try { conn.close(); } catch (SQLException ignore) {}
				  }
		 
		 
	 }
	 private static File createSampleFile() throws IOException {
	        File file = File.createTempFile("aws-java-sdk-", ".txt");
	        file.deleteOnExit();

	        Writer writer = new OutputStreamWriter(new FileOutputStream(file));
	        writer.write("abcdefghijklmnopqrstuvwxyz\n");
	        writer.write("01234567890112345678901234\n");
	        writer.write("!@#$%^&*()-=[]{};':',.<>/?\n");
	        writer.write("01234567890112345678901234\n");
	        writer.write("abcdefghijklmnopqrstuvwxyz\n");
	        writer.close();

	        return file;
	    }
}