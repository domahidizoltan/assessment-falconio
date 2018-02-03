var host = "http://localhost:8080"
var stompClient;
var beforeCreateTime = "";

function initWSConnection() {
    var socket = new SockJS(host + '/falcon-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, connectWSCallback, errorWSCallback);
}

function loadMessages() {
    console.log("loading messages...");
    $.get(host + "/messages/?limit=3&createTime=" + beforeCreateTime, function(data) {
        var btnText = data.length != 0 ? "get more" : "no more";
        $("#getMoreBtn").text(btnText);

        data.forEach(addMessageToList);
    });
}

function addMessageToList(message, prepend) {
    var block = "<div class='message'>" +
                    "<div class='id'>" + message.id + "</div>" +
                    "<div class='createTime'>" + message.createTime + "</div>" +
                    "<div class='content'><pre>" + message.content + "</pre></div>" +
                 "</div>";

    if (prepend !== "prepend") {
        beforeCreateTime = message.createTime;
        $('#messages').append(block);
    } else {
        $('#messages').prepend(block);
    }
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
        addMessageToList(message, "prepend");
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

function scrollToPlace(place) {
    var toPlace = place == "bottom" ? $(document).height() : 0;
    $("html, body").animate({ scrollTop: toPlace }, "slow");
}

$(document).ready(function (){
    initWSConnection();
    loadMessages();
    $("#new-message").focus();
});
$(window).on('beforeunload', disconnectWS);