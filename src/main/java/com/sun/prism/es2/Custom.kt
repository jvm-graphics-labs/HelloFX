package com.sun.prism.es2

import glm_.BYTES
import glm_.b
import glm_.bool
import kool.Ptr
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11C
import org.lwjgl.opengl.GL11C.GL_NO_ERROR
import org.lwjgl.opengl.GL11C.GL_VERSION
import org.lwjgl.opengl.WGL.wglCreateContext
import org.lwjgl.opengl.WGL.wglMakeCurrent
import org.lwjgl.system.APIUtil.apiLog
import org.lwjgl.system.JNI.callI
import org.lwjgl.system.JNI.callP
import org.lwjgl.system.MemoryUtil.*
import org.lwjgl.system.windows.GDI32
import org.lwjgl.system.windows.GDI32.SetPixelFormat
import org.lwjgl.system.windows.WinBase
import uno.glfw.HWND

object Custom {

    object gl {

        val GetError: Ptr
        val GetString: Ptr
        val GetIntegerv: Ptr

        val error: Int
            get() = callI(GetError)

        operator fun get(param: Int) = memUTF8Safe(callP(GetString, param))

        init {
            var error = WinBase.GetLastError()
            val functionProvider = GL.getFunctionProvider()
                    ?: throw IllegalStateException("OpenGL library has not been loaded.")

            // We don't have a current ContextCapabilities when this method is called so we have to use the native bindings directly.
            GetError = functionProvider.getFunctionAddress("glGetError")
            GetString = functionProvider.getFunctionAddress("glGetString")
            GetIntegerv = functionProvider.getFunctionAddress("glGetIntegerv")

            if (GetError == NULL || GetString == NULL || GetIntegerv == NULL) {
                throw IllegalStateException("Core OpenGL functions could not be found. Make sure that the OpenGL library has been loaded correctly.")
            }

            if (error != GL_NO_ERROR)
                apiLog("An OpenGL context was in an error state before the creation of its capabilities instance. Error: 0x%X".format(error))
        }
    }

    init {

        WinGLContext._nInitialize = WinGLContext._nInitialize { nativeDInfo: Ptr, nativePFInfo: Ptr, vSyncRequest: Boolean ->

            if (nativeDInfo == NULL || nativePFInfo == NULL)
                return@_nInitialize NULL

            val dInfo = DrawableInfo(nativeDInfo)
            val pfInfo = PixelFormatInfo(nativePFInfo)

            val hdc = dInfo.hdc
            val pixelFormat = pfInfo.pixelFormat
            if(!SetPixelFormat(hdc, pixelFormat, null)) {
                val error = WinBase.GetLastError()
                System.err.println("Failed in SetPixelFormat")
                return@_nInitialize NULL
            }

            val hglrc: HGLRC = wglCreateContext(hdc)

            if (hglrc == NULL) {
                printAndReleaseResources(hglrc = hglrc, message = "Failed in wglCreateContext")
                return@_nInitialize NULL
            }

            if (!wglMakeCurrent(hdc, hglrc)) {
                printAndReleaseResources(hglrc = hglrc, message = "Failed in wglMakeCurrent")
                return@_nInitialize NULL
            }

            /* Get the OpenGL version */
            val glVersion = gl[GL_VERSION] ?: run {
                printAndReleaseResources(hglrc = hglrc, message = "glVersion == null")
                return@_nInitialize NULL
            }

            NULL
        }
    }

    class DrawableInfo(val ptr: Ptr) {

        var onScreen
            get() = memGetByte(ptr).bool
            set(value) = memPutByte(ptr, value.b)

        var hdc: HDC
            get() = memGetLong(ptr + Byte.BYTES)
            set(value) = memPutLong(ptr + Byte.BYTES, value)

        var hwnd: HWND
            get() = HWND(memGetLong(ptr + Byte.BYTES + Long.BYTES))
            set(value) = memPutLong(ptr + Byte.BYTES + Long.BYTES, value.L)
    }

    class PixelFormatInfo(val ptr: Ptr) {

        var pixelFormat: Int
            get() = memGetInt(ptr)
            set(value) = memPutInt(ptr, value)

        var dummyHwnd: HWND
            get() = HWND(memGetLong(ptr + Int.BYTES))
            set(value) = memPutLong(ptr + Int.BYTES, value.L)

        var dummyHdc: HDC
            get() = memGetLong(ptr + Int.BYTES + Long.BYTES)
            set(value) = memPutLong(ptr + Int.BYTES + Long.BYTES, value)

        var dummySzAppName: String
            get() = TODO()
            set(value) = TODO()
    }
}

typealias HDC = Ptr
typealias HGLRC = Ptr