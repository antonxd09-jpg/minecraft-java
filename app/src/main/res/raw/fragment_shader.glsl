precision mediump float;
uniform vec3 u_LightPos;
varying vec3 v_Color;
varying vec3 v_Normal;
void main() {
    float distance = length(u_LightPos - vec3(0.0));
    vec3 lightVector = normalize(u_LightPos - vec3(0.0));
    float diffuse = max(dot(v_Normal, lightVector), 0.1);
    diffuse = diffuse * (1.0 / (1.0 + (0.1 * distance * distance)));
    diffuse = diffuse + 0.3; // Ambient light
    gl_FragColor = vec4(v_Color * diffuse, 1.0);
}
