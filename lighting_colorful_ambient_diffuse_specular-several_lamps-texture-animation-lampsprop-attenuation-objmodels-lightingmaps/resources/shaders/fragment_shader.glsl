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
uniform sampler2D u_AmbientTextureUnit;		// Ambient texture
uniform sampler2D u_DiffuseTextureUnit;		// Diffuse texture
uniform sampler2D u_SpecularTextureUnit;	// Specular texture
uniform int u_LampsCount;					// Lamps count

varying vec3 v_Position;					// Interpolated position for this fragment.
varying vec3 v_Normal;						// Interpolated normal for this fragment.
varying vec2 v_Texture;						// Texture coordinates.

struct Lamp {
	float ambientStrength;
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

vec4 toVec4(vec3 v) {
	return vec4(v, 1);
}

// The entry point for our fragment shader.
void main() {
	vec3 viewDir = normalize(u_ViewPos - v_Position);
	vec3 norm = normalize(v_Normal);

	vec3 ambientResult = vec3(0, 0, 0); // result of ambient lighting for all lamps
	vec3 diffuseResult = vec3(0, 0, 0); // result of diffuse lighting for all lamps
	vec3 specularResult = vec3(0, 0, 0); // result of specular lighting for all lamps

	for (int i = 0; i<u_LampsCount; i++) {
		// attenuation
		float attenuation = calculateAttenuation(u_Lamps[i]);

		// ambient
		vec3 ambient = u_Lamps[i].ambientStrength * u_Lamps[i].lampColor * attenuation;

		// diffuse
		vec3 lightDir = normalize(u_Lamps[i].lampPos - v_Position);
		float diff = max(dot(norm, lightDir), 0.0);
		vec3 diffuse = u_Lamps[i].diffuseStrength * diff * u_Lamps[i].lampColor * attenuation;

		// specular
		vec3 reflectDir = reflect(-lightDir, v_Normal);
		float spec = pow(max(dot(viewDir, reflectDir), 0.0), u_Lamps[i].shininess);
		vec3 specular = u_Lamps[i].specularStrength * spec * u_Lamps[i].lampColor * attenuation;

		// result for this(i) lamp
		ambientResult += ambient;
		diffuseResult += diffuse;
		specularResult += specular;
	}

	gl_FragColor =
			toVec4(ambientResult) * texture2D(u_AmbientTextureUnit, v_Texture) +
			toVec4(diffuseResult) * texture2D(u_DiffuseTextureUnit, v_Texture) +
			toVec4(specularResult) * texture2D(u_SpecularTextureUnit, v_Texture);
}
