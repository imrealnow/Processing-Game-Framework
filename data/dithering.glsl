#define PROCESSING_COLOR_SHADER

uniform vec2 sketchSize;

uniform sampler2D texture;

uniform sampler2D noiseTexture;


void main(void) {
		
	vec2 uv = gl_FragCoord.xy / sketchSize.xy;
	uv.y = uv.y;
	
	vec3 sourcePixel = texture2D(texture, uv).rgb;
	float grayscale = length(sourcePixel*vec3(0.2126,0.7152,0.0722));
	
	vec3 ditherPixel = texture2D(noiseTexture, vec2(mod(gl_FragCoord.xy/vec2(8.0,8.0),1.0))).xyz;
	float ditherGrayscale = (ditherPixel.x + ditherPixel.y + ditherPixel.z) / 3.0;
	ditherGrayscale -= 0.5;
	
	float ditheredResult = grayscale + ditherGrayscale;
	
	float bit = ditheredResult >= 0.5 ? 1.0 : 0.0;
	gl_FragColor = vec4(bit,bit,bit,1);
	
}
