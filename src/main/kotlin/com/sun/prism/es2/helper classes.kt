package com.sun.prism.es2

import com.sun.*
import glm_.BYTES
import glm_.b
import glm_.bool
import kool.Buffer
import kool.Ptr
import kool.adr
import kool.lib.fill
import kool.set
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11C.*
import org.lwjgl.system.MemoryUtil.*
import uno.awt.Display
import uno.glfw.HWND
import java.nio.ByteBuffer
import java.nio.FloatBuffer

class WinContextInfo(val buffer: ByteBuffer) {

    constructor() : this(Buffer(530))

    var hglrc: HGLRC
        get() = buffer.getLong(0)
        set(value) {
            buffer.putLong(0, value)
        }

    var version: String?
        get() = memUTF8Safe(adr + Long.BYTES)
        set(value) {
            buffer.putLong(Long.BYTES, memUTF8Safe(value)?.adr ?: NULL)
        }

    var vendor: String?
        get() = memUTF8Safe(adr + Long.BYTES * 2)
        set(value) {
            buffer.putLong(Long.BYTES * 2, memUTF8Safe(value)?.adr ?: NULL)
        }

    var renderer: String?
        get() = memUTF8Safe(adr + Long.BYTES * 3)
        set(value) {
            buffer.putLong(Long.BYTES * 3, memUTF8Safe(value)?.adr ?: NULL)
        }

    var glExtensions: String?
        get() = memUTF8Safe(adr + Long.BYTES * 4)
        set(value) {
            buffer.putLong(Long.BYTES * 4, memUTF8Safe(value)?.adr ?: NULL)
        }

    var major: Int
        get() = buffer.getInt(Long.BYTES * 5)
        set(value) {
            buffer.putInt(Long.BYTES * 5, value)
        }

    var minor: Int
        get() = buffer.getInt(Long.BYTES * 5 + Int.BYTES)
        set(value) {
            buffer.putInt(Long.BYTES * 5 + Int.BYTES, value)
        }

    var wglExtensions: String?
        get() = memUTF8Safe(adr + Long.BYTES * 5 + Int.BYTES * 2)
        set(value) {
            buffer.putLong(Long.BYTES * 5 + Int.BYTES * 2, memUTF8Safe(value)?.adr ?: NULL)
        }

    var wglSwapIntervalEXT: Ptr
        get() = buffer.getLong(Long.BYTES * 7)
        set(value) {
            buffer.putLong(Long.BYTES * 7, value)
        }

