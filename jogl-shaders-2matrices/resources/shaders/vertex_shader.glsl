#version 130
in vec3 inColor;
in vec3 inPosition;

uniform mat4 pv_Matrix;

out vec3 color;

void main()
{
    vec4 v = vec4(inPosition, 1.0);
    gl_Position = pv_Matrix * v;
    //float distance = sqrt(pow(inPosition.x, 2) + pow(inPosition.y, 2) + pow(inPosition.z, 2))*10 + 1;
    color = vec3(inColor.x, inColor.y, inColor.z);
}
