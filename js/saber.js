var scene = new THREE.Scene();
var camera = new THREE.PerspectiveCamera( 75, window.innerWidth / window.innerHeight, 0.1, 1000 );

var renderer = new THREE.WebGLRenderer();
renderer.setSize( window.innerWidth, window.innerHeight );
document.body.appendChild( renderer.domElement );


var geometry = new THREE.BoxGeometry( 2, 2, 2 );
var material = new THREE.MeshLambertMaterial( { 
	color: 0x4e3057 , 
	emissive: 0x4e3057, 
	emissiveIntensity: 0.1} );
var cube = new THREE.Mesh( geometry, material );

scene.add( cube );

// add lighting
var ambLight = new THREE.AmbientLight( 0xffffff);
var light = new THREE.DirectionalLight( 0xffffff, 1 );

scene.add(ambLight);
scene.add(light);

camera.position.z = 5;

var xRate = 0;
var yRate = 0;

function render() {
	requestAnimationFrame( render );

	cube.rotation.x = (cube.rotation.x + xRate) %  (2 * Math.PI);
	cube.rotation.y = (cube.rotation.y + yRate) %  (2 * Math.PI);

	renderer.render( scene, camera );


}

window.onkeydown = function(e) {
   var key = e.keyCode ? e.keyCode : e.which;
   switch(key){
   	case 37: yRate = - 0.02; break; // left
   	case 38: xRate = - 0.02; break; // up
   	case 39: yRate = 0.02; break; // right
   	case 40: xRate = 0.02; break; // down
   }
}

window.onkeyup = function() {
	xRate = 0;
	yRate = 0;
}

render();

// var socket = io.connect('http://localhost:3000');

// socket.on('news', function (data) {
// console.log(data);
// socket.emit('event', { my: 'data' });
// });