    // gl function pointers
    var glActiveTexture: Ptr
        get() = buffer.getLong(Long.BYTES * 8)
        set(value) {
            buffer.putLong(Long.BYTES * 8, value)
        }
    var glAttachShader: Ptr
        get() = buffer.getLong(Long.BYTES * 9)
        set(value) {
            buffer.putLong(Long.BYTES * 9, value)
        }
    var glBindAttribLocation: Ptr
        get() = buffer.getLong(Long.BYTES * 10)
        set(value) {
            buffer.putLong(Long.BYTES * 10, value)
        }
    var glBindFramebuffer: Ptr
        get() = buffer.getLong(Long.BYTES * 11)
        set(value) {
            buffer.putLong(Long.BYTES * 11, value)
        }
    var glBindRenderbuffer: Ptr
        get() = buffer.getLong(Long.BYTES * 12)
        set(value) {
            buffer.putLong(Long.BYTES * 12, value)
        }
    var glCheckFramebufferStatus: Ptr
        get() = buffer.getLong(Long.BYTES * 13)
        set(value) {
            buffer.putLong(Long.BYTES * 13, value)
        }
    var glCompileShader: Ptr
        get() = buffer.getLong(Long.BYTES * 14)
        set(value) {
            buffer.putLong(Long.BYTES * 14, value)
        }
    var glCreateProgram: Ptr
        get() = buffer.getLong(Long.BYTES * 15)
        set(value) {
            buffer.putLong(Long.BYTES * 15, value)
        }
    var glCreateShader: Ptr
        get() = buffer.getLong(Long.BYTES * 16)
        set(value) {
            buffer.putLong(Long.BYTES * 16, value)
        }
    var glDeleteBuffers: Ptr
        get() = buffer.getLong(Long.BYTES * 17)
        set(value) {
            buffer.putLong(Long.BYTES * 17, value)
        }
    var glDeleteFramebuffers: Ptr
        get() = buffer.getLong(Long.BYTES * 18)
        set(value) {
            buffer.putLong(Long.BYTES * 18, value)
        }
    var glDeleteProgram: Ptr
        get() = buffer.getLong(Long.BYTES * 19)
        set(value) {
            buffer.putLong(Long.BYTES * 19, value)
        }
    var glDeleteShader: Ptr
        get() = buffer.getLong(Long.BYTES * 20)
        set(value) {
            buffer.putLong(Long.BYTES * 20, value)
        }
    var glDeleteRenderbuffers: Ptr
        get() = buffer.getLong(Long.BYTES * 21)
        set(value) {
            buffer.putLong(Long.BYTES * 21, value)
        }
    var glDetachShader: Ptr
        get() = buffer.getLong(Long.BYTES * 22)
        set(value) {
            buffer.putLong(Long.BYTES * 22, value)
        }
    var glDisableVertexAttribArray: Ptr
        get() = buffer.getLong(Long.BYTES * 23)
        set(value) {
            buffer.putLong(Long.BYTES * 23, value)
        }
    var glEnableVertexAttribArray: Ptr
        get() = buffer.getLong(Long.BYTES * 24)
        set(value) {
            buffer.putLong(Long.BYTES * 24, value)
        }
    var glFramebufferRenderbuffer: Ptr
        get() = buffer.getLong(Long.BYTES * 25)
        set(value) {
            buffer.putLong(Long.BYTES * 25, value)
        }
    var glFramebufferTexture2D: Ptr
        get() = buffer.getLong(Long.BYTES * 26)
        set(value) {
            buffer.putLong(Long.BYTES * 26, value)
        }
    var glGenFramebuffers: Ptr
        get() = buffer.getLong(Long.BYTES * 27)
        set(value) {
            buffer.putLong(Long.BYTES * 27, value)
        }
    var glGenRenderbuffers: Ptr
        get() = buffer.getLong(Long.BYTES * 28)
        set(value) {
            buffer.putLong(Long.BYTES * 28, value)
        }
    var glGetProgramiv: Ptr
        get() = buffer.getLong(Long.BYTES * 29)
        set(value) {
            buffer.putLong(Long.BYTES * 29, value)
        }
    var glGetShaderiv: Ptr
        get() = buffer.getLong(Long.BYTES * 30)
        set(value) {
            buffer.putLong(Long.BYTES * 30, value)
        }
    var glGetUniformLocation: Ptr
        get() = buffer.getLong(Long.BYTES * 31)
        set(value) {
            buffer.putLong(Long.BYTES * 31, value)
        }
    var glLinkProgram: Ptr
        get() = buffer.getLong(Long.BYTES * 32)
        set(value) {
            buffer.putLong(Long.BYTES * 32, value)
        }
    var glRenderbufferStorage: Ptr
        get() = buffer.getLong(Long.BYTES * 33)
        set(value) {
            buffer.putLong(Long.BYTES * 33, value)
        }
    var glShaderSource: Ptr
        get() = buffer.getLong(Long.BYTES * 34)
        set(value) {
            buffer.putLong(Long.BYTES * 34, value)
        }
    var glGetShaderInfoLog: Ptr
        get() = buffer.getLong(Long.BYTES * 35)
        set(value) {
            buffer.putLong(Long.BYTES * 35, value)
        }
    var glGetProgramInfoLog: Ptr
        get() = buffer.getLong(Long.BYTES * 36)
        set(value) {
            buffer.putLong(Long.BYTES * 36, value)
        }
    var glBufferSubData: Ptr
        get() = buffer.getLong(Long.BYTES * 37)
        set(value) {
            buffer.putLong(Long.BYTES * 37, value)
        }
    var glUniform1f: Ptr
        get() = buffer.getLong(Long.BYTES * 38)
        set(value) {
            buffer.putLong(Long.BYTES * 38, value)
        }
    var glUniform2f: Ptr
        get() = buffer.getLong(Long.BYTES * 39)
        set(value) {
            buffer.putLong(Long.BYTES * 39, value)
        }
    var glUniform3f: Ptr
        get() = buffer.getLong(Long.BYTES * 40)
        set(value) {
            buffer.putLong(Long.BYTES * 40, value)
        }
    var glUniform4f: Ptr
        get() = buffer.getLong(Long.BYTES * 41)
        set(value) {
            buffer.putLong(Long.BYTES * 41, value)
        }
    var glUniform4fv: Ptr
        get() = buffer.getLong(Long.BYTES * 42)
        set(value) {
            buffer.putLong(Long.BYTES * 42, value)
        }
    var glUniform1i: Ptr
        get() = buffer.getLong(Long.BYTES * 43)
        set(value) {
            buffer.putLong(Long.BYTES * 43, value)
        }
    var glUniform2i: Ptr
        get() = buffer.getLong(Long.BYTES * 44)
        set(value) {
            buffer.putLong(Long.BYTES * 44, value)
        }
    var glUniform3i: Ptr
        get() = buffer.getLong(Long.BYTES * 45)
        set(value) {
            buffer.putLong(Long.BYTES * 45, value)
        }
    var glUniform4i: Ptr
        get() = buffer.getLong(Long.BYTES * 46)
        set(value) {
            buffer.putLong(Long.BYTES * 46, value)
        }
    var glUniform4iv: Ptr
        get() = buffer.getLong(Long.BYTES * 47)
        set(value) {
            buffer.putLong(Long.BYTES * 47, value)
        }
    var glUniformMatrix4fv: Ptr
        get() = buffer.getLong(Long.BYTES * 48)
        set(value) {
            buffer.putLong(Long.BYTES * 48, value)
        }
    var glUseProgram: Ptr
        get() = buffer.getLong(Long.BYTES * 49)
        set(value) {
            buffer.putLong(Long.BYTES * 49, value)
        }
    var glValidateProgram: Ptr
        get() = buffer.getLong(Long.BYTES * 50)
        set(value) {
            buffer.putLong(Long.BYTES * 50, value)
        }
    var glVertexAttribPointer: Ptr
        get() = buffer.getLong(Long.BYTES * 51)
        set(value) {
            buffer.putLong(Long.BYTES * 51, value)
        }

