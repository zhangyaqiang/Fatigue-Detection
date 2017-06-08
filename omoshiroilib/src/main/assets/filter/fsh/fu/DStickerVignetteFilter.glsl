precision highp float;
varying highp vec2 textureCoordinate;
uniform sampler2D inputImageTexture;
uniform sampler2D inputImageTexture2;
uniform int faceCnt;
uniform int flipSticker;
uniform vec2 leftTop;
uniform vec2 rightBottom;
vec4 blendNormal(vec4 c1, vec4 c2) {
    vec4 outputColor;
    outputColor.r = c1.r + c2.r * c2.a * (1.0 - c1.a);
    outputColor.g = c1.g + c2.g * c2.a * (1.0 - c1.a);
    outputColor.b = c1.b + c2.b * c2.a * (1.0 - c1.a);
    outputColor.a = c1.a + c2.a * (1.0 - c1.a);
    return outputColor;
}
void main() {
    vec4 c2 = texture2D(inputImageTexture, textureCoordinate);
    if (faceCnt < 1) {
        gl_FragColor = c2;
        return;
    }
    if (textureCoordinate.x <= rightBottom.x && textureCoordinate.x >= leftTop.x &&
        textureCoordinate.y <= rightBottom.y && textureCoordinate.y >= leftTop.y) {
        float x_coord = (textureCoordinate.x - leftTop.x) / (rightBottom.x - leftTop.x);
        float y_coord = (textureCoordinate.y - leftTop.y) / (rightBottom.y - leftTop.y);
        vec2 coordUse;
        if (flipSticker == 1) {
            coordUse = vec2(x_coord, 1.0 - y_coord);
        } else {
            coordUse = vec2(x_coord, y_coord);
        }
        vec4 c1 = texture2D(inputImageTexture2, coordUse);
        gl_FragColor = blendNormal(c1, c2);
    } else {
        gl_FragColor = c2;
    }
}
