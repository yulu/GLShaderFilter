vec3 filter(vec3 color) {
    vec3 layer = RadiusGradient(vec3(1., 1., 1.), vec3(0.5, 0.5, 0.5), vec2(0.5, 0.5));
    //do mapping
    color.r= (color.r*(255.-32.) + 32.)/255.;
    color.g= (color.g*(255.-22.) + 22.)/255.;
    color.b= (color.b*(255.-22.) + 22.)/255.;
    color = BrightnessContrastSaturation(color, 1.0, 1.1, 1.1);
    return MultiplyBlender(color, layer, 1.);
}