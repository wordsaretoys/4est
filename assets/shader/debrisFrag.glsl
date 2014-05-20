precision mediump float;

uniform samplerCube image;

varying vec3 tex;
varying float fade;
varying vec4 fadeColor;

void main(void) {
	gl_FragColor = mix(textureCube(image, tex), fadeColor, fade);
}
