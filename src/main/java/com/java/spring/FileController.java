package com.java.spring;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.java.beans.FileUpload;
import com.java.beans.Login;
import com.java.jdbc.JDBCDBConnection;



@Controller
public class FileController {
	
	private static final Logger logger = LogManager.getLogger(FileController.class);

	

	
	
	@RequestMapping("/createUserProfileDB")
	public String createUserProfileDB(Map<String, Object> model) {
		String redirect = "Success";
	    logger.info("Start");
		 Connection conn = null;
		  Statement setupStatement = null;


		  try {
		    // Create connection to RDS DB instance
		    conn = JDBCDBConnection.getRemoteConnection();
		    
		    // Create a table and write two rows
		    setupStatement = conn.createStatement();
		    String createTable = "create table user_profile"+
"("+
	"user_id varchar(50) primary key,"+
	"user_password varchar(50) not null,"+
	"user_first_name varchar(50) not null,"+
	"user_last_name varchar(50) not null"+
");";
		   // String insertRow1 = "INSERT INTO Beanstalk (Resource) VALUES ('EC2 Instance');";
		   // String insertRow2 = "INSERT INTO Beanstalk (Resource) VALUES ('RDS Instance');";
		    
		    String insertRow1 = "insert into user_profile values ('BBABU', 'balachandar', 'Balachandar', 'Babu');";
		    String insertRow2 = "insert into user_profile values ('RAJI', 'rajalakshmi', 'Rajalakshmi', 'Babu');";
		    String insertRow3 = "insert into user_profile values ('KRISH', 'krishna', 'Krishna', 'Babu');";
		    
		    
		    setupStatement.addBatch(createTable);
		    setupStatement.addBatch(insertRow1);
		    setupStatement.addBatch(insertRow2);
		    setupStatement.executeBatch();
		    setupStatement.close();
		    
		    logger.info("Created");
		    
		  } catch (SQLException ex) {
		    // Handle any errors
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		    model.put("message",  ex.getMessage());
		    redirect = "error";
		  } finally {
		    System.out.println("Closing the connection.");
		    if (conn != null) try { conn.close(); } catch (SQLException ignore) {}
		  }		  
		  return redirect;		
	}
	
