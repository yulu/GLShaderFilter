vec3 softGlow(vec3 color) {
    vec3 val = BrightnessContrastSaturation(color, 1., 1., 0.2);

    val = 1./(1. + exp(-1.*(2.+0.85*20.)*(val - 0.5)));
    val = val * 1.2;
    val = clamp(val, vec3(0.), vec3(1.));

    return val;
}

vec3 filter(vec3 color) {

    float dx = uPixelSize.x*1.5;
	float dy = uPixelSize.y*1.5;

	//Gussian Blur
	vec3 sample0 = 	(texture2D(sTexture, vec2(vTextureCoord.x - dx, vTextureCoord.y + dy)).rgb);
	vec3 sample1 = 	(texture2D(sTexture, vec2(vTextureCoord.x - dx, vTextureCoord.y)).rgb);
	vec3 sample2 = 	(texture2D(sTexture, vec2(vTextureCoord.x - dx, vTextureCoord.y - dy)).rgb);
	vec3 sample3 = 	(texture2D(sTexture, vec2(vTextureCoord.x, vTextureCoord.y + dy)).rgb);
	vec3 sample4 = 	(texture2D(sTexture, vec2(vTextureCoord.x, vTextureCoord.y)).rgb);
	vec3 sample5 = 	(texture2D(sTexture, vec2(vTextureCoord.x, vTextureCoord.y - dy)).rgb);
	vec3 sample6 = 	(texture2D(sTexture, vec2(vTextureCoord.x + dx, vTextureCoord.y + dy)).rgb);
	vec3 sample7 = 	(texture2D(sTexture, vec2(vTextureCoord.x + dx, vTextureCoord.y)).rgb);
	vec3 sample8 = 	(texture2D(sTexture, vec2(vTextureCoord.x + dx, vTextureCoord.y - dy)).rgb);

	vec3 target = vec3(0., 0., 0.);
    target += 1.*(sample0+sample2+sample6+sample8);
    target += 2.*(sample1+sample3+sample5+sample7);
    target += 4.*(sample4);
    target /= 16.;

    //screen
    color = ScreenBlender(softGlow(target), color, 0.6);

    return color;

}