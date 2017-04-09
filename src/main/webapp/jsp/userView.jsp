<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<title>User View</title>
</head>
<body>

	<div class="login-title">
		<h1>User View</h1>
	</div>
	<div class="login-content">
		<table border="1" cellpadding="10" cellspacing="0">
			<tr>
				<th>id</th>
				<th>email</th>
				<th>password</th>
				<th>operate</th>
			</tr>
			<c:forEach var="userInfo" items="${pageBean.userList}">
				<tr>
					<td>${userInfo.id}</td>
					<td>${userInfo.email}</td>
					<td>${userInfo.password}</td>
					<td><span><a
							href="/SpringMVC/usermodify.do?id=${userInfo.id}"
							id="usermodify">Modify</a></span> <span><a
							href="/SpringMVC/userdelete.do?id=${userInfo.id}"
							id="userdelete">Delete</a></span></td>
				</tr>
			</c:forEach>
		</table>	
	</div>

	<div>
		<span>Want to add more user?</span> 
		<span> <a href="/SpringMVC/register.do">Add</a></span>
	</div>

</body>
</html>