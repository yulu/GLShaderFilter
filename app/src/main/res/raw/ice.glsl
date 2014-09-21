vec3 filter(vec3 color) {
    //color mapping
    color.b = (color.b*(255.-36.) + 36.)/255.;

    //overlay
    vec3 layer = RadiusGradient(vec3(228./255., 228./255., 234./255.), vec3(33./255., 33./255., 33./255.), vec2(0.4, 0.5));

    color.r = texture2D(uTexture, vec2(layer.r, color.r)).r;
    color.g = texture2D(uTexture, vec2(layer.g, color.g)).g;
    color.b = texture2D(uTexture, vec2(layer.b, color.b)).b;

    return color;
}