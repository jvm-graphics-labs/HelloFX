package com.sun.javafx.tk.quantum

import glm_.d
import glm_.f
import glm_.func.cos
import glm_.func.sin
import glm_.glm
import glm_.mat4x4.Mat4d
import glm_.vec2.Vec2i
import glm_.vec3.Vec3d
import glm_.vec4.Vec4
import gln.draw.glDrawArrays
import gln.glViewport
import gln.glf.glf
import gln.glf.semantic
import gln.program.GlslProgram
import gln.uniform.glUniform
import gln.vertexArray.glBindVertexArray
import gln.vertexArray.glEnableVertexAttribArray
import gln.vertexArray.glVertexAttribPointer
import kool.FloatBuffer
import kool.IntBuffer
import kool.use
import org.lwjgl.opengl.GL15.glGenBuffers
import org.lwjgl.opengl.GL15C.*
import org.lwjgl.opengl.GL20C.glGetUniformLocation
import org.lwjgl.opengl.GL30C.glGenVertexArrays
import uno.glfw.GlfwWindow
import uno.glfw.glfw
import java.lang.Math.PI
import java.lang.Math.sqrt

fun main() {

    glfw.init("3.3")

    val window = GlfwWindow(1280, 720, "Gears").apply {
        init()
    }

    val gears = Gears()

    window.loop {
        gears.reshape(window.size)
        gears.draw()
    }
}

class Gears {

    val gear1 = Gear(1.0, 4.0, 1.0, 20, 0.7, Vec4(0.8f, 0.1f, 0.0f, 1.0f))
    val gear2 = Gear(0.5, 2.0, 2.0, 10, 0.7, Vec4(0.0f, 0.8f, 0.2f, 1.0f))
    val gear3 = Gear(1.3, 2.0, 0.5, 10, 0.7, Vec4(0.2f, 0.2f, 1.0f, 1.0f))

    val program = Program()

    val proj = glm.frustum(-1.0, 1.0, -1.0, 1.0, 5.0, 100.0)
    val view = Mat4d()
    var model = Mat4d()

    val light = Vec3d()

    var count = 0
    var startTime = System.currentTimeMillis() / 1000.0

    val viewRot = Vec3d(20.0, 30.0, 0.0)

    var distance = 40.0
    var angle = 0.0

    fun reshape(size: Vec2i) {

        val h = size.aspect.d

        glViewport(size)
        proj(glm.frustum(-1.0, 1.0, -1 / h, 1 / h, 5.0, 100.0))

        distance = if (size.x < size.y) 40.0 else 80.0
    }

    fun draw() {

        angle += 2.0

        glDisable(GL_CULL_FACE)
        glEnable(GL_DEPTH_TEST)

        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        program.use()

        // VIEW
        view(1.0)
                .translateAssign(0.0, 0.0, -distance)
                .rotateXYZassign(viewRot * PI / 180)

        // LIGHT
        view.timesAssign(light(5.0, 5.0, 10.0)).normalizeAssign()
        glUniform3f(program.light, light)

        // GEAR 1
        model(1.0)
                .translateAssign(-3.0, -2.0, 0.0)
                .rotateZassign(angle * PI / 180)
        gear1.draw()

        // GEAR 2
        model(1.0)
                .translateAssign(3.1, -2.0, 0.0)
                .rotateZassign((-2 * angle - 9) * PI / 180)
        gear2.draw()

        // GEAR 3
        model(1.0)
                .translateAssign(-3.1, 4.2, 0.0)
                .rotateZassign((-2 * angle - 25) * PI / 180)
        gear3.draw()

        count++

        val theTime = System.currentTimeMillis() / 1000.0
        if (theTime >= startTime + 1.0) {
            println("$count fps")
            startTime = theTime
            count = 0
        }
    }

    fun Gear.draw() {
        val mv = view * model
        glUniform(program.mv3, mv.normal())
        glUniform(program.mvp, proj * mv)
        glUniform(program.color, color)

        glBindVertexArray(vao)
        glDrawArrays(vertexCount)
    }
}

