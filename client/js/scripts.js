var host = "http://localhost:8080"
var stompClient;

function loadMessages() {
    console.log("loading messages...");
    $.get(host + "/messages/", function(data) {
        data.forEach(addMessageToList)
    });

    $("#new-message").focus();

    var socket = new SockJS(host + '/falcon-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, connectWSCallback, errorWSCallback);
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
    stompClient.send("/app/save-message", {}, message);
}

function connectWSCallback(frame) {
    console.log('Connected: ' + frame);

    stompClient.subscribe('/topic/messages', function(data) {
        var message = JSON.parse(data.body);
        addMessageToList(message);
    });

    stompClient.subscribe('/topic/errors', function(data) {
        alert("Could not save data: " + data.body);
    });

}

function errorWSCallback(error) {
    alert("Could not connect to websocket. Please refresh the page.");
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