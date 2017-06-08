precision mediump float;
varying highp vec2 textureCoordinate;
varying highp vec2 textureCoordinate2;
uniform sampler2D inputImageTexture;
uniform sampler2D inputImageTexture2;
uniform sampler2D inputImageTexture3;
uniform sampler2D inputImageTexture4;
uniform sampler2D inputImageTexture5;
uniform sampler2D inputImageTexture6;
uniform int drawMask;
vec4 blendNormal(vec4 c1, vec4 c2) {
    vec4 outputColor;
    outputColor.r = c1.r + c2.r * c2.a * (1.0 - c1.a);
    outputColor.g = c1.g + c2.g * c2.a * (1.0 - c1.a);
    outputColor.b = c1.b + c2.b * c2.a * (1.0 - c1.a);
    outputColor.a = c1.a + c2.a * (1.0 - c1.a);
    return outputColor;
}
void main() {
    gl_FragColor = texture2D(inputImageTexture, textureCoordinate);
    if (drawMask == 1) {
        vec4 color2 = texture2D(inputImageTexture2, textureCoordinate2);
        gl_FragColor = blendNormal(color2, gl_FragColor);
    } else if (drawMask == 2) {
        vec4 color2 = texture2D(inputImageTexture3, textureCoordinate2);
        gl_FragColor = blendNormal(color2, gl_FragColor);
    } else if (drawMask == 3) {
        vec4 color2 = texture2D(inputImageTexture4, textureCoordinate2);
        gl_FragColor = blendNormal(color2, gl_FragColor);
    } else if (drawMask == 4) {
        vec4 color2 = texture2D(inputImageTexture5, textureCoordinate2);
        gl_FragColor = blendNormal(color2, gl_FragColor);
    } else if (drawMask == 5) {
        vec4 color2 = texture2D(inputImageTexture6, textureCoordinate2);
        gl_FragColor = blendNormal(color2, gl_FragColor);
    }
}