	@RequestMapping("/getUserProfileDB")
	public String getUserProfileDB(Map<String, Object> model) {
		
		 Connection conn = null;
		  Statement setupStatement = null;
		  Statement readStatement = null;
		  ResultSet resultSet = null;
		  String results = "";
		  int numresults = 0;
		  String statement = null;

		  try{
		  conn = JDBCDBConnection.getRemoteConnection();
		    
		    readStatement = conn.createStatement();
		    resultSet = readStatement.executeQuery("SELECT user_id,user_password FROM user_profile;");
		    

		    while (resultSet.next()) {              

		            System.out.println(resultSet.getString("user_id"));
		            System.out.println(resultSet.getString("user_password"));
		           /* System.out.println(resultSet.getString("Col 3"));                    
		            System.out.println(resultSet.getString("Col n"));*/
		    }
		    
		    resultSet.close();
		    readStatement.close();
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
		  
		  return "FileUploadSuccess";
		
	}
	
	@RequestMapping("/createUserContent")
	public String createUserContent(Map<String, Object> model, @ModelAttribute Login login) {
		String redirect = "Success";
	    logger.info("Start"+login.getUserID());
		 Connection conn = null;
		  Statement setupStatement = null;


		  try {
		    // Create connection to RDS DB instance
		    conn = JDBCDBConnection.getRemoteConnection();
		    
		    // Create a table and write two rows
		    setupStatement = conn.createStatement();
		    String createTable ="create table user_content"+
"("+
	"user_id varchar(50) not null,"+
	"file_name varchar(50) not null,"+
	"file_description varchar(50),"+
	"uploaded_on datetime not null,"+
	"updated_on datetime not null,"+
    "primary key (user_id, file_name)"+
");";


		   // String insertRow1 = "INSERT INTO Beanstalk (Resource) VALUES ('EC2 Instance');";
		   // String insertRow2 = "INSERT INTO Beanstalk (Resource) VALUES ('RDS Instance');";
		    
		    String insertRow1 = "insert into user_content values ('user1', 'filename1', 'filedescription1', '2017-10-15 20:40:37', '2017-10-15 20:40:37');";

		    
		    setupStatement.addBatch(createTable);
		    setupStatement.addBatch(insertRow1);
		    setupStatement.executeBatch();
		    setupStatement.close();
		    
		    logger.info("Created");
		    
		  } catch (SQLException ex) {
		    // Handle any errors
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		    model.put("message",  ex.getMessage());
		    redirect = "error";
		  } finally {
		    System.out.println("Closing the connection.");
		    if (conn != null) try { conn.close(); } catch (SQLException ignore) {}
		  }		  
		  return redirect;
	}
	
	@RequestMapping("/getUserContent")
	public String getUserContent(Map<String, List<FileUpload>> model, @ModelAttribute Login login) {
		 logger.info("Start"+login.getUserID());
		 Connection conn = null;
		  Statement setupStatement = null;
		  Statement readStatement = null;
		  ResultSet resultSet = null;
		  String results = "";
		  int numresults = 0;
		  String statement = null;
		  FileUpload fileUpload = new FileUpload();
		  List<FileUpload> fileUploadList = new ArrayList<FileUpload>();
		  try{
		  conn = JDBCDBConnection.getRemoteConnection();
		    
		  // Create connection to RDS DB instance
		    conn = JDBCDBConnection.getRemoteConnection();
		    // Create a table and write two rows
		    setupStatement = conn.createStatement();
		    String selectQuery =  "SELECT user_id,file_name, file_description, uploaded_on, updated_on FROM user_content where user_id = ? ;";
		    
				    PreparedStatement prepStmt = conn.prepareStatement(selectQuery);
				    prepStmt.setString(1, "BBABU");
				    
				   
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
			    System.out.println("SQLException: " + ex.getMessage());
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
	 
	 
	 public  String downloadFile(final HttpServletRequest request, final HttpServletResponse response) {

			
			String existingBucketName  = "myapp-content"; 
		    String keyName             = "SampleCloud";
	        AmazonS3 s3Client = new AmazonS3Client(new ProfileCredentialsProvider());
	        try {
	        	 S3Object o = s3Client.getObject(existingBucketName, keyName);
	        	    S3ObjectInputStream s3is = o.getObjectContent();
	        	   /* FileOutputStream fos = new FileOutputStream(new File(keyName));
	        	    byte[] read_buf = new byte[1024];
	        	    int read_len = 0;
	        	    while ((read_len = s3is.read(read_buf)) > 0) {
	        	        fos.write(read_buf, 0, read_len);
	        	        
	        	    }
	        	 //   byte[] byteArray =

	        	    		response.setContentType("application/text");
	        	    		response.setHeader("Content-Disposition", "filename=\"THE FILE NAME\"");
	        	    		response.setContentLength(read_buf.length);
	        	    		OutputStream os = response.getOutputStream();

	        	    		try {
	        	    		   os = fos;
	        	    		} catch (Exception excp) {
	        	    		   //handle error
	        	    		} finally {
	        	    		    os.close();
	        	    		}
	        	    
	        	    s3is.close();
	        	    fos.close();
	            
	        	    System.out.println("Done");*/
	        	    byte[] read_buf = new byte[1024];
	        	    
	        	    try {
	        	    	response.setContentType("application/text");
	    	    		response.setHeader("Content-Disposition", "filename=\"THE FILE NAME\"");
	    	    		response.setContentLength(read_buf.length);
	        	        OutputStream os = response.getOutputStream();
	        	        byte[] buf = new byte[8192];
	        	        
	        	        InputStream is = new FileInputStream(new File(keyName));
	        	        int c = 0;
	        	        while ((c = is.read(buf, 0, buf.length)) > 0) {
	        	            os.write(buf, 0, c);
	        	            os.flush();
	        	        }
	        	        os.close();
	        	        is.close();
	        	    } catch (IOException e) {
	        	        e.printStackTrace();
	        	    }
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
/*	public String uploadFileHandler(@RequestParam("file") MultipartFile multipartile, @ModelAttribute FileUpload fileUpload) throws Exception {
		 
		 Connection conn = null;


		  try{
		  conn = JDBCDBConnection.getRemoteConnection();
		 
		 PreparedStatement ps = conn.prepareStatement(
			        "INSERT INTO user_content (user_id,file_name,file_description,uploaded_on,updated_on)" +
			        " VALUES (?, ?,?, ?,?) ");
			ps.setString(1, fileUpload.getUserId());
			ps.setString(2, multipartile.getOriginalFilename());
			ps.setString(3, fileUpload.getDescription());
			ps.setDate(4, java.sql.Date.valueOf(java.time.LocalDate.now()));
			ps.setDate(5, java.sql.Date.valueOf(java.time.LocalDate.now()));
			int euReturnValue = ps.executeUpdate();
			System.out.println(String.format("executeUpdate returned %d", euReturnValue));

		  } catch (SQLException ex) {
			    // Handle any errors
			    System.out.println("SQLException: " + ex.getMessage());
			    System.out.println("SQLState: " + ex.getSQLState());
			    System.out.println("VendorError: " + ex.getErrorCode());
			  } finally {
			       System.out.println("Closing the connection.");
			      if (conn != null) try { conn.close(); } catch (SQLException ignore) {}
			  }
		 // model.put("message", this.message);
			  return "forward:/getUserContentById";
		 
	    if (!multipartile.isEmpty()) {
	        try {
	        	File file = new File( multipartile.getOriginalFilename());
	        	System.out.println(file.length());
	        	String existingBucketName  = "balachandarawsproject"; 
	            String keyName             = "bala/Sample.txt";
	           // String filePath            = "C:\\raji\\MSSE\\Fall 2017\\Cloud Computing\\Term Project\\Project1\\S3FileUpload.txt"; 
	            
	            AWSCredentials  credentials = new ProfileCredentialsProvider().getCredentials();
	            
	            AmazonS3 s3Client = new AmazonS3Client(credentials); 
	            
	            s3Client.putObject(new PutObjectRequest(
	            		existingBucketName, keyName, file));
	            
	            InputStream is=multipartile.getInputStream();
	           //s3Client.putObject(new PutObjectRequest(existingBucketName, keyName,is));
	            
	            s3Client.putObject(new PutObjectRequest(existingBucketName, keyName, createSampleFile()).withCannedAcl(CannedAccessControlList.PublicRead));

	            // Create a list of UploadPartResponse objects. You get one of these
	            // for each part upload.
	            List<PartETag> partETags = new ArrayList<PartETag>();

	            // Step 1: Initialize.
	            InitiateMultipartUploadRequest initRequest = new 
	                 InitiateMultipartUploadRequest(existingBucketName, keyName);
	            InitiateMultipartUploadResult initResponse = 
	            	                   s3Client.initiateMultipartUpload(initRequest);

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
	                throw e;
	            }
	        	
	        }
	        catch (Exception ex){
	        	ex.printStackTrace();
	        	 throw ex;
	        }
	        }

	 }
*/	 
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
	
/*	@RequestMapping("/nextPage1")
	public String  nextPage2(Map<String, Object> model) {
		model.put("message", this.message);
		return "springBoot";
	}
	
	@RequestMapping("/nextPage3")
	public String  nextPage3(Map<String, Object> model) {
		model.put("message", this.message);
		return "welcome";
	}
*/
}