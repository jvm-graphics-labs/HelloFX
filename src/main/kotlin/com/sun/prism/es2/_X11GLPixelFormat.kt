package com.sun.prism.es2

import com.sun.prism.es2.GLPixelFormat.Attributes.*
import com.sun.prism.es2._X11GLFactory.MAX_GLX_ATTRS_LENGTH
import glm_.L
import glm_.bool
import kool.adr
import org.lwjgl.opengl.GLX13.*
import org.lwjgl.system.MemoryUtil.NULL
import org.lwjgl.system.linux.X11.*
import org.lwjgl.system.linux.XSetWindowAttributes
import java.nio.ByteBuffer

object _X11GLPixelFormat {

    init {

        fun getGLXAttrs(attrs: IntArray): IntArray {
            var index = 0

            val glxAttrs = IntArray(MAX_GLX_ATTRS_LENGTH)
            /* Specify pbuffer as default */
            glxAttrs[index++] = GLX_DRAWABLE_TYPE
            glxAttrs[index++] = when {
                attrs[ONSCREEN].bool -> GLX_PBUFFER_BIT or GLX_WINDOW_BIT
                else -> GLX_PBUFFER_BIT
            }

            // only interested in RGBA type
            glxAttrs[index++] = GLX_RENDER_TYPE
            glxAttrs[index++] = GLX_RGBA_BIT

            // only interested in FBConfig with associated X Visual type
            glxAttrs[index++] = GLX_X_RENDERABLE
            glxAttrs[index++] = True

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

            glxAttrs[index] = None

            return glxAttrs
        }

        X11GLPixelFormat.nCreatePixelFormat = X11GLPixelFormat.nCreatePixelFormat { _, attrs ->

            if (attrs == null)
                return@nCreatePixelFormat NULL
            val glxAttrs = getGLXAttrs(attrs)

            // RT-27386
            // TODO: Need to use nativeScreen to create this requested pixelformat
            // currently hack to work on a single monitor system
            val display = XOpenDisplay(null as ByteBuffer?)
            if (display == NULL) {
                System.err.println("Failed in XOpenDisplay")
                return@nCreatePixelFormat NULL
            }

            val screen = XDefaultScreen(display)

            val fbConfigList = glXChooseFBConfig(display, screen, glxAttrs) ?: run {
                System.err.println("Failed in glXChooseFBConfig")
                return@nCreatePixelFormat NULL
            }

//            #if 0 // TESTING ONLY
//            visualInfo = glXGetVisualFromFBConfig(display, fbConfigList[0])
//            if (visualInfo == NULL) {
//                printAndReleaseResources(display, fbConfigList, NULL,
//                        None, NULL, None,
//                        "Failed in  glXGetVisualFromFBConfig")
//                return 0
//            }
//
//            fprintf(stderr, "found a %d-bit visual (visual ID = 0x%x)\n",
//                    visualInfo->depth, (unsigned int) visualInfo->visualid)
//            #endif

            val visualInfo = glXGetVisualFromFBConfig(display, fbConfigList[0]) ?: run {
                printAndReleaseResources(display, fbConfigList, message = "Failed in glXGetVisualFromFBConfig")
                return@nCreatePixelFormat NULL
            }

//            #if 0 // TESTING ONLY
//            fprintf(stderr, "found a %d-bit visual (visual ID = 0x%x)\n",
//                    visualInfo->depth, (unsigned int) visualInfo->visualid)
//            #endif

            val root = XRootWindow(display, visualInfo.screen())

            // Create a colormap
            val cmap = XCreateColormap(display, root, visualInfo.visual(), AllocNone)

            // Create a 1x1 window
            val winAttrs = XSetWindowAttributes.calloc().colormap(cmap).border_pixel(0).event_mask((KeyPressMask or ExposureMask or StructureNotifyMask).L)
            val win_mask = CWColormap or CWBorderPixel or CWEventMask
            val win = XCreateWindow(display, root, 0, 0, 1, 1, 0,
                    visualInfo.depth(), InputOutput, visualInfo.visual(), win_mask.L, winAttrs)

            if (win == NULL) {
                printAndReleaseResources(display, fbConfigList, visualInfo, win, cmap = cmap, message = "Failed in XCreateWindow")
                return@nCreatePixelFormat NULL
            }

            // allocate the structure
            val pfInfo = PixelFormatInfo().apply {
                // initialize the structure
                this.display = display
                fbConfig = fbConfigList[0]
                dummyWin = win
                dummyCmap = cmap
            }
            nXFree(visualInfo.adr)
            XFree(fbConfigList)

            pfInfo.adr
        }
    }
}