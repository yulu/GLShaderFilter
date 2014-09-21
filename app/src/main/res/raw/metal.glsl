vec3 filter(vec3 color) {
    vec2 tc = (2.0 * vTextureCoord) - 1.0;
    float d = dot(tc, tc);

    vec2 lookup = vec2(d, color.r);
    color.r = texture2D(uTexture, lookup).r;
    lookup.y = color.g;
    color.g = texture2D(uTexture, lookup).g;
    lookup.y = color.b;
    color.b	= texture2D(uTexture, lookup).b;


    color.b = (color.b*(228.-28.) + 28.)/255.;


    return BrightnessContrastSaturation(color, 1., 1.1, 1.);
}