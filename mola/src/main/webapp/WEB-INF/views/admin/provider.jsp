<%--
  Created by IntelliJ IDEA.
  User: bilgi
  Date: 3/22/15
  Time: 1:53 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Admin Index</title>
    <script type="text/javascript" src="/resources/scripts/jquery-1.11.2.js"></script>
</head>
<body>
<script type="text/javascript">
    $(document).ready(function(){
        $('#pauseTicker').on('click', function(){
            $.ajax({
                url: "/admin/pause.json"
            }).done(function() {
                $( this ).addClass( "done" );
            });
        });
    });
</script>
<div style="text-align: center;"><h1>Admininstration Console</h1></div>
<div>
    <button id="pauseTicker">Pause</button>
    <div id="hostCredentialsForm">
        <form>
            <table>
                <tr>
                    <td><label>Host</label><input id="host" name="host" type="text"/></td>
                </tr>
                <tr>
                    <td>
                        <label>Provider</label>
                        <select name="accountType" id="accountType">
                            <c:forEach items="${accountTypes}" var="accountType">
                                <option value="${accountType.value}">${accountType}</option>
                            </c:forEach>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td> <label>Username</label><input id="username" name="username" type="text"/></td>
                </tr>
                <tr>
                    <td><label>Password</label><input id="password" name="password" type="password"/></td>
                </tr>

            </table>
            <input type="submit"/>
        </form>
    </div>
</div>
</body>
</html>
