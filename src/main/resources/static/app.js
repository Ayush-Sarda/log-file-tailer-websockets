var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    // $("#send").prop("disabled", !connected);
    // $("#send").prop("disabled", connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#loginfo").html("");
}

function connect() {
    var socket = new SockJS('/websocket-tailer');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/log', function (greeting) {
            showGreeting(JSON.parse(greeting.body).content);
        });
    });
    var obj = {
        name: "start"
    };
    // stompClient.send("/app/log", {}, JSON.stringify(obj));
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
    $("#name-form").show();
}

function sendName() {
    stompClient.send("/app/log", {}, JSON.stringify({'name': $("#name").val()}));
    $("#name-form").hide();
}

function showGreeting(message) {
    $("#loginfo").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
});