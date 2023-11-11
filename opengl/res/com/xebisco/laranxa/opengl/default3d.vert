#version 330

const int MAX_WEIGHTS = 4;
const int MAX_BONES = 150;

in vec3 position;
in vec2 texCoord;
in vec3 normal;
in vec3 tangent;
in vec3 bitangent;
in vec4 boneWeights;
in ivec4 boneIndices;

out vec3 outPosition;
out vec3 outNormal;
out vec3 outTangent;
out vec3 outBitangent;
out vec2 outTextCoord;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 transformationMatrix;
uniform mat4 bonesMatrices[MAX_BONES];

void main() {

    vec4 initPos = vec4(0, 0, 0, 0);
    vec4 initNormal = vec4(0, 0, 0, 0);
    vec4 initTangent = vec4(0, 0, 0, 0);
    vec4 initBitangent = vec4(0, 0, 0, 0);
    int count = 0;
    for (int i = 0; i < MAX_WEIGHTS; i++) {
        float weight = boneWeights[i];
        if (weight > 0) {
            count++;
            int boneIndex = boneIndices[i];
            vec4 tmpPos = bonesMatrices[boneIndex] * vec4(position, 1.0);
            initPos += weight * tmpPos;
            vec4 tmpNormal = bonesMatrices[boneIndex] * vec4(normal, 0.0);
            initNormal += weight * tmpNormal;
            vec4 tmpTangent = bonesMatrices[boneIndex] * vec4(tangent, 0.0);
            initTangent += weight * tmpTangent;
            vec4 tmpBitangent = bonesMatrices[boneIndex] * vec4(bitangent, 0.0);
            initTangent += weight * tmpBitangent;
        }
    }
    if (count == 0) {
        initPos = vec4(position, 1.0);
        initNormal = vec4(normal, 0.0);
        initTangent = vec4(tangent, 0.0);
        initBitangent = vec4(bitangent, 0.0);
    }

    mat4 modelViewMatrix = viewMatrix * transformationMatrix;
    vec4 mvPosition =  modelViewMatrix * initPos;
    gl_Position   = projectionMatrix * mvPosition;
    outPosition   = mvPosition.xyz;
    outNormal     = normalize(modelViewMatrix * initNormal).xyz;
    outTangent    = normalize(modelViewMatrix * initTangent).xyz;
    outBitangent  = normalize(modelViewMatrix * initBitangent).xyz;
    outTextCoord  = texCoord;
}