class Gear(innerRadius: Double, outerRadius: Double, width: Double, teeth: Int, toothDepth: Double,
           val color: Vec4) {

    val vao = IntBuffer(1)
    var vertexCount = 0

    init {

        val vertices = arrayListOf<Double>()

        val r0 = innerRadius
        val r1 = outerRadius - toothDepth / 2
        val r2 = outerRadius + toothDepth / 2

        var da = 2 * PI / teeth / 4

        val currentNormal = Vec3d(0.0, 0.0, 1.0)

        fun addVertex(x: Double, y: Double, z: Double) {
            vertices += x
            vertices += y
            vertices += z
            vertices += currentNormal.x
            vertices += currentNormal.y
            vertices += currentNormal.z
            vertexCount += 1
        }

        // draw front face
        for (i in 0..teeth) {
            val angle = i * 2 * PI / teeth
            val da3 = 4 * da

            // step 1
            addVertex(r0 * angle.cos, r0 * angle.sin, width * 0.5)
            addVertex(r1 * angle.cos, r1 * angle.sin, width * 0.5)
            addVertex(r1 * (angle - da).cos, r1 * (angle - da).sin, width * 0.5)

            addVertex(r0 * angle.cos, r0 * angle.sin, width * 0.5)
            addVertex(r1 * (angle - da).cos, r1 * (angle - da).sin, width * 0.5)
            addVertex(r0 * (angle - da).cos, r0 * (angle - da).sin, width * 0.5)

            // Step 2
            addVertex(r0 * (angle - da).cos, r0 * (angle - da).sin, width * 0.5)
            addVertex(r1 * (angle - da).cos, r1 * (angle - da).sin, width * 0.5)
            addVertex(r1 * (angle - da3).cos, r1 * (angle - da3).sin, width * 0.5)

            addVertex(r0 * (angle - da).cos, r0 * (angle - da).sin, width * 0.5)
            addVertex(r1 * (angle - da3).cos, r1 * (angle - da3).sin, width * 0.5)
            addVertex(r0 * (angle - da3).cos, r0 * (angle - da3).sin, width * 0.5)
        }

        // draw front sides of teeth
        da = 2 * PI / teeth / 4
        for (i in 0 until teeth) {
            val angle = i * 2 * PI / teeth

            addVertex(r1 * angle.cos, r1 * angle.sin, width * 0.5)
            addVertex(r2 * (angle + da).cos, r2 * (angle + da).sin, width * 0.5)
            addVertex(r2 * (angle + 2 * da).cos, r2 * (angle + 2 * da).sin, width * 0.5)

            addVertex(r1 * angle.cos, r1 * angle.sin, width * 0.5)
            addVertex(r2 * (angle + 2 * da).cos, r2 * (angle + 2 * da).sin, width * 0.5)
            addVertex(r1 * (angle + 3 * da).cos, r1 * (angle + 3 * da).sin, width * 0.5)
        }

        currentNormal(0.0, 0.0, -1.0)
        var da3 = 4 * da

        // draw back face 
        for (i in 0..teeth) {
            val angle = i * 2 * PI / teeth

            addVertex(r1 * angle.cos, r1 * angle.sin, -width * 0.5)
            addVertex(r0 * angle.cos, r0 * angle.sin, -width * 0.5)
            addVertex(r0 * (angle - da).cos, r0 * (angle - da).sin, -width * 0.5)

            addVertex(r1 * angle.cos, r1 * angle.sin, -width * 0.5)
            addVertex(r0 * (angle - da).cos, r0 * (angle - da).sin, -width * 0.5)
            addVertex(r1 * (angle - da).cos, r1 * (angle - da).sin, -width * 0.5)


            addVertex(r1 * (angle - da).cos, r1 * (angle - da).sin, -width * 0.5)
            addVertex(r0 * (angle - da).cos, r0 * (angle - da).sin, -width * 0.5)
            addVertex(r0 * (angle - da3).cos, r0 * (angle - da3).sin, -width * 0.5)

            addVertex(r1 * (angle - da).cos, r1 * (angle - da).sin, -width * 0.5)
            addVertex(r0 * (angle - da3).cos, r0 * (angle - da3).sin, -width * 0.5)
            addVertex(r1 * (angle - da3).cos, r1 * (angle - da3).sin, -width * 0.5)
        }

        // draw back sides of teeth
        da = 2 * PI / teeth / 4
        for (i in 0 until teeth) {
            val angle = i * 2 * PI / teeth

            addVertex(r1 * (angle + 3 * da).cos, r1 * (angle + 3 * da).sin, -width * 0.5)
            addVertex(r2 * (angle + 2 * da).cos, r2 * (angle + 2 * da).sin, -width * 0.5)
            addVertex(r2 * (angle + da).cos, r2 * (angle + da).sin, -width * 0.5)

            addVertex(r1 * (angle + 3 * da).cos, r1 * (angle + 3 * da).sin, -width * 0.5)
            addVertex(r2 * (angle + da).cos, r2 * (angle + da).sin, -width * 0.5)
            addVertex(r1 * angle.cos, r1 * angle.sin, -width * 0.5)
        }

        currentNormal(0.0, 0.0, -1.0) // Copied from above

        // draw outward faces of teeth
        for (i in 0 until teeth) {
            val angle = i * 2 * PI / teeth

            currentNormal(angle.cos, angle.sin, 0.0)
            var u = r2 * (angle + da).cos - r1 * angle.cos
            var v = r2 * (angle + da).sin - r1 * angle.sin
            val len = sqrt(u * u + v * v)
            u /= len
            v /= len

            // First quad
            addVertex(r1 * angle.cos, r1 * angle.sin, width * 0.5)
            addVertex(r1 * angle.cos, r1 * angle.sin, -width * 0.5)
            currentNormal(v, -u, 0.0)
            addVertex(r2 * (angle + da).cos, r2 * (angle + da).sin, -width * 0.5)

            currentNormal(angle.cos, angle.sin, 0.0)
            addVertex(r1 * angle.cos, r1 * angle.sin, width * 0.5)
            currentNormal(v, -u, 0.0)
            addVertex(r2 * (angle + da).cos, r2 * (angle + da).sin, width * 0.5)
            addVertex(r2 * (angle + da).cos, r2 * (angle + da).sin, -width * 0.5)

            // Second quad
            currentNormal(v, -u, 0.0)
            addVertex(r2 * (angle + da).cos, r2 * (angle + da).sin, width * 0.5)
            addVertex(r2 * (angle + da).cos, r2 * (angle + da).sin, -width * 0.5)
            currentNormal(angle.cos, angle.sin, 0.0)
            addVertex(r2 * (angle + 2 * da).cos, r2 * (angle + 2 * da).sin, -width * 0.5)

            currentNormal(v, -u, 0.0)
            addVertex(r2 * (angle + da).cos, r2 * (angle + da).sin, width * 0.5)
            currentNormal(angle.cos, angle.sin, 0.0)
            addVertex(r2 * (angle + 2 * da).cos, r2 * (angle + 2 * da).sin, width * 0.5)
            addVertex(r2 * (angle + 2 * da).cos, r2 * (angle + 2 * da).sin, -width * 0.5)

            // Third quad
            currentNormal(angle.cos, angle.sin, 0.0)
            addVertex(r2 * (angle + 2 * da).cos, r2 * (angle + 2 * da).sin, width * 0.5)
            addVertex(r2 * (angle + 2 * da).cos, r2 * (angle + 2 * da).sin, -width * 0.5)
            u = r1 * (angle + 3 * da).cos - r2 * (angle + 2 * da).cos
            v = r1 * (angle + 3 * da).sin - r2 * (angle + 2 * da).sin
            currentNormal(v, -u, 0.0)
            addVertex(r1 * (angle + 3 * da).cos, r1 * (angle + 3 * da).sin, -width * 0.5)

            currentNormal(angle.cos, angle.sin, 0.0)
            addVertex(r2 * (angle + 2 * da).cos, r2 * (angle + 2 * da).sin, width * 0.5)
            currentNormal(v, -u, 0.0)
            addVertex(r1 * (angle + 3 * da).cos, r1 * (angle + 3 * da).sin, width * 0.5)
            addVertex(r1 * (angle + 3 * da).cos, r1 * (angle + 3 * da).sin, -width * 0.5)

            // Fourth quad
            u = r1 * (angle + 3 * da).cos - r2 * (angle + 2 * da).cos
            v = r1 * (angle + 3 * da).sin - r2 * (angle + 2 * da).sin
            currentNormal(v, -u, 0.0)
            addVertex(r1 * (angle + 3 * da).cos, r1 * (angle + 3 * da).sin, width * 0.5)
            addVertex(r1 * (angle + 3 * da).cos, r1 * (angle + 3 * da).sin, -width * 0.5)
            currentNormal(angle.cos, angle.sin, 0.0)
            currentNormal((angle + 4 * da).cos, (angle + 4 * da).sin, 0.0)
            addVertex(r1 * (angle + 4 * da).cos, r1 * (angle + 4 * da).sin, -width * 0.5)

            u = r1 * (angle + 3 * da).cos - r2 * (angle + 2 * da).cos
            v = r1 * (angle + 3 * da).sin - r2 * (angle + 2 * da).sin
            currentNormal(v, -u, 0.0)
            addVertex(r1 * (angle + 3 * da).cos, r1 * (angle + 3 * da).sin, width * 0.5)
            currentNormal(angle.cos, angle.sin, 0.0)
            currentNormal((angle + 4 * da).cos, (angle + 4 * da).sin, 0.0)
            addVertex(r1 * (angle + 4 * da).cos, r1 * (angle + 4 * da).sin, width * 0.5)
            addVertex(r1 * (angle + 4 * da).cos, r1 * (angle + 4 * da).sin, -width * 0.5)

        }

        // draw inside radius cylinder
        da3 = 4 * da
        for (i in 0..teeth) {
            val angle = i * 2 * PI / teeth

            currentNormal(-angle.cos, -angle.sin, 0.0)
            addVertex(r0 * angle.cos, r0 * angle.sin, -width * 0.5)
            addVertex(r0 * angle.cos, r0 * angle.sin, width * 0.5)
            currentNormal(-(angle - da).cos, -(angle - da).sin, 0.0)
            addVertex(r0 * (angle - da).cos, r0 * (angle - da).sin, width * 0.5)

            currentNormal(-angle.cos, -angle.sin, 0.0)
            addVertex(r0 * angle.cos, r0 * angle.sin, -width * 0.5)
            currentNormal(-(angle - da).cos, -(angle - da).sin, 0.0)
            addVertex(r0 * (angle - da).cos, r0 * (angle - da).sin, width * 0.5)
            addVertex(r0 * (angle - da).cos, r0 * (angle - da).sin, -width * 0.5)


            currentNormal(-(angle - da).cos, -(angle - da).sin, 0.0)
            addVertex(r0 * (angle - da).cos, r0 * (angle - da).sin, -width * 0.5)
            addVertex(r0 * (angle - da).cos, r0 * (angle - da).sin, width * 0.5)
            currentNormal(-(angle - da3).cos, -(angle - da3).sin, 0.0)
            addVertex(r0 * (angle - da3).cos, r0 * (angle - da3).sin, width * 0.5)

            currentNormal(-(angle - da).cos, -(angle - da).sin, 0.0)
            addVertex(r0 * (angle - da).cos, r0 * (angle - da).sin, -width * 0.5)
            currentNormal(-(angle - da3).cos, -(angle - da3).sin, 0.0)
            addVertex(r0 * (angle - da3).cos, r0 * (angle - da3).sin, width * 0.5)
            addVertex(r0 * (angle - da3).cos, r0 * (angle - da3).sin, -width * 0.5)
        }

        // Build VAO and VBOs
        // Allocate and activate Vertex Array Object
        glGenVertexArrays(vao)
        glBindVertexArray(vao)
        // Allocate Vertex Buffer Object
        val buffer = glGenBuffers()

        // VBO for vertex data
        FloatBuffer(vertices.size) { vertices[it].f }.use {
            glBindBuffer(GL_ARRAY_BUFFER, buffer)
            glBufferData(GL_ARRAY_BUFFER, it, GL_STATIC_DRAW)
            glVertexAttribPointer(glf.pos3_nor3)
            glEnableVertexAttribArray(glf.pos3_nor3)
        }
    }
}

class Program : GlslProgram(
        vertSrc = """
            #version 330

            uniform mat4 mvp;
            uniform mat3 mv3;

            uniform vec3 light;

            layout(location = ${semantic.attr.POSITION}) in  vec3 position;
            layout(location = ${semantic.attr.NORMAL}) in  vec3 normal;

            out float shade;

            void main() {
                vec3 normal_ = normalize(mv3 * normal);
                shade = max(dot(normal_, light), 0.0);
                gl_Position = mvp * vec4(position, 1.0);
            }
            """.trimIndent(),
        fragSrc = """
            #version 330

            uniform vec4 color;

            in float shade;

            layout(location = ${semantic.frag.COLOR}) out vec4 outColor;

            void main() {
                outColor = vec4(color.xyz * shade, color.w);
            }
            """.trimIndent()) {

    val mvp = glGetUniformLocation(name, "mvp")
    val mv3 = glGetUniformLocation(name, "mv3")
    val light = glGetUniformLocation(name, "light")
    val color = glGetUniformLocation(name, "color")
}