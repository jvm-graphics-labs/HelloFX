package com.sun.javafx.tk.quantum;

import com.sun.javafx.geom.Vec3f;
import com.sun.prism.es2._WinGLContext;
import com.sun.prism.es2._WinGLFactory;
import com.sun.prism.es2._WinGLPixelFormat;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.shape.VertexFormat;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.windows.User32;
import org.lwjgl.system.windows.WNDCLASSEX;
import org.lwjgl.system.windows.WindowProc;

import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;

import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.windows.WindowsLibrary.HINSTANCE;

public class PNTMeshVertexBufferLengthTest {

    // Sleep time showing/hiding window in milliseconds
    private static final int SLEEP_TIME = 1000;

    // Size of a vertex
    private static final int VERTEX_SIZE = 9;

    private static final float meshScale = 15;
    private static final float minX = -10;
    private static final float minY = -10;
    private static final float maxX = 10;
    private static final float maxY = 10;
    private static final float funcValue = -10.0f;

    private static final Vec3f v1 = new Vec3f();
    private static final Vec3f v2 = new Vec3f();

    private static void computeNormal(Vec3f pa, Vec3f pb, Vec3f pc, Vec3f normal) {
        // compute Normal |(v1-v0)X(v2-v0)|
        v1.sub(pb, pa);
        v2.sub(pc, pa);
        normal.cross(v1, v2);
        normal.normalize();
    }

    private static double getSinDivX(double x, double y) {
        double r = Math.sqrt(x * x + y * y);
        return funcValue * (r == 0 ? 1 : Math.sin(r) / r);
    }

    private static void buildTriangleMesh(MeshView meshView,
                                          int subDivX, int subDivY, float scale) {

        final int pointSize = 3;
        final int normalSize = 3;
        final int texCoordSize = 2;
        final int faceSize = 9; // 3 point indices, 3 normal indices and 3 texCoord indices per triangle
        int numDivX = subDivX + 1;
        int numVerts = (subDivY + 1) * numDivX;
        float points[] = new float[numVerts * pointSize];
        float texCoords[] = new float[numVerts * texCoordSize];
        int faceCount = subDivX * subDivY * 2;
        float normals[] = new float[faceCount * normalSize];
        int faces[] = new int[faceCount * faceSize];

        // Create points and texCoords
        for (int y = 0; y <= subDivY; y++) {
            float dy = (float) y / subDivY;
            double fy = (1 - dy) * minY + dy * maxY;
            for (int x = 0; x <= subDivX; x++) {
                float dx = (float) x / subDivX;
                double fx = (1 - dx) * minX + dx * maxX;
                int index = y * numDivX * pointSize + (x * pointSize);
                points[index] = (float) fx * scale;
                points[index + 1] = (float) fy * scale;
                points[index + 2] = (float) getSinDivX(fx, fy) * scale;
                index = y * numDivX * texCoordSize + (x * texCoordSize);
                texCoords[index] = dx;
                texCoords[index + 1] = dy;
            }
        }

        // Initial faces and normals
        int normalCount = 0;
        Vec3f[] triPoints = new Vec3f[3];
        triPoints[0] = new Vec3f();
        triPoints[1] = new Vec3f();
        triPoints[2] = new Vec3f();
        Vec3f normal = new Vec3f();
        for (int y = 0; y < subDivY; y++) {
            for (int x = 0; x < subDivX; x++) {
                int p00 = y * numDivX + x;
                int p01 = p00 + 1;
                int p10 = p00 + numDivX;
                int p11 = p10 + 1;
                int tc00 = y * numDivX + x;
                int tc01 = tc00 + 1;
                int tc10 = tc00 + numDivX;
                int tc11 = tc10 + 1;

                int ii = p00 * 3;
                triPoints[0].x = points[ii];
                triPoints[0].y = points[ii + 1];
                triPoints[0].z = points[ii + 2];
                ii = p10 * 3;
                triPoints[1].x = points[ii];
                triPoints[1].y = points[ii + 1];
                triPoints[1].z = points[ii + 2];
                ii = p11 * 3;
                triPoints[2].x = points[ii];
                triPoints[2].y = points[ii + 1];
                triPoints[2].z = points[ii + 2];
                computeNormal(triPoints[0], triPoints[1], triPoints[2], normal);

                int normalIndex = normalCount * normalSize;
                normals[normalIndex] = normal.x; //nx
                normals[normalIndex + 1] = normal.y; //ny
                normals[normalIndex + 2] = normal.z; //nz

                int index = (y * subDivX * faceSize + (x * faceSize)) * 2;
                faces[index + 0] = p00;
                faces[index + 1] = normalCount;
                faces[index + 2] = tc00;
                faces[index + 3] = p10;
                faces[index + 4] = normalCount;
                faces[index + 5] = tc10;
                faces[index + 6] = p11;
                faces[index + 7] = normalCount++;
                faces[index + 8] = tc11;
                index += faceSize;

                ii = p11 * 3;
                triPoints[0].x = points[ii];
                triPoints[0].y = points[ii + 1];
                triPoints[0].z = points[ii + 2];
                ii = p01 * 3;
                triPoints[1].x = points[ii];
                triPoints[1].y = points[ii + 1];
                triPoints[1].z = points[ii + 2];
                ii = p00 * 3;
                triPoints[2].x = points[ii];
                triPoints[2].y = points[ii + 1];
                triPoints[2].z = points[ii + 2];
                computeNormal(triPoints[0], triPoints[1], triPoints[2], normal);
                normalIndex = normalCount * normalSize;
                normals[normalIndex] = normal.x; //nx
                normals[normalIndex + 1] = normal.y; //ny
                normals[normalIndex + 2] = normal.z; //nz

                faces[index + 0] = p11;
                faces[index + 1] = normalCount;
                faces[index + 2] = tc11;
                faces[index + 3] = p01;
                faces[index + 4] = normalCount;
                faces[index + 5] = tc01;
                faces[index + 6] = p00;
                faces[index + 7] = normalCount++;
                faces[index + 8] = tc00;
            }
        }


        TriangleMesh triangleMesh = new TriangleMesh(VertexFormat.POINT_NORMAL_TEXCOORD);
        triangleMesh.getPoints().setAll(points);
        triangleMesh.getNormals().setAll(normals);
        triangleMesh.getTexCoords().setAll(texCoords);
        triangleMesh.getFaces().setAll(faces);
        meshView.setMesh(triangleMesh);
    }

