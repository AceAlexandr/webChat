

var uniqueId = function() {
	var date = Date.now();
	var random = Math.random() * Math.random();

	return Math.floor(date * random).toString();
};

var appState = {
	mainUrl : 'chat',
	token : 'TN11EN',
	hash:'0'
};
var intervalId;
var MESSAGES_LIST=[];

var theMessage = function(nick,text,d) {
	return {
		messageText:text,
		user: nick,
		date: d,
		id:uniqueId()
	};
};

var theSendedMessage = function(nick,text) {
	return {
		messageText:text,
		user: nick
	};
};

var theEditedMessage = function(id,text) {
	return {
		messageText:text,
		id: id
	};
};

var theDeletedMessage = function(id) {
	return {
		id: id
	};
};

function run(){
	var nickContainer = document.getElementsByClassName('nickSection')[0];
	var senMessageContainer = document.getElementsByClassName('sendMessagearea')[0];
	nickContainer.addEventListener('click', nickChange);
	senMessageContainer.addEventListener('click', sendMessage);
	restoreNickname();
	//updateMessages();
	getMessages();
	//initReloader();
}

function updateMessages() {
	getMessages();
	
}

function initReloader() {
	intervalId = setInterval(getMessages, 31000);
}
function killReloader() {
	if(intervalId)
		clearInterval(intervalId);
}

function getToken(index) {
	var number = index * 8 + 11;
	return "TN" + number + "EN";
}

function restoreNickname(){
	if(typeof(Storage) == "undefined") {
		alert('localStorage is not accessible');
		return;
	}
	var item = localStorage.getItem("Users nickname");
	var labelNickname=document.getElementById('labelNickname');
	if(labelNickname.innerText){
		labelNickname.innerText=item && JSON.parse(item)||'Nickname';
		return;
	}
	else{
		labelNickname.textContent=item && JSON.parse(item)||'Nickname';
		return;
	}

}

function store(listToSave) {
	if(typeof(Storage) == "undefined") {
		alert('localStorage is not accessible');
		return;
	}
	localStorage.setItem("messages", JSON.stringify(listToSave));
}

function storeNickname(nick) {
	if(typeof(Storage) == "undefined") {
		alert('localStorage is not accessible');
		return;
	}
	localStorage.setItem("Users nickname", JSON.stringify(nick));
}

//EVENTS PROCESSING

function deleteMessage(elem){//удаление сообщения
	//killReloader();
	var name;
	if(elem.parentNode.parentNode.firstChild.firstChild.innerText){
		name=elem.parentNode.parentNode.firstChild.firstChild.innerText;
		var currentNick=document.getElementById("labelNickname").innerText;
	}
	else{
		name=elem.parentNode.parentNode.firstChild.firstChild.textContent;
		var currentNick=document.getElementById("labelNickname").textContent;
	}
	if(name!=currentNick){
		alert("it is not your message");
		return;
	}
	var oneMessage=(elem.parentNode.parentNode.parentNode).parentNode;
	var id = oneMessage.attributes['id'].value;
	var message=theDeletedMessage(id);
	doDelete(appState.mainUrl, JSON.stringify(message), function(){
		//getMessages();
	});
	elem.parentNode.removeChild(elem);
	//initReloader();
}

function editMessage(elem){//изменение сообщения
		var name;
		if(elem.parentNode.parentNode.firstChild.firstChild.innerText){		
			name=elem.parentNode.parentNode.firstChild.firstChild.innerText;
			var currentNick=document.getElementById("labelNickname").innerText;
		}
		else{
			name=elem.parentNode.parentNode.firstChild.firstChild.textContent;
			var currentNick=document.getElementById("labelNickname").textContent;
		}
	if(name==currentNick){
		var oneMessage=(elem.parentNode.parentNode.parentNode).parentNode;
		var textField=oneMessage.getElementsByClassName('wrap')[0];
		if(textField.innerText){
			var text=textField.innerText;
			textField.innerText='';
		}
		else{
			var text=textField.textContent;
			textField.textContent='';
		}
		textField.appendChild(createTextare(text));
	}
	else{
			alert('It is not your message');
	}
}

function createTextare(text){//создание поля для изменения <textarea class="form-control" rows="5" id="messageText"></textarea>
	var divTextArea = document.createElement('div');
	divTextArea.classList.add('col-sm-11');
	divTextArea.classList.add('divTextArea');
	var textArea1 = document.createElement('textarea');
	textArea1.setAttribute('id', 'newEditedMessage');
	textArea1.setAttribute('rows', '3');
	textArea1.classList.add('form-control');
	textArea1.classList.add('newEditedMessage');
	textArea1.value=text;
	var saveHref=document.createElement('a');
	saveHref.appendChild(document.createTextNode('Save'));
	saveHref.classList.add('saveEditedMessage');
	saveHref.setAttribute('id', 'saveEditedMessage');
	saveHref.setAttribute('onclick','saveEditedMessage(this)');
	divTextArea.appendChild(textArea1);
	divTextArea.appendChild(saveHref);
	return divTextArea;
}

