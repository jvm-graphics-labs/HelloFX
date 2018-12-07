//package com.sun.javafx.tk.quantum
//
//import javafx.application.Application
//import javafx.scene.Scene
//import javafx.scene.control.Label
//import javafx.scene.layout.StackPane
//import javafx.stage.Stage
//import com.sun.javafx.tk.quantum.ViewPainter
//import org.lwjgl.opengl.GL
//import org.lwjgl.opengl.GL11
//
//fun main() = Application.launch(HelloFX::class.java)
//
//class HelloFX : Application() {
//
//    override fun start(stage: Stage) {
//        val javaVersion = System.getProperty("java.version")
//        val javafxVersion = System.getProperty("javafx.version")
//        val l = Label("Hello, JavaFX $javafxVersion, running on Java $javaVersion.")
//        val scene = Scene(StackPane(l), 640.0, 480.0)
//        stage.scene = scene
//        stage.show()
//
//
//
//        ViewPainter.begin_doPaint = Runnable { GL.createCapabilities() }
//        ViewPainter.clear_doPaint = Runnable {
//            GL11.glClearColor(1f, 0.5f, 0f, 1f)
//            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)
//        }
//        ViewPainter.end_doPaint = Runnable { println("end") }
//    }
//}