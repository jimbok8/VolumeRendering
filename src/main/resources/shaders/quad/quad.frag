#version 130
uniform vec2 viewSize;
uniform mat4 iV;
uniform mat4 iP;

uniform sampler3D tex;
uniform sampler2D colorMap;
uniform int depthSampleCount;
uniform float zScale;

out vec4 color;




struct Ray {
    vec3 origin;
    vec3 direction;
    vec3 inv_direction;
    int sign[3];
};

Ray makeRay(vec3 origin, vec3 direction) {
    vec3 inv_direction = vec3(1.0) / direction;

    return Ray(
    origin,
    direction,
    inv_direction,
    int[3](
    ((inv_direction.x < 0.0) ? 1 : 0),
    ((inv_direction.y < 0.0) ? 1 : 0),
    ((inv_direction.z < 0.0) ? 1 : 0)
    )
    );
}
Ray CreateCameraRay(vec2 uv)
{
    // Transform the camera origin to world space
    vec3 origin = (iV* vec4(0.0f, 0.0f, 0.0f, 1.0f)).xyz;

    // Invert the perspective projection of the view-space position
    vec3 direction = (iP * vec4(uv, 0.0f, 1.0f)).xyz;
    // Transform the direction from camera to world space and normalize
    direction = (iV* vec4(direction, 0.0f)).xyz;
    direction = normalize(direction);
    return makeRay(origin, direction);
}
/*
	From: https://github.com/hpicgs/cgsee/wiki/Ray-Box-Intersection-on-the-GPU
*/
void intersect(
in Ray ray, in vec3 aabb[2],
out float tmin, out float tmax
){
    float tymin, tymax, tzmin, tzmax;
    tmin = (aabb[ray.sign[0]].x - ray.origin.x) * ray.inv_direction.x;
    tmax = (aabb[1-ray.sign[0]].x - ray.origin.x) * ray.inv_direction.x;
    tymin = (aabb[ray.sign[1]].y - ray.origin.y) * ray.inv_direction.y;
    tymax = (aabb[1-ray.sign[1]].y - ray.origin.y) * ray.inv_direction.y;
    tzmin = (aabb[ray.sign[2]].z - ray.origin.z) * ray.inv_direction.z;
    tzmax = (aabb[1-ray.sign[2]].z - ray.origin.z) * ray.inv_direction.z;
    tmin = max(max(tmin, tymin), tzmin);
    tmax = min(min(tmax, tymax), tzmax);
}


void main(){
    vec2 vUV = 2.0 * gl_FragCoord.xy / viewSize - 1.0;
    Ray ray = CreateCameraRay(vUV);
  //  ray.direction.z /= zScale;
   // ray.origin.z /= zScale;
    vec3 aabb[2] = vec3[2](
    vec3(-1.0, -1.0, -zScale),
    vec3(1.0, 1.0, zScale)
    );

    float tmin = 0.0;
    float tmax = 0.0;

    intersect(ray, aabb, tmin, tmax);
    if (tmax < tmin){
        discard;
        return;
    }

    vec3 start = (ray.origin.xyz + tmin*ray.direction.xyz - aabb[0])/(aabb[1]-aabb[0]);
    vec3 end = (ray.origin.xyz + tmax*ray.direction.xyz - aabb[0])/(aabb[1]-aabb[0]);

    float len = distance(end, start);
    int sampleCount = int(float(depthSampleCount)*len);
    vec3 increment = (end-start)/float(sampleCount);
    float incLength = length(increment);
    increment = normalize(increment);
    vec3 pos = start;

    float px = 0.0;
    vec4 pxColor = vec4(0.0, 0.0, 0.0, 0.0);
    vec3 texCo = vec3(0.0, 0.0, 0.0);

    float last = 0.0;
    for (int count = 0; count < sampleCount; count++){

        texCo = mix(start, end, float(count)/float(sampleCount));// - originOffset;

        px = max(px, texture(tex, texCo).r);

        if (px >= 0.99){
            break;
        }
    }
    pxColor = texture(colorMap, vec2(px, 0.0));

    color =pxColor;
}