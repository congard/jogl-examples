/*
 * @author Congard
 * dbcongard@gmail.com
 * t.me/congard
 * github.com/congard
 */

#version 130

uniform mat4 u_MVPMatrix;      // A constant representing the combined model/view/projection matrix.
uniform mat4 u_MVMatrix;       // A constant representing the combined model/view matrix.
uniform float u_NormalMapping;  // Normal mapping; 0 - false, 1 - true

attribute vec4 a_Position;     // Per-vertex position information we will pass in.
attribute vec3 a_Normal;       // Per-vertex normal information we will pass in.
attribute vec3 a_Tangent;	   // Per-vertex tangent information we will pass in.
attribute vec3 a_Bitangent;	   // Per-vertex bitangent information we will pass in.
attribute vec2 a_Texture;	   // Per-vertex texture information we will pass in.

varying mat4 v_MVMatrix;	   // This will be passed into the fragment shader.
varying mat3 v_TBN;			   // This will be passed into the fragment shader.
varying vec4 v_Position;       // This will be passed into the fragment shader.
varying vec3 v_Normal;         // This will be passed into the fragment shader.
varying vec2 v_Texture;		   // This will be passed into the fragment shader.
varying float v_NormalMapping;  // This will be passed into the fragment shader.

void main() {
	// creating TBN (tangent-bitangent-normal) matrix if normal mapping enabled
	if (u_NormalMapping == 1) {
		vec3 T = normalize(vec3(u_MVMatrix * vec4(a_Tangent, 0.0)));
		vec3 B = normalize(vec3(u_MVMatrix * vec4(a_Bitangent, 0.0)));
		vec3 N = normalize(vec3(u_MVMatrix * vec4(a_Normal, 0.0)));
		mat3 TBN = mat3(T, B, N);
		v_TBN = TBN;
	}

    // gl_Position is a special variable used to store the final position.
    // Multiply the vertex by the matrix to get the final point in normalized screen coordinates.
    gl_Position = u_MVPMatrix * a_Position;

    // sending all needed variables to fragment shader
    v_Position = a_Position;
    v_Texture = a_Texture;
    v_NormalMapping = u_NormalMapping;
    v_MVMatrix = u_MVMatrix;
    v_Normal = a_Normal;
}
