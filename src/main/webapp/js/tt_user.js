$(function() {
	$("#tt_userLogin").validate({
		rules : {
			username : {
				required : true,
				email : true
			},
			password : {
				required : true,
				rangelength : [ 5, 10 ]
			}
		}
	});
});

$(function() {
	$("#tt_userRegister").validate({
		rules : {
			username : {
				required : true,
				email : true
			},
			password : {
				required : true,
				rangelength : [ 5, 10 ]
			},
			confirmpassword : {
				required : true,
				equalTo : "#password"
			}
		}
	});
});