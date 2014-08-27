vec3 filter() {
    return texture2D(sTexture, vTextureCoord).rgb;
}