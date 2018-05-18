<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
</head>
<body>
服务器返回的信息：
<input type="text" id="show"/>

浏览器发送的信息：
<input type="text" id="msg"/>
<input type="button" value="send" id="send"/>

<script src="http://apps.bdimg.com/libs/jquery/1.8.3/jquery.min.js"></script>
<script>
    var w = null ;
    if('WebSocket' in window){
        w = new WebSocket('ws://localhost:8080/QTravel/WebsocketByAnnotation') ;
    }else {
        console.warn('不支持websocket') ;
    }

    w.onopen = function(obj){
        console.info('open') ;
        console.info(obj) ;
    } ;
    w.onmessage = function(obj){
// 这里接收服务器返回的信息
        console.info('msg') ;
        $('#show').val(obj.data) ;
    } ;
    w.onclose = function (obj) {
        console.info('close') ;
        console.info(obj) ;
    } ;


    $('#send').click(function(){
        var msg = $('#msg').val() ;
        w.send(msg) ;
    }) ;

</script>
</body>
</html>