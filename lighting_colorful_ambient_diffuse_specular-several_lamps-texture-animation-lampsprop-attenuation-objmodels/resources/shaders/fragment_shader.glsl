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

uniform vec3 u_ViewPos;						// Camera position
uniform vec3 u_AmbientColor;				// Ambient lighting color
uniform sampler2D u_TextureUnit;			// Texture
uniform int u_LampsCount;					// Lamps count

varying vec3 v_Position;					// Interpolated position for this fragment.
varying vec3 v_Normal;						// Interpolated normal for this fragment.
varying vec2 v_Texture;						// Texture coordinates.
varying mat4 v_MVPMatrix;

struct Lamp {
	float diffuseStrength;
	float specularStrength;
	float kc; // constant term
	float kl; // linear term
	float kq; // quadratic term
	int shininess;
	vec3 lampPos;
	vec3 lampColor;
};

uniform Lamp u_Lamps[MAX_LAMPS_COUNT];

float calculateAttenuation(Lamp lamp) {
	float distance = length(lamp.lampPos - v_Position);
	return 1.0 / (
					lamp.kc +
					lamp.kl * distance +
					lamp.kq * (distance * distance)
			);
}

// The entry point for our fragment shader.
void main() {
	vec3 viewDir = normalize(u_ViewPos - v_Position);
	//
	vec3 norm = normalize(v_Normal);
	// ambient
	float ambientStrength = 0.1;
	vec3 ambient = ambientStrength * u_AmbientColor;

	vec3 result = vec3(0, 0, 0); // result of diffuse + specular lighting for all lamps

	for (int i = 0; i<u_LampsCount; i++) {
		// attenuation
		float attenuation = calculateAttenuation(u_Lamps[i]);

		// diffuse
		vec3 lightDir = normalize(u_Lamps[i].lampPos - v_Position);
		float diff = max(dot(norm, lightDir), 0.0);
		vec3 diffuse = u_Lamps[i].diffuseStrength * diff * u_Lamps[i].lampColor * attenuation;

		// specular
		vec3 reflectDir = reflect(-lightDir, v_Normal);
		float spec = pow(max(dot(viewDir, reflectDir), 0.0), u_Lamps[i].shininess);
		vec3 specular = u_Lamps[i].specularStrength * spec * u_Lamps[i].lampColor * attenuation;

		// result for this(i) lamp
		result += diffuse + specular;
	}

	gl_FragColor = vec4(ambient + result, 1) * texture2D(u_TextureUnit, v_Texture);
}
