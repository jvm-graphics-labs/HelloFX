package com.sun

import glm_.BYTES
import glm_.b
import glm_.i
import glm_.s
import kool.Ptr
import kool.adr
import org.lwjgl.system.MemoryUtil
import org.lwjgl.system.MemoryUtil.*
import org.lwjgl.system.windows.PIXELFORMATDESCRIPTOR
import org.lwjgl.system.windows.WNDCLASSEX
import org.lwjgl.system.windows.WindowProc
import uno.glfw.HWND
import java.nio.ByteBuffer

typealias HDC = Ptr
typealias HGLRC = Ptr
typealias WNDPROC = Ptr
typealias HINSTANCE = Ptr
typealias HICON = Ptr
typealias HCURSOR = Ptr
typealias HBRUSH = Ptr
typealias HWND = UInt
typealias HMENU = Long

typealias GLXContext = Long
typealias GLXFBConfig = Long
typealias Window = Long
typealias Colormap = Long

val HWND.isValid get() = L != MemoryUtil.NULL
val HWND.isInvalid get() = L == MemoryUtil.NULL

val COLOR_WINDOWFRAME = 6L

var WNDCLASSEX.size: Int
    get() = WNDCLASSEX.ncbSize(adr)
    set(value) = WNDCLASSEX.ncbSize(adr, value)
var WNDCLASSEX.style: Int
    get() = WNDCLASSEX.nstyle(adr)
    set(value) = WNDCLASSEX.nstyle(adr, value)
var WNDCLASSEX.wndProc: WindowProc
    get() = WNDCLASSEX.nlpfnWndProc(adr)
    set(value) = WNDCLASSEX.nlpfnWndProc(adr, value)
var WNDCLASSEX.clsExtra: Int
    get() = WNDCLASSEX.ncbClsExtra(adr)
    set(value) = WNDCLASSEX.ncbClsExtra(adr, value)
var WNDCLASSEX.wndExtra: Int
    get() = WNDCLASSEX.ncbWndExtra(adr)
    set(value) = WNDCLASSEX.ncbWndExtra(adr, value)
var WNDCLASSEX.instance: HINSTANCE
    get() = WNDCLASSEX.nhInstance(adr)
    set(value) = WNDCLASSEX.nhInstance(adr, value)
var WNDCLASSEX.icon: HICON
    get() = WNDCLASSEX.nhIcon(adr)
    set(value) = WNDCLASSEX.nhIcon(adr, value)
var WNDCLASSEX.cursor: HCURSOR
    get() = WNDCLASSEX.nhCursor(adr)
    set(value) = WNDCLASSEX.nhCursor(adr, value)
var WNDCLASSEX.background: HBRUSH
    get() = WNDCLASSEX.nhbrBackground(adr)
    set(value) = WNDCLASSEX.nhbrBackground(adr, value)
var WNDCLASSEX.menuName: ByteBuffer?
    get() = WNDCLASSEX.nlpszMenuName(adr)
    set(value) = WNDCLASSEX.nlpszMenuName(adr, value)
var WNDCLASSEX.className: ByteBuffer
    get() = WNDCLASSEX.nlpszClassName(adr)
    set(value) = WNDCLASSEX.nlpszClassName(adr, value)

var PIXELFORMATDESCRIPTOR.size: Int
    get() = PIXELFORMATDESCRIPTOR.nnSize(adr).i
    set(value) = PIXELFORMATDESCRIPTOR.nnSize(adr, value.s)
var PIXELFORMATDESCRIPTOR.version: Int
    get() = PIXELFORMATDESCRIPTOR.nnVersion(adr).i
    set(value) = PIXELFORMATDESCRIPTOR.nnVersion(adr, value.s)
var PIXELFORMATDESCRIPTOR.flags: Int
    get() = PIXELFORMATDESCRIPTOR.ndwFlags(adr)
    set(value) = PIXELFORMATDESCRIPTOR.ndwFlags(adr, value)
var PIXELFORMATDESCRIPTOR.pixelType: Byte
    get() = PIXELFORMATDESCRIPTOR.niPixelType(adr)
    set(value) = PIXELFORMATDESCRIPTOR.niPixelType(adr, value)
var PIXELFORMATDESCRIPTOR.colorBits: Int
    get() = PIXELFORMATDESCRIPTOR.ncColorBits(adr).i
    set(value) = PIXELFORMATDESCRIPTOR.ncColorBits(adr, value.b)
var PIXELFORMATDESCRIPTOR.redBits: Int
    get() = PIXELFORMATDESCRIPTOR.ncRedBits(adr).i
    set(value) = PIXELFORMATDESCRIPTOR.ncRedBits(adr, value.b)
var PIXELFORMATDESCRIPTOR.redShift: Int
    get() = PIXELFORMATDESCRIPTOR.ncRedShift(adr).i
    set(value) = PIXELFORMATDESCRIPTOR.ncRedShift(adr, value.b)
