package com.sun.javafx.tk.quantum

import com.sun.javafx.geom.Vec3f
import com.sun.prism.es2._WinGLContext
import com.sun.prism.es2._WinGLFactory
import com.sun.prism.es2._WinGLPixelFormat
import glm_.d
import glm_.f
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import javafx.application.Application
import javafx.scene.Group
import javafx.scene.PerspectiveCamera
import javafx.scene.Scene
import javafx.scene.shape.MeshView
import javafx.scene.shape.TriangleMesh
import javafx.scene.shape.VertexFormat
import javafx.scene.transform.Rotate
import javafx.stage.Stage

fun main() {
    Application.launch(MeshVertexBufferLengthTest::class.java)
}

class MeshVertexBufferLengthTest : Application() {

    // Application class. An instance is created and initialized before running
    // the first test, and it lives through the execution of all tests.

    var primaryStage: Stage? = null
    lateinit var meshView: MeshView

    override fun start(primaryStage: Stage) {
        primaryStage.title = "PNTMeshVertexBufferLengthTest"
        val triangleMesh = TriangleMesh(VertexFormat.POINT_NORMAL_TEXCOORD)
        meshView = MeshView(triangleMesh)
        val rotateGrp = Group(meshView).apply {
            rotate = -30.0
            rotationAxis = Rotate.X_AXIS
        }
        val translateGrp = Group(rotateGrp).apply {
            translateX = 200.0
            translateY = 200.0
            translateZ = 100.0
        }
        val root = Group(translateGrp)

        this.primaryStage = primaryStage.apply {
            scene = Scene(root).apply {
                camera = PerspectiveCamera()
            }
            x = 0.0
            y = 0.0
            width = 400.0
            height = 400.0
            show()
        }

        //            buildTriangleMesh(meshView, 0, 0, meshScale);
        //            buildTriangleMesh(meshView, 1, 1, meshScale);
        //            buildTriangleMesh(meshView, 2, 2, meshScale);
        //            buildTriangleMesh(meshView, 7, 7, meshScale);
        buildTriangleMesh(meshView, 50, 50, meshScale)

        //            ViewPainter.begin_doPaint = GL::createCapabilities;
        //            ViewPainter.clear_doPaint = () -> {
        //                GL11.glClearColor(1f, 0.5f, 1f, 1f);
        //                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        //            };
        //            ViewPainter.end_doPaint = () -> System.out.println("end");
    }

    companion object {

        private val meshScale = 15f
        private val minX = -10f
        private val minY = -10f
        private val maxX = 10f
        private val maxY = 10f
        private val funcValue = -10.0f

        private val v1 = Vec3f()
        private val v2 = Vec3f()

        private fun computeNormal(points: Array<Vec3>): Vec3 {
            // compute Normal |(v1-v0)X(v2-v0)|
            val (a, b, c) = points
            val v1 = b - a
            val v2 = c - a
            return (v1 cross v2).normalize()
        }

        private fun getSinDivX(x: Double, y: Double): Double {
            val r = Math.sqrt(x * x + y * y)
            return funcValue * if (r == 0.0) 1.0 else Math.sin(r) / r
        }

        private fun buildTriangleMesh(meshView: MeshView, subDivX: Int, subDivY: Int, scale: Float) {

            val pointSize = Vec3.length
            val normalSize = Vec3.length
            val texCoordSize = Vec2.length
            val faceSize = 3 * 3 // 3 point indices, 3 normal indices and 3 texCoord indices per triangle
            val numDivX = subDivX + 1
            val numVerts = (subDivY + 1) * numDivX
            val points = FloatArray(numVerts * pointSize)
            val texCoords = FloatArray(numVerts * texCoordSize)
            val faceCount = subDivX * subDivY * 2
            val normals = FloatArray(faceCount * normalSize)
            val faces = IntArray(faceCount * faceSize)

            // Create points and texCoords
            for (y in 0..subDivY) {
                val dy = y.f / subDivY
                val fy = ((1 - dy) * minY + dy * maxY).d
                for (x in 0..subDivX) {
                    val dx = x.f / subDivX
                    val fx = ((1 - dx) * minX + dx * maxX).d
                    var index = y * numDivX * pointSize + x * pointSize
                    points[index] = fx.f * scale
                    points[index + 1] = fy.f * scale
                    points[index + 2] = getSinDivX(fx, fy).f * scale
                    index = y * numDivX * texCoordSize + x * texCoordSize
                    texCoords[index] = dx
                    texCoords[index + 1] = dy
                }
            }

            // Initial faces and normals
            var normalCount = 0
            val triPoints = Array(3) { Vec3() }
            for (y in 0 until subDivY)
                for (x in 0 until subDivX) {
                    val p00 = y * numDivX + x
                    val p01 = p00 + 1
                    val p10 = p00 + numDivX
                    val p11 = p10 + 1
                    val tc00 = y * numDivX + x
                    val tc01 = tc00 + 1
                    val tc10 = tc00 + numDivX
                    val tc11 = tc10 + 1

                    triPoints[0].put(points, p00 * 3)
                    triPoints[1].put(points, p10 * 3)
                    triPoints[2].put(points, p11 * 3)
                    computeNormal(triPoints).let { normal ->
                        val normalIndex = normalCount * normalSize
                        normal.to(normals, normalIndex)
                    }
                    var index = (y * subDivX * faceSize + x * faceSize) * 2
                    faces[index + 0] = p00
                    faces[index + 1] = normalCount
                    faces[index + 2] = tc00
                    faces[index + 3] = p10
                    faces[index + 4] = normalCount
                    faces[index + 5] = tc10
                    faces[index + 6] = p11
                    faces[index + 7] = normalCount++
                    faces[index + 8] = tc11
                    index += faceSize

                    triPoints[0].put(points, p11 * 3)
                    triPoints[1].put(points, p01 * 3)
                    triPoints[2].put(points, p00 * 3)
                    computeNormal(triPoints).let { normal ->
                        val normalIndex = normalCount * normalSize
                        normal.to(normals, normalIndex)
                    }
                    faces[index + 0] = p11
                    faces[index + 1] = normalCount
                    faces[index + 2] = tc11
                    faces[index + 3] = p01
                    faces[index + 4] = normalCount
                    faces[index + 5] = tc01
                    faces[index + 6] = p00
                    faces[index + 7] = normalCount++
                    faces[index + 8] = tc00
                }

            meshView.mesh = TriangleMesh(VertexFormat.POINT_NORMAL_TEXCOORD).also {
                it.points.setAll(*points)
                it.normals.setAll(*normals)
                it.texCoords.setAll(*texCoords)
                it.faces.setAll(*faces)
            }
        }

        val f = _WinGLFactory
        val p = _WinGLPixelFormat
        val c = _WinGLContext
    }
}