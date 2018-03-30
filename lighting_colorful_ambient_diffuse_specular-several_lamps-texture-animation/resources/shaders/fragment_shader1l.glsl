/*
 * @author Congard
 * dbcongard@gmail.com
 * t.me/congard
 * github.com/congard
 *
 * fragment shader for use only one lamp
 */

#version 130

precision mediump float;					// Set the default precision to medium. We don't need as high of a
											// precision in the fragment shader.

#define MAX_LAMPS_COUNT 8					// Max lamps count.

uniform vec3 u_LampsPos[MAX_LAMPS_COUNT];	// The position of lamps in eye space.
uniform vec3 u_LampsColors[MAX_LAMPS_COUNT];
uniform vec3 u_ViewPos;
uniform sampler2D u_TextureUnit;
uniform int u_LampsCount;

varying vec3 v_Position;					// Interpolated position for this fragment.
varying vec3 v_Normal;						// Interpolated normal for this fragment.
varying vec2 v_Texture;						// Texture coordinates.

// The entry point for our fragment shader.
void main() {
	float ambientStrength = 0.1;
	vec3 ambient = ambientStrength * u_LampsColors[0];

	vec3 norm = normalize(v_Normal);
	vec3 lightDir = normalize(u_LampsPos[0] - v_Position);
	float diff = max(dot(norm, lightDir), 0.0);
	vec3 diffuse = diff * u_LampsColors[0];

	float specularStrength = 0.5f;
	vec3 viewDir = normalize(u_ViewPos - v_Position);
	vec3 reflectDir = reflect(-lightDir, v_Normal);
	float spec = pow(max(dot(viewDir, reflectDir), 0.0), 32);
	vec3 specular = specularStrength * spec * u_LampsColors[0];

	gl_FragColor = vec4(ambient + diffuse + specular, 1) * texture2D(u_TextureUnit, v_Texture);
}
