package com.sun.prism.es2

import com.sun.prism.es2.GLPixelFormat.Attributes.*
import glm_.*
import kool.*
import org.lwjgl.PointerBuffer
import org.lwjgl.opengl.GL11C.*
import org.lwjgl.opengl.GLX13.*
import org.lwjgl.system.MemoryUtil.*
import org.lwjgl.system.Pointer
import org.lwjgl.system.linux.X11.*
import org.lwjgl.system.linux.XSetWindowAttributes
import uno.kotlin.parseInt
import java.nio.ByteBuffer


object _X11GLFactory {

    const val MAX_GLX_ATTRS_LENGTH = 50

    init {

//        WinGLFactory.nInitialize = WinGLFactory.nInitialize { attrs ->
//
//            val szAppName = "Choose Pixel Format"
//
//            val stak = stackPush()
//
//            val pfd = stak.getPFD(attrs)
//
//            /*  Select a specified pixel format and bound current context to it so that we can get the wglChoosePixelFormatARB entry point.
//                Otherwise wglxxx entry point will always return null.
//                That's why we need to create a dummy window also.             */
//            val hwnd = stak.createDummyWindow(szAppName)
//            if (hwnd.isInvalid)
//                return@nInitialize NULL
//
//            val hdc = GetDC(hwnd.L)
//            if (hdc == NULL) {
//                printAndReleaseResources(hwnd, hdc = hdc, szAppName = szAppName, message = "Failed in GetDC")
//                return@nInitialize NULL
//            }
//
//            val pixelFormat = ChoosePixelFormat(hdc, pfd)
//            if (pixelFormat < 1) {
//                printAndReleaseResources(hwnd, hdc = hdc, szAppName = szAppName, message = "Failed in ChoosePixelFormat")
//                return@nInitialize NULL
//            }
//
//            if (!SetPixelFormat(hdc, pixelFormat, null)) {
//                printAndReleaseResources(hwnd, hdc = hdc, szAppName = szAppName, message = "Failed in SetPixelFormat")
//                return@nInitialize NULL
//            }
//
//            val hglrc = wglCreateContext(hdc)
//            if (hglrc == NULL) {
//                printAndReleaseResources(hwnd, hglrc, hdc, szAppName, "Failed in wglCreateContext")
//                return@nInitialize NULL
//            }
//
//            if (!wglMakeCurrent(hdc, hglrc)) {
//                printAndReleaseResources(hwnd, hglrc, hdc, szAppName, "Failed in wglMakeCurrent")
//                return@nInitialize NULL
//            }
//
//            /* Get the OpenGL version */
//            val glVersion = gl[GL_VERSION] ?: run {
//                printAndReleaseResources(hwnd, hglrc, hdc, szAppName, "glVersion == null")
//                return@nInitialize NULL
//            }
//
//            val major = glVersion[0].parseInt()
//            val minor = glVersion[2].parseInt()
//
//            /* Targeted Cards: Intel HD Graphics, Intel HD Graphics 2000/3000,
//                Radeon HD 2350, GeForce FX (with newer drivers), GeForce 7 series or higher
//                Check for OpenGL 2.1 or later.     */
//            if (major < 2 || (major == 2 && minor < 1)) {
//                System.err.println("GL_VERSION (major.minor) = $major.$minor")
//                printAndReleaseResources(hwnd, hglrc, hdc, szAppName)
//                return@nInitialize NULL
//            }
//
//            /* Get the OpenGL vendor and renderer */
//            val glVendor = gl[GL_VENDOR] ?: "<UNKNOWN>"
//            val glRenderer = gl[GL_RENDERER] ?: "<UNKNOWN>"
//
//            val glExtensions = gl[GL_EXTENSIONS] ?: run {
//                printAndReleaseResources(hwnd, hglrc, hdc, szAppName, "glExtensions == null")
//                return@nInitialize NULL
//            }
//
//            // We use GL_ARB_pixel_buffer_object as an guide to determine PS 3.0 capable.
//            if ("GL_ARB_pixel_buffer_object" !in glExtensions) {
//                printAndReleaseResources(hwnd, hglrc, hdc, szAppName, "GL profile isn't PS 3.0 capable")
//                return@nInitialize NULL
//            }
//
//            val wglGetExtensionsStringARB_Adr: Adr = wglGetProcAddress("wglGetExtensionsStringARB")
//            if (wglGetExtensionsStringARB_Adr == NULL) {
//                printAndReleaseResources(hwnd, hglrc, hdc, szAppName, "wglGetExtensionsStringARB is not supported!")
//                return@nInitialize NULL
//            }
//            val wglExtensions: String = when (val str = callPP(wglGetExtensionsStringARB_Adr, hdc)) {
//                NULL -> return@nInitialize NULL.also {
//                    printAndReleaseResources(hwnd, hglrc, hdc, szAppName, "wglExtensions == null")
//                }
//                else -> memASCII(str)
//            }
//
//            /*  Note: We are only storing the string information of a driver.
//                Assuming a system with a single or homogeneous GPUs. For the case
//                of heterogeneous GPUs system the string information will need to move to
//                GLContext class. */
//
//            // allocate the structure
//            val ctxInfo = WinContextInfo().apply {
//                version = glVersion
//                vendor = glVendor
//                renderer = glRenderer
//                this.glExtensions = glExtensions
//                this.wglExtensions = wglExtensions
//                this.minor = minor
//                this.major = major
//                gl2 = true
//            }
//
//            printAndReleaseResources(hwnd, hglrc, hdc, szAppName)
//
//            stak.pop()
//            ctxInfo.adr
//        }

        fun setGLXAttrs(attrs: IntArray): IntArray {

            val glxAttrs = IntArray(MAX_GLX_ATTRS_LENGTH) // value, attr pair plus a None

            var index = 0

            /* Specify pbuffer as default */
            glxAttrs[index++] = GLX_DRAWABLE_TYPE
            glxAttrs[index++] = when {
                attrs[ONSCREEN].bool -> GLX_PBUFFER_BIT
                else -> GLX_PBUFFER_BIT
            }

            /* only interested in RGBA type */
            glxAttrs[index++] = GLX_RENDER_TYPE
            glxAttrs[index++] = GLX_RGBA_BIT

            /* only interested in FBConfig with associated X Visual type */
            glxAttrs[index++] = GLX_X_RENDERABLE
            glxAttrs[index++] = true.i

            glxAttrs[index++] = GLX_DOUBLEBUFFER
            glxAttrs[index++] = attrs[DOUBLEBUFFER]

            glxAttrs[index++] = GLX_RED_SIZE
            glxAttrs[index++] = attrs[RED_SIZE]
            glxAttrs[index++] = GLX_GREEN_SIZE
            glxAttrs[index++] = attrs[GREEN_SIZE]
            glxAttrs[index++] = GLX_BLUE_SIZE
            glxAttrs[index++] = attrs[BLUE_SIZE]
            glxAttrs[index++] = GLX_ALPHA_SIZE
            glxAttrs[index++] = attrs[ALPHA_SIZE]

            glxAttrs[index++] = GLX_DEPTH_SIZE
            glxAttrs[index++] = attrs[DEPTH_SIZE]

            glxAttrs[index] = 0

            return glxAttrs
        }

        fun queryGLX13(display: Long): Boolean = stak {

            val errorBase = IntBuffer(1)
            val eventBase = IntBuffer(1)

            if (!glXQueryExtension(display, errorBase, eventBase)) {
                System.err.println("ES2 Prism: Error - GLX extension is not supported")
                System.err.println("\tGLX version 1.3 or higher is required")
                return false
            }

            val major = IntBuffer(1)
            val minor = IntBuffer(1)
            /* Query the GLX version number */
            if (!glXQueryVersion(display, major, minor)) {
                System.err.println("ES2 Prism: Error - Unable to query GLX version")
                System.err.println("\tGLX version 1.3 or higher is required")
                return false
            }

            /*
                fprintf(stderr, "Checking GLX version : %d.%d\n", major, minor);
             */

            /* Check for GLX 1.3 and higher */
            if (!(major[0] == 1 && minor[0] >= 3)) {
                System.err.println("ES2 Prism: Error - reported GLX version = ${major[0]}.${minor[1]}")
                System.err.println("\tGLX version 1.3 or higher is required")

                return false
            }

            return true
        }

        fun printAndReleaseResources(display: Long, fbConfigList: PointerBuffer, visualInfo: Pointer? = null,
                                     win: Long = NULL, ctx: Long = NULL, cmap: Long = NULL, message: String = "") {
            if (message.isNotEmpty())
                System.err.println(message)
            if (display == NULL)
                return
            glXMakeCurrent(display, NULL, NULL)
            if (fbConfigList[0] != NULL)
                XFree(fbConfigList)
            if (visualInfo != null && visualInfo.adr != NULL) {
                nXFree(visualInfo.adr)
            }
            if (ctx != NULL)
                glXDestroyContext(display, ctx)
            if (win != NULL)
                XDestroyWindow(display, win)
            if (cmap != NULL)
                XFreeColormap(display, cmap)
        }

        X11GLFactory.nInitialize = X11GLFactory.nInitialize { attr ->

            val glxAttrs = setGLXAttrs(attr)

            val display = XOpenDisplay(null as ByteBuffer?)
            if (display == NULL)
                return@nInitialize NULL

            val screen = XDefaultScreen(display)

            if (!queryGLX13(display))
                return@nInitialize NULL

            val fbConfigList = glXChooseFBConfig(display, screen, glxAttrs) ?: run {
                System.err.println("Prism ES2 Error - nInitialize: glXChooseFBConfig failed")
                return@nInitialize NULL
            }

            val visualInfo = glXGetVisualFromFBConfig(display, fbConfigList[0]) ?: run {
                printAndReleaseResources(display, fbConfigList, message = "Failed in  glXGetVisualFromFBConfig")
                return@nInitialize NULL
            }

            /*
                fprintf(stderr, "found a %d-bit visual (visual ID = 0x%x)\n",
                        visualInfo->depth, (unsigned int) visualInfo->visualid);
            */
            val root = XRootWindow(display, visualInfo.screen())

            /* Create a colormap */
            val cmap = XCreateColormap(display, root, visualInfo.visual(), AllocNone)

            /* Create a 1x1 window */
            val winAttrs = XSetWindowAttributes.calloc().colormap(cmap).border_pixel(0).event_mask((KeyPressMask or ExposureMask or StructureNotifyMask).L)
            val winMask = CWColormap or CWBorderPixel or CWEventMask
            val win = XCreateWindow(display, root, 0, 0, 1, 1, 0,
                    visualInfo.depth(), InputOutput, visualInfo.visual(), winMask.L, winAttrs)

            if (win == NULL) {
                printAndReleaseResources(display, fbConfigList, visualInfo, win, message = "Failed in XCreateWindow")
                return@nInitialize NULL
            }

//            val old_error_handler = XSetErrorHandler(x11errorDetector)

            val ctx = glXCreateNewContext(display, fbConfigList[0], GLX_RGBA_TYPE, NULL, true)

//            XSync(display, 0) // sync needed for the GLX error detection.

//            if (x11errorhit) {
//                // An X11 Error was hit along the way. This would happen if GLX is
//                // disabled which recently became the X11 default for remote connections
//                printAndReleaseResources(display, fbConfigList, visualInfo, win, ctx, cmap,
//                        "Error in glXCreateNewContext, remote GLX is likely disabled")
//                XSync(display, 0) // sync needed for the GLX error detection.
//                XSetErrorHandler(old_error_handler)
//                return 0
//            }

//            XSetErrorHandler(old_error_handler);

            if (ctx == NULL) {
                printAndReleaseResources(display, fbConfigList, visualInfo, win, ctx, cmap, message = "Failed in glXCreateNewContext")
                return@nInitialize NULL
            }

            if (!glXMakeCurrent(display, win, ctx)) {
                printAndReleaseResources(display, fbConfigList, visualInfo, win, ctx, cmap, message = "Failed in glXMakeCurrent")
                return@nInitialize NULL
            }

            /* Get the OpenGL version */
            val glVersion = gl[GL_VERSION] ?: run {
                printAndReleaseResources(display, fbConfigList, visualInfo, win, ctx, cmap, message = "glVersion == null")
                return@nInitialize NULL
            }

            /* find out the version, major and minor version number */
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
                System.err.println("Prism-ES2 Error : GL_VERSION (major.minor) = $major.$minor")
                printAndReleaseResources(display, fbConfigList, visualInfo, win, ctx, cmap)
                return@nInitialize NULL
            }

            /* Get the OpenGL vendor and renderer */
            val glVendor = gl[GL_VENDOR] ?: "<UNKNOWN>"
            val glRenderer = gl[GL_RENDERER] ?: "<UNKNOWN>"

            val glExtensions = gl[GL_EXTENSIONS] ?: run {
                printAndReleaseResources(display, fbConfigList, visualInfo, win, ctx, cmap, "Prism-ES2 Error : glExtensions == null")
                return@nInitialize NULL
            }

            // We use GL_ARB_pixel_buffer_object as an guide to
            // determine PS 3.0 capable.
            if ("GL_ARB_pixel_buffer_object" !in glExtensions) {
                printAndReleaseResources(display, fbConfigList, visualInfo, win, ctx, cmap, "GL profile isn't PS 3.0 capable")
                return@nInitialize NULL
            }

            val glxExtensions = glXGetClientString(display, GLX_EXTENSIONS) ?: run {
                printAndReleaseResources(display, fbConfigList, visualInfo, win, ctx, cmap, "glxExtensions == null")
                return@nInitialize NULL
            }

            /* Note: We are only storing the string information of a driver.
             Assuming a system with a single or homogeneous GPUs. For the case
             of heterogeneous GPUs system the string information will need to move to
             GLContext class. */
            /* allocate the structure */
            val ctxInfo = X11ContextInfo().apply {
                /* initialize the structure */
                version = glVersion
                vendor = glVendor
                renderer = glRenderer
                this.glExtensions = glExtensions
                this.glxExtensions = glxExtensions
                this.major = major
                this.minor = minor
                gl2 = true

                /* Information required by GLass at startup */
                this.display = display
                this.screen = screen
                this.visualID = visualInfo.visualid().i
            }
            /* Releasing native resources */
            printAndReleaseResources(display, fbConfigList, visualInfo, win, ctx, cmap)

            ctxInfo.adr
        }
    }
}