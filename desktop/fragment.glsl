#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP 
#endif
varying LOWP vec4 v_color;
varying vec4 v_data0;
varying vec4 v_data1;
varying float v_data2;
varying float v_data3;
varying vec2 v_texCoords0;
varying vec2 v_texCoords1;
varying vec2 v_texCoords2;
uniform sampler2D u_texture;
uniform sampler2D u_texture2;
uniform sampler2D u_texture3;
uniform sampler2D u_texture4;
uniform sampler2D u_texture5;
uniform sampler2D u_texture6;
uniform sampler2D u_texture7;
uniform sampler2D u_texture8;
uniform sampler2D u_texture9;
uniform sampler2D u_texture10;
uniform sampler2D u_texture11;
uniform sampler2D u_texture12;
uniform sampler2D u_texture13;
uniform sampler2D u_texture14;
uniform sampler2D u_texture15;
uniform sampler2D u_texture16;

vec4 sample(float t, vec2 v_texCoords);

void main() {
	vec4 src = sample(v_data1, v_texCoords0);
	vec4 mask = texture2D(u_texture16, v_texCoords1);
	vec4 mask2 = texture2D(u_texture16, v_texCoords2);
	vec2 pCoords = vec2(v_data2 + (float(round(((float(src.r) * 255.0)))) / 4096.0), v_data3 + (float(round(((float(v_color.r) * 255.0)))) / 4096.0));
	vec4 pSrc = 0;
	if (src.a == 1) {
		pSrc = texture2D(u_texture16, pCoords);
	} else {
		pSrc = src;
	}
	switch (int(v_data0)) {
	case 0: //default shader
		gl_FragColor = v_color * src;
		break;
	case 1: //set to color
		gl_FragColor = v_color * vec4(1.0, 1.0, 1.0, src.a);
		break;
	case 2: //1 mask
		gl_FragColor = 0;
		if (src.a && mask.a) {
			gl_FragColor = v_color * src;
		}
		break;
	case 3: //2 masks
		gl_FragColor = 0;
		if (src.a && mask.a && mask2.a) {
			gl_FragColor = v_color * src;
		}
		break;
	case 4: //no mask with palette
		gl_FragColor = pSrc;
		break;
	case 5: //1 mask with palette
		gl_FragColor = 0;
		if (src.a && mask.a) {
			gl_FragColor = pSrc;
		}
		break;
	case 6: //2 masks with palette
		gl_FragColor = 0;
		if (src.a && mask.a && mask2.a) {
			gl_FragColor = pSrc;
		}
		break;
	}
}

vec4 sample(float t, vec2 v_texCoords) {
	switch (int(t)) {
	case 0:
		return texture2D(u_texture, v_texCoords);
	case 1:
		return texture2D(u_texture2, v_texCoords);
	case 2:
		return texture2D(u_texture3, v_texCoords);
	case 3:
		return texture2D(u_texture4, v_texCoords);
	case 4:
		return texture2D(u_texture5, v_texCoords);
	case 5:
		return texture2D(u_texture6, v_texCoords);
	case 6:
		return texture2D(u_texture7, v_texCoords);
	case 7:
		return texture2D(u_texture8, v_texCoords);
	case 8:
		return texture2D(u_texture9, v_texCoords);
	case 9:
		return texture2D(u_texture10, v_texCoords);
	case 10:
		return texture2D(u_texture11, v_texCoords);
	case 11:
		return texture2D(u_texture12, v_texCoords);
	case 12:
		return texture2D(u_texture13, v_texCoords);
	case 13:
		return texture2D(u_texture14, v_texCoords);
	case 14:
		return texture2D(u_texture15, v_texCoords);
	case 15:
		return texture2D(u_texture16, v_texCoords);
	default:
		return texture2D(u_texture, v_texCoords);
	}
}
