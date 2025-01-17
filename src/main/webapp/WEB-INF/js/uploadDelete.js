//전체 선택 또는 전체 해제
$('#all').click(function(){
    if($('#all').prop('checked'))
        $('input[name="check"]').prop('checked', true);
    else
        $('input[name="check"]').prop('checked', false);
}); // #all

// 전체 선택한 후 개별 선택사항을 1개라도 해제한다면 전체 선택이 해제 되어야 함
$('input[name="check"]').click(function(){
    console.log('개수 = ' + $('input[name="check"]').length); // checkbox 의 개수
    console.log('개수 = ' + $('input[name="check"]:checked').length); // true 로 선택한 check의 개수

    // 전체 체크박스의 개수와 선택된 체크박스 개수를 비교하여 전체 선택 여부를 갱신
    $('#all').prop('checked', $('input[name="check"]').length == $('input[name="check"]:checked').length);
    // false 는 해제, true 는 선택
	
});

$(function(){
	$('#uploadDeleteBtn').click(function(){
		$.ajax({
				type: 'post',
                url: '/spring/user/uploadDelete',
                data: $('#uploadListForm').serialize(), // 체크된 항목만 서버로 간다.
                success: function() {
                	alert("이미지 삭제 완료");
                	location.href = '/spring/user/uploadList';
                },
                error: function(e) {
                    console.log(e); 
                }
		}); // $.ajax
	}); // #uploadDeleteBtn
});