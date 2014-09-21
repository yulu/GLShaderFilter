const mat3 saturate = mat3(
                        1.210300,
                        -0.089700,
                        -0.091000,
                        -0.176100,
                        1.123900,
                        -0.177400,
                        -0.034200,
                        -0.034200,
                        1.265800);

const vec3 desaturate = vec3(.3, .59, .11);

vec3 filter(vec3 color) {

    //vec3 layer = RadiusGradient(vec3(1., 1., 1.), vec3(0.5, 0.5, 0.5), vec2(0.2, 0.2));

    float desaturatedColor;
    vec3 result;
    desaturatedColor = dot(desaturate, color);

    result = color * (desaturatedColor / 227. );

    color = saturate * mix(color, result, .5);

    color.r = (color.r*(255.-17.) + 17.)/255.;
    color.b = (color.b*(255.-29.) + 29.)/255.;

    //return MultiplyBlender(color, layer, 2.);
    //overlay
    vec3 layer = RadiusGradient(vec3(166./255., 166./255., 166./255.), vec3(60./255., 60./255., 50./255.), vec2(0.5, 0.5));

    color.r = texture2D(uTexture, vec2(layer.r, color.r)).r;
    color.g = texture2D(uTexture, vec2(layer.g, color.g)).g;
    color.b = texture2D(uTexture, vec2(layer.b, color.b)).b;

    return BrightnessContrastSaturation(color, 1.8, 1., 1.);
}