    // Used to launch the application before running any test
    private static final CountDownLatch launchLatch = new CountDownLatch(1);

    // Singleton Application instance
    static MyApp myApp;

    private CountDownLatch latch = new CountDownLatch(1);

    // Application class. An instance is created and initialized before running
    // the first test, and it lives through the execution of all tests.
    public static class MyApp extends Application {

        Stage primaryStage = null;
        MeshView meshView;

        @Override
        public void init() {
            PNTMeshVertexBufferLengthTest.myApp = this;
        }

        @Override
        public void start(Stage primaryStage) throws Exception {
            primaryStage.setTitle("PNTMeshVertexBufferLengthTest");
            TriangleMesh triangleMesh = new TriangleMesh(VertexFormat.POINT_NORMAL_TEXCOORD);
            meshView = new MeshView(triangleMesh);
            Group rotateGrp = new Group(meshView);
            rotateGrp.setRotate(-30);
            rotateGrp.setRotationAxis(Rotate.X_AXIS);
            Group translateGrp = new Group(rotateGrp);
            translateGrp.setTranslateX(200);
            translateGrp.setTranslateY(200);
            translateGrp.setTranslateZ(100);
            Group root = new Group(translateGrp);

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setX(0);
            primaryStage.setY(0);
            primaryStage.setWidth(400);
            primaryStage.setHeight(400);
            PerspectiveCamera perspectiveCamera = new PerspectiveCamera();
            scene.setCamera(perspectiveCamera);
            primaryStage.show();
            this.primaryStage = primaryStage;
            launchLatch.countDown();

//
//
//            for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
//                System.out.println(ste);
//            }


//            buildTriangleMesh(myApp.meshView, 0, 0, meshScale);
//            buildTriangleMesh(myApp.meshView, 1, 1, meshScale);
//            buildTriangleMesh(myApp.meshView, 2, 2, meshScale);
//            buildTriangleMesh(myApp.meshView, 7, 7, meshScale);
            buildTriangleMesh(myApp.meshView, 50, 50, meshScale);


//            custom.init());

//            ViewPainter.begin_doPaint = GL::createCapabilities;
//            ViewPainter.clear_doPaint = () -> {
//                GL11.glClearColor(1f, 0.5f, 1f, 1f);
//                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
//            };
//            ViewPainter.end_doPaint = () -> System.out.println("end");
        }
    }

    public static void main(String[] args) throws InterruptedException {

        _WinGLFactory f = _WinGLFactory.INSTANCE;
        _WinGLPixelFormat p = _WinGLPixelFormat.INSTANCE;
        _WinGLContext c = _WinGLContext.INSTANCE;

        Application.launch(MyApp.class, (String[]) null);

        Platform.exit();
    }
}
