vec3 filter(vec3 color) {
    float gray = dot(color, vec3(0.299, 0.587, 0.114));
    return BrightnessContrastSaturation(vec3(gray), 1., 1.2, 1.);
}