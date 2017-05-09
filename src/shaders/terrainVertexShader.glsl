#version 400 core

in vec3 position;
in vec2 textureCoords;
in vec3 normal;

out vec2 pass_textureCoords;
out float visibility;

flat out vec3 diffuse;
flat out vec3 specular;
flat out vec4 vertexColour;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPosition[4];
uniform vec3 lightColour[4];
uniform float shineDamper;
uniform float reflectivity;

uniform sampler2D backgroundTexture;
uniform sampler2D rTexture;
uniform sampler2D gTexture;
uniform sampler2D bTexture;
uniform sampler2D blendMap;

const float density = 0.0025;
const float gradient = 10;

void main(void){

	vec4 worldPosition = transformationMatrix * vec4(position, 1.0);
	vec4 positionRelativeToCam = viewMatrix * worldPosition;

	vec4 vertexPosition = projectionMatrix * positionRelativeToCam;
	gl_Position = vertexPosition;

	pass_textureCoords = textureCoords;
	vec3 surfaceNormal = (transformationMatrix * vec4(normal, 0.0)).xyz;
	vec3 toCameraVector = (inverse(viewMatrix) * vec4(0.0, 0.0, 0.0, 1.0)).xyz - worldPosition.xyz;

	// distance
	float distance = length(positionRelativeToCam.xyz);
	visibility = exp(-pow((distance * density), gradient));
	visibility = clamp(visibility, 0.0, 1.0);

	// initilaize the variable
	diffuse = vec3(0);
	specular = vec3(0);

	vec3 unitNormal = normalize(surfaceNormal);
	vec3 unitVectorToCamera = normalize(toCameraVector);

	// initilaize toLightVector
	vec3 toLightVector[4];
	for (int a = 0; a < 4; a++) {
		toLightVector[a] = lightPosition[a] - worldPosition.xyz;
	}

	// calculate diffuse and specular
	for (int i = 0; i < 4; i++) {
		// unitLightVector
		vec3 unitLightVector = normalize(toLightVector[i]);

		// Diffuse
		float brightness = dot(unitNormal, unitLightVector);

		// specular
		vec3 lightDirection = -unitLightVector;
		vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);
		float specularFactor = dot(reflectedLightDirection, unitVectorToCamera);
		specularFactor = max(specularFactor, 0.0);
		float dampedFactor = pow(specularFactor, shineDamper);

		// Add to diffuse
		diffuse += brightness * lightColour[i];
		specular += dampedFactor * lightColour[i] * reflectivity;
	}

	diffuse = clamp(diffuse, 0, 1);
	diffuse = (1 - diffuse) * 0.2 - 0.1;
	specular = vec3(0.03);

	// Terrain Texture
	vec4 blendMapColour = texture(blendMap, pass_textureCoords);
	float backTextureAmount = 1 - (blendMapColour.r + blendMapColour.g + blendMapColour.b);
	vec2 tiledCoords = pass_textureCoords * 40.0;
	vec4 backgroundTextureColour = texture(backgroundTexture, tiledCoords) * backTextureAmount;
	vec4 rTextureColour = texture(rTexture, tiledCoords) * blendMapColour.r;
	vec4 gTextureColour = texture(gTexture, tiledCoords) * blendMapColour.g;
	vec4 bTextureColour = texture(bTexture, tiledCoords) * blendMapColour.b;

	// Terrain Texture Color
	vertexColour = backgroundTextureColour + rTextureColour + gTextureColour + bTextureColour;

	// End of Shader Code

}
