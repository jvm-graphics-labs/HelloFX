package com.sun.prism.es2

import com.sun.*
import com.sun.prism.es2.GLPixelFormat.Attributes.*
import glm_.*
import kool.Buffer
import kool.Ptr
import kool.adr
import org.lwjgl.opengl.WGL.wglDeleteContext
import org.lwjgl.opengl.WGL.wglMakeCurrent
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil.*
import org.lwjgl.system.windows.*
import org.lwjgl.system.windows.GDI32.*
import org.lwjgl.system.windows.User32.*
import uno.glfw.HWND
import java.nio.ByteBuffer

fun MemoryStack.getPFD(attrArr: IntArray) = PIXELFORMATDESCRIPTOR.callocStack(this).apply {
    size = PIXELFORMATDESCRIPTOR.SIZEOF
    version = 1 // Version number
    flags = PFD_SUPPORT_OPENGL
    if (attrArr[ONSCREEN] != 0)
        flags = flags or PFD_DRAW_TO_WINDOW
    if (attrArr[DOUBLEBUFFER] != 0)
        flags = flags or PFD_DOUBLEBUFFER
    pixelType = PFD_TYPE_RGBA
    colorBits = attrArr[RED_SIZE] + attrArr[GREEN_SIZE] + attrArr[BLUE_SIZE] + attrArr[ALPHA_SIZE]
    // RGB bits and pixel sizes
    redBits = attrArr[RED_SIZE]
    greenBits = attrArr[GREEN_SIZE]
    blueBits = attrArr[BLUE_SIZE]
    alphaBits = attrArr[ALPHA_SIZE]
    // no alpha buffer info
    // no accumulation buffer
    depthBits = attrArr[DEPTH_SIZE]
    // no stencil buffer and auxiliary buffers
    layerType = PFD_MAIN_PLANE
    // reserved, must be 0
    // no layer mask, visible mask or damage mask
}

val WndProc = object : WindowProc() {
    override fun invoke(hwnd: Long, uMsg: Int, wParam: Long, lParam: Long): Long = DefWindowProc(hwnd, uMsg, wParam, lParam)
}

fun MemoryStack.createDummyWindow(szAppName: String): HWND {

    val szTitle = "Dummy Window"

    val wc = WNDCLASSEX.callocStack(this).apply {
        // windows class structure
        // Fill in window class structure with parameters that describe the main window.
        size = WNDCLASSEX.SIZEOF
        style = CS_HREDRAW or CS_VREDRAW // Class style(s).
        wndProc = WndProc // Window Procedure
//            clsExtra = 0 // No per-class extra data.
//            wndExtra = 0 // No per-window extra data.
        instance = WindowsLibrary.HINSTANCE // Owner of this class TODO check
//            icon = NULL // Icon name
//            cursor = NULL // Cursor
        background = COLOR_WINDOWFRAME // Default color
//            menuName = null // Menu from .RC
        className = UTF16(szAppName) // Name to register as
    }
    /* Register the window class */
    if (RegisterClassEx(wc) == 0.s) {
        System.err.println("createDummyWindow: couldn't register class")
        return HWND(NULL)
    }

    // Create a main window for this application instance.
    val hWnd = HWND(CreateWindowEx(
            User32.WS_EX_APPWINDOW,
            szAppName, // app name
            szTitle, // Text for window title bar
            WS_OVERLAPPEDWINDOW // Window style
                    // NEED THESE for OpenGL calls to work!
                    or WS_CLIPCHILDREN or WS_CLIPSIBLINGS,
            0, 0, 1, 1, // x, y, width, height
            NULL, // no parent window
            NULL, // Use the window class menu.
            NULL, // This instance owns this window
            NULL) // We don't use any extra data
    )

    /* If window could not be created, return zero */
    if (hWnd.isInvalid) {
        System.err.println("createDummyWindow: couldn't create window")
        UnregisterClass(szAppName, NULL)
    }
    return hWnd
}

fun printAndReleaseResources(hwnd: HWND? = null, hglrc: HGLRC = NULL, hdc: HDC = NULL, szAppName: String = "", message: String? = null) {
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