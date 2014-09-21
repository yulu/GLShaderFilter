/*
    Copyright 2014 YU LU

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

#extension GL_OES_EGL_image_external : require

precision mediump float;

uniform samplerExternalOES sTexture;
uniform sampler2D uTexture;
uniform vec2 uPixelSize;

varying vec2 vTextureCoord;

const float PI = 3.14159265;
const vec3 W = vec3(0.2125, 0.7154, 0.0721);

//B-C-S adjustment
vec3 BrightnessContrastSaturation(vec3 color, float brt, float con, float sat) {
    vec3 black = vec3(0., 0., 0.);
    vec3 middle = vec3(0.5, 0.5, 0.5);
    float luminance = dot(color, W);
    vec3 gray = vec3(luminance, luminance, luminance);

    color = mix(black, color, brt);
    color = mix(middle, color, con);
    return mix(gray, color, sat);
}

//overlay blender
vec3 OverlayBlender(vec3 Color, vec3 filter) {
    vec3 filter_result;
    float luminance = dot(filter, W);

    if(luminance < 0.5)
        filter_result = 2. * filter * Color;
    else
        filter_result = 1. - (1. - (2. *(filter - 0.5)))*(1. - Color);

    return filter_result;
}

//multiply blender
vec3 MultiplyBlender(vec3 Color, vec3 filter, float alpha) {
    vec3 filter_result;
    float luminance = dot(filter, W);

    filter_result = filter * Color * alpha;

    return filter_result;
}

//screen blender
vec3 ScreenBlender(vec3 overlay, vec3 underlay, float alpha) {
    return 1.0 - (1.0 - (overlay * alpha)) * (1.0 - underlay);
}


//radius gradient
vec3 RadiusGradient(vec3 center, vec3 corner, vec2 center_point) {
    vec3 color;
    vec2 p = vTextureCoord - center_point;
    float radius = length(p);

    color = radius * (corner - center) + center;

    return color;
}

__FILTER__

void main(){
    vec3 color = texture2D(sTexture, vTextureCoord).rgb;
    gl_FragColor = vec4(filter(color), 1.0);
}