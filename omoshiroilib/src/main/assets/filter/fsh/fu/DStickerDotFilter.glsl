precision highp float;
varying highp vec2 textureCoordinate;
uniform sampler2D inputImageTexture;
uniform sampler2D inputImageTexture2;
uniform int faceCnt;
uniform int flipSticker;
uniform vec2 alignPoint0;
uniform vec2 alignPoint1;
uniform vec2 alignPoint2;
uniform vec2 alignPoint3;
uniform vec2 alignPoint4;
uniform vec2 size0;
uniform vec2 size1;
uniform vec2 size2;
uniform vec2 size3;
uniform vec2 size4;
uniform mat4 rotateMatrix0;
uniform mat4 rotateMatrix1;
uniform mat4 rotateMatrix2;
uniform mat4 rotateMatrix3;
uniform mat4 rotateMatrix4;
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
    if (faceCnt < 1) {
        return;
    }
    if (faceCnt > 0) {
        vec4 originCoord = vec4(textureCoordinate.x, textureCoordinate.y, 0, 1.0);
        originCoord = rotateMatrix0 * originCoord;
        if (originCoord.x <= (alignPoint0.x + size0.x * 0.5) && originCoord.x > (alignPoint0.x - size0.x * 0.5) &&
            originCoord.y <= (alignPoint0.y + size0.y * 0.5) && originCoord.y > (alignPoint0.y - size0.y * 0.5)) {
            float x_coord = (originCoord.x - alignPoint0.x + size0.x * 0.5) / size0.x;
            float y_coord;
            if (originCoord.y > alignPoint0.y) {
                y_coord = (originCoord.y - alignPoint0.y + size0.y * 0.5) / size0.y;
            } else {
                y_coord = (originCoord.y - alignPoint0.y + size0.y * 0.5) / size0.y;
            }
            vec2 coordUse;
            if (flipSticker == 1) {
                coordUse = vec2(x_coord, 1.0 - y_coord);
            } else {
                coordUse = vec2(x_coord, y_coord);
            }
            vec4 c1 = texture2D(inputImageTexture2, coordUse);
            gl_FragColor = blendNormal(c1, gl_FragColor);
        }
    }
    if (faceCnt > 1) {
        vec4 originCoord = vec4(textureCoordinate.x, textureCoordinate.y, 0, 1.0);
        originCoord = rotateMatrix1 * originCoord;
        if (originCoord.x <= (alignPoint1.x + size1.x * 0.5) && originCoord.x > (alignPoint1.x - size1.x * 0.5) &&
            originCoord.y <= (alignPoint1.y + size1.y * 0.5) && originCoord.y > (alignPoint1.y - size1.y * 0.5)) {
            float x_coord = (originCoord.x - alignPoint1.x + size1.x * 0.5) / size1.x;
            float y_coord;
            if (originCoord.y > alignPoint1.y) {
                y_coord = (originCoord.y - alignPoint1.y + size1.y * 0.5) / size1.y;
            } else {
                y_coord = (originCoord.y - alignPoint1.y + size1.y * 0.5) / size1.y;
            }
            vec2 coordUse;
            if (flipSticker == 1) {
                coordUse = vec2(x_coord, 1.0 - y_coord);
            } else {
                coordUse = vec2(x_coord, y_coord);
            }
            vec4 c1 = texture2D(inputImageTexture2, coordUse);
            gl_FragColor = blendNormal(c1, gl_FragColor);
        }
    }
    if (faceCnt > 2) {
        vec4 originCoord = vec4(textureCoordinate.x, textureCoordinate.y, 0, 1.0);
        originCoord = rotateMatrix2 * originCoord;
        if (originCoord.x <= (alignPoint2.x + size2.x * 0.5) && originCoord.x > (alignPoint2.x - size2.x * 0.5) &&
            originCoord.y <= (alignPoint2.y + size2.y * 0.5) && originCoord.y > (alignPoint2.y - size2.y * 0.5)) {
            float x_coord = (originCoord.x - alignPoint2.x + size2.x * 0.5) / size2.x;
            float y_coord;
            if (originCoord.y > alignPoint2.y) {
                y_coord = (originCoord.y - alignPoint2.y + size2.y * 0.5) / size2.y;
            } else {
                y_coord = (originCoord.y - alignPoint2.y + size2.y * 0.5) / size2.y;
            }
            vec2 coordUse;
            if (flipSticker == 1) {
                coordUse = vec2(x_coord, 1.0 - y_coord);
            } else {
                coordUse = vec2(x_coord, y_coord);
            }
            vec4 c1 = texture2D(inputImageTexture2, coordUse);
            gl_FragColor = blendNormal(c1, gl_FragColor);
        }
    }
    if (faceCnt > 3) {
        vec4 originCoord = vec4(textureCoordinate.x, textureCoordinate.y, 0, 1.0);
        originCoord = rotateMatrix3 * originCoord;
        if (originCoord.x <= (alignPoint3.x + size3.x * 0.5) && originCoord.x > (alignPoint3.x - size3.x * 0.5) &&
            originCoord.y <= (alignPoint3.y + size3.y * 0.5) && originCoord.y > (alignPoint3.y - size3.y * 0.5)) {
            float x_coord = (originCoord.x - alignPoint3.x + size3.x * 0.5) / size3.x;
            float y_coord;
            if (originCoord.y > alignPoint3.y) {
                y_coord = (originCoord.y - alignPoint3.y + size3.y * 0.5) / size3.y;
            } else {
                y_coord = (originCoord.y - alignPoint3.y + size3.y * 0.5) / size3.y;
            }
            vec2 coordUse;
            if (flipSticker == 1) {
                coordUse = vec2(x_coord, 1.0 - y_coord);
            } else {
                coordUse = vec2(x_coord, y_coord);
            }
            vec4 c1 = texture2D(inputImageTexture2, coordUse);
            gl_FragColor = blendNormal(c1, gl_FragColor);
        }
    }
    if (faceCnt > 4) {
        vec4 originCoord = vec4(textureCoordinate.x, textureCoordinate.y, 0, 1.0);
        originCoord = rotateMatrix4 * originCoord;
        if (originCoord.x <= (alignPoint4.x + size4.x * 0.5) && originCoord.x > (alignPoint4.x - size4.x * 0.5) &&
            originCoord.y <= (alignPoint4.y + size4.y * 0.5) && originCoord.y > (alignPoint4.y - size4.y * 0.5)) {
            float x_coord = (originCoord.x - alignPoint4.x + size4.x * 0.5) / size4.x;
            float y_coord;
            if (originCoord.y > alignPoint4.y) {
                y_coord = (originCoord.y - alignPoint4.y + size4.y * 0.5) / size4.y;
            } else {
                y_coord = (originCoord.y - alignPoint4.y + size4.y * 0.5) / size4.y;
            }
            vec2 coordUse;
            if (flipSticker == 1) {
                coordUse = vec2(x_coord, 1.0 - y_coord);
            } else {
                coordUse = vec2(x_coord, y_coord);
            }
            vec4 c1 = texture2D(inputImageTexture2, coordUse);
            gl_FragColor = blendNormal(c1, gl_FragColor);
        }
    }
}
