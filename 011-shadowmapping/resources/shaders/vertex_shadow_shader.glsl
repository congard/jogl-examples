/*
 * @author Congard
 * dbcongard@gmail.com
 * t.me/congard
 * github.com/congard
 */

#version 130

attribute vec3 a_Position;
uniform mat4 u_LightSpaceMatrix;
uniform mat4 u_ModelMatrix;

void main() {
	gl_Position = u_LightSpaceMatrix * u_ModelMatrix * vec4(a_Position, 1.0);
}