function saveEditedMessage(elem){//сохранение изменения
	//killReloader();
	elem.style.backgroundColor = 'yellow';
	var oneMessage=(elem.parentNode.parentNode.parentNode).parentNode;
	var textFieldDiv=oneMessage.getElementsByClassName('wrap')[0];
	var text = (textFieldDiv.getElementsByClassName('newEditedMessage')[0]).value;
	var id = oneMessage.attributes['id'].value;
	var message=theEditedMessage(id,text);
	put(appState.mainUrl, JSON.stringify(message), function(){
		//getMessages();
	});
	//initReloader();
	textFieldDiv.removeChild(textFieldDiv.getElementsByClassName('divTextArea'));
}

function nickChange(evtObj) {
	if(evtObj.type === 'click' && evtObj.target.classList.contains('btnAceptNickname')){
		var newNickname = document.getElementById('newNickname');
		var labelNickname = document.getElementById('labelNickname');
		if(newNickname.value){
		
			if(labelNickname.innerText){
				labelNickname.innerText=(newNickname.value);
			}
			else{
				labelNickname.textContent=(newNickname.value);
			}
			storeNickname(newNickname.value);
			newNickname.value='';
			
		}
	}
}

function sendMessage(evtObj) {
	if(evtObj.type === 'click' && evtObj.target.classList.contains('btnSend')){
		//killReloader();
		var messageText = document.getElementById('messageTextArea');
		var Nickname= document.getElementById('labelNickname');
		if(messageText.value&&Nickname)
		{
			//var mes=theMessage(Nickname.innerText||Nickname.textContent, messageText.value,createCurrentDateString());
			var mes=theSendedMessage(Nickname.innerText||Nickname.textContent, messageText.value);
			addTodo(mes);
			messageText.value='';
		}
		//initReloader();
	}
}


//SERVER INTERACTION PART
function addTodo(message) {
	post(appState.mainUrl, JSON.stringify(message), function(){
		//getMessages();
	});
}

function getMessages(continueWith) {
	var url = appState.mainUrl + '?token=' + appState.token;
	//alert("url    "+url);
	get(url, function (responseText) {
		//alert("Enterresponse");
		console.assert(responseText != null);
		var response = JSON.parse(responseText);
		if(response.hash==appState.hash){
			appState.token = response.token;
			createAllTasks(response.messages);
			continueWith && continueWith();
		}else {
			//alert("hash !!!");
			appState.hash=response.hash;
			appState.token='TN11EN';
			cleenMessagesDiv();
		}
		getMessages();
	});
}

function post(url,message, continueWith, continueWithError) {
	ajax('POST', url, message, continueWith, continueWithError);
}

function doDelete(url,message, continueWith, continueWithError) {
	ajax('DELETE', url, message, continueWith, continueWithError);
}

function put(url,message, continueWith, continueWithError) {
	ajax('PUT', url, message, continueWith, continueWithError);
}

function get(url, continueWith, continueWithError) {
	ajax('GET', url, null, continueWith, continueWithError);
}

function ajax(method, url, data, continueWith, continueWithError) {
	var xhr = new XMLHttpRequest();
	continueWithError = continueWithError || defaultErrorHandler;
	xhr.open(method||'GET', url, true);
	//xhr.onload = function () {
	//	if (xhr.readyState !== 4)
	//		return;
	//	if(xhr.status != 200&&xhr.status!=304) {
	//		continueWithError('Error on the server side 1, response ' + xhr.status);
	//		return;
	//	}
	//	if(xhr.status==304) {
	//		//alert("304");
	//		//var response = JSON.parse(xhr.responseText);
	//		//if(response.hash==appState.hash){
	//		//	return;
	//		//}else {
	//		//	//alert("hash !!!");
	//		//	appState.hash=response.hash;
	//		//	appState.token='TN11EN';
	//		//	cleenMessagesDiv();
	//		//	getMessages();
	//		//	return;
	//		//}
	//	}
	//	if(isError(xhr.responseText)) {
	//		continueWithError('Error on the server side (parsing), response ' + xhr.responseText);
	//		return;
	//	}
	//	defaultSuccessHandler();
	//	continueWith(xhr.responseText);
	//};
	xhr.onreadystatechange = function(){
		if(xhr.readyState == 4 && xhr.status == 200){
			if (xhr.readyState !== 4)
				return;
			if(xhr.status != 200) {
				continueWithError('Error on the server side 1, response ' + xhr.status);
				return;
			}
			if(isError(xhr.responseText)) {
				continueWithError('Error on the server side (parsing), response ' + xhr.responseText);
				return;
			}
			defaultSuccessHandler();
			continueWith(xhr.responseText);
			}
		};

	xhr.ontimeout = function () {
		continueWithError('Server timed out !');
	}
	xhr.onerror = function (e) {
		var errMsg = 'Server connection error !\n';
		continueWithError(errMsg);
	};
	xhr.send(data);
}


function createAllTasks(allMessages) {
	for(var i = 0; i < allMessages.length; i++) {
		addTodoInternal(allMessages[i]);
	}
}

