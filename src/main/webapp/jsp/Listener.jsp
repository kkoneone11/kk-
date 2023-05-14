<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 2023/4/27
  Time: 9:25
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html lang="en">
<html>
<head>
    <meta charset="UTF-8">
    <title>Online Users</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f2f2f2;
        }
        h1 {
            text-align: center;
            margin-top: 50px;
            color: #333;
        }
        .container {
            display: flex;
            flex-direction: column;
            align-items: center;
            margin-top: 50px;
        }
        .count {
            font-size: 48px;
            font-weight: bold;
            color: #333;
        }

        .test th {
            background-color: #f2f2f2;
            color: #000;
            font-weight: bold;
            text-align: center;
            padding: 10px;
            border: 1px solid #ddd;
        }
    </style>
</head>
<body>
<h1>Online Users</h1>
<div class="container">
    <%--ajax实现查看人工在线人数--%>
    <div class="count">
        <input type="button" value="查看在线员工人数" id="listener">
    </div>
    <div id="data-container">

    </div>


<%--    显示员工信息--%>
    <div class = "container">
        <a href="http://localhost:8080/EmpInfoServlet">查看在线员工信息</a>
        <table class="test">

            <tr>
                <th>员工id</th>
                <th>员工姓名</th>
                <th>员工电话</th>
            </tr>

            <c:forEach items="${sessionScope.employeeList}" var="e">
                <c:if test="${not empty e}">
                    <tr>
                        <th><c:out value="${e.id}"/></th>
                        <th><c:out value="${e.name}"/></th>
                        <th><c:out value="${e.phone}"/></th>
                    </tr>
                </c:if>

            </c:forEach>
        </table>
    </div>




</div>

<script src="../../plugins/vue/vue.js"></script>
<!-- 引入组件库 -->
<script src="../../plugins/element-ui/index.js"></script>
<script src="https://code.jquery.com/jquery-3.1.1.js"></script>

<script>
    $(function (){
        $("#listener").click(function (){
            $.ajax({
                method:'GET',
                url:'http://localhost:8080/listener/total',
                dataType:"json",
                contentType: "application/json;charset=utf-8",
                success:function (res){
                    if(res.code==1){
                        var html="";
                        html+="<tr>"+
                            "<td>"+res.data+"</td>"+
                            "</tr>"
                        $("#data-container").html(html);
                    }
                },
            });
        });
    });

</script>
</body>
</html>
