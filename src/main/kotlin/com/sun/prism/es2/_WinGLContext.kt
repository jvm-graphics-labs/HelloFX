package com.sun.prism.es2

import kool.Adr
import org.lwjgl.opengl.GL11C.*
import org.lwjgl.opengl.WGL.*
import org.lwjgl.system.JNI.callJ
import org.lwjgl.system.JNI.callPP
import org.lwjgl.system.MemoryUtil.NULL
import org.lwjgl.system.MemoryUtil.memASCII
import org.lwjgl.system.windows.GDI32.SetPixelFormat
import org.lwjgl.system.windows.WinBase
import uno.kotlin.parseInt

object _WinGLContext {

    init {

        WinGLContext.nInitialize = WinGLContext.nInitialize { nativeDInfo, nativePFInfo, vSyncRequest ->

            if (nativeDInfo == NULL || nativePFInfo == NULL)
                return@nInitialize NULL

            val dInfo = DrawableInfo(nativeDInfo)
            val pfInfo = PixelFormatInfo(nativePFInfo)

            val hdc = dInfo.hdc
            val pixelFormat = pfInfo.pixelFormat

            if (!SetPixelFormat(hdc, pixelFormat, null)) {
                System.err.println("Failed in SetPixelFormat")
                return@nInitialize NULL
            }

            val hglrc = wglCreateContext(hdc)
            if (hglrc == NULL) {
                printAndReleaseResources(hglrc = hglrc, message = "Failed in wglCreateContext")
                return@nInitialize NULL
            }

            if (!wglMakeCurrent(hdc, hglrc)) {
                printAndReleaseResources(hglrc = hglrc, message = "Failed in wglMakeCurrent")
                return@nInitialize NULL
            }

            // Get the OpenGL version
            val glVersion = gl[GL_VERSION] ?: run {
                printAndReleaseResources(hglrc = hglrc, message = "glVersion == null")
                return@nInitialize NULL
            }

            // find out the version, major and minor version number
            val major = glVersion[0].parseInt()
            val minor = glVersion[2].parseInt()

            /*
             * Supported Cards: Intel HD Graphics, Intel HD Graphics 2000/3000,
             * Radeon HD 2350, GeForce FX (with newer drivers), GeForce 6 series or higher
             *
             * Check for OpenGL 2.0 or later.
             */
            if (major < 2) {
                System.err.println("GL_VERSION (major.minor) = $major.$minor")
                printAndReleaseResources(hglrc = hglrc)
                return@nInitialize NULL
            }

            // Get the OpenGL vendor and renderer
            val glVendor = gl[GL_VENDOR] ?: "<UNKNOWN>"
            val glRenderer = gl[GL_RENDERER] ?: "<UNKNOWN>"

            val glExtensions = gl[GL_EXTENSIONS] ?: run {
                printAndReleaseResources(hglrc = hglrc, message = "glExtensions == null")
                return@nInitialize NULL
            }

            // We use GL 2.0 and GL_ARB_pixel_buffer_object as an guide to determine PS 3.0 capable.
            if ("GL_ARB_pixel_buffer_object" !in glExtensions) {
                printAndReleaseResources(hglrc = hglrc, message = "GL profile isn't PS 3.0 capable")
                return@nInitialize NULL
            }

            val wglGetExtensionsStringARB_Adr: Adr = wglGetProcAddress("wglGetExtensionsStringARB")
            if (wglGetExtensionsStringARB_Adr == NULL) {
                printAndReleaseResources(hglrc = hglrc, message = "wglGetExtensionsStringARB is not supported!")
                return@nInitialize NULL
            }
            val wglExtensions: String = when (val str = callPP(wglGetExtensionsStringARB_Adr, hdc)) {
                NULL -> return@nInitialize NULL.also {
                    printAndReleaseResources(hglrc = hglrc, message = "wglExtensions == null")
                }
                else -> memASCII(str)
            }

            /*
                fprintf(stderr, "glExtensions: %s\n", glExtensions);
                fprintf(stderr, "wglExtensions: %s\n", wglExtensions);
             */

            /* allocate the structure */
            val ctxInfo = WinContextInfo().apply {

                /* initialize the structure */
                version = glVersion
                vendor = glVendor
                renderer = glRenderer
                this.glExtensions = glExtensions
                this.wglExtensions = wglExtensions
                this.major = major
                this.minor = minor
                this.hglrc = hglrc

                // set function pointers
                glActiveTexture = wglGetProcAddress("glActiveTexture");
                glAttachShader = wglGetProcAddress("glAttachShader");
                glBindAttribLocation = wglGetProcAddress("glBindAttribLocation");
                glBindFramebuffer = wglGetProcAddress("glBindFramebuffer");
                glBindRenderbuffer = wglGetProcAddress("glBindRenderbuffer");
                glCheckFramebufferStatus = wglGetProcAddress("glCheckFramebufferStatus");
                glCreateProgram = wglGetProcAddress("glCreateProgram");
                glCreateShader = wglGetProcAddress("glCreateShader");
                glCompileShader = wglGetProcAddress("glCompileShader");
                glDeleteBuffers = wglGetProcAddress("glDeleteBuffers");
                glDeleteFramebuffers = wglGetProcAddress("glDeleteFramebuffers");
                glDeleteProgram = wglGetProcAddress("glDeleteProgram");
                glDeleteRenderbuffers = wglGetProcAddress("glDeleteRenderbuffers");
                glDeleteShader = wglGetProcAddress("glDeleteShader");
                glDetachShader = wglGetProcAddress("glDetachShader");
                glDisableVertexAttribArray = wglGetProcAddress("glDisableVertexAttribArray");
                glEnableVertexAttribArray = wglGetProcAddress("glEnableVertexAttribArray");
                glFramebufferRenderbuffer = wglGetProcAddress("glFramebufferRenderbuffer");
                glFramebufferTexture2D = wglGetProcAddress("glFramebufferTexture2D");
                glGenFramebuffers = wglGetProcAddress("glGenFramebuffers");
                glGenRenderbuffers = wglGetProcAddress("glGenRenderbuffers");
                glGetProgramiv = wglGetProcAddress("glGetProgramiv");
                glGetShaderiv = wglGetProcAddress("glGetShaderiv");
                glGetUniformLocation = wglGetProcAddress("glGetUniformLocation");
                glLinkProgram = wglGetProcAddress("glLinkProgram");
                glRenderbufferStorage = wglGetProcAddress("glRenderbufferStorage");
                glShaderSource = wglGetProcAddress("glShaderSource");
                glUniform1f = wglGetProcAddress("glUniform1f");
                glUniform2f = wglGetProcAddress("glUniform2f");
                glUniform3f = wglGetProcAddress("glUniform3f");
                glUniform4f = wglGetProcAddress("glUniform4f");
                glUniform4fv = wglGetProcAddress("glUniform4fv");
                glUniform1i = wglGetProcAddress("glUniform1i");
                glUniform2i = wglGetProcAddress("glUniform2i");
                glUniform3i = wglGetProcAddress("glUniform3i");
                glUniform4i = wglGetProcAddress("glUniform4i");
                glUniform4iv = wglGetProcAddress("glUniform4iv");
                glUniformMatrix4fv = wglGetProcAddress("glUniformMatrix4fv");
                glUseProgram = wglGetProcAddress("glUseProgram");
                glValidateProgram = wglGetProcAddress("glValidateProgram");
                glVertexAttribPointer = wglGetProcAddress("glVertexAttribPointer");
                glGenBuffers = wglGetProcAddress("glGenBuffers");
                glBindBuffer = wglGetProcAddress("glBindBuffer");
                glBufferData = wglGetProcAddress("glBufferData");
                glBufferSubData = wglGetProcAddress("glBufferSubData");
                glGetShaderInfoLog = wglGetProcAddress("glGetShaderInfoLog");
                glGetProgramInfoLog = wglGetProcAddress("glGetProgramInfoLog");
                glTexImage2DMultisample = wglGetProcAddress("glTexImage2DMultisample");
                glRenderbufferStorageMultisample = wglGetProcAddress("glRenderbufferStorageMultisample");
                glBlitFramebuffer = wglGetProcAddress("glBlitFramebuffer");

                if ("WGL_EXT_swap_control" in wglExtensions) {
                    wglSwapIntervalEXT = wglGetProcAddress("wglSwapIntervalEXT")

                    // initialize platform states and properties to match cached states and properties
                    callJ(wglSwapIntervalEXT, 0)
                }
                state.vSyncEnabled = false
                vSyncRequested = vSyncRequest

                init()
            }

            // Release context once we are all done
            wglMakeCurrent(NULL, NULL);

            ctxInfo.adr
        }
    }
}