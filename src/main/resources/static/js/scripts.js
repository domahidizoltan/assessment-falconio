var host = "http://localhost:8080"

function loadMessages() {
    console.log("loading messages...");
    $.get(host + "/messages/", function(data) {
        data.forEach(function(message) {
            var block = "<div class='message'>" +
                            "<div class='id'>" + message.id + "</div>" +
                            "<div class='createTime'>" + message.createTime + "</div>" +
                            "<div class='content'><pre>" + message.content + "</pre></div>" +
                         "</div>";
            $('#messages').prepend(block);
        })
    });

    $("#new-message").focus();
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

$(document).ready(loadMessages);