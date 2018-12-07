package com.sun.prism.es2

import org.lwjgl.opengl.WGL.wglDeleteContext
import org.lwjgl.opengl.WGL.wglMakeCurrent
import org.lwjgl.system.MemoryUtil.NULL
import org.lwjgl.system.windows.User32.*
import uno.glfw.HWND

fun printAndReleaseResources(hwnd: HWND? = null, hglrc: HGLRC, hdc: HDC = NULL, szAppName: String? = null, message: String? = null) {
    message?.let(System.err::println)
    wglMakeCurrent(NULL, NULL)
    if (hglrc != NULL)
        wglDeleteContext(hglrc)
    if (hdc != NULL)
        hwnd?.let {
            ReleaseDC(hwnd.L, hdc)
        DestroyWindow(hwnd.L)
        UnregisterClass(szAppName, NULL)
    }
}