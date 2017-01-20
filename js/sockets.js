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
	var qrcode = new QRCode(document.getElementById("qrcode"), {width: 160, height: 160});
	
	$.ajax({
		url: 'http://localhost:3000/openConnection',
		data: {id: channelId}
	}).done(function(data){
		
		if (data.success){
			qrcode.makeCode(channelId);
			socketConn = io.connect('http://localhost:5000/' + channelId);
			socketConn.on('data-recv', function(data){
				setRotations(data);
			});

			socketConn.on('connection-ack', function(data){
				console.log('connected!');
				socketConn.send('rot-data', {xRot: 2, yRot: 1, zRot: 1.5});
			});
		}
	});
}