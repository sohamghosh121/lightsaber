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

var xRot = 0
var yRot = 0;
var zRot = 0;

function render() {
	requestAnimationFrame( render );

	cube.rotation.x = xRot;
	cube.rotation.y = yRot;
	cube.rotation.z = zRot;

	renderer.render( scene, camera );


}

render();

function setRotations(o){
	xRot = o['x'];
	yRot = o['y'];
	zRot = o['z'];
}