    var glGenBuffers: Ptr
        get() = buffer.getLong(Long.BYTES * 52)
        set(value) {
            buffer.putLong(Long.BYTES * 52, value)
        }
    var glBindBuffer: Ptr
        get() = buffer.getLong(Long.BYTES * 53)
        set(value) {
            buffer.putLong(Long.BYTES * 53, value)
        }
    var glBufferData: Ptr
        get() = buffer.getLong(Long.BYTES * 54)
        set(value) {
            buffer.putLong(Long.BYTES * 54, value)
        }
    var glTexImage2DMultisample: Ptr
        get() = buffer.getLong(Long.BYTES * 55)
        set(value) {
            buffer.putLong(Long.BYTES * 55, value)
        }
    var glRenderbufferStorageMultisample: Ptr
        get() = buffer.getLong(Long.BYTES * 56)
        set(value) {
            buffer.putLong(Long.BYTES * 56, value)
        }
    var glBlitFramebuffer: Ptr
        get() = buffer.getLong(Long.BYTES * 57)
        set(value) {
            buffer.putLong(Long.BYTES * 57, value)
        }

    /* For state caching */
    val state = StateInfo(adr + Long.BYTES * 58) // 40 Bytes

    /* this pointers represent cached values of glVertexAttribPointer values */
    /* they should be properly updated in case of glVertexAttribPointer call */
    /* see setVertexAttributePointers */
//    float *vbFloatData;
//    char  *vbByteData;

    var gl2: Boolean
        get() = buffer.get(520).bool
        set(value) {
            buffer.put(520, value.b)
        }
    var vSyncRequested: Boolean
        get() = buffer.get(528).bool
        set(value) {
            buffer.put(528, value.b)
        }

    val adr get() = buffer.adr

    fun init() {

        GL.createCapabilities()

        glEnable(GL_BLEND)
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA)

        // initialize states and properties to match cached states and properties

        // depthtest is set to false
        // Note: This state is cached in GLContext.java
        state.apply {
            depthWritesEnabled = false
            glDepthMask(depthWritesEnabled)
            glDisable(GL_DEPTH_TEST)

            if (scissorEnabled) {
                scissorEnabled = false
                glDisable(GL_SCISSOR_TEST)
            }

            clearColor.fill(0f)
            glClearColor(clearColor[0], clearColor[1], clearColor[2], clearColor[3])

//        ctxInfo->vbFloatData = NULL
//        ctxInfo->vbByteData = NULL
            fillMode = GL_FILL
            cullEnable = false
            cullMode = GL_BACK
            fbo = 0
        }
    }
}

