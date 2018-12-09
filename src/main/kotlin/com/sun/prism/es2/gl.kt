package com.sun.prism.es2

import kool.Ptr
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11C.GL_NO_ERROR
import org.lwjgl.system.APIUtil.apiLog
import org.lwjgl.system.JNI.callI
import org.lwjgl.system.JNI.callP
import org.lwjgl.system.MemoryUtil.NULL
import org.lwjgl.system.MemoryUtil.memUTF8Safe

object gl {

    val GetError: Ptr
    val GetString: Ptr
    val GetIntegerv: Ptr

    val error: Int
        get() = callI(GetError)

    operator fun get(param: Int) = memUTF8Safe(callP(GetString, param))

    init {
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