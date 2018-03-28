/*
 * @author Congard
 * dbcongard@gmail.com
 * t.me/congard
 * github.com/congard
 */

#version 130

precision mediump float;					// Set the default precision to medium. We don't need as high of a
											// precision in the fragment shader.

#define MAX_LAMPS_COUNT 2					// Max lamps. 2 * 3 = 6 points, 2 vertices

uniform vec3 u_LampsPos[MAX_LAMPS_COUNT];	// The position of lamps in eye space.
uniform sampler2D u_TextureUnit;

varying vec3 v_Position;					// Interpolated position for this fragment.
varying vec3 v_Normal;						// Interpolated normal for this fragment.
varying vec2 v_Texture;						// Texture coordinates.

// The entry point for our fragment shader.
void main() {
	float finalDiffuse = 0;

	for (int i = 0; i<MAX_LAMPS_COUNT; i++) {
		// Will be used for attenuation.
		float distance = length(u_LampsPos[i] - v_Position);

		// Get a lighting direction vector from the light to the vertex.
		vec3 lightVector = normalize(u_LampsPos[i] - v_Position);

		// Calculate the dot product of the light vector and vertex normal. If the normal and light vector are
		// pointing in the same direction then it will get max illumination.
		float diffuse = max(dot(v_Normal, lightVector), 0.1);

		// Add attenuation.
		diffuse = diffuse * (1.0 / (1.0 + (0.25 * distance * distance)));

		// Calculate final diffuse from MAX_LAMPS_COUNT
		finalDiffuse += diffuse;
	}

    // Multiply the color by the diffuse illumination level to get final output color.
    // 4 - coefficient for increasing brightness
	// texture2D(sampler2D, vec2)::out vec4 color
	gl_FragColor = texture2D(u_TextureUnit, v_Texture) * finalDiffuse * 4;
}