var PIXELFORMATDESCRIPTOR.greenBits: Int
    get() = PIXELFORMATDESCRIPTOR.ncGreenBits(adr).i
    set(value) = PIXELFORMATDESCRIPTOR.ncGreenBits(adr, value.b)
var PIXELFORMATDESCRIPTOR.greenShift: Int
    get() = PIXELFORMATDESCRIPTOR.ncGreenShift(adr).i
    set(value) = PIXELFORMATDESCRIPTOR.ncGreenShift(adr, value.b)
var PIXELFORMATDESCRIPTOR.blueBits: Int
    get() = PIXELFORMATDESCRIPTOR.ncBlueBits(adr).i
    set(value) = PIXELFORMATDESCRIPTOR.ncBlueBits(adr, value.b)
var PIXELFORMATDESCRIPTOR.blueShift: Int
    get() = PIXELFORMATDESCRIPTOR.ncBlueShift(adr).i
    set(value) = PIXELFORMATDESCRIPTOR.ncBlueShift(adr, value.b)
var PIXELFORMATDESCRIPTOR.alphaBits: Int
    get() = PIXELFORMATDESCRIPTOR.ncAlphaBits(adr).i
    set(value) = PIXELFORMATDESCRIPTOR.ncAlphaBits(adr, value.b)
var PIXELFORMATDESCRIPTOR.alphaShift: Int
    get() = PIXELFORMATDESCRIPTOR.ncAlphaShift(adr).i
    set(value) = PIXELFORMATDESCRIPTOR.ncAlphaShift(adr, value.b)
var PIXELFORMATDESCRIPTOR.accumBits: Int
    get() = PIXELFORMATDESCRIPTOR.ncAccumBits(adr).i
    set(value) = PIXELFORMATDESCRIPTOR.ncAccumBits(adr, value.b)
var PIXELFORMATDESCRIPTOR.accumRedBits: Int
    get() = PIXELFORMATDESCRIPTOR.ncAccumRedBits(adr).i
    set(value) = PIXELFORMATDESCRIPTOR.ncAccumRedBits(adr, value.b)
var PIXELFORMATDESCRIPTOR.accumGreenBits: Int
    get() = PIXELFORMATDESCRIPTOR.ncAccumGreenBits(adr).i
    set(value) = PIXELFORMATDESCRIPTOR.ncAccumGreenBits(adr, value.b)
var PIXELFORMATDESCRIPTOR.accumBlueBits: Int
    get() = PIXELFORMATDESCRIPTOR.ncAccumBlueBits(adr).i
    set(value) = PIXELFORMATDESCRIPTOR.ncAccumBlueBits(adr, value.b)
var PIXELFORMATDESCRIPTOR.accumAlphaBits: Int
    get() = PIXELFORMATDESCRIPTOR.ncAccumAlphaBits(adr).i
    set(value) = PIXELFORMATDESCRIPTOR.ncAccumAlphaBits(adr, value.b)
var PIXELFORMATDESCRIPTOR.depthBits: Int
    get() = PIXELFORMATDESCRIPTOR.ncDepthBits(adr).i
    set(value) = PIXELFORMATDESCRIPTOR.ncDepthBits(adr, value.b)
var PIXELFORMATDESCRIPTOR.stencilBits: Int
    get() = PIXELFORMATDESCRIPTOR.ncStencilBits(adr).i
    set(value) = PIXELFORMATDESCRIPTOR.ncStencilBits(adr, value.b)
var PIXELFORMATDESCRIPTOR.auxBuffers: Int
    get() = PIXELFORMATDESCRIPTOR.ncAuxBuffers(adr).i
    set(value) = PIXELFORMATDESCRIPTOR.ncAuxBuffers(adr, value.b)
var PIXELFORMATDESCRIPTOR.layerType: Byte
    get() = PIXELFORMATDESCRIPTOR.niLayerType(adr)
    set(value) = PIXELFORMATDESCRIPTOR.niLayerType(adr, value)
var PIXELFORMATDESCRIPTOR.reserved: Int
    get() = PIXELFORMATDESCRIPTOR.nbReserved(adr).i
    set(value) = PIXELFORMATDESCRIPTOR.nbReserved(adr, value.b)
var PIXELFORMATDESCRIPTOR.layerMask: Int
    get() = PIXELFORMATDESCRIPTOR.ndwLayerMask(adr)
    set(value) = PIXELFORMATDESCRIPTOR.ndwLayerMask(adr, value)
var PIXELFORMATDESCRIPTOR.visibleMask: Int
    get() = PIXELFORMATDESCRIPTOR.ndwVisibleMask(adr)
    set(value) = PIXELFORMATDESCRIPTOR.ndwVisibleMask(adr, value)
var PIXELFORMATDESCRIPTOR.damageMask: Int
    get() = PIXELFORMATDESCRIPTOR.ndwDamageMask(adr)
    set(value) = PIXELFORMATDESCRIPTOR.ndwDamageMask(adr, value)