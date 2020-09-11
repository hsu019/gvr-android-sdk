package com.google.vr.sdk.samples.video360.rendering;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.util.Map;
import java.util.HashMap;
import java.nio.FloatBuffer;

import static com.google.vr.sdk.samples.video360.rendering.Utils.checkGlError;

public final class FBO {
    public static FBO createFBO(int textureId, int texWidth, int texHeight, int viewWidth, int viewHeight) {
        return new FBO(textureId, texWidth, texHeight, viewWidth, viewHeight);
    }

    static Map<Integer, Integer[]> SeiInfo = new HashMap<>();
    static {
        SeiInfo.put(0, new Integer[] {3840, 2048, 384, 256, 512, 1152, 2304, 1280, 0, 0});
        SeiInfo.put(1, new Integer[] {3840, 2048, 384, 256, 512, 1536, 2304, 1280, 256, 0});
        SeiInfo.put(2, new Integer[] {3840, 2048, 384, 256, 512, 1920, 2304, 1280, 512, 0});
        SeiInfo.put(3, new Integer[] {3840, 2048, 384, 256, 512, 2304, 2304, 1280, 768, 0});
        SeiInfo.put(4, new Integer[] {3840, 2048, 384, 256, 768, 1152, 2304, 1280, 1024, 0});
        SeiInfo.put(5, new Integer[] {3840, 2048, 384, 256, 768, 1536, 2304, 1280, 0, 384});
        SeiInfo.put(6, new Integer[] {3840, 2048, 384, 256, 768, 1920, 2304, 1280, 256, 384});
        SeiInfo.put(7, new Integer[] {3840, 2048, 384, 256, 768, 2304, 2304, 1280, 512, 384});
        SeiInfo.put(8, new Integer[] {3840, 2048, 384, 256, 1024, 1152, 2304, 1280, 768, 384});
        SeiInfo.put(9, new Integer[] {3840, 2048, 384, 256, 1024, 1536, 2304, 1280, 1024, 384});
        SeiInfo.put(10, new Integer[] {3840, 2048, 384, 256, 1024, 1920, 2304, 1280, 0, 768});
        SeiInfo.put(11, new Integer[] {3840, 2048, 384, 256, 1024, 2304, 2304, 1280, 256, 768});
        SeiInfo.put(12, new Integer[] {3840, 2048, 384, 256, 1280, 1152, 2304, 1280, 512, 768});
        SeiInfo.put(13, new Integer[] {3840, 2048, 384, 256, 1280, 1536, 2304, 1280, 768, 768});
        SeiInfo.put(14, new Integer[] {3840, 2048, 384, 256, 1280, 1920, 2304, 1280, 1024, 768});
        SeiInfo.put(15, new Integer[] {3840, 2048, 384, 256, 1280, 2304, 2304, 1280, 0, 1152});
        SeiInfo.put(16, new Integer[] {3840, 2048, 384, 256, 1536, 1152, 2304, 1280, 256, 1152});
        SeiInfo.put(17, new Integer[] {3840, 2048, 384, 256, 1536, 1536, 2304, 1280, 512, 1152});
        SeiInfo.put(18, new Integer[] {3840, 2048, 384, 256, 1536, 1920, 2304, 1280, 768, 1152});
        SeiInfo.put(19, new Integer[] {3840, 2048, 384, 256, 1536, 2304, 2304, 1280, 1024, 1152});

        SeiInfo.put(20, new Integer[] {1280, 768, 256, 256, 0, 0, 2304, 1280, 0, 1536});
        SeiInfo.put(21, new Integer[] {1280, 768, 256, 256, 0, 256, 2304, 1280, 256, 1536});
        SeiInfo.put(22, new Integer[] {1280, 768, 256, 256, 0, 512, 2304, 1280, 512, 1536});
        SeiInfo.put(23, new Integer[] {1280, 768, 256, 256, 0, 768, 2304, 1280, 768, 1536});
        SeiInfo.put(24, new Integer[] {1280, 768, 256, 256, 0, 1024, 2304, 1280,1024, 1536});
        SeiInfo.put(25, new Integer[] {1280, 768, 256, 256, 256, 0, 2304, 1280, 0, 1792});
        SeiInfo.put(26, new Integer[] {1280, 768, 256, 256, 256, 256, 2304, 1280, 256, 1792});
        SeiInfo.put(27, new Integer[] {1280, 768, 256, 256, 256, 512, 2304, 1280, 512, 1792});
        SeiInfo.put(28, new Integer[] {1280, 768, 256, 256, 256, 768, 2304, 1280, 768, 1792});
        SeiInfo.put(29, new Integer[] {1280, 768, 256, 256, 256, 1024, 2304, 1280, 1024, 1792});
        SeiInfo.put(30, new Integer[] {1280, 768, 256, 256, 512, 0, 2304, 1280, 0, 2048});
        SeiInfo.put(31, new Integer[] {1280, 768, 256, 256, 512, 256, 2304, 1280, 256, 2048});
        SeiInfo.put(32, new Integer[] {1280, 768, 256, 256, 512, 512, 2304, 1280, 512, 2048});
        SeiInfo.put(33, new Integer[] {1280, 768, 256, 256, 512, 768, 2304, 1280, 768, 2048});
        SeiInfo.put(34, new Integer[] {1280, 768, 256, 256, 512, 1024, 2304, 1280, 1024, 2048});
    }

