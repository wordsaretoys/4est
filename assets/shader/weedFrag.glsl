precision mediump float;

uniform sampler2D weed;

varying vec2 tex;
varying float fade;
varying vec4 fadeColor;

void main(void) {
	gl_FragColor = mix(texture2D(weed, tex), fadeColor, fade);
}
