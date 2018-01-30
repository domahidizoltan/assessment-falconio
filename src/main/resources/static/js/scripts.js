var host = "http://localhost:8080"
var stompClient;

function loadMessages() {
    console.log("loading messages...");
    $.get(host + "/messages/", function(data) {
        data.forEach(addMessageToList)
    });

    $("#new-message").focus();

    var socket = new SockJS('http://localhost:8080/falcon-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, connectWSCallback);
}

function addMessageToList(message) {
    var block = "<div class='message'>" +
                    "<div class='id'>" + message.id + "</div>" +
                    "<div class='createTime'>" + message.createTime + "</div>" +
                    "<div class='content'><pre>" + message.content + "</pre></div>" +
                 "</div>";
    $('#messages').prepend(block);
}

function submitMessage() {
    var textarea = $("#new-message");
    var message = textarea.val();
    console.log("saving message: " + message);
    $.ajax(host + "/messages/", {
        type: "POST",
        dataType: 'json',
        data: message,
        contentType: 'application/json',
        timeout: 3000,
        success: function() { textarea.val(""); },
        error: function(request, status, errorThrown) { alert($.parseJSON(request.responseText).error); }
    });
}

function connectWSCallback(frame) {
    console.log('Connected: ' + frame);
    stompClient.subscribe('/topic/messages', function(data) {
        var message = JSON.parse(data.body);
        addMessageToList(message);
    });
}

function disconnectWS() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

$(document).ready(loadMessages);
$(window).on('beforeunload', disconnectWS)