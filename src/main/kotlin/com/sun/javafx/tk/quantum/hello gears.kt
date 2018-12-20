package com.sun.javafx.tk.quantum

import glm_.d
import glm_.glm
import glm_.vec2.Vec2i
import glm_.vec4.Vec4
import gln.glClearColor
import javafx.animation.AnimationTimer
import javafx.animation.TranslateTransition
import javafx.application.Application
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

    //Drawing a Circle
    val circle = Circle().apply {
        centerX = 150.0
        centerY = 135.0
        radius = 1.0
        fill = Color.BROWN
        strokeWidth = 20.0
    }

    init {
        TranslateTransition().apply {
            duration = Duration.millis(1000.0)
            node = circle
            byX = 300.0
            cycleCount = 50
            isAutoReverse = false
//            play()
        }
        object : AnimationTimer() {
            override fun handle(now: Long) {}
        }.start()
    }

    override fun start(primaryStage: Stage) {
        primaryStage.title = "Hello Gears"
        val label = Label("clearColor: $clearColor")

        val root = Group(circle).apply {
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

//    val frameFrequency = Duration.ofMillis(10)
//    val queueCapacity = 8  // may need tuning
//    val frameQueue = ArrayBlockingQueue<Image>(queueCapacity)
//
//    val nextFrame = AtomicReference<Image>()
//
//    val display = ImageView()
//
//    val fileReadThread = Thread {
//        // pseudocode...
////        while (moreImagesToRead()) {
////            val image = readImageFromFile()
////            frameQueue.put(image)
////        }
//    }
//
//    init {
//
//        fileReadThread.isDaemon = true
//        fileReadThread.start()
//
//        val service = object : ScheduledService<Void>() {
//            override fun createTask() = object : Task<Void>() {
//                @Throws(InterruptedException::class)
//                override fun call(): Void? {
//                    val image = frameQueue.take()
//                    if (nextFrame.getAndSet(image) == null) {
//                        Platform.runLater {
//                            val img = nextFrame.getAndSet(null)
//                            // display img:
//                            display.image = img
//                        }
//                    }
//                    return null
//                }
//            }
//        }
//        service.start()
//    }
}