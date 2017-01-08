<%--
  Created by IntelliJ IDEA.
  User: bilgi
  Date: 3/25/15
  Time: 7:47 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/base.jsp"%>
<html>
<head>
    <title>Accounts</title>
    <style type="text/css">
        .accountsTable{
            width: 300px;
            align: right;
        }
    </style>
</head>
<body>
<div class="mainBody" style="text-align: center;">
    <h1>Accounts Page</h1>
    <c:forEach items="${accounts}" var="account">
        <div id="account-${account.accountId}" style="text-align: center;">
            <table class="accountsTable" align="center" style="border: solid; ">
                <tr>
                    <td>Account Name:</td><td>${account.accountName}</td>
                </tr>
                <tr>
                    <td>Account Id:</td><td>${account.accountId}</td>
                </tr>
                <tr>
                    <td>Account Balance:</td><td>${account.balance}</td>
                </tr>
            </table>
        </div>
    </c:forEach>
</div>

</body>
</html>
