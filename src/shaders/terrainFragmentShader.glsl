#version 400 core

in vec2 pass_textureCoords;
in float visibility;

flat in vec3 diffuse;
flat in vec3 specular;
flat in vec4 vertexColour;

out vec4 out_Color;

uniform vec3 skyColour;

void main(void){

	vec4 totalColour = vec4(vertexColour);
	totalColour -= vec4(1.1 * diffuse, 1);

	out_Color = totalColour + vec4(specular, 1.0);
	out_Color = mix(vec4(skyColour, 1.0), out_Color, visibility);

}
