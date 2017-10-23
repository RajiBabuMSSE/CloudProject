<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ page trimDirectiveWhitespaces="true" %>
    <%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<!-- <html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Spring Boot - New Features</title>
</head>
<body>
Spring Boot - New Features
 <body>
      <h3>File Upload:</h3>
      Select a file to upload: <br />
      <form action = "uploadFile" method = "post"
         enctype = "multipart/form-data">
         <input type = "file" name = "file" size = "50" />
         <br />
         <input type = "submit" value = "Upload File" />
      </form>
   </body>
</body>
</html> -->

<html>
<head>
<meta http-equiv="Content-type" content="text/html;charset=UTF-8">
<title>Show File Data</title>
<style type='text/css'>
body {
    font-family: sans-serif;
}
</style>
<script type='text/javascript'>
function showFileSize() {
    var input, file;

    // (Can't use `typeof FileReader === "function"` because apparently
    // it comes back as "object" on some browsers. So just see if it's there
    // at all.)
    if (!window.FileReader) {
        bodyAppend("p", "The file API isn't supported on this browser yet.");
        return;
    }

    input = document.getElementById('file');
    if (!input) {
        bodyAppend("p", "Um, couldn't find the fileinput element.");
    }
    else if (!input.files) {
        bodyAppend("p", "This browser doesn't seem to support the `files` property of file inputs.");
    }
    else if (!input.files[0]) {
        bodyAppend("p", "Please select a file before clicking 'Load'");
    }
    else {
        file = input.files[0];
     //   bodyAppend("p", "File " + file.name + " is " + file.size + " bytes in size");
        if(file.size>10485760){
        	alert("Can't upload the file. File Size is more than 10 MB");
        return;
        }
    }
    document.fileUpload.submit()
}

function bodyAppend(tagName, innerHTML) {
    var elm;

    elm = document.createElement(tagName);
    elm.innerHTML = innerHTML;
    document.body.appendChild(elm);
}
function bodyAppend(tagName, innerHTML) {
    var elm;

    elm = document.createElement(tagName);
    elm.innerHTML = innerHTML;
    document.body.appendChild(elm);
}

function downloadFile(fileName){

	document.getElementById("fileName").value = fileName;
    document.fileDownload.submit();
	
}

function deleteFile(){
	var radios = document.getElementsByName('deleteRadio');
	var fileName;
	for (var i = 0, length = radios.length; i < length; i++) {
	   if (radios[i].checked) {
	       fileName = radios[i].value.trim();
	       break;
	   }
	}

	document.getElementById("fileNameToDelete").value= fileName;

	   document.fileDelete.submit();

	} 
/* 
function deleteFile(){
	
	
	var table = document.getElementById("tableFiles");
	for (var i = 0, row; row = table.rows[i]; i++) {
	   //iterate through rows
	   //rows would be accessed using the "row" variable assigned in the for loop
	   
	   if (document.getElementsByName("deleteRadio").checked = true){
		   alert ('Radio checked');
	   }
	   for (var j = 0, col; col = row.cells[j]; j++) {
	     //iterate through columns
	     //columns would be accessed using the "col" variable assigned in the for loop
	   }  
	}
	
	
 
	
    document.fileDelete.submit(); 
	
} */
</script>
</head>
<body onload="prepopulateFields();load(17);">
<div align="right">
<h3><center>FILE MANAGEMENT SYSTEM</center></h3>
<table>
<tr>
<td></td>
<td></td>
<td >
Welcome <b><%= session.getAttribute("userName") %><b> 
</td>
</tr>
<tr>
<td></td>
<td></td>
<td >
<a href="/logout" >Logout</a> 
</td>
</tr>
</table>
</div>
<div align="center">
 <form name= "fileUpload" action = "fileUpload" method = "post" enctype = "multipart/form-data">
<input type = "file" name = "file" id="file" size = "50" />
Description: 
 <input type="text" id="description" name="description" />

<input type='button' id='btnLoad' value='Upload' onclick='showFileSize();'>
</form>
<br>
<br>
<br>
	 <table border="1" id="tableFiles">
		<tr>
		<td>Delete</td>
		<td>FileName
		</td>
		<td>Description
		</td><td>UploadedOn
		</td><td>UpdatedOn
		</td>
		</tr>
			
<c:forEach items="${fileUploadList}" var="fileUpload">
       
		
		<tr>
		<td><input type="radio" name="deleteRadio" value ="${fileUpload.fileName}"/></td>
		<td>
		<a href="#" onclick="downloadFile('${fileUpload.fileName}');">${fileUpload.fileName}</a>
		</td>
		<td>${fileUpload.description}
		</td><td>${fileUpload.uploadedOn}
		</td><td>${fileUpload.updatedOn}
		</td>
		</tr>

    </c:forEach>
</table><br>
    <form name= "fileDelete"  action="fileDelete" method="get">
    <button type="button" id=deleteButton name="deleteButton" onclick = "deleteFile();" >Delete</button>
      <input type="hidden" id="fileNameToDelete" name="fileNameToDelete" />
</form>



    <form name= "fileDownload"  action="fileDownload" method="get">
  <input type="hidden" id="fileName" name="fileName" />
    </form>
 
</div>

</body>
</html>