    private static final String[] VERTEX_SHADER_CODE =
            new String[] {
                    "uniform mat4 uMvpMatrix;",
                    "attribute vec4 aPosition;",
                    "attribute vec2 aTexCoords;",
                    "varying vec2 vTexCoords;",

                    "void main() {",
                    "  gl_Position = uMvpMatrix * aPosition;",
                    "  vTexCoords = aTexCoords;",
                    "}"
            };
    private static final String[] FRAGMENT_SHADER_CODE =
            new String[] {
                    "#extension GL_OES_EGL_image_external : require",
                    "precision mediump float;",

                    "uniform samplerExternalOES uTexture;",
                    "varying vec2 vTexCoords;",
                    "void main() {",
                    "  gl_FragColor = texture2D(uTexture, vTexCoords);",
                    "}"
            };

    private int viewWidth;
    private int viewHeight;
    private int texWidth;
    private int texHeight;

    private int program;
    private int mvpMatrixHandle;
    private int positionHandle;
    private int texCoordsHandle;
    private int textureHandle;
    private int textureId;

    private final boolean useLD = false;

    private final int hdSize = 4 * 5;
    private final int ldSize = 3 * 5;

    private final int[] frameBufferId = new int[1];
    private final int[] frameBufferTextureId = new int[1];
    private final float[] frameBufferMatrix = new float[16];

    private final float[][] hdFrameBufferVertices = new float[hdSize][16];
    private final float[][] hdFrameBufferTexcoords = new float[hdSize][8];
    private FloatBuffer[] hdFrameBufferVertexBuffer = new FloatBuffer[hdSize];
    private FloatBuffer[] hdFrameBufferTexcoordBuffer = new FloatBuffer[hdSize];

    private final float[][] ldFrameBufferVertices = new float[ldSize][16];
    private final float[][] ldFrameBufferTexcoords = new float[ldSize][8];
    private FloatBuffer[] ldFrameBufferVertexBuffer = new FloatBuffer[ldSize];
    private FloatBuffer[] ldFrameBufferTexcoordBuffer = new FloatBuffer[ldSize];

