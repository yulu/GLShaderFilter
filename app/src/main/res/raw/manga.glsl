vec3 filter() {
    vec3 color = texture2D(sTexture, vTextureCoord).rgb;
    return vec3(color.b, color.g, color.r);
}