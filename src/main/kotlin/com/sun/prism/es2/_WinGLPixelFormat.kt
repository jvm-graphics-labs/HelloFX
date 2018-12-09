package com.sun.prism.es2

import com.sun.isInvalid
import org.lwjgl.system.MemoryStack.stackPush
import org.lwjgl.system.MemoryUtil.NULL
import org.lwjgl.system.windows.GDI32.ChoosePixelFormat
import org.lwjgl.system.windows.User32.GetDC

object _WinGLPixelFormat {

    init {

        WinGLPixelFormat.nCreatePixelFormat = WinGLPixelFormat.nCreatePixelFormat { _, attrs ->

            val szAppName = "Choose Pixel Format"

            if (attrs == null)
                return@nCreatePixelFormat NULL

            val stak = stackPush()
            val pfd = stak.getPFD(attrs)

            // RT-27438
            // TODO: Need to use nativeScreen to create this requested pixelformat
            // currently hack to work on a single monitor system
            val hwnd = stak.createDummyWindow(szAppName)

            if (hwnd.isInvalid)
                return@nCreatePixelFormat NULL

            val hdc = GetDC(hwnd.L)
            if (hdc == NULL) {
                printAndReleaseResources(hwnd, hdc = hdc, szAppName = szAppName, message = "Failed in GetDC")
                return@nCreatePixelFormat NULL
            }

            val pixelFormat = ChoosePixelFormat(hdc, pfd)
            if (pixelFormat < 1) {
                printAndReleaseResources(hwnd, hdc = hdc, szAppName = szAppName, message = "Failed in ChoosePixelFormat")
                return@nCreatePixelFormat NULL
            }

            /* allocate the structure */
            val pfInfo = PixelFormatInfo().apply {
                /* initialize the structure */
                this.pixelFormat = pixelFormat
                dummyHwnd = hwnd
                dummyHdc = hdc
                dummySzAppName = szAppName
            }
            stak.pop()
            pfInfo.adr
        }
    }
}