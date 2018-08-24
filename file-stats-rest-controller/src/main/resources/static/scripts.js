var backPath = [""];
var currFolderName="";


function searchString(){
//	if(backPath.indexOf(currFolderName)==-1)
	backPath.push(currFolderName);
	var show= new XMLHttpRequest();
	show.onreadystatechange= searchResponse;
	var pattern= document.getElementById("search_text").value;
	show.open("GET","/searchPattern?pattern="+pattern+"&folder="+currFolderName,true);
	show.send();
}
function searchKeyword(){
	var show= new XMLHttpRequest();
	show.onreadystatechange= searchResponse;
	var keyword= document.getElementById("search_text").value;
	show.open("GET","/searchKeyword?keyword="+keyword+"&folder="+currFolderName,true);
	show.send();	

}	

function searchResponse(){
	if(this.readyState == 4 && this.status == 200){
		if(jQuery.isEmptyObject(this.responseText))
			alert("No files to show!");
		else{
			var json = this.responseText;
			var data_ = JSON.parse(json);
			document.getElementById("table tbody").innerHTML="<thead class=\"thead-dark\"><tr><th>"+"<a href=\"#\" onclick='sortTableByName()'>File Name<//a>"+"</th><th>"+"<a href=\"#\" onclick='sortTableByType()'>Type<//a>"+"</th><th>"+"<a href=\"#\" onclick='sortTableBySize()'>Size<//a>"+"</th><th>"+"<a href=\"#\" onclick='sortTableByWords()'>Word Count<//a>"+"</th><th>"+"<a href=\"#\" onclick='sortTableByLines()'>Line Count<//a>"+"</th></tr></thead>";
			for(var ij in data_) {
				if(data_[ij]._type=="Folder"){
					document.getElementById("table tbody").innerHTML=document.getElementById("table tbody").innerHTML+"<tr><td><a href=\"#\" onclick='showFilesInThisSubFolder(\""+data_[ij]._file_name+"\")'>"+ data_[ij]._file_name +"</a>"+"</td><td>"+data_[ij]._type+"</td><td>-</td><td>-</td><td>-</td><td> <button type=\"button\" class=\"btn btn-outline-info btn-sm\" disabled>Show Tokens</button> </td></tr>";
				}
				else{
					document.getElementById("table tbody").innerHTML=document.getElementById("table tbody").innerHTML+"<tr><td>"+data_[ij]._file_name+"</td><td>"+data_[ij]._type+"</td><td>"+data_[ij]._size+" bytes</td><td>"+data_[ij]._words+"</td><td>"+data_[ij]._lines+"</td><td> <button type=\"button\" class=\"btn btn-outline-info btn-sm \" data-toggle=\"modal\" data-target=\"#tokenModal\" onclick=getTokensForThisFile(\""+data_[ij]._file_name+"\") >Show Tokens</button> </td></tr>";
				}				
			}

		}
	}

}		







function addPath(){
	var show= new XMLHttpRequest();
	show.onreadystatechange= resp;
	var path_= document.getElementById("input_path").value;
	var res_= encodeURI(path_);
	show.open("GET","/addPath?path="+res_,true);
	show.send();
}
function resp(){
	if(this.readyState == 4 && this.status == 200){
		if(jQuery.isEmptyObject(this.responseText)){
			var alert= document.getElementById("alert-failure");
			alert.style.display="block";
			alert.innerHTML="Invalid Path";
			setTimeout(function(){
				alert.style.display="none";
			}, 1000);

		}
		else{

		}
		document.getElementById("table tbody").innerHTML="";
	}
}








function showAllPaths(){
	document.getElementById("folders").innerHTML="";
	var folderRequest= new XMLHttpRequest();
	folderRequest.onreadystatechange= folderResponse;
	folderRequest.open("GET","/lastFolder",true);
	folderRequest.send();
}
function folderResponse(){
	if(this.readyState == 4 && this.status == 200){
		if(jQuery.isEmptyObject(this.responseText)){
			var alert= document.getElementById("alert-failure");
			alert.style.display="block";
			alert.innerHTML="No Folders to show, try adding path first !";
			setTimeout(function(){
				alert.style.display="none";
			}, 1500);

		}
		else{
			var json = this.responseText;
			$("#folder-header").show();
			document.getElementById("folders").innerHTML="<a href=\"#\"  class=\"list-group-item list-group-item-action\"  onclick='showFilesInThisFolder(\""+json+"\")'>"+json+"</a>";
		}
	}
}





