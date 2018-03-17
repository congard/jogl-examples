#version 130
in vec3 color;

out vec4 outColor;

void main()
{
    outColor = vec4(color.x, color.y, color.z, 1.0);
}
