attribute vec3 center;
attribute vec3 position;
attribute vec3 texturec;

uniform mat4 projector;
uniform mat4 modelview;
uniform float fadeLight;
uniform float fadeDistance;

varying vec3 tex;
varying float fade;
varying vec4 fadeColor;

void main(void) {
	vec4 pos = modelview * vec4(position + center, 1.0);
	gl_Position = projector * pos;
	tex = texturec;

	fade = clamp(length(pos.xyz) / fadeDistance, 0.0, 1.0);
	fadeColor = vec4(fadeLight, fadeLight, fadeLight, 1.0);
}