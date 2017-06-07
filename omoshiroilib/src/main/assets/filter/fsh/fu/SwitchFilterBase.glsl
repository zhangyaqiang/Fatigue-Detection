attribute vec4 position;
attribute vec4 inputTextureCoordinate;
attribute vec4 inputTextureCoordinate2;
attribute vec4 inputTextureCoordinate3;
varying vec2 textureCoordinate;
varying vec2 textureCoordinate2;
varying vec2 textureCoordinate3;
void main() {
    gl_Position = position;
    textureCoordinate = inputTextureCoordinate.xy;
    textureCoordinate2 = inputTextureCoordinate2.xy;
    textureCoordinate3 = inputTextureCoordinate3.xy;
}


precision mediump float;
varying highp vec2 textureCoordinate;
//alpha
varying highp vec2 textureCoordinate2;
varying highp vec2 textureCoordinate3;
uniform sampler2D inputImageTexture;
uniform sampler2D inputImageTexture2;
uniform sampler2D inputImageTexture3;
uniform sampler2D inputImageTexture4;
uniform int drawMask;
uniform int faceCnt;
uniform int m_orientation;
uniform int isAndroid;
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
    
    if (faceCnt == 1) {
        gl_FragColor = texture2D(inputImageTexture, textureCoordinate);
        vec4 pickSample = texture2D(inputImageTexture3, textureCoordinate);
        if (1 == isAndroid)
            pickSample = texture2D(inputImageTexture3, vec2(textureCoordinate.x, 1.0 - textureCoordinate.y));
        if (m_orientation == 2) {
            pickSample = texture2D(inputImageTexture3, vec2(1.0 - textureCoordinate.x, 1.0 - textureCoordinate.y));
            if (1 == isAndroid)
                pickSample = texture2D(inputImageTexture3, vec2(1.0 - textureCoordinate.x, textureCoordinate.y));
        }
        if (m_orientation == 3) {
            pickSample = texture2D(inputImageTexture4, vec2(textureCoordinate.x, textureCoordinate.y));
            if (1 == isAndroid)
                pickSample = texture2D(inputImageTexture4, vec2(textureCoordinate.x, 1.0 - textureCoordinate.y));
        }
        if (m_orientation == 4) {
            pickSample = texture2D(inputImageTexture4, vec2(1.0 - textureCoordinate.x, 1.0 - textureCoordinate.y));
            if (1 == isAndroid)
                pickSample = texture2D(inputImageTexture4, vec2(1.0 - textureCoordinate.x, textureCoordinate.y));
        }
        gl_FragColor = blendNormal(pickSample, gl_FragColor);
        return;
    }
    if (drawMask == 1) {
        vec4 color1 = texture2D(inputImageTexture, textureCoordinate3);
        float alpha1 = texture2D(inputImageTexture2, textureCoordinate2).a;
        gl_FragColor = mix(gl_FragColor, color1, alpha1);
       
    } else if (drawMask == 2) {
        vec4 color2 = texture2D(inputImageTexture, textureCoordinate3);
        float alpha2 = texture2D(inputImageTexture2, textureCoordinate2).a;
        gl_FragColor = mix(gl_FragColor, color2, alpha2);
    } else if (drawMask == 3) {
        vec4 color2 = texture2D(inputImageTexture, textureCoordinate3);
        float alpha2 = texture2D(inputImageTexture2, textureCoordinate2).a;
        gl_FragColor = mix(gl_FragColor, color2, alpha2);
    } else if (drawMask == 4) {
        vec4 color2 = texture2D(inputImageTexture, textureCoordinate3);
        float alpha2 = texture2D(inputImageTexture2, textureCoordinate2).a;
        gl_FragColor = mix(gl_FragColor, color2, alpha2);
    }
}