function back(){
	if(backPath.length <= 1){
		var alert= document.getElementById("alert-failure");
		alert.style.display="block";
		alert.innerHTML="Cannot go back further, try selecting a folder!";
		setTimeout(function(){
			alert.style.display="none";
		}, 1000);

	}
	else{
		backPath.pop();
		showFilesInThisFolder(backPath[backPath.length-1]);
	}
}






function showFilesInThisFolder(data){

	if(backPath.indexOf(data)==-1)
		backPath.push(data);
	emptyTable();
	currFolderName=data;
	var xhttp= new XMLHttpRequest();
	xhttp.onreadystatechange= xresp;
	xhttp.open("GET","/filesInThisFolder?folder_name="+data,true);
	xhttp.send();

}
function xresp(){
	if(this.readyState == 4 && this.status == 200){
		//document.getElementById("folders").innerHTML= this.responseText;
		var json = this.responseText;
		var data_ = JSON.parse(json);
		document.getElementById("table tbody").innerHTML=document.getElementById("table tbody").innerHTML+"<thead class=\"thead-dark\"><tr><th>"+"<a href=\"#\" onclick='sortTableByName()'>File Name<//a>"+"</th><th>"+"<a href=\"#\" onclick='sortTableByType()'>Type<//a>"+"</th><th>"+"<a href=\"#\" onclick='sortTableBySize()'>Size<//a>"+"</th><th>"+"<a href=\"#\" onclick='sortTableByWords()'>Word Count<//a>"+"</th><th>"+"<a href=\"#\" onclick='sortTableByLines()'>Line Count<//a>"+"</th><th>Tokens</th></tr></thead>";
		for(var ij in data_) {
			if(data_[ij]._type=="Folder"){
				document.getElementById("table tbody").innerHTML=document.getElementById("table tbody").innerHTML+"<tr><td><a href=\"#\" onclick='showFilesInThisSubFolder(\""+data_[ij]._file_name+"\")'>"+ data_[ij]._file_name +"</a>"+"</td><td>"+data_[ij]._type+"</td><td>-</td><td>-</td><td>-</td><td> <button type=\"button\" class=\"btn btn-outline-info btn-sm\" disabled>Show Tokens</button> </td></tr>";
			}
			else{
				document.getElementById("table tbody").innerHTML=document.getElementById("table tbody").innerHTML+"<tr><td><a href=\"#\" onclick='openFile(\""+data_[ij]._file_name+"\")'>"+ data_[ij]._file_name +"</a>"+"</td><td>"+data_[ij]._type+"</td><td>"+data_[ij]._size+" bytes </td><td>"+data_[ij]._words+"</td><td>"+data_[ij]._lines+"</td><td> <button type=\"button\" class=\"btn btn-outline-info btn-sm  \" data-toggle=\"modal\" data-target=\"#tokenModal\" onclick=getTokensForThisFile(\""+data_[ij]._file_name+"\") >Show Tokens</button> </td></tr>";
			}				
		}
	}

}





function emptyTable(){
	document.getElementById("table tbody").innerHTML="";
}

function openFile(fileName){
	var xhttp= new XMLHttpRequest();
	xhttp.onreadystatechange= openFileresp;
	xhttp.open("GET","/openFileInViewer?fileName="+fileName,true);
	xhttp.send();

}
function openFileresp(){
	var json=this.responseText;
}



function showFilesInThisSubFolder(data){
	if(backPath.indexOf(data)==-1)
		backPath.push(data);
	emptyTable();
	currFolderName=data;
	var xhttp= new XMLHttpRequest();
	xhttp.onreadystatechange= xresp;
	xhttp.open("GET","/filesInThisFolder?folder_name="+data,true);
	xhttp.send();
}




