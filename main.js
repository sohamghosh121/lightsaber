const PORT = 3000;

const path = require('path');
const express = require('express');

// start websocket


// start server
var app = express();
var server = require('http').Server(app);

var io = require('socket.io').listen(5000);

function startNewChannel(channelId) {
	var chat = io
	.of('/' + channelId)
	.on('connection', function(socket){
		console.log('new connection');
		socket.on('rot-data', function(data){
			console.log('received')
			chat.emit('rot-data-recv', data); // forward data to listener on web
		});

		
	});
}


app.listen(PORT, function () {
  console.log('App listening on port ' + PORT + '!')
});

app.use(express.static('static'));

// server logic 
app.get('/', function (req, res) {
  res.sendFile(path.join(__dirname + '/index.html'));
});

app.get('/openConnection', function(req, res){
	var channelId = req.query.id;
	console.log('opening new channel at ' + channelId);
	startNewChannel(channelId, res);
	res.send({success: true});
});


app.use('/node_modules',  express.static(__dirname + '/node_modules'));
app.use('/js',  express.static(__dirname + '/js'));



