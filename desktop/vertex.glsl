attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;
attribute vec2 a_texCoord1;
attribute vec2 a_texCoord2;

attribute float a_data0;
attribute float a_data1;
attribute float a_data2;
attribute float a_data3;

uniform mat4 u_projTrans;
varying vec4 v_color;
varying vec2 v_texCoords0;
varying vec2 v_texCoords1;
varying vec2 v_texCoords2;
varying float v_data0;
varying float v_data1;
varying float v_data2;
varying float v_data3;

void main()
{
   v_data0 = a_data0;
   v_data1 = a_data1;
   v_data2 = a_data2;
   v_data3 = a_data3;
   v_color = a_color;
   v_color.a = v_color.a * (255.0/254.0);
   v_texCoords0 = a_texCoord0;
   v_texCoords1 = a_texCoord1;
   v_texCoords2 = a_texCoord2;
   gl_Position =  u_projTrans * a_position;
}
