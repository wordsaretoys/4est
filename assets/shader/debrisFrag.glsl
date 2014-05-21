precision mediump float;

const vec3 light = vec3(-1, 0, 0);

uniform samplerCube texture;

varying vec3 tex;
varying vec3 norm;

void main(void) {
	vec3 tn = textureCube(texture, tex).xyz;
	vec3 nn = norm + 2.0 * normalize(tn) - 1.0;
	float lit = max(dot(light, nn), 0.1);
	gl_FragColor = textureCube(texture, tex) * lit;
}
