vec3 filter(vec3 color) {
    vec3 layer = RadiusGradient(vec3(1., 1., 1.), vec3(0.1, 0.1, 0.1), vec2(0.5, 0.5));

    color = BrightnessContrastSaturation(color, 1.2, 1.2, 1.2);
    //return mix(color, MultiplyBlender(color, layer), 0.7);
    return MultiplyBlender(color, layer, 0.8);
}