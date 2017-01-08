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
                url: "/admin/pause.json",
                accept: "JSON",
                success: function (response) {
                    if (response) {
                        console.log(response);
                    } else {
                        // error code here
                    }
                }
            }).done(function() {
                $( this ).addClass( "done" );
            });
        });
    });
</script>
<div style="text-align: center;"><h1>Admininstration Console</h1></div>
<div>
    <c:choose>
        <c:when test="${tickerStatus}" >
            <c:set var="tickerStatusLabel" scope="session" value="Paused"/>
            <c:set var="tickerButtonLabel" scope="session" value="Unpause Ticker"/>
        </c:when>
        <c:otherwise>
            <c:set var="tickerButtonLabel" scope="session" value="Pause Ticker"/>
            <c:set var="tickerStatusLabel" scope="session" value="Running"/>
        </c:otherwise>
    </c:choose>
    <div style="text-align: center;"><h1>Ticker Service Status <b>${tickerStatusLabel}</b></h1>
    <div>
        <button style="width:200px;" id="pauseTicker">${tickerButtonLabel}</button>
    </div>
    </div>
</div>
</body>
</html>
