#version 130

precision mediump float;					// Set the default precision to medium. We don't need as high of a
											// precision in the fragment shader.

#define MAX_LAMPS_COUNT 2					// Max lamps. 2 * 3 = 6 points, 2 vertices

uniform vec3 u_LampsPos[MAX_LAMPS_COUNT];	// The position of lamps in eye space.

varying vec3 v_Position;					// Interpolated position for this fragment.
varying vec4 v_Color;						// This is the color from the vertex shader interpolated across the
											// triangle per fragment.
varying vec3 v_Normal;						// Interpolated normal for this fragment.

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
    // 2.5 - coefficient for increasing brightness
    gl_FragColor = v_Color * finalDiffuse * 2.5;
}
