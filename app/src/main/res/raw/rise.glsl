vec3 filter() {
    vec3 color = texture2D(sTexture, vTextureCoord).rgb;
    vec3 W = vec3(0.2125, 0.7154, 0.0721);
    float luminance = dot(color, W);
    return vec3(luminance, luminance, luminance);
}