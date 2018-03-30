/*
 * @author Congard
 * dbcongard@gmail.com
 * t.me/congard
 * github.com/congard
 */

#version 130

precision mediump float;					// Set the default precision to medium. We don't need as high of a
											// precision in the fragment shader.

#define MAX_LAMPS_COUNT 8					// Max lamps count.

uniform vec3 u_LampsPos[MAX_LAMPS_COUNT];	// The position of lamps in eye space.
uniform vec3 u_LampsColors[MAX_LAMPS_COUNT];
uniform vec3 u_ViewPos;						// Camera position
uniform vec3 u_AmbientColor;				// Ambient lighting color
uniform sampler2D u_TextureUnit;			// Texture
uniform int u_LampsCount;					// Lamps count

varying vec3 v_Position;					// Interpolated position for this fragment.
varying vec3 v_Normal;						// Interpolated normal for this fragment.
varying vec2 v_Texture;						// Texture coordinates.

// The entry point for our fragment shader.
void main() {
	// for specular
	float specularStrength = 0.5f;
	vec3 viewDir = normalize(u_ViewPos - v_Position);
	//
	vec3 norm = normalize(v_Normal);
	// ambient
	float ambientStrength = 0.1;
	vec3 ambient = ambientStrength * u_AmbientColor;

	vec3 result = vec3(0, 0, 0); // result of diffuse + specular lighting for all lamps

	for (int i = 0; i<u_LampsCount; i++) {
		// diffuse
		vec3 lightDir = normalize(u_LampsPos[i] - v_Position);
		float diff = max(dot(norm, lightDir), 0.0);
		vec3 diffuse = diff * u_LampsColors[i];

		// specular
		vec3 reflectDir = reflect(-lightDir, v_Normal);
		float spec = pow(max(dot(viewDir, reflectDir), 0.0), 32);
		vec3 specular = specularStrength * spec * u_LampsColors[i];

		// result for this(i) lamp
		result += diffuse + specular;
	}

	gl_FragColor = vec4(ambient + result, 1) * texture2D(u_TextureUnit, v_Texture);
}
