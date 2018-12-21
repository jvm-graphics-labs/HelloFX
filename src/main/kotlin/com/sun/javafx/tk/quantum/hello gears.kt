package com.sun.javafx.tk.quantum

import glm_.d
import glm_.glm
import glm_.vec2.Vec2i
import glm_.vec4.Vec4
import gln.glClearColor
import gln.glGetVec4i
import gln.glScissor
import gln.glViewport
import gln.glf.semantic
import javafx.animation.AnimationTimer
import javafx.application.Application
import javafx.application.Platform
import javafx.scene.Group
import javafx.scene.PerspectiveCamera
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.stage.Stage
import javafx.util.Duration
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11


fun main() {
//    System.setProperty("quantum.multithreaded", "false");
    System.setProperty("quantum.verbose", "false");
    System.setProperty("quantum.debug", "false");
    System.setProperty("prism.dirtyopts", "false") // force full window rendering
    System.setProperty("javafx.animation.fullspeed", "true")
    Application.launch(HelloGears::class.java)
}

class HelloGears : Application() {

    val clearColor = Vec4(1f, 0.5f, 0f, 1f)
    lateinit var gears: Gears
    val size = Vec2i(1024, 768)

    init {
        object : AnimationTimer() {
            override fun handle(now: Long) {}
        }.start()
    }

    override fun start(primaryStage: Stage) {
        primaryStage.title = "Hello Gears"
        val label = Label("clearColor: $clearColor")

        val root = Group().apply {
            children += HBox(20.0,
                    Button("Change background").apply {
                        setOnAction {
                            for (i in 0..3)
                                clearColor[i] = glm.linearRand(0f, 1f)
                            label.text = "clearColor: $clearColor"
                        }
                    }, label)
        }

        primaryStage.apply {
            scene = Scene(root).apply {
                camera = PerspectiveCamera()
            }
            x = 0.0
            y = 0.0
            width = size.x.d
            height = size.y.d
            show()
        }

        ViewPainter.init = Runnable {
            GL.createCapabilities()
            untouchGL { gears = Gears() }
        }
        ViewPainter.clear = Runnable {

            untouchGL {
                glClearColor(clearColor)
                gears.reshape(size)
                gears.draw()
            }
        }
    }
}