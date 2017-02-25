var scene = new THREE.Scene();
var camera = new THREE.PerspectiveCamera( 75, window.innerWidth / window.innerHeight, 0.1, 1000 );

var renderer = new THREE.WebGLRenderer();
renderer.setSize( window.innerWidth, window.innerHeight );
document.body.appendChild( renderer.domElement );


var obj = new LightSaber().geometry;
scene.add( obj );

// texture (background)

var texture = new THREE.TextureLoader().load( './../space.jpg' );

var backgroundMesh = new THREE.Mesh(
	new THREE.PlaneGeometry(8, 5, 0),
	new THREE.MeshBasicMaterial({
	    map: texture
}));

backgroundMesh.side = THREE.BackSide;

backgroundMesh.position.z = 0;
scene.add(backgroundMesh);

// add lighting
var ambLight = new THREE.AmbientLight( 0xffffff);
var light = new THREE.DirectionalLight( 0xffffff, 1 );

scene.add(ambLight);
scene.add(light);

camera.position.z = 2;

var xRot = 0.12
var yRot = 0.3;
var zRot = 0.03;

function render() {
	requestAnimationFrame( render );

	obj.rotation.x = xRot;
	obj.rotation.y = yRot;
	obj.rotation.z = zRot;

	renderer.render( scene, camera );
}

render();

function setRotations(o){
	console.log(o)
	xRot = o['x'];
	yRot = o['y'];
	zRot = o['z'];
}



