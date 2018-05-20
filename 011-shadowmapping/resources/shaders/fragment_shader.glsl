/*
 * @author Congard
 * dbcongard@gmail.com
 * t.me/congard
 * github.com/congard
 */

#version 130
#extension GL_EXT_texture_array : enable

precision mediump float;						// Set the default precision to medium. We don't need as high of a
											// precision in the fragment shader.

#define MAX_LAMPS_COUNT 8					// Max lamps count.

uniform vec3 u_ViewPos;						// Camera position
uniform int u_LampsCount;					// Lamps count
uniform int u_ShadowMapWidth = 1024;		// shadow map width / default is 1024
uniform int u_ShadowMapHeight = 1024;		// shadow map height / default is 1024

varying mat4 v_MVMatrix;					// Model View matrix
varying mat3 v_TBN;							// Tangent Bitangent Normal matrix
varying vec4 v_Position;					// Position for this fragment.
varying vec3 v_Normal;						// Interpolated normal for this fragment.
varying vec2 v_Texture;						// Texture coordinates.
varying float v_NormalMapping;				// Is normal mapping enabled 0 - false, 1 - true

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
	mat4 lightSpaceMatrix;
	mat4 lightModelMatrix;
};

uniform sampler2DArray shadowMaps;

uniform struct Mapping {
	sampler2D ambient;
	sampler2D diffuse;
	sampler2D specular;
	sampler2D normal;
} u_Mapping;

uniform Lamp u_Lamps[MAX_LAMPS_COUNT];

vec3 norm;
vec3 fragPos;
vec4 fragLightSpacePos;
float shadow;

float calculateShadow(vec4 fragPosLightSpace, int texture, vec3 lightDir) {
	// perform perspective divide
	vec3 projCoords = fragPosLightSpace.xyz / fragPosLightSpace.w;
	// transform to [0,1] range
	projCoords = projCoords * 0.5 + 0.5;
	// get closest depth value from light’s perspective (using [0,1] range fragPosLight as coords)
	float closestDepth = texture2DArray(shadowMaps, vec3(projCoords.xy, texture)).r;
	// get depth of current fragment from light’s perspective
	float currentDepth = projCoords.z;
	// check whether current frag pos is in shadow
	//float bias = 0.005; // simple bias

	/*
	 * Here we have a maximum bias of 0.05 and a minimum of 0.005 based on the surface’s normal and
	 * light direction. This way surfaces like the floor that are almost perpendicular to the light source get a small
	 * bias, while surfaces like the cube’s side-faces get a much larger bias.
	*/
	float bias = max(0.05 * (1.0 - dot(norm, lightDir)), 0.005);
	//float shadow = currentDepth - bias > closestDepth ? 1.0 : 0.0; // simple shadow

	// soft shadow pcf 3*3
	float shadow = 0;
	vec2 texelSize = 1.0 / vec2(u_ShadowMapWidth, u_ShadowMapHeight);
	for(int x = -1; x <= 1; x++) {
		for(int y = -1; y <= 1; y++) {
			float pcfDepth = texture2DArray(shadowMaps, vec3(projCoords.xy + vec2(x, y) * texelSize, texture)).r;
			shadow += currentDepth - bias > pcfDepth ? 1.0 : 0.0;
		}
	}

	shadow /= 9.0;

	return shadow;
}

float calculateAttenuation(Lamp lamp) {
	float distance = length(lamp.lampPos - fragPos);
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
	 // Transform the vertex into eye space.
	fragPos = vec3(v_MVMatrix * v_Position);

	vec3 viewDir = normalize(u_ViewPos - fragPos);
	if (v_NormalMapping == 0) norm = vec3(normalize(v_MVMatrix * vec4(v_Normal, 0)));
	else { // using normal map if normal mapping enabled
		norm = texture2D(u_Mapping.normal, v_Texture).rgb;
		norm = normalize(norm * 2.0 - 1.0); // from [0; 1] to [-1; -1]
		norm = normalize(v_TBN * norm);
	}

	vec3 ambientResult = vec3(0, 0, 0); // result of ambient lighting for all lamps
	vec3 diffuseResult = vec3(0, 0, 0); // result of diffuse lighting for all lamps
	vec3 specularResult = vec3(0, 0, 0); // result of specular lighting for all lamps

	for (int i = 0; i<u_LampsCount; i++) {
		// attenuation
		float attenuation = calculateAttenuation(u_Lamps[i]);

		// ambient
		vec3 ambient = u_Lamps[i].ambientStrength * u_Lamps[i].lampColor * attenuation;

		// diffuse
		vec3 lightDir = normalize(u_Lamps[i].lampPos - fragPos);
		float diff = max(dot(norm, lightDir), 0.0);
		vec3 diffuse = u_Lamps[i].diffuseStrength * diff * u_Lamps[i].lampColor * attenuation;

		// specular
		vec3 reflectDir = reflect(-lightDir, norm);
		float spec = pow(max(dot(viewDir, reflectDir), 0.0), u_Lamps[i].shininess);
		vec3 specular = u_Lamps[i].specularStrength * spec * u_Lamps[i].lampColor * attenuation;

		// fragment position in light space
		fragLightSpacePos = u_Lamps[i].lightSpaceMatrix * u_Lamps[i].lightModelMatrix * v_Position;
		// calculate shadow
		shadow = calculateShadow(fragLightSpacePos, i, lightDir);

		// result for this(i) lamp
		ambientResult += ambient;
		diffuseResult += diffuse * (1-shadow);
		specularResult += specular * (1-shadow);
	}
	
	gl_FragColor =
			toVec4(ambientResult) * texture2D(u_Mapping.ambient, v_Texture) +
			toVec4(diffuseResult) * texture2D(u_Mapping.diffuse, v_Texture) +
			toVec4(specularResult) * texture2D(u_Mapping.specular, v_Texture);
}
