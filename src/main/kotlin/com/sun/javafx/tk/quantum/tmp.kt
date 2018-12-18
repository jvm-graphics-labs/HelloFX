package com.sun.javafx.tk.quantum

import glm_.mat3x3.Mat3
import glm_.mat3x3.Mat3d
import glm_.mat4x4.Mat4
import glm_.mat4x4.Mat4d
import glm_.vec3.Vec3d
import gln.glGetVec4i
import gln.glScissor
import gln.glViewport
import gln.glf.semantic
import gln.uniform.glUniform
import gln.uniform.glUniform3f
import kool.adr
import kool.stak
import org.lwjgl.opengl.GL15C
import org.lwjgl.opengl.GL20C
import org.lwjgl.opengl.GL30C
import org.lwjgl.opengl.GL33C

fun times(res: Vec3d, a: Mat4d, b0: Double, b1: Double, b2: Double): Vec3d {
    res[0] = a[0, 0] * b0 + a[1, 0] * b1 + a[2, 0] * b2
    res[1] = a[0, 1] * b0 + a[1, 1] * b1 + a[2, 1] * b2
    res[2] = a[0, 2] * b0 + a[1, 2] * b1 + a[2, 2] * b2
    return res
}

fun Mat4d.timesAssign(b: Vec3d) = times(b, this, b.x, b.y, b.z)

fun glUniform3f(location: Int, vec3d: Vec3d) = glUniform3f(location, vec3d.x, vec3d.y, vec3d.z)

fun Mat4d.normal(res: Mat3d = Mat3d()): Mat3d {
    val v00v11 = v00 * v11
    val v01v10 = v01 * v10
    val v02v10 = v02 * v10
    val v00v12 = v00 * v12
    val v01v12 = v01 * v12
    val v02v11 = v02 * v11
    val det = (v00v11 - v01v10) * v22 + (v02v10 - v00v12) * v21 + (v01v12 - v02v11) * v20
    val s = 1.0 / det
    // Invert and transpose in one go
    res.v00 = (v11 * v22 - v21 * v12) * s
    res.v01 = (v20 * v12 - v10 * v22) * s
    res.v02 = (v10 * v21 - v20 * v11) * s
    res.v10 = (v21 * v02 - v01 * v22) * s
    res.v11 = (v00 * v22 - v20 * v02) * s
    res.v12 = (v20 * v01 - v00 * v21) * s
    res.v20 = (v01v12 - v02v11) * s
    res.v21 = (v02v10 - v00v12) * s
    res.v22 = (v00v11 - v01v10) * s
    return res
}

fun glUniform(location: Int, mat3: Mat3d) = glUniform(location, Mat3(mat3))

fun glUniform(location: Int, mat4: Mat4d) = glUniform(location, Mat4(mat4))

fun untouchGL(block: () -> Unit) {

    // Backup GL state
    val lastActiveTexture = GL20C.glGetInteger(GL20C.GL_ACTIVE_TEXTURE)
    GL20C.glActiveTexture(GL20C.GL_TEXTURE0 + semantic.sampler.DIFFUSE)
    val lastProgram = GL20C.glGetInteger(GL20C.GL_CURRENT_PROGRAM)
    val lastTexture = GL20C.glGetInteger(GL20C.GL_TEXTURE_BINDING_2D)
    val lastSampler = GL20C.glGetInteger(GL33C.GL_SAMPLER_BINDING)
    val lastArrayBuffer = GL20C.glGetInteger(GL15C.GL_ARRAY_BUFFER_BINDING)
    val lastVertexArray = GL20C.glGetInteger(GL30C.GL_VERTEX_ARRAY_BINDING)
    val lastPolygonMode = GL20C.glGetInteger(GL20C.GL_POLYGON_MODE)
    val lastViewport = glGetVec4i(GL20C.GL_VIEWPORT)
    val lastScissorBox = glGetVec4i(GL20C.GL_SCISSOR_BOX)
    val lastBlendSrcRgb = GL20C.glGetInteger(GL20C.GL_BLEND_SRC_RGB)
    val lastBlendDstRgb = GL20C.glGetInteger(GL20C.GL_BLEND_DST_RGB)
    val lastBlendSrcAlpha = GL20C.glGetInteger(GL20C.GL_BLEND_SRC_ALPHA)
    val lastBlendDstAlpha = GL20C.glGetInteger(GL20C.GL_BLEND_DST_ALPHA)
    val lastBlendEquationRgb = GL20C.glGetInteger(GL20C.GL_BLEND_EQUATION_RGB)
    val lastBlendEquationAlpha = GL20C.glGetInteger(GL20C.GL_BLEND_EQUATION_ALPHA)
    val lastEnableBlend = GL20C.glIsEnabled(GL20C.GL_BLEND)
    val lastEnableCullFace = GL20C.glIsEnabled(GL20C.GL_CULL_FACE)
    val lastEnableDepthTest = GL20C.glIsEnabled(GL20C.GL_DEPTH_TEST)
    val lastEnableScissorTest = GL20C.glIsEnabled(GL20C.GL_SCISSOR_TEST)

    block()

    // Restore modified GL state
    GL20C.glUseProgram(lastProgram)
    GL20C.glBindTexture(GL20C.GL_TEXTURE_2D, lastTexture)
    GL33C.glBindSampler(0, lastSampler)
    GL20C.glActiveTexture(lastActiveTexture)
    GL30C.glBindVertexArray(lastVertexArray)
    GL20C.glBindBuffer(GL20C.GL_ARRAY_BUFFER, lastArrayBuffer)
    GL20C.glBlendEquationSeparate(lastBlendEquationRgb, lastBlendEquationAlpha)
    GL20C.glBlendFuncSeparate(lastBlendSrcRgb, lastBlendDstRgb, lastBlendSrcAlpha, lastBlendDstAlpha)
    if (lastEnableBlend) GL20C.glEnable(GL20C.GL_BLEND) else GL20C.glDisable(GL20C.GL_BLEND)
    if (lastEnableCullFace) GL20C.glEnable(GL20C.GL_CULL_FACE) else GL20C.glDisable(GL20C.GL_CULL_FACE)
    if (lastEnableDepthTest) GL20C.glEnable(GL20C.GL_DEPTH_TEST) else GL20C.glDisable(GL20C.GL_DEPTH_TEST)
    if (lastEnableScissorTest) GL20C.glEnable(GL20C.GL_SCISSOR_TEST) else GL20C.glDisable(GL20C.GL_SCISSOR_TEST)
    GL20C.glPolygonMode(GL20C.GL_FRONT_AND_BACK, lastPolygonMode)
    glViewport(lastViewport)
    glScissor(lastScissorBox)
}