function addTodoInternal(message) {

		var item = createMessage(message.user, message.messageText, message.id, message.date);
		var items = document.getElementsByClassName('allMessages')[0];
		MESSAGES_LIST.push(message);
		items.appendChild(item);
		store(MESSAGES_LIST);
}

function isError(text) {
	if(text == "")
		return false;
	try {
		var obj = JSON.parse(text);
	} catch(ex) {
		return true;
	}
	return false;
	//return !!obj.error;
}

function defaultErrorHandler(string) {
	var output = document.getElementById('serverStatus');
	output.innerText = JSON.stringify(string, null, 2);
}

function defaultSuccessHandler() {
	var output = document.getElementById('serverStatus');
	output.innerText ="online";
}


//Create div message part
function cleenMessagesDiv(){
	var items = document.getElementsByClassName('allMessages')[0];
	while(items.hasChildNodes())
	items.removeChild(items.firstChild);
}

function createMessage(nick,text,id,date){
	//	alert(date);
	var message = document.createElement('div');
	message.classList.add('row');
	message.classList.add('bg-success');
	message.classList.add('oneMessage');
	message.classList.add('marginT');
	message.setAttribute('id',id);
	message.appendChild(createPictureMessage());
	message.appendChild(MessagePart(nick,text,date));
	return message
}

function MessagePart(nick,text,date){
	var divH=createHeader(nick,date);
	var divText=createTextMessage(text);
	
	var message = document.createElement('div');
	message.classList.add('col-sm-10');
	message.appendChild(divH);
	message.appendChild(divText);
	return message
}

function createPictureMessage(){
	
	var pictDiv = document.createElement('div');
	var pict = document.createElement('img');	
	pict.setAttribute('src',"resources/img/message.png");
	pict.setAttribute('alt', 'Thumbnail Image');
	pict.setAttribute('width', '50');
	pict.setAttribute('height', '50');
	pict.classList.add('img-thumbnail');
	pict.classList.add('bg-success');
	pictDiv.appendChild(pict);
	pictDiv.classList.add('col-sm-2');
	return pictDiv;
}

function createHeader(nick, date){
	var div1 = document.createElement('div');
	div1.appendChild(createNickDiv(nick));
	if(date){
		div1.appendChild(createDataDiv(date));
	}
	else{
		div1.appendChild(createDataDiv(createCurrentDateString()));
	}
	div1.appendChild(editMessageDiv());
	div1.appendChild(delMessageDiv());
	div1.classList.add('row');
	div1.setAttribute('align', 'left');
	return div1;
	};

function createCurrentDateString(){
	var date = new Date();
	var dateString;
	var month=date.getMonth();
	if(month<10){
		var t='0'+date.getMonth();
		month=t;
	}
	var date1=date.getDate();
	if(date1<10){
		var t='0'+date.getDate();
		date1=t;
	}
	var hour=date.getHours();
		if(hour<10){
		var t='0'+date.getHours();
		hour=t;
	}
	var minutes=date.getMinutes();
		if(minutes<10){
		var t='0'+date.getMinutes();
		minutes=t;
	}
	dateString=date1+'.'+month+'-'+hour+':'+minutes;
	return dateString;
}
	
function createDataDiv(dateString){
	var font1=document.createElement('font');
	font1.appendChild(document.createTextNode(dateString));
	font1.setAttribute('size', '1');
	font1.setAttribute('color', 'gray');
	var dataDiv=document.createElement('div');
	dataDiv.appendChild(font1);
	dataDiv.classList.add('col-sm-3');
	return dataDiv;
}

function createNickDiv(nick){
	var nickDiv=document.createElement('div');
	var nSpan=document.createElement('span');
	nSpan.appendChild(document.createTextNode(nick));
	nSpan.classList.add('messageNickname');
	nickDiv.appendChild(nSpan);	
	nickDiv.classList.add('col-sm-6');
	return nickDiv;
}

function editMessageDiv(){
	var editDiv=document.createElement('div');
	var editHref=document.createElement('a');
	editHref.appendChild(document.createTextNode('Edit'));
	editHref.classList.add('editMessage');
	editHref.setAttribute('id', 'editMessage');
	editHref.setAttribute('onclick','editMessage(this)');
	editDiv.appendChild(editHref);
	editDiv.classList.add('col-sm-1');
	editDiv.classList.add('marginR');
	return editDiv;	
}

function delMessageDiv(){
	var delDiv=document.createElement('div');
	var delHref=document.createElement('a');
	delHref.appendChild(document.createTextNode('X'));
	delHref.classList.add('delMessage');
	delHref.setAttribute('id', 'delMessage');
	delHref.setAttribute('onclick','deleteMessage(this)');
	delDiv.appendChild(delHref);
	delDiv.classList.add('col-sm-1');
	return delDiv;	
}

function createTextMessage(text){
	var message = document.createElement('div');
	message.classList.add('row');
	message.classList.add('wrap');
	message.appendChild(document.createTextNode(text));
	return message;
}