class X11ContextInfo(val buffer: ByteBuffer) {

    constructor() : this(Buffer(530))

    var display: Display
        get() = buffer.getLong(0)
        set(value) {
            buffer.putLong(0, value)
        }

    var context: GLXContext
        get() = buffer.getLong(Long.BYTES)
        set(value) {
            buffer.putLong(Long.BYTES, value)
        }

    var screen: Int
        get() = buffer.getInt(Long.BYTES * 2)
        set(value) {
            buffer.putInt(Long.BYTES * 2, value)
        }

    var visualID: Int
        get() = buffer.getInt(Long.BYTES * 2 + Int.BYTES)
        set(value) {
            buffer.putInt(Long.BYTES * 2 + Int.BYTES, value)
        }

    var version: String?
        get() = memUTF8Safe(adr + Long.BYTES * 3)
        set(value) {
            buffer.putLong(Long.BYTES * 3, memUTF8Safe(value)?.adr ?: NULL)
        }

    var vendor: String?
        get() = memUTF8Safe(adr + Long.BYTES * 4)
        set(value) {
            buffer.putLong(Long.BYTES * 4, memUTF8Safe(value)?.adr ?: NULL)
        }

    var renderer: String?
        get() = memUTF8Safe(adr + Long.BYTES * 5)
        set(value) {
            buffer.putLong(Long.BYTES * 5, memUTF8Safe(value)?.adr ?: NULL)
        }

    var glExtensions: String?
        get() = memUTF8Safe(adr + Long.BYTES * 6)
        set(value) {
            buffer.putLong(Long.BYTES * 6, memUTF8Safe(value)?.adr ?: NULL)
        }

    var major: Int
        get() = buffer.getInt(Long.BYTES * 7)
        set(value) {
            buffer.putInt(Long.BYTES * 7, value)
        }

    var minor: Int
        get() = buffer.getInt(Long.BYTES * 7 + Int.BYTES)
        set(value) {
            buffer.putInt(Long.BYTES * 7 + Int.BYTES, value)
        }

    var glxExtensions: String?
        get() = memUTF8Safe(adr + Long.BYTES * 7 + Int.BYTES * 2)
        set(value) {
            buffer.putLong(Long.BYTES * 7 + Int.BYTES * 2, memUTF8Safe(value)?.adr ?: NULL)
        }

    var glXSwapIntervalSGI: Ptr
        get() = buffer.getLong(Long.BYTES * 9)
        set(value) {
            buffer.putLong(Long.BYTES * 9, value)
        }

