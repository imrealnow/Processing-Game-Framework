#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D texture;
uniform vec2 iResolution;
uniform float pixelate;

void main() {
    
    vec2 UV = gl_FragCoord.xy / iResolution.xy;
    vec2 sampleUVCoord = (floor(gl_FragCoord.xy / pixelate) * pixelate + 0.5) / iResolution.xy;

    // Sample the texture with pixelated coordinates
    vec3 color = texture2D(texture, sampleUVCoord).rgb;
    
    gl_FragColor = vec4(color, 1.0);
}
