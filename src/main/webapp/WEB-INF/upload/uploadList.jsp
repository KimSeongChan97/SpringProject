<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>업로드 이미지 리스트</title>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/uploadList.css">
<link rel="icon" href="${pageContext.request.contextPath}/static/favicon.ico" type="image/x-icon">
</head>
<body>

<div class="home-container">
    <a href="/spring/"><i class="fa-solid fa-house"></i> HOME</a>
</div>


<div class="table-container">
	<form id="uploadListForm">
    <input type="button" value="삭제" id="uploadDeleteBtn" />

    <table border="1" frame="hsides" rules="rows">
        <tr>
            <th><input type="checkbox" id="all" /></th>
            <th width="80">
                <i class="fa-solid fa-list-ol"></i> 번호
                
            </th>
            <th width="150">
                <i class="fa-solid fa-image"></i> 이미지
            </th>
            <th width="150">
                <i class="fa-solid fa-tag"></i> 상품명
            </th>
        </tr>

        <c:forEach var="userUploadDTO" items="${list}">
            <tr>
                <td><input type="checkbox" name="check" id="check" value="${userUploadDTO.seq}" /></td>
                
                <td>${userUploadDTO.seq}</td>
                <td>
                	<!-- Object Storage -->
                    <a href="/spring/user/uploadView?seq=${userUploadDTO.seq}"> 
                        <img src="http://kr.object.ncloudstorage.com/bitcamp-9th-bucket-65/storage/${userUploadDTO.imageFileName}" 
                             alt="${userUploadDTO.imageName}" width="80" height="80">     
                    </a>       
                </td>
                <td>${userUploadDTO.imageName}</td>
            </tr>
        </c:forEach>
    </table> 
    </form>
</div>


<script type="text/javascript" src="https://code.jquery.com/jquery-3.7.1.min.js"></script> 
<script type="text/javascript" src="${pageContext.request.contextPath}/js/uploadDelete.js"></script>

</body>
</html>
