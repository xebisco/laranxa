#version 330

const float SPECULAR_POWER = 10;
const int MAX_POINT_LIGHTS = 10;
const int MAX_SPOT_LIGHTS = 10;

in vec3 outPosition;
in vec3 outNormal;
in vec2 outTextCoord;
in vec3 outTangent;
in vec3 outBitangent;

out vec4 fragColor;

struct Attenuation {
    float constant;
    float linear;
    float exponent;
};

struct DirectionalLight {
    vec3 color;
    vec3 direction;
    float intensity;
};
struct PointLight {
    vec3 position;
    vec3 color;
    float intensity;
    Attenuation att;
};
struct SpotLight
{
    PointLight pl;
    vec3 conedir;
    float cutoff;
};

struct Material {
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    float reflectance;
    int hasTexture;
    int hasNormalMap;
};

uniform sampler2D texture_sampler;
uniform sampler2D normal_sampler;
uniform Material material;

uniform DirectionalLight directional_light;
uniform vec3 ambient_light;
uniform PointLight point_lights[MAX_POINT_LIGHTS];
uniform SpotLight spot_lights[MAX_SPOT_LIGHTS];

vec4 calcLightColor(vec4 diffuse, vec4 specular, vec3 lightColor, float light_intensity, vec3 position, vec3 to_light_dir, vec3 normal) {
    vec4 diffuseColor = vec4(0, 0, 0, 1);
    vec4 specColor = vec4(0, 0, 0, 1);

    float diffuseFactor = max(dot(normal, to_light_dir), 0.0);
    diffuseColor = diffuse * vec4(lightColor, 1.0) * light_intensity * diffuseFactor;

    vec3 camera_direction = normalize(-position);
    vec3 from_light_dir = -to_light_dir;
    vec3 reflected_light = normalize(reflect(from_light_dir, normal));

    float specularFactor = max(dot(camera_direction, reflected_light), 0.0);
    specularFactor = pow(specularFactor, SPECULAR_POWER);
    specColor = specular * light_intensity * specularFactor * material.reflectance * vec4(lightColor, 1.0);

    return (diffuseColor * specColor);
}

vec4 calcPointLight(vec4 diffuse, vec4 specular, PointLight light, vec3 position, vec3 normal) {
    vec3 light_direction = light.position - position;
    vec3 to_light_dir  = normalize(light_direction);
    vec4 light_color = calcLightColor(diffuse, specular, light.color, light.intensity, position, to_light_dir, normal);

    float distance = length(light_direction);
    float attenuationInv = light.att.constant + light.att.linear * distance +
    light.att.exponent * distance * distance;
    return (light_color / attenuationInv);
}

vec4 calcSpotLight(vec4 diffuse, vec4 specular, SpotLight light, vec3 position, vec3 normal) {
    vec3 light_direction = light.pl.position - position;
    vec3 to_light_dir  = normalize(light_direction);
    vec3 from_light_dir  = -to_light_dir;
    float spot_alfa = dot(from_light_dir, normalize(light.conedir));
    vec4 color = vec4(0, 0, 0, 0);
    if (spot_alfa > light.cutoff)
    {
        color = calcPointLight(diffuse, specular, light.pl, position, normal);
        color *= (1.0 - (1.0 - spot_alfa)/(1.0 - light.cutoff));
    }
    return (color);
}

vec4 calcDirectionalLight(vec4 diffuse, vec4 specular, DirectionalLight light, vec3 position, vec3 normal) {
    return calcLightColor(diffuse, specular, light.color, light.intensity, position, normalize(light.direction), normal);
}

vec4 ambient;
vec4 diffuse;
vec4 specular;

void setupColors(Material material, vec2 textCoord) {
    if (material.hasTexture == 1) {
        ambient = texture(texture_sampler, textCoord);
        diffuse = ambient;
        specular = ambient;
    } else {
        ambient = material.ambient;
        diffuse = material.diffuse;
        specular = material.specular;
    }
}

vec3 calcNormal(vec3 normal, vec3 tangent, vec3 bitangent, vec2 textCoord) {
    mat3 TBN = mat3(tangent, bitangent, normal);
    vec3 newNormal = texture(normal_sampler, textCoord).rgb;
    newNormal = normalize(newNormal * 2.0 - 1.0);
    newNormal = normalize(TBN * newNormal);
    return newNormal;
}

void main() {
    setupColors(material, outTextCoord);

    vec3 normal = outNormal;
    if (material.hasNormalMap > 0) {
        normal = calcNormal(outNormal, outTangent, outBitangent, outTextCoord);
    }

    vec4 diffuseSpecularComp = calcDirectionalLight(diffuse, specular, directional_light, outPosition, outNormal);

    for (int i=0; i<MAX_POINT_LIGHTS; i++) {
        if (point_lights[i].intensity > 0) {
            diffuseSpecularComp += calcPointLight(diffuse, specular, point_lights[i], outPosition, outNormal);
        }
    }

    for (int i=0; i<MAX_SPOT_LIGHTS; i++) {
        if (spot_lights[i].pl.intensity > 0) {
            diffuseSpecularComp += calcSpotLight(diffuse, specular, spot_lights[i], outPosition, outNormal);
        }
    }

    fragColor = ambient * vec4(ambient_light, 1) + diffuseSpecularComp;

    if (material.hasTexture == 1 && ambient.a == 0){
        discard;
    }
}