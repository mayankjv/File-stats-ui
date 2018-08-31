
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
		stompClient.subscribe('/socketresponse/status', function (status) {
			console.log(status.body);
			showStatus(JSON.parse(status.body));
		});
	});
}


function watcher() {
	var socket = new SockJS('/filestatisticsui');
	stompClient = Stomp.over(socket);
	stompClient.connect({}, function (frame) {
		//setConnected(true);
		console.log('Connected: ' + frame);
		stompClient.subscribe('/socketresponse/watcher', function (newList) {
			console.log(newList.body);
			showUpdatedList(JSON.parse(newList.body));
		});
	});
}


function showUpdatedList(data){
	console.log(data);
}

function showStatus(data) {

	data=JSON.parse(data);
	$("#progress-bar").width(data.name+"%").text(data.name+"%").show();
	console.log(data.name);
	if(data.name == "100"){
		setTimeout(function(){ $("#progress-bar").hide(); }, 1000);
	}

}


$( "#text-content-btn" ).click(function() { 
	sendName();
});

