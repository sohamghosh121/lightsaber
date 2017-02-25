class LightSaber {

	constructor(){
		var geometry = new THREE.CylinderGeometry( .03, .03, 2, 32);
		var material = new THREE.MeshLambertMaterial( { 
			color: 'white' , 
			emissive: '#00FFFF', 
			emissiveIntensity: 0.5} );
		var obj = new THREE.Mesh( geometry, material );

		var glowGeometry = new THREE.CylinderGeometry( .05, .05, 2, 32);
		var glowMaterial = new THREE.MeshBasicMaterial({transparent: true, opacity: 0.5, color: "#00FFFF" });
		var glow = new THREE.Mesh(glowGeometry, glowMaterial);

		var hilt = new THREE.CylinderGeometry( .08, .08, .3, 32);
		var hiltMaterial = new THREE.MeshLambertMaterial({color: "#bfbfbf" });
		var hilt = new THREE.Mesh(hilt, hiltMaterial);
		hilt.position.setY(-1);

		obj.add(glow);
		obj.add(hilt);
		obj.position.setZ(.2);

		this.geometry = obj;
	}
}