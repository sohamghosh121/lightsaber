// all about the socket communication

var randomChannelGenerator = function (length) {
	var text = "";
	var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	for (var i = 0; i < length; i++) {
		text += possible.charAt(Math.floor(Math.random() * possible.length));
	}
	return text;
}

var socketConn = null;
var channelId;

var spawnConnection = function() {
	var channelId = randomChannelGenerator(12);
	console.log('channelId ' + channelId);
	var qrcode = new QRCode(document.getElementById("qrcode"), {width: 160, height: 160});
	
	$.ajax({
		url: 'http://localhost:3000/openConnection',
		data: {id: channelId}
	}).done(function(data){
		
		if (data.success){
			qrcode.makeCode(channelId);
			socketConn = io.connect('http://localhost:5000/' + channelId);

			socketConn.on('connect', function(v){
				socketConn.on('rot-data-recv', function(data){
					setRotations(data);
				});

			});
		}
	});
}