    private FBO(int textureId, int texWidth, int texHeight, int viewWidth, int viewHeight) {
        this.textureId = textureId;

        this.texWidth = texWidth;
        this.texHeight = texHeight;

        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;

        program = Utils.compileProgram(VERTEX_SHADER_CODE, FRAGMENT_SHADER_CODE);

        mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMvpMatrix");
        positionHandle = GLES20.glGetAttribLocation(program, "aPosition");
        texCoordsHandle = GLES20.glGetAttribLocation(program, "aTexCoords");
        textureHandle = GLES20.glGetUniformLocation(program, "uTexture");

        mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMvpMatrix");
        positionHandle = GLES20.glGetAttribLocation(program, "aPosition");
        texCoordsHandle = GLES20.glGetAttribLocation(program, "aTexCoords");
        textureHandle = GLES20.glGetUniformLocation(program, "uTexture");

        GLES20.glGenTextures(1, frameBufferTextureId, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, frameBufferTextureId[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA,
                texWidth, texHeight, 0, GLES20.GL_RGBA,
                GLES20.GL_UNSIGNED_BYTE, null);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        GLES20.glGenFramebuffers(1, frameBufferId, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferId[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, frameBufferTextureId[0], 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        Matrix.orthoM(frameBufferMatrix, 0, -1, 1, -1, 1, -1, 1);

        for(int i = 0; i < hdSize; ++i) {
            int idx = i;

            Integer[] info = SeiInfo.get(idx);

            float dx = info[2].floatValue() / info[0].floatValue();
            float dy = info[3].floatValue() / info[1].floatValue();

            float sx = info[5].floatValue() / info[0].floatValue();
            float sy = info[4].floatValue() / info[1].floatValue();

            float ex = sx + dx;
            float ey = sy + dy;

            sx = (sx - 0.5f) * 2.0f;
            sy = (sy - 0.5f) * 2.0f;

            ex = (ex - 0.5f) * 2.0f;
            ey = (ey - 0.5f) * 2.0f;

            float du = info[2].floatValue() / info[6].floatValue();
            float dv = info[3].floatValue() / info[7].floatValue();

            float su = info[9].floatValue() / info[6].floatValue();
            float sv = info[8].floatValue() / info[7].floatValue();

            float eu = su + du;
            float ev = sv + dv;

            hdFrameBufferVertices[i][0] = sx;
            hdFrameBufferVertices[i][1] = ey;
            hdFrameBufferVertices[i][2] = 0.0f;
            hdFrameBufferVertices[i][3] = 1.0f;

            hdFrameBufferVertices[i][4] = sx;
            hdFrameBufferVertices[i][5] = sy;
            hdFrameBufferVertices[i][6] = 0.0f;
            hdFrameBufferVertices[i][7] = 1.0f;

            hdFrameBufferVertices[i][8] = ex;
            hdFrameBufferVertices[i][9] = ey;
            hdFrameBufferVertices[i][10] = 0.0f;
            hdFrameBufferVertices[i][11] = 1.0f;

            hdFrameBufferVertices[i][12] = ex;
            hdFrameBufferVertices[i][13] = sy;
            hdFrameBufferVertices[i][14] = 0.0f;
            hdFrameBufferVertices[i][15] = 1.0f;

            hdFrameBufferTexcoords[i][0] = su;
            hdFrameBufferTexcoords[i][1] = ev;
            hdFrameBufferTexcoords[i][2] = su;
            hdFrameBufferTexcoords[i][3] = sv;
            hdFrameBufferTexcoords[i][4] = eu;
            hdFrameBufferTexcoords[i][5] = ev;
            hdFrameBufferTexcoords[i][6] = eu;
            hdFrameBufferTexcoords[i][7] = sv;

            hdFrameBufferVertexBuffer[i] = Utils.createBuffer(hdFrameBufferVertices[i]);
            hdFrameBufferTexcoordBuffer[i] = Utils.createBuffer(hdFrameBufferTexcoords[i]);
        }

        for(int i = 0; i < ldSize; ++i) {
            int idx = hdSize + i;

            Integer[] info = SeiInfo.get(idx);

            float dx = info[2].floatValue() / info[0].floatValue();
            float dy = info[3].floatValue() / info[1].floatValue();

            float sx = info[5].floatValue() / info[0].floatValue();
            float sy = info[4].floatValue() / info[1].floatValue();

            float ex = sx + dx;
            float ey = sy + dy;

            sx = (sx - 0.5f) * 2.0f;
            sy = (sy - 0.5f) * 2.0f;

            ex = (ex - 0.5f) * 2.0f;
            ey = (ey - 0.5f) * 2.0f;

            float du = info[2].floatValue() / info[6].floatValue();
            float dv = info[3].floatValue() / info[7].floatValue();

            float su = info[9].floatValue() / info[6].floatValue();
            float sv = info[8].floatValue() / info[7].floatValue();

            float eu = su + du;
            float ev = sv + dv;

            ldFrameBufferVertices[i][0] = sx;;
            ldFrameBufferVertices[i][1] = ey;
            ldFrameBufferVertices[i][2] = 0.0f;
            ldFrameBufferVertices[i][3] = 1.0f;

            ldFrameBufferVertices[i][4] = sx;
            ldFrameBufferVertices[i][5] = sy;
            ldFrameBufferVertices[i][6] = 0.0f;
            ldFrameBufferVertices[i][7] = 1.0f;

            ldFrameBufferVertices[i][8] = ex;
            ldFrameBufferVertices[i][9] = ey;
            ldFrameBufferVertices[i][10] = 0.0f;
            ldFrameBufferVertices[i][11] = 1.0f;

            ldFrameBufferVertices[i][12] = ex;
            ldFrameBufferVertices[i][13] = sy;
            ldFrameBufferVertices[i][14] = 0.0f;
            ldFrameBufferVertices[i][15] = 1.0f;

            ldFrameBufferTexcoords[i][0] = su;
            ldFrameBufferTexcoords[i][1] = ev;
            ldFrameBufferTexcoords[i][2] = su;
            ldFrameBufferTexcoords[i][3] = sv;
            ldFrameBufferTexcoords[i][4] = eu;
            ldFrameBufferTexcoords[i][5] = ev;
            ldFrameBufferTexcoords[i][6] = eu;
            ldFrameBufferTexcoords[i][7] = sv;

            ldFrameBufferVertexBuffer[i] = Utils.createBuffer(ldFrameBufferVertices[i]);
            ldFrameBufferTexcoordBuffer[i] = Utils.createBuffer(ldFrameBufferTexcoords[i]);
        }
    }

    public void destroy() {
        GLES20.glDeleteTextures(1, new int[]{ textureId }, 0);

        GLES20.glDeleteProgram(program);
        GLES20.glDeleteTextures(1, frameBufferTextureId, 0);
        GLES20.glDeleteFramebuffers(1, frameBufferId, 0);
    }

    public void use() {
        GLES20.glUseProgram(program);
        checkGlError();

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferId[0]);

        GLES20.glViewport(0, 0, texWidth, texHeight);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glEnableVertexAttribArray(texCoordsHandle);
        checkGlError();

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, frameBufferMatrix, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
        GLES20.glUniform1i(textureHandle, 0);
        checkGlError();

        if(useLD) {
            for(int i = 0; i < ldSize; ++i) {
                ldFrameBufferVertexBuffer[i].position(0);
                GLES20.glVertexAttribPointer(
                        positionHandle,
                        4,
                        GLES20.GL_FLOAT,
                        false,
                        0,
                        ldFrameBufferVertexBuffer[i]);
                checkGlError();

                ldFrameBufferTexcoordBuffer[i].position(0);
                GLES20.glVertexAttribPointer(
                        texCoordsHandle,
                        2,
                        GLES20.GL_FLOAT,
                        false,
                        0,
                        ldFrameBufferTexcoordBuffer[i]);
                checkGlError();

                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
                checkGlError();
            }
        }
        else {
            for(int i = 0; i < hdSize; ++i) {
                hdFrameBufferVertexBuffer[i].position(0);
                GLES20.glVertexAttribPointer(
                        positionHandle,
                        4,
                        GLES20.GL_FLOAT,
                        false,
                        0,
                        hdFrameBufferVertexBuffer[i]);
                checkGlError();

                hdFrameBufferTexcoordBuffer[i].position(0);
                GLES20.glVertexAttribPointer(
                        texCoordsHandle,
                        2,
                        GLES20.GL_FLOAT,
                        false,
                        0,
                        hdFrameBufferTexcoordBuffer[i]);
                checkGlError();

                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
                checkGlError();
            }
        }

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(texCoordsHandle);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        GLES20.glViewport(0, 0, viewWidth, viewHeight);
    }

    public int texId() {
        return frameBufferTextureId[0];
    }
}
