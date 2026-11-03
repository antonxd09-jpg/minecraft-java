uniform mat4 u_MVPMatrix;
uniform mat4 u_MVMatrix;
attribute vec4 a_Position;
attribute vec3 a_Normal;
attribute vec3 a_Color;
varying vec3 v_Color;
varying vec3 v_Normal;
void main() {
    gl_Position = u_MVPMatrix * a_Position;
    v_Normal = normalize(vec3(u_MVMatrix * vec4(a_Normal, 0.0)));
    v_Color = a_Color;
}
