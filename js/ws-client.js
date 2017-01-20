const WebSocket = require('ws');


function openWebSocketClient(channel) {
	const ws = new WebSocket('ws://localhost:8080/' + channel + '/receive');

	ws.on('open', function open() {
	  ws.send('something');
	});

	ws.on('message', function incoming(data, flags) {
	  console.log(data);
	});

	return ws;
}

module.exports = openWebSocketClient;
