#version 330

uniform mat4 mvp;
uniform mat3 mvp3;

uniform vec3 light;

layout(location = 0) in  vec3 position;
layout(location = 1) in  vec3 normal;

out float shade;

void main() {
    vec3 normal_ = normalize(mvp3 * normal);
    shade = max(dot(normal_, light), 0.0);
    gl_Position = mvp * vec4(position, 1.0);
}