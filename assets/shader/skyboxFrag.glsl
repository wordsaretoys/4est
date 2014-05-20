precision mediump float;

uniform samplerCube image;
varying vec3 tex;

void main(void) {
	gl_FragColor = textureCube(image, tex);
}