function getTokensForThisFile(fileName){
	var xhttp= new XMLHttpRequest();
	xhttp.onreadystatechange= tokenResponse;
	xhttp.open("GET","/tokensForAFile?fileName="+fileName,true);
	xhttp.send();			
}
function tokenResponse(){
	if(this.readyState == 4 && this.status == 200){
		var json = this.responseText;
		var data_ = JSON.parse(json);
		document.getElementById("modal-table").innerHTML="<thead class=\"thead-dark\"><tr><th>Token</th><th>Count</th></tr></thead>";
		for(i in Object.keys(data_)){
			document.getElementById("modal-table").innerHTML=document.getElementById("modal-table").innerHTML+"<tr><td>"+Object.keys(data_)[i]+"</td><td>"+Object.values(data_)[i]+"</td></tr>";		
		}
	}			
}






function sortTableByName() {
	var table, rows, switching, i, x, y, shouldSwitch;
	table = document.getElementById("table tbody");
	switching = true;
	while (switching) {
		switching = false;
		rows = table.rows;
		for (i = 1; i < (rows.length - 1); i++) {
			shouldSwitch = false;
			x = rows[i].getElementsByTagName("TD")[0];
			y = rows[i + 1].getElementsByTagName("TD")[0];
			if (x.innerHTML.toLowerCase() > y.innerHTML.toLowerCase()) {
				shouldSwitch = true;
				break;
			}
		}
		if (shouldSwitch) {
			rows[i].parentNode.insertBefore(rows[i + 1], rows[i]);
			switching = true;
		}
	}
}
function sortTableByType() {
	var table, rows, switching, i, x, y, shouldSwitch;
	table = document.getElementById("table tbody");
	switching = true;
	while (switching) {
		switching = false;
		rows = table.rows;
		for (i = 1; i < (rows.length - 1); i++) {
			shouldSwitch = false;
			x = rows[i].getElementsByTagName("TD")[1];
			y = rows[i + 1].getElementsByTagName("TD")[1];
			if (x.innerHTML.toLowerCase() > y.innerHTML.toLowerCase()) {
				shouldSwitch = true;
				break;
			}
		}
		if (shouldSwitch) {
			rows[i].parentNode.insertBefore(rows[i + 1], rows[i]);
			switching = true;
		}
	}
}
function sortTableBySize() {
	var table, rows, switching, i, x, y, shouldSwitch;
	table = document.getElementById("table tbody");
	switching = true;
	while (switching) {
		switching = false;
		rows = table.rows;
		for (i = 1; i < (rows.length - 1); i++) {
			shouldSwitch = false;
			x = rows[i].getElementsByTagName("TD")[2];
			y = rows[i + 1].getElementsByTagName("TD")[2];
			if (Number(x.innerHTML) > Number(y.innerHTML)) {
				shouldSwitch = true;
				break;
			}
		}
		if (shouldSwitch) {
			rows[i].parentNode.insertBefore(rows[i + 1], rows[i]);
			switching = true;
		}
	}
}
function sortTableByWords() {
	var table, rows, switching, i, x, y, shouldSwitch;
	table = document.getElementById("table tbody");
	switching = true;
	while (switching) {
		switching = false;
		rows = table.rows;
		for (i = 1; i < (rows.length - 1); i++) {
			shouldSwitch = false;
			x = rows[i].getElementsByTagName("TD")[3];
			y = rows[i + 1].getElementsByTagName("TD")[3];
			if (Number(x.innerHTML) > Number(y.innerHTML)) {
				shouldSwitch = true;
				break;
			}
		}
		if (shouldSwitch) {
			rows[i].parentNode.insertBefore(rows[i + 1], rows[i]);
			switching = true;
		}
	}
}
function sortTableByLines() {
	var table, rows, switching, i, x, y, shouldSwitch;
	table = document.getElementById("table tbody");
	switching = true;
	while (switching) {
		switching = false;
		rows = table.rows;
		for (i = 1; i < (rows.length - 1); i++) {
			shouldSwitch = false;
			x = rows[i].getElementsByTagName("TD")[4];
			alert(typeof(x));
			y = rows[i + 1].getElementsByTagName("TD")[4];
			if (Number(x.innerHTML) > Number(y.innerHTML)) {
				shouldSwitch = true;
				break;
			}
		}
		if (shouldSwitch) {
			rows[i].parentNode.insertBefore(rows[i + 1], rows[i]);
			switching = true;
		}
	}
}
