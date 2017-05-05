#version 400 core

in vec2 pass_textureCoords;
in float visibility;

flat in vec3 diffuse;
flat in vec3 specular;

out vec4 out_Color;

uniform sampler2D backgroundTexture;
uniform sampler2D rTexture;
uniform sampler2D gTexture;
uniform sampler2D bTexture;
uniform sampler2D blendMap;

uniform vec3 skyColour;

void main(void){

	// Terrain Texture
	vec4 blendMapColour = texture(blendMap, pass_textureCoords);
	float backTextureAmount = 1 - (blendMapColour.r + blendMapColour.g + blendMapColour.b);
	vec2 tiledCoords = pass_textureCoords * 40.0;
	vec4 backgroundTextureColour = texture(backgroundTexture, tiledCoords) * backTextureAmount;
	vec4 rTextureColour = texture(rTexture, tiledCoords) * blendMapColour.r;
	vec4 gTextureColour = texture(gTexture, tiledCoords) * blendMapColour.g;
	vec4 bTextureColour = texture(bTexture, tiledCoords) * blendMapColour.b;

	// Terrain Texture Color
	vec4 totalColour = backgroundTextureColour + rTextureColour + gTextureColour + bTextureColour;
	totalColour -= vec4(diffuse, 1);

	out_Color = totalColour + vec4(specular, 1.0);
	out_Color = mix(vec4(skyColour, 1.0), out_Color, visibility);

}
