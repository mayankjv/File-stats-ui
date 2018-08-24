
function sendName() {

	stompClient.send("/socket/message", {}, JSON.stringify({'name': $("#text-content").val()}));
}


var stompClient = null;


$(document).ready(function() {	
	connectSocket();
});

function connectSocket() {
	var socket = new SockJS('/filestatisticsui');
	stompClient = Stomp.over(socket);
	stompClient.connect({}, function (frame) {
		//setConnected(true);
		console.log('Connected: ' + frame);
		stompClient.subscribe('/socketresponse/greetings', function (greeting) {
			console.log(greeting.body);
			showGreeting(JSON.parse(greeting.body));
		});
	});
}


function showGreeting(data) {

	data=JSON.parse(data);
	$("#progress-bar").width(data.name+"%").text(data.name+"%").show();
	console.log(data.name);
	if(data.name == "100"){
		setTimeout(function(){ $("#progress-bar").hide(); }, 1000);
		$("#progress-bar").width("0%").text("0%");
	}

}


$( "#text-content-btn" ).click(function() { 
	sendName();
});

