package com.sun.prism.es2

import kool.Adr
import org.lwjgl.opengl.GL11C.*
import org.lwjgl.opengl.GLX.glXDestroyContext
import org.lwjgl.opengl.GLX.glXMakeCurrent
import org.lwjgl.opengl.GLX11.GLX_EXTENSIONS
import org.lwjgl.opengl.GLX11.glXGetClientString
import org.lwjgl.opengl.GLX13.GLX_RGBA_TYPE
import org.lwjgl.opengl.GLX13.glXCreateNewContext
import org.lwjgl.opengl.GLX14.glXGetProcAddress
import org.lwjgl.opengl.WGL.*
import org.lwjgl.system.JNI.callJ
import org.lwjgl.system.JNI.callPP
import org.lwjgl.system.MemoryUtil.NULL
import org.lwjgl.system.MemoryUtil.memASCII
import org.lwjgl.system.linux.DynamicLinkLoader.dlsym
import org.lwjgl.system.macosx.DynamicLinkLoader.RTLD_DEFAULT
import org.lwjgl.system.windows.GDI32.SetPixelFormat
import org.lwjgl.system.windows.WinBase
import uno.kotlin.parseInt

object _X11GLContext {

    init {

        X11GLContext.nInitialize = X11GLContext.nInitialize { nativeDInfo, nativePFInfo, vSyncRequest ->

            if (nativeDInfo == NULL || nativePFInfo == NULL)
                return@nInitialize NULL

            val dInfo = DrawableInfo(nativeDInfo)
            val pfInfo = PixelFormatInfo(nativePFInfo)

            val display = pfInfo.display
            val fbConfig = pfInfo.fbConfig
            val win = dInfo.win

            val ctx = glXCreateNewContext(display, fbConfig, GLX_RGBA_TYPE, NULL, true)

            if (ctx == NULL) {
                System.err.println("Failed in glXCreateNewContext")
                return@nInitialize NULL
            }

            if (!glXMakeCurrent(display, win, ctx)) {
                glXDestroyContext(display, ctx)
                System.err.println("Failed in glXMakeCurrent")
                return@nInitialize NULL
            }

            // Get the OpenGL version
            val glVersion = gl[GL_VERSION] ?: run {
                glXDestroyContext(display, ctx)
                System.err.println("glVersion == null")
                return@nInitialize NULL
            }

            // find out the version, major and minor version number
            val major = glVersion[0].parseInt()
            val minor = glVersion[2].parseInt()

            /*
                fprintf(stderr, "GL_VERSION string = %s\n", glVersion);
                fprintf(stderr, "GL_VERSION (major.minor) = %d.%d\n",
                        versionNumbers[0], versionNumbers[1]);
            */

            /*
             * Targeted Cards: Intel HD Graphics, Intel HD Graphics 2000/3000,
             * Radeon HD 2350, GeForce FX (with newer drivers), GeForce 7 series or higher
             *
             * Check for OpenGL 2.1 or later.
             */
            if (major < 2 || (major == 2 && minor < 1)) {
                glXDestroyContext(display, ctx)
                System.err.println("Prism-ES2 Error : GL_VERSION (major.minor) = $major.$minor")
                return@nInitialize NULL
            }

            // Get the OpenGL vendor and renderer
            val glVendor = gl[GL_VENDOR] ?: "<UNKNOWN>"
            val glRenderer = gl[GL_RENDERER] ?: "<UNKNOWN>"

            val glExtensions = gl[GL_EXTENSIONS] ?: run {
                glXDestroyContext(display, ctx)
                System.err.println("glExtensions == null")
                return@nInitialize NULL
            }

            // We use GL_ARB_pixel_buffer_object as an guide to determine PS 3.0 capable.
            if ("GL_ARB_pixel_buffer_object" !in glExtensions) {
                glXDestroyContext(display, ctx)
                System.err.println("GL profile isn't PS 3.0 capable")
                return@nInitialize NULL
            }

            val glxExtensions = glXGetClientString(display, GLX_EXTENSIONS) ?: run {
                glXDestroyContext(display, ctx)
                System.err.println("glxExtensions == null")
                return@nInitialize NULL
            }

            /*
                fprintf(stderr, "glExtensions: %s\n", glExtensions);
                fprintf(stderr, "glxExtensions: %s\n", glxExtensions);
             */

            // allocate the structure
            val ctxInfo = X11ContextInfo().apply {
                // initialize the structure
                version = glVersion
                vendor = glVendor
                renderer = glRenderer
                this.glExtensions = glExtensions
                this.glxExtensions = glxExtensions
                this.major = major
                this.minor = minor
                this.display = display
                context = ctx

                // set function pointers
                glActiveTexture = dlsym(RTLD_DEFAULT, "glActiveTexture")
                glAttachShader = dlsym(RTLD_DEFAULT, "glAttachShader")
                glBindAttribLocation = dlsym(RTLD_DEFAULT, "glBindAttribLocation")
                glBindFramebuffer = dlsym(RTLD_DEFAULT, "glBindFramebuffer")
                glBindRenderbuffer = dlsym(RTLD_DEFAULT, "glBindRenderbuffer")
                glCheckFramebufferStatus = dlsym(RTLD_DEFAULT, "glCheckFramebufferStatus")
                glCreateProgram = dlsym(RTLD_DEFAULT, "glCreateProgram")
                glCreateShader = dlsym(RTLD_DEFAULT, "glCreateShader")
                glCompileShader = dlsym(RTLD_DEFAULT, "glCompileShader")
                glDeleteBuffers = dlsym(RTLD_DEFAULT, "glDeleteBuffers")
                glDeleteFramebuffers = dlsym(RTLD_DEFAULT, "glDeleteFramebuffers")
                glDeleteProgram = dlsym(RTLD_DEFAULT, "glDeleteProgram")
                glDeleteRenderbuffers = dlsym(RTLD_DEFAULT, "glDeleteRenderbuffers")
                glDeleteShader = dlsym(RTLD_DEFAULT, "glDeleteShader")
                glDetachShader = dlsym(RTLD_DEFAULT, "glDetachShader")
                glDisableVertexAttribArray = dlsym(RTLD_DEFAULT, "glDisableVertexAttribArray")
                glEnableVertexAttribArray = dlsym(RTLD_DEFAULT, "glEnableVertexAttribArray")
                glFramebufferRenderbuffer = dlsym(RTLD_DEFAULT, "glFramebufferRenderbuffer")
                glFramebufferTexture2D = dlsym(RTLD_DEFAULT, "glFramebufferTexture2D")
                glGenFramebuffers = dlsym(RTLD_DEFAULT, "glGenFramebuffers")
                glGenRenderbuffers = dlsym(RTLD_DEFAULT, "glGenRenderbuffers")
                glGetProgramiv = dlsym(RTLD_DEFAULT, "glGetProgramiv")
                glGetShaderiv = dlsym(RTLD_DEFAULT, "glGetShaderiv")
                glGetUniformLocation = dlsym(RTLD_DEFAULT, "glGetUniformLocation")
                glLinkProgram = dlsym(RTLD_DEFAULT, "glLinkProgram")
                glRenderbufferStorage = dlsym(RTLD_DEFAULT, "glRenderbufferStorage")
                glShaderSource = dlsym(RTLD_DEFAULT, "glShaderSource")
                glUniform1f = dlsym(RTLD_DEFAULT, "glUniform1f")
                glUniform2f = dlsym(RTLD_DEFAULT, "glUniform2f")
                glUniform3f = dlsym(RTLD_DEFAULT, "glUniform3f")
                glUniform4f = dlsym(RTLD_DEFAULT, "glUniform4f")
                glUniform4fv = dlsym(RTLD_DEFAULT, "glUniform4fv")
                glUniform1i = dlsym(RTLD_DEFAULT, "glUniform1i")
                glUniform2i = dlsym(RTLD_DEFAULT, "glUniform2i")
                glUniform3i = dlsym(RTLD_DEFAULT, "glUniform3i")
                glUniform4i = dlsym(RTLD_DEFAULT, "glUniform4i")
                glUniform4iv = dlsym(RTLD_DEFAULT, "glUniform4iv")
                glUniformMatrix4fv = dlsym(RTLD_DEFAULT, "glUniformMatrix4fv")
                glUseProgram = dlsym(RTLD_DEFAULT, "glUseProgram")
                glValidateProgram = dlsym(RTLD_DEFAULT, "glValidateProgram")
                glVertexAttribPointer = dlsym(RTLD_DEFAULT, "glVertexAttribPointer")
                glGenBuffers = dlsym(RTLD_DEFAULT, "glGenBuffers")
                glBindBuffer = dlsym(RTLD_DEFAULT, "glBindBuffer")
                glBufferData = dlsym(RTLD_DEFAULT, "glBufferData")
                glBufferSubData = dlsym(RTLD_DEFAULT, "glBufferSubData")
                glGetShaderInfoLog = dlsym(RTLD_DEFAULT, "glGetShaderInfoLog")
                glGetProgramInfoLog = dlsym(RTLD_DEFAULT, "glGetProgramInfoLog")
                glTexImage2DMultisample = dlsym(RTLD_DEFAULT, "glTexImage2DMultisample")
                glRenderbufferStorageMultisample = dlsym(RTLD_DEFAULT, "glRenderbufferStorageMultisample")
                glBlitFramebuffer = dlsym(RTLD_DEFAULT, "glBlitFramebuffer")

                if ("GLX_SGI_swap_control" in glxExtensions) {
                    glXSwapIntervalSGI = dlsym(RTLD_DEFAULT, "glXSwapIntervalSGI")

                    if (glXSwapIntervalSGI == NULL)
                        glXSwapIntervalSGI = glXGetProcAddress("glXSwapIntervalSGI")
                }

                // initialize platform states and properties to match cached states and properties
                if (glXSwapIntervalSGI != NULL)
                    glXSwapIntervalSGI = 0

                state.vSyncEnabled = false
                vSyncRequested = vSyncRequest

                init()
            }

            // Release context once we are all done
            glXMakeCurrent(display, NULL, NULL)

            ctxInfo.adr
        }
    }
}