    // gl function pointers
    var glActiveTexture: Ptr
        get() = buffer.getLong(Long.BYTES * 10)
        set(value) {
            buffer.putLong(Long.BYTES * 10, value)
        }
    var glAttachShader: Ptr
        get() = buffer.getLong(Long.BYTES * 11)
        set(value) {
            buffer.putLong(Long.BYTES * 11, value)
        }
    var glBindAttribLocation: Ptr
        get() = buffer.getLong(Long.BYTES * 12)
        set(value) {
            buffer.putLong(Long.BYTES * 12, value)
        }
    var glBindFramebuffer: Ptr
        get() = buffer.getLong(Long.BYTES * 13)
        set(value) {
            buffer.putLong(Long.BYTES * 13, value)
        }
    var glBindRenderbuffer: Ptr
        get() = buffer.getLong(Long.BYTES * 14)
        set(value) {
            buffer.putLong(Long.BYTES * 14, value)
        }
    var glCheckFramebufferStatus: Ptr
        get() = buffer.getLong(Long.BYTES * 15)
        set(value) {
            buffer.putLong(Long.BYTES * 15, value)
        }
    var glCompileShader: Ptr
        get() = buffer.getLong(Long.BYTES * 16)
        set(value) {
            buffer.putLong(Long.BYTES * 16, value)
        }
    var glCreateProgram: Ptr
        get() = buffer.getLong(Long.BYTES * 17)
        set(value) {
            buffer.putLong(Long.BYTES * 17, value)
        }
    var glCreateShader: Ptr
        get() = buffer.getLong(Long.BYTES * 18)
        set(value) {
            buffer.putLong(Long.BYTES * 18, value)
        }
    var glDeleteBuffers: Ptr
        get() = buffer.getLong(Long.BYTES * 19)
        set(value) {
            buffer.putLong(Long.BYTES * 19, value)
        }
    var glDeleteFramebuffers: Ptr
        get() = buffer.getLong(Long.BYTES * 20)
        set(value) {
            buffer.putLong(Long.BYTES * 20, value)
        }
    var glDeleteProgram: Ptr
        get() = buffer.getLong(Long.BYTES * 21)
        set(value) {
            buffer.putLong(Long.BYTES * 21, value)
        }
    var glDeleteShader: Ptr
        get() = buffer.getLong(Long.BYTES * 22)
        set(value) {
            buffer.putLong(Long.BYTES * 22, value)
        }
    var glDeleteRenderbuffers: Ptr
        get() = buffer.getLong(Long.BYTES * 23)
        set(value) {
            buffer.putLong(Long.BYTES * 23, value)
        }
    var glDetachShader: Ptr
        get() = buffer.getLong(Long.BYTES * 24)
        set(value) {
            buffer.putLong(Long.BYTES * 24, value)
        }
    var glDisableVertexAttribArray: Ptr
        get() = buffer.getLong(Long.BYTES * 25)
        set(value) {
            buffer.putLong(Long.BYTES * 25, value)
        }
    var glEnableVertexAttribArray: Ptr
        get() = buffer.getLong(Long.BYTES * 26)
        set(value) {
            buffer.putLong(Long.BYTES * 26, value)
        }
    var glFramebufferRenderbuffer: Ptr
        get() = buffer.getLong(Long.BYTES * 27)
        set(value) {
            buffer.putLong(Long.BYTES * 27, value)
        }
    var glFramebufferTexture2D: Ptr
        get() = buffer.getLong(Long.BYTES * 28)
        set(value) {
            buffer.putLong(Long.BYTES * 28, value)
        }
    var glGenFramebuffers: Ptr
        get() = buffer.getLong(Long.BYTES * 29)
        set(value) {
            buffer.putLong(Long.BYTES * 29, value)
        }
    var glGenRenderbuffers: Ptr
        get() = buffer.getLong(Long.BYTES * 30)
        set(value) {
            buffer.putLong(Long.BYTES * 30, value)
        }
    var glGetProgramiv: Ptr
        get() = buffer.getLong(Long.BYTES * 31)
        set(value) {
            buffer.putLong(Long.BYTES * 31, value)
        }
    var glGetShaderiv: Ptr
        get() = buffer.getLong(Long.BYTES * 32)
        set(value) {
            buffer.putLong(Long.BYTES * 32, value)
        }
    var glGetUniformLocation: Ptr
        get() = buffer.getLong(Long.BYTES * 33)
        set(value) {
            buffer.putLong(Long.BYTES * 33, value)
        }
    var glLinkProgram: Ptr
        get() = buffer.getLong(Long.BYTES * 34)
        set(value) {
            buffer.putLong(Long.BYTES * 34, value)
        }
    var glRenderbufferStorage: Ptr
        get() = buffer.getLong(Long.BYTES * 35)
        set(value) {
            buffer.putLong(Long.BYTES * 35, value)
        }
    var glShaderSource: Ptr
        get() = buffer.getLong(Long.BYTES * 36)
        set(value) {
            buffer.putLong(Long.BYTES * 36, value)
        }
    var glGetShaderInfoLog: Ptr
        get() = buffer.getLong(Long.BYTES * 37)
        set(value) {
            buffer.putLong(Long.BYTES * 37, value)
        }
    var glGetProgramInfoLog: Ptr
        get() = buffer.getLong(Long.BYTES * 38)
        set(value) {
            buffer.putLong(Long.BYTES * 38, value)
        }
    var glBufferSubData: Ptr
        get() = buffer.getLong(Long.BYTES * 39)
        set(value) {
            buffer.putLong(Long.BYTES * 39, value)
        }
    var glUniform1f: Ptr
        get() = buffer.getLong(Long.BYTES * 40)
        set(value) {
            buffer.putLong(Long.BYTES * 40, value)
        }
    var glUniform2f: Ptr
        get() = buffer.getLong(Long.BYTES * 41)
        set(value) {
            buffer.putLong(Long.BYTES * 41, value)
        }
    var glUniform3f: Ptr
        get() = buffer.getLong(Long.BYTES * 42)
        set(value) {
            buffer.putLong(Long.BYTES * 42, value)
        }
    var glUniform4f: Ptr
        get() = buffer.getLong(Long.BYTES * 43)
        set(value) {
            buffer.putLong(Long.BYTES * 43, value)
        }
    var glUniform4fv: Ptr
        get() = buffer.getLong(Long.BYTES * 44)
        set(value) {
            buffer.putLong(Long.BYTES * 44, value)
        }
    var glUniform1i: Ptr
        get() = buffer.getLong(Long.BYTES * 45)
        set(value) {
            buffer.putLong(Long.BYTES * 45, value)
        }
    var glUniform2i: Ptr
        get() = buffer.getLong(Long.BYTES * 46)
        set(value) {
            buffer.putLong(Long.BYTES * 46, value)
        }
    var glUniform3i: Ptr
        get() = buffer.getLong(Long.BYTES * 47)
        set(value) {
            buffer.putLong(Long.BYTES * 47, value)
        }
    var glUniform4i: Ptr
        get() = buffer.getLong(Long.BYTES * 48)
        set(value) {
            buffer.putLong(Long.BYTES * 48, value)
        }
    var glUniform4iv: Ptr
        get() = buffer.getLong(Long.BYTES * 49)
        set(value) {
            buffer.putLong(Long.BYTES * 49, value)
        }
    var glUniformMatrix4fv: Ptr
        get() = buffer.getLong(Long.BYTES * 50)
        set(value) {
            buffer.putLong(Long.BYTES * 50, value)
        }
    var glUseProgram: Ptr
        get() = buffer.getLong(Long.BYTES * 51)
        set(value) {
            buffer.putLong(Long.BYTES * 51, value)
        }
    var glValidateProgram: Ptr
        get() = buffer.getLong(Long.BYTES * 52)
        set(value) {
            buffer.putLong(Long.BYTES * 52, value)
        }
    var glVertexAttribPointer: Ptr
        get() = buffer.getLong(Long.BYTES * 53)
        set(value) {
            buffer.putLong(Long.BYTES * 53, value)
        }

