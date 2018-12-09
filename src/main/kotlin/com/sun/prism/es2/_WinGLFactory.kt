package com.sun.prism.es2

import com.sun.*
import glm_.BYTES
import glm_.b
import glm_.bool
import glm_.s
import kool.Adr
import kool.Buffer
import kool.Ptr
import kool.adr
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11C.*
import org.lwjgl.opengl.WGL.*
import org.lwjgl.system.APIUtil.apiLog
import org.lwjgl.system.JNI.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryStack.stackPush
import org.lwjgl.system.MemoryUtil.*
import org.lwjgl.system.windows.*
import org.lwjgl.system.windows.GDI32.*
import org.lwjgl.system.windows.User32.*
import uno.glfw.HWND
import uno.kotlin.parseInt
import java.nio.ByteBuffer


object _WinGLFactory {

    init {

        WinGLFactory.nInitialize = WinGLFactory.nInitialize { attrs ->

            val szAppName = "Choose Pixel Format"

            val stak = stackPush()

            val pfd = stak.getPFD(attrs)

            /*  Select a specified pixel format and bound current context to it so that we can get the wglChoosePixelFormatARB entry point.
                Otherwise wglxxx entry point will always return null.
                That's why we need to create a dummy window also.             */
            val hwnd = stak.createDummyWindow(szAppName)
            if (hwnd.isInvalid)
                return@nInitialize NULL

            val hdc = GetDC(hwnd.L)
            if (hdc == NULL) {
                printAndReleaseResources(hwnd, hdc = hdc, szAppName = szAppName, message = "Failed in GetDC")
                return@nInitialize NULL
            }

            val pixelFormat = ChoosePixelFormat(hdc, pfd)
            if (pixelFormat < 1) {
                printAndReleaseResources(hwnd, hdc = hdc, szAppName = szAppName, message = "Failed in ChoosePixelFormat")
                return@nInitialize NULL
            }

            if (!SetPixelFormat(hdc, pixelFormat, null)) {
                printAndReleaseResources(hwnd, hdc = hdc, szAppName = szAppName, message = "Failed in SetPixelFormat")
                return@nInitialize NULL
            }

            val hglrc = wglCreateContext(hdc)
            if (hglrc == NULL) {
                printAndReleaseResources(hwnd, hglrc, hdc, szAppName, "Failed in wglCreateContext")
                return@nInitialize NULL
            }

            if (!wglMakeCurrent(hdc, hglrc)) {
                printAndReleaseResources(hwnd, hglrc, hdc, szAppName, "Failed in wglMakeCurrent")
                return@nInitialize NULL
            }

            /* Get the OpenGL version */
            val glVersion = gl[GL_VERSION] ?: run {
                printAndReleaseResources(hwnd, hglrc, hdc, szAppName, "glVersion == null")
                return@nInitialize NULL
            }

            val major = glVersion[0].parseInt()
            val minor = glVersion[2].parseInt()

            /* Targeted Cards: Intel HD Graphics, Intel HD Graphics 2000/3000,
                Radeon HD 2350, GeForce FX (with newer drivers), GeForce 7 series or higher
                Check for OpenGL 2.1 or later.     */
            if (major < 2 || (major == 2 && minor < 1)) {
                System.err.println("GL_VERSION (major.minor) = $major.$minor")
                printAndReleaseResources(hwnd, hglrc, hdc, szAppName)
                return@nInitialize NULL
            }

            /* Get the OpenGL vendor and renderer */
            val glVendor = gl[GL_VENDOR] ?: "<UNKNOWN>"
            val glRenderer = gl[GL_RENDERER] ?: "<UNKNOWN>"

            val glExtensions = gl[GL_EXTENSIONS] ?: run {
                printAndReleaseResources(hwnd, hglrc, hdc, szAppName, "glExtensions == null")
                return@nInitialize NULL
            }

            // We use GL_ARB_pixel_buffer_object as an guide to determine PS 3.0 capable.
            if ("GL_ARB_pixel_buffer_object" !in glExtensions) {
                printAndReleaseResources(hwnd, hglrc, hdc, szAppName, "GL profile isn't PS 3.0 capable")
                return@nInitialize NULL
            }

            val wglGetExtensionsStringARB_Adr: Adr = wglGetProcAddress("wglGetExtensionsStringARB")
            if (wglGetExtensionsStringARB_Adr == NULL) {
                printAndReleaseResources(hwnd, hglrc, hdc, szAppName, "wglGetExtensionsStringARB is not supported!")
                return@nInitialize NULL
            }
            val wglExtensions: String = when (val str = callPP(wglGetExtensionsStringARB_Adr, hdc)) {
                NULL -> return@nInitialize NULL.also {
                    printAndReleaseResources(hwnd, hglrc, hdc, szAppName, "wglExtensions == null")
                }
                else -> memASCII(str)
            }

            /*  Note: We are only storing the string information of a driver.
                Assuming a system with a single or homogeneous GPUs. For the case
                of heterogeneous GPUs system the string information will need to move to
                GLContext class. */

            // allocate the structure
            val ctxInfo = WinContextInfo().apply {
                version = glVersion
                vendor = glVendor
                renderer = glRenderer
                this.glExtensions = glExtensions
                this.wglExtensions = wglExtensions
                this.minor = minor
                this.major = major
                gl2 = true
            }

            printAndReleaseResources(hwnd, hglrc, hdc, szAppName)

            stak.pop()
            ctxInfo.adr
        }

    }
}