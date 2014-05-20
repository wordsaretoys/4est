attribute vec3 position;
attribute vec3 texturec;

uniform mat4 projector;
uniform mat4 rotations;

varying vec3 tex;

void main(void) {
	vec4 pos = rotations * vec4(position, 1.0);
	gl_Position = projector * pos;
	tex = texturec;
}
