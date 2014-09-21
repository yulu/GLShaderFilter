vec3 filter(vec3 color) {
    //do mapping
    color = BrightnessContrastSaturation(color, 0.8, 1.2, 0.8);

    vec3 overlay = texture2D(uTexture, vTextureCoord).rgb;


    color = MultiplyBlender(color, overlay, 1.);
    //do mapping
    color.r = (color.r*(255.-27.) + 27.)/255.;

    return color;

}