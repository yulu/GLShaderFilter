vec3 filter(vec3 color) {

    //do mapping
    color.r = (color.r*(255.-27.) + 27.)/255.;
    color.b = (color.b*(255.-26.) + 26.)/255.;

    //overlay
    vec3 layer = RadiusGradient(vec3(166./255., 166./255., 166./255.), vec3(60./255., 60./255., 50./255.), vec2(0.5, 0.5));

    color.r = texture2D(uTexture, vec2(layer.r, color.r)).r;
    color.g = texture2D(uTexture, vec2(layer.g, color.g)).g;
    color.b = texture2D(uTexture, vec2(layer.b, color.b)).b;

    return color;
}