    var glGenBuffers: Ptr
        get() = buffer.getLong(Long.BYTES * 54)
        set(value) {
            buffer.putLong(Long.BYTES * 54, value)
        }
    var glBindBuffer: Ptr
        get() = buffer.getLong(Long.BYTES * 55)
        set(value) {
            buffer.putLong(Long.BYTES * 55, value)
        }
    var glBufferData: Ptr
        get() = buffer.getLong(Long.BYTES * 56)
        set(value) {
            buffer.putLong(Long.BYTES * 56, value)
        }
    var glTexImage2DMultisample: Ptr
        get() = buffer.getLong(Long.BYTES * 57)
        set(value) {
            buffer.putLong(Long.BYTES * 57, value)
        }
    var glRenderbufferStorageMultisample: Ptr
        get() = buffer.getLong(Long.BYTES * 58)
        set(value) {
            buffer.putLong(Long.BYTES * 58, value)
        }
    var glBlitFramebuffer: Ptr
        get() = buffer.getLong(Long.BYTES * 59)
        set(value) {
            buffer.putLong(Long.BYTES * 59, value)
        }

    /* For state caching */
    val state = StateInfo(adr + Long.BYTES * 60) // 40 Bytes

    /* this pointers represent cached values of glVertexAttribPointer values */
    /* they should be properly updated in case of glVertexAttribPointer call */
    /* see setVertexAttributePointers */
//    float *vbFloatData;
//    char  *vbByteData;

    var gl2: Boolean
        get() = buffer.get(536).bool
        set(value) {
            buffer.put(536, value.b)
        }
    var vSyncRequested: Boolean
        get() = buffer.get(544).bool
        set(value) {
            buffer.put(544, value.b)
        }

    val adr get() = buffer.adr

    fun init() {

        GL.createCapabilities()

        glEnable(GL_BLEND)
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA)

        // initialize states and properties to match cached states and properties

        // depthtest is set to false
        // Note: This state is cached in GLContext.java
        state.apply {
            depthWritesEnabled = false
            glDepthMask(depthWritesEnabled)
            glDisable(GL_DEPTH_TEST)

            if (scissorEnabled) {
                scissorEnabled = false
                glDisable(GL_SCISSOR_TEST)
            }

            clearColor.fill(0f)
            glClearColor(clearColor[0], clearColor[1], clearColor[2], clearColor[3])

//        ctxInfo->vbFloatData = NULL
//        ctxInfo->vbByteData = NULL
            fillMode = GL_FILL
            cullEnable = false
            cullMode = GL_BACK
            fbo = 0
        }
    }
}

