<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html lang="en">
<head>
</head>
<body>
<h2>File Upload Success</h2>

<div class="container">

		<div class="starter-template">
		
		<table border="1">
		<tr>
		<td>FileName
		</td>
		<td>Description
		</td><td>UploadedOn
		</td><td>UpdatedOn
		</td>
		</tr>
		<tr>
		<td>${fileUpload.fileName}
		</td>
		<td>${fileUpload.description}
		</td><td>${fileUpload.uploadedOn}
		</td><td>${fileUpload.updatedOn}
		</td>
		</tr>
		</div>

	</div>
</body>
</html>
