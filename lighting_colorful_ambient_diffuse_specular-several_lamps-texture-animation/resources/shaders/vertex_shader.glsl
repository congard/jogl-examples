/*
 * @author Congard
 * dbcongard@gmail.com
 * t.me/congard
 * github.com/congard
 */

#version 130

uniform mat4 u_MVPMatrix;      // A constant representing the combined model/view/projection matrix.
uniform mat4 u_MVMatrix;       // A constant representing the combined model/view matrix.

attribute vec4 a_Position;     // Per-vertex position information we will pass in.
attribute vec3 a_Normal;       // Per-vertex normal information we will pass in.
attribute vec2 a_Texture;	   // Per-vertex texture information we will pass in.

varying vec3 v_Position;       // This will be passed into the fragment shader.
varying vec3 v_Normal;         // This will be passed into the fragment shader.
varying vec2 v_Texture;		   // This will be passed into the fragment shader.

void main() {
    // Transform the vertex into eye space.
    v_Position = vec3(u_MVMatrix * a_Position);

    // Pass through the texture.
    v_Texture = a_Texture;

    // Transform the normal's orientation into eye space.
    v_Normal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));

    // gl_Position is a special variable used to store the final position.
    // Multiply the vertex by the matrix to get the final point in normalized screen coordinates.
    gl_Position = u_MVPMatrix * a_Position;
}