class StateInfo(val ptr: Ptr) {

    var depthWritesEnabled: Boolean
        get() = memGetBoolean(ptr)
        set(value) = memPutByte(ptr, value.b)

    var scissorEnabled: Boolean
        get() = memGetBoolean(ptr + Byte.BYTES)
        set(value) = memPutByte(ptr + Byte.BYTES, value.b)

    var clearColor: FloatBuffer
        get() = memFloatBuffer(ptr + Float.BYTES, 4)
        set(value) {
            for (i in 0..3)
                memPutFloat(ptr + Float.BYTES * (1 + i), value[i])
        }

    var vSyncEnabled: Boolean
        get() = memGetBoolean(ptr + Float.BYTES * 5)
        set(value) = memPutByte(ptr + Float.BYTES * 5, value.b)

    var cullEnable: Boolean
        get() = memGetBoolean(ptr + Float.BYTES * 5 + Byte.BYTES)
        set(value) = memPutByte(ptr + Float.BYTES * 5 + Byte.BYTES, value.b)

    var cullMode: Int
        get() = memGetInt(ptr + Float.BYTES * 6)
        set(value) = memPutInt(ptr + Float.BYTES * 6, value)

    var fillMode: Int
        get() = memGetInt(ptr + Float.BYTES * 7)
        set(value) = memPutInt(ptr + Float.BYTES * 7, value)

    var fbo: Int
        get() = memGetInt(ptr + Float.BYTES * 8)
        set(value) = memPutInt(ptr + Float.BYTES * 8, value)
}

class DrawableInfo(val ptr: Ptr) {

    var onScreen
        get() = memGetByte(ptr).bool
        set(value) = memPutByte(ptr, value.b)

    // win

    var hdc: HDC
        get() = memGetLong(ptr + Long.BYTES)
        set(value) = memPutLong(ptr + Long.BYTES, value)

    var hwnd: HWND
        get() = HWND(memGetLong(ptr + Long.BYTES * 2))
        set(value) = memPutLong(ptr + Long.BYTES * 2, value.L)

    // linux

    var display: Display
        get() = memGetLong(ptr + Long.BYTES)
        set(value) = memPutLong(ptr + Long.BYTES, value)

    var win: Window
        get() = memGetLong(ptr + Long.BYTES * 2)
        set(value) = memPutLong(ptr + Long.BYTES * 2, value)
}

class PixelFormatInfo(val buffer: ByteBuffer) {

    constructor() : this(Buffer(size))
    constructor(ptr: Ptr) : this(memByteBuffer(ptr, size))

    // win

    var pixelFormat: Int
        get() = buffer.getInt(0)
        set(value) {
            buffer.putInt(0, value)
        }

    var dummyHwnd: HWND
        get() = HWND(buffer.getLong(Long.BYTES))
        set(value) {
            buffer.putLong(Long.BYTES, value.L)
        }

    var dummyHdc: HDC
        get() = buffer.getLong(Long.BYTES * 2)
        set(value) {
            buffer.putLong(Long.BYTES * 2, value)
        }

    var dummySzAppName: String
        get() = memUTF8(adr + Long.BYTES * 3)
        set(value) {
            buffer.putLong(Long.BYTES * 3, memUTF8(value).adr)
        }

    // linux

    var display: Display
        get() = buffer.getLong(0)
        set(value) {
            buffer.putLong(0, value)
        }

    var fbConfig: GLXFBConfig
        get() = buffer.getLong(Long.BYTES)
        set(value) {
            buffer.putLong(Long.BYTES, value)
        }

    var dummyWin: Window
        get() = buffer.getLong(Long.BYTES * 2)
        set(value) {
            buffer.putLong(Long.BYTES * 2, value)
        }

    var dummyCmap: Colormap
        get() = buffer.getLong(Long.BYTES * 3)
        set(value) {
            buffer.putLong(Long.BYTES * 3, value)
        }


    val adr get() = buffer.adr

    companion object {
        val size = 32
    }
}

class XSetWindowAttributes()