<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<link href="<%=request.getContextPath()%>/css/tt_user.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-3.2.0.min.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.validate.min.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/tt_user.js"></script>
	<title>Login</title>
</head>
<body>

	<div class="login-title">
		<h1>Login</h1>
	</div>
	<div class="login-content">
		<form action="<%=request.getContextPath()%>/jsp/userView.jsp" method="post" id="tt_userLogin">
			<div class="form-group">
				<input type="text" name="username" placeholder="username">
			</div>
			<div class="form-group">
				<input type="text" name="password" placeholder="password">
			</div>
			<div class="form-group">
				<button type="submit">Login</button>
			</div>
			<div class="form-group">
				Forget password?<a href="javascript:void(0)">Find</a>Not register? <a
					href="<%=request.getContextPath()%>/jsp/userRegister.jsp">Register</a>
			</div>
		</form>
	</div>

</body>
</html>