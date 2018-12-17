package com.sun.javafx.tk.quantum

import glm_.mat3x3.Mat3d
import glm_.mat4x4.Mat4d
import glm_.vec3.Vec3d
import gln.uniform.glUniform3f
import kool.adr
import kool.stak
import org.lwjgl.opengl.GL20C

fun times(res: Vec3d, a: Mat4d, b0: Double, b1: Double, b2: Double): Vec3d {
    res[0] = a[0, 0] * b0 + a[1, 0] * b1 + a[2, 0] * b2 + a[3, 0]
    res[1] = a[0, 1] * b0 + a[1, 1] * b1 + a[2, 1] * b2 + a[3, 1]
    res[2] = a[0, 2] * b0 + a[1, 2] * b1 + a[2, 2] * b2 + a[3, 2]
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
    /* Invert and transpose in one go */
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

fun glUniform(location: Int, mat3: Mat3d) = stak {
    GL20C.nglUniformMatrix3fv(location, 1, false, mat3.toBuffer(it).adr)
}

fun glUniform(location: Int, mat4: Mat4d) = stak {
    GL20C.nglUniformMatrix4fv(location, 1, false, mat4.toBuffer(it).adr)
}