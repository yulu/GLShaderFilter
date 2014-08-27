#extension GL_OES_EGL_image_external : require

precision mediump float;

uniform samplerExternalOES sTexture;
varying vec2 vTextureCoord;

__FILTER__

void main(){

    gl_FragColor = vec4(filter(), 1.0);
}