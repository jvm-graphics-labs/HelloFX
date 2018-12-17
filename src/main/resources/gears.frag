#version 330

uniform vec4 color;

in float shade;

layout(location = 0) out vec4 outColor;

void main() {
    outColor = vec4(color.xyz * shade, color.w);
}