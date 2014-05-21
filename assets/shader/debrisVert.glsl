const int RotorCount = 48;

attribute vec3 center;
attribute vec3 normal;
attribute float rotor;
attribute vec3 position;
attribute vec3 texturec;

uniform mat4 projector;
uniform mat4 modelview;
uniform mat3 rotations[RotorCount];

varying vec3 tex;
varying vec3 norm;

void main(void) {
	
	mat3 rt = rotations[int(rotor)];
	
	vec4 pos = modelview * vec4(rt * position + center, 1.0);
	gl_Position = projector * pos;
	tex = texturec;

	norm = rt * normal;
}
