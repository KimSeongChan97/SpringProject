$(function(){
	$('#uploadUpdateBtn').click(function(){
		let formData = new FormData($('#uploadUpdateForm')[0]); 
		
		$.ajax({
			type: 'post', // POST 방식으로 서버에 데이터를 전송
            enctype: 'multipart/form-data', // 파일을 포함한 데이터를 전송하기 위해 설정
            processData: false, // 데이터를 쿼리스트링으로 변환하지 않도록 설정
            contentType: false, // 'multipart/form-data'로 전송하기 위해 contentType 설정을 false로 설정
            url: '/spring/user/uploadUpdate', // 파일을 전송할 서버의 URL
            data: formData, // 폼 데이터를 담은 formData 객체 전송
            success: function(data) { 
                // 요청이 성공적으로 완료되었을 때 실행되는 콜백 함수
                // 업로드가 완료되면, 페이지를 "/spring/user/uploadList"로 리다이렉트합니다.
                alert('이미지 수정 완료, 목록으로 넘어갑니다.');
                location.href = "/spring/user/uploadList"; 
            },
            error: function(e) { 
                // 요청이 실패했을 때 오류 메시지를 콘솔에 출력합니다.
                console.log(e); 
            }   
		}); // $.ajax
		
	}); // #uploadUpdateBtn
	
}); // $(function)