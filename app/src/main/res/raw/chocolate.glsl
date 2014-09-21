vec3 filter(vec3 color) {
    float gray = dot(color, vec3(0.299, 0.587, 0.114));
    color = OverlayBlender(vec3(gray), color);
    color = MultiplyBlender(vec3(0.984, 0.949, 0.639), color, 0.588235);
    color = ScreenBlender(vec3(0.909, 0.396, 0.702), color, 0.2);
    color = ScreenBlender(vec3(0.035, 0.286, 0.914), color, 0.168627);
    color = BrightnessContrastSaturation(color, 1.4, 1.1, 1.1);

    return color;
}