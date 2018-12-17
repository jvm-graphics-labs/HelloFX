package com.sun.javafx.tk.quantum;

import org.joml.Matrix3d;
import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;

import static java.lang.Math.*;
import static org.lwjgl.opengl.ARBVertexArrayObject.glBindVertexArray;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15C.glBindBuffer;
import static org.lwjgl.opengl.GL15C.glGenBuffers;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20C.*;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.memAllocFloat;
import static org.lwjgl.system.MemoryUtil.memFree;

class Gears_ {

    public static void main(String[] args) {
        System.out.println("ciao");
    }

    private final Gear
            gear1,
            gear2,
            gear3;

    private final int program;

    private final int u_NORMAL;
    private final int u_MVP;
    private final int u_LIGHT;
    private final int u_COLOR;

    // ---------------------

    private final Matrix4d
            P = new Matrix4d(),
            V = new Matrix4d(),
            MVP = new Matrix4d();

    private final Matrix3d normal = new Matrix3d();
    private final Vector3d light = new Vector3d();

    private final FloatBuffer vec3f = BufferUtils.createFloatBuffer(3);
    private final FloatBuffer mat3f = BufferUtils.createFloatBuffer(3 * 3);
    private final FloatBuffer mat4f = BufferUtils.createFloatBuffer(4 * 4);

    // ---------------------

    private long count = 0;
    private double startTime;

    private double
            view_rotx = 20.0f,
            view_roty = 30.0f,
            view_rotz = 0.0f;

    private double distance = 40.0f;
    private double angle = 0.0f;

    Gears_() {
        glDisable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);

        P.setFrustum(-1.0, 1.0, -1.0, 1.0, 5.0, 100.0);

        program = loadShaders("gears.vert", "gears.frag");

        u_MVP = glGetUniformLocation(program, "mvp");
        u_NORMAL = glGetUniformLocation(program, "mvp3");
        u_LIGHT = glGetUniformLocation(program, "light");
        u_COLOR = glGetUniformLocation(program, "color");

        gear1 = new Gear(1.0, 4.0, 1.0, 20, 0.7, new float[]{0.8f, 0.1f, 0.0f, 1.0f});
        gear2 = new Gear(0.5, 2.0, 2.0, 10, 0.7, new float[]{0.0f, 0.8f, 0.2f, 1.0f});
        gear3 = new Gear(1.3, 2.0, 0.5, 10, 0.7, new float[]{0.2f, 0.2f, 1.0f, 1.0f});

        startTime = System.currentTimeMillis() / 1000.0;
    }

    void idle() {
        angle += 2.0;
    }

    void reshape(int width, int height) {
        float h = (float) height / (float) width;

        glViewport(0, 0, width, height);
        P.setFrustum(-1.0, 1.0, -h, h, 5.0, 100.0);

        distance = width < height ? 40.0 : 80.0;
    }

    void draw() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // VIEW
        V.translation(0.0, 0.0, -distance);
        V.rotateX(view_rotx * PI / 180);
        V.rotateY(view_roty * PI / 180);
        V.rotateZ(view_rotz * PI / 180);

        // LIGHT
        V.transformDirection(light.set(5.0, 5.0, 10.0)).normalize();
        vec3f.put(0, (float) light.x);
        vec3f.put(1, (float) light.y);
        vec3f.put(2, (float) light.z);
        glUniform3fv(u_LIGHT, vec3f);

        // GEAR 1
        MVP
                .translation(-3.0, -2.0, 0.0)
                .rotateZ(angle * PI / 180);
        drawGear(gear1);

        // GEAR 2
        MVP
                .translation(3.1, -2.0, 0.0)
                .rotateZ((-2.0 * angle - 9.0) * PI / 180);
        drawGear(gear2);

        // GEAR 3
        MVP
                .translation(-3.1, 4.2, 0.0)
                .rotateZ((-2.0 * angle - 25.0) * PI / 180);
        drawGear(gear3);

        count++;

        double theTime = System.currentTimeMillis() / 1000.0;
        if (theTime >= startTime + 1.0) {
            System.out.format("%d fps\n", count);
            startTime = theTime;
            count = 0;
        }
    }

    private void drawGear(Gear gear) {
        V.mul(MVP, MVP);
        glUniformMatrix3fv(u_NORMAL, false, MVP.normal(normal).get(mat3f));
        P.mul(MVP, MVP);
        glUniformMatrix4fv(u_MVP, false, MVP.get(mat4f));
        glUniform4fv(u_COLOR, gear.color);

        glBindVertexArray(gear.vao);
        glDrawArrays(GL_TRIANGLES, 0, gear.vertexCount);
    }

    private static void printShaderInfoLog(int obj) {
        int infologLength = glGetShaderi(obj, GL_INFO_LOG_LENGTH);
        if (infologLength > 0) {
            glGetShaderInfoLog(obj);
            System.out.format("%s\n", glGetShaderInfoLog(obj));
        }
    }

    private static void printProgramInfoLog(int obj) {
        int infologLength = glGetProgrami(obj, GL_INFO_LOG_LENGTH);
        if (infologLength > 0) {
            glGetProgramInfoLog(obj);
            System.out.format("%s\n", glGetProgramInfoLog(obj));
        }
    }

    private static void compileShader(int shader, ByteBuffer code) {
        try (MemoryStack stack = stackPush()) {
            PointerBuffer pp = stack.mallocPointer(1);
            IntBuffer pi = stack.mallocInt(1);

            pp.put(0, code);
            pi.put(0, code.remaining());
            glShaderSource(shader, pp, pi);
            glCompileShader(shader);
            printShaderInfoLog(shader);

            if (glGetShaderi(shader, GL_COMPILE_STATUS) != GL_TRUE)
                throw new IllegalStateException("Failed to compile shader.");
        }
    }

    private static int compileShaders(ByteBuffer vs, ByteBuffer fs) {
        int v = glCreateShader(GL_VERTEX_SHADER);
        int f = glCreateShader(GL_FRAGMENT_SHADER);

        compileShader(v, vs);
        compileShader(f, fs);

        int p = glCreateProgram();
        glAttachShader(p, v);
        glAttachShader(p, f);
        glLinkProgram(p);
        printProgramInfoLog(p);

        if (glGetProgrami(p, GL_LINK_STATUS) != GL_TRUE)
            throw new IllegalStateException("Failed to link program.");

        glUseProgram(p);
        return p;
    }

    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }

    private static ByteBuffer readFile(String fileName, int bufferSize) throws IOException {
        ByteBuffer buffer;

        try (
                InputStream source = ClassLoader.getSystemResourceAsStream(fileName);
                ReadableByteChannel rbc = Channels.newChannel(source)
        ) {
            buffer = BufferUtils.createByteBuffer(bufferSize);

            while (true) {
                int bytes = rbc.read(buffer);
                if (bytes == -1)
                    break;
                if (buffer.remaining() == 0)
                    buffer = resizeBuffer(buffer, buffer.capacity() * 2);
            }
        }

        buffer.flip();
        return buffer;
    }

    private static int loadShaders(String vertFileName, String fragFileName) {
        try {
            ByteBuffer vs = readFile(vertFileName, 4096);
            ByteBuffer fs = readFile(fragFileName, 4096);

            return compileShaders(vs, fs);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private class Gear {

        final FloatBuffer color;

        final int vao;
        private int vertexCount;

        private ArrayList<Double> gVertices = new ArrayList<>();

        private double
                currentNormalsX,
                currentNormalsY,
                currentNormalsZ;

        Gear(double inner_radius, double outer_radius, double width, int teeth, double tooth_depth, float[] color) {
            this.color = BufferUtils.createFloatBuffer(4);
            this.color.put(color).flip();

            double r0 = inner_radius;
            double r1 = outer_radius - tooth_depth / 2.0;
            double r2 = outer_radius + tooth_depth / 2.0;

            double da = 2.0 * PI / teeth / 4.0;

            setCurrentNormal(0.0, 0.0, 1.0);

            /* draw front face */
            for (int i = 0; i <= teeth; i++) {
                double angle = i * 2.0 * PI / teeth;
                double da3 = 4 * da;

                // step 1
                addVertex(r0 * cos(angle), r0 * sin(angle), width * 0.5);
                addVertex(r1 * cos(angle), r1 * sin(angle), width * 0.5);
                addVertex(r1 * cos(angle - da), r1 * sin(angle - da), width * 0.5);

                addVertex(r0 * cos(angle), r0 * sin(angle), width * 0.5);
                addVertex(r1 * cos(angle - da), r1 * sin(angle - da), width * 0.5);
                addVertex(r0 * cos(angle - da), r0 * sin(angle - da), width * 0.5);

                // Step 2
                addVertex(r0 * cos(angle - da), r0 * sin(angle - da), width * 0.5);
                addVertex(r1 * cos(angle - da), r1 * sin(angle - da), width * 0.5);
                addVertex(r1 * cos(angle - da3), r1 * sin(angle - da3), width * 0.5);

                addVertex(r0 * cos(angle - da), r0 * sin(angle - da), width * 0.5);
                addVertex(r1 * cos(angle - da3), r1 * sin(angle - da3), width * 0.5);
                addVertex(r0 * cos(angle - da3), r0 * sin(angle - da3), width * 0.5);
            }

            /* draw front sides of teeth */
            da = 2.0 * PI / teeth / 4.0;
            for (int i = 0; i < teeth; i++) {
                double angle = i * 2.0 * PI / teeth;

                addVertex(r1 * cos(angle), r1 * sin(angle), width * 0.5);
                addVertex(r2 * cos(angle + da), r2 * sin(angle + da), width * 0.5);
                addVertex(r2 * cos(angle + 2 * da), r2 * sin(angle + 2 * da), width * 0.5);

                addVertex(r1 * cos(angle), r1 * sin(angle), width * 0.5);
                addVertex(r2 * cos(angle + 2 * da), r2 * sin(angle + 2 * da), width * 0.5);
                addVertex(r1 * cos(angle + 3 * da), r1 * sin(angle + 3 * da), width * 0.5);
            }

            setCurrentNormal(0.0, 0.0, -1.0);
            double da3 = 4 * da;

            /* draw back face */
            for (int i = 0; i <= teeth; i++) {
                double angle = i * 2.0 * PI / teeth;

                addVertex(r1 * cos(angle), r1 * sin(angle), -width * 0.5);
                addVertex(r0 * cos(angle), r0 * sin(angle), -width * 0.5);
                addVertex(r0 * cos(angle - da), r0 * sin(angle - da), -width * 0.5);

                addVertex(r1 * cos(angle), r1 * sin(angle), -width * 0.5);
                addVertex(r0 * cos(angle - da), r0 * sin(angle - da), -width * 0.5);
                addVertex(r1 * cos(angle - da), r1 * sin(angle - da), -width * 0.5);


                addVertex(r1 * cos(angle - da), r1 * sin(angle - da), -width * 0.5);
                addVertex(r0 * cos(angle - da), r0 * sin(angle - da), -width * 0.5);
                addVertex(r0 * cos(angle - da3), r0 * sin(angle - da3), -width * 0.5);

                addVertex(r1 * cos(angle - da), r1 * sin(angle - da), -width * 0.5);
                addVertex(r0 * cos(angle - da3), r0 * sin(angle - da3), -width * 0.5);
                addVertex(r1 * cos(angle - da3), r1 * sin(angle - da3), -width * 0.5);
            }

            /* draw back sides of teeth */
            da = 2.0 * PI / teeth / 4.0;
            for (int i = 0; i < teeth; i++) {
                double angle = i * 2.0 * PI / teeth;

                addVertex(r1 * cos(angle + 3 * da), r1 * sin(angle + 3 * da), -width * 0.5);
                addVertex(r2 * cos(angle + 2 * da), r2 * sin(angle + 2 * da), -width * 0.5);
                addVertex(r2 * cos(angle + da), r2 * sin(angle + da), -width * 0.5);

                addVertex(r1 * cos(angle + 3 * da), r1 * sin(angle + 3 * da), -width * 0.5);
                addVertex(r2 * cos(angle + da), r2 * sin(angle + da), -width * 0.5);
                addVertex(r1 * cos(angle), r1 * sin(angle), -width * 0.5);
            }

            setCurrentNormal(0.0, 0.0, -1.0); // Copied from above

            /* draw outward faces of teeth */
            for (int i = 0; i < teeth; i++) {
                double angle = i * 2.0 * PI / teeth;

                setCurrentNormal(cos(angle), sin(angle), 0.0);
                double u = r2 * cos(angle + da) - r1 * cos(angle);
                double v = r2 * sin(angle + da) - r1 * sin(angle);
                double len = sqrt(u * u + v * v);
                u /= len;
                v /= len;

                // First quad
                addVertex(r1 * cos(angle), r1 * sin(angle), width * 0.5);
                addVertex(r1 * cos(angle), r1 * sin(angle), -width * 0.5);
                setCurrentNormal(v, -u, 0.0);
                addVertex(r2 * cos(angle + da), r2 * sin(angle + da), -width * 0.5);

                setCurrentNormal(cos(angle), sin(angle), 0.0);
                addVertex(r1 * cos(angle), r1 * sin(angle), width * 0.5);
                setCurrentNormal(v, -u, 0.0);
                addVertex(r2 * cos(angle + da), r2 * sin(angle + da), width * 0.5);
                addVertex(r2 * cos(angle + da), r2 * sin(angle + da), -width * 0.5);

                // Second quad
                setCurrentNormal(v, -u, 0.0);
                addVertex(r2 * cos(angle + da), r2 * sin(angle + da), width * 0.5);
                addVertex(r2 * cos(angle + da), r2 * sin(angle + da), -width * 0.5);
                setCurrentNormal(cos(angle), sin(angle), 0.0);
                addVertex(r2 * cos(angle + 2 * da), r2 * sin(angle + 2 * da), -width * 0.5);

                setCurrentNormal(v, -u, 0.0);
                addVertex(r2 * cos(angle + da), r2 * sin(angle + da), width * 0.5);
                setCurrentNormal(cos(angle), sin(angle), 0.0);
                addVertex(r2 * cos(angle + 2 * da), r2 * sin(angle + 2 * da), width * 0.5);
                addVertex(r2 * cos(angle + 2 * da), r2 * sin(angle + 2 * da), -width * 0.5);

                // Third quad
                setCurrentNormal(cos(angle), sin(angle), 0.0);
                addVertex(r2 * cos(angle + 2 * da), r2 * sin(angle + 2 * da), width * 0.5);
                addVertex(r2 * cos(angle + 2 * da), r2 * sin(angle + 2 * da), -width * 0.5);
                u = r1 * cos(angle + 3 * da) - r2 * cos(angle + 2 * da);
                v = r1 * sin(angle + 3 * da) - r2 * sin(angle + 2 * da);
                setCurrentNormal(v, -u, 0.0);
                addVertex(r1 * cos(angle + 3 * da), r1 * sin(angle + 3 * da), -width * 0.5);

                setCurrentNormal(cos(angle), sin(angle), 0.0);
                addVertex(r2 * cos(angle + 2 * da), r2 * sin(angle + 2 * da), width * 0.5);
                setCurrentNormal(v, -u, 0.0);
                addVertex(r1 * cos(angle + 3 * da), r1 * sin(angle + 3 * da), width * 0.5);
                addVertex(r1 * cos(angle + 3 * da), r1 * sin(angle + 3 * da), -width * 0.5);

                // Fourth quad
                u = r1 * cos(angle + 3 * da) - r2 * cos(angle + 2 * da);
                v = r1 * sin(angle + 3 * da) - r2 * sin(angle + 2 * da);
                setCurrentNormal(v, -u, 0.0);
                addVertex(r1 * cos(angle + 3 * da), r1 * sin(angle + 3 * da), width * 0.5);
                addVertex(r1 * cos(angle + 3 * da), r1 * sin(angle + 3 * da), -width * 0.5);
                setCurrentNormal(cos(angle), sin(angle), 0.0);
                setCurrentNormal(cos(angle + 4 * da), sin(angle + 4 * da), 0.0);
                addVertex(r1 * cos(angle + 4 * da), r1 * sin(angle + 4 * da), -width * 0.5);

                u = r1 * cos(angle + 3 * da) - r2 * cos(angle + 2 * da);
                v = r1 * sin(angle + 3 * da) - r2 * sin(angle + 2 * da);
                setCurrentNormal(v, -u, 0.0);
                addVertex(r1 * cos(angle + 3 * da), r1 * sin(angle + 3 * da), width * 0.5);
                setCurrentNormal(cos(angle), sin(angle), 0.0);
                setCurrentNormal(cos(angle + 4 * da), sin(angle + 4 * da), 0.0);
                addVertex(r1 * cos(angle + 4 * da), r1 * sin(angle + 4 * da), width * 0.5);
                addVertex(r1 * cos(angle + 4 * da), r1 * sin(angle + 4 * da), -width * 0.5);

            }

            /* draw inside radius cylinder */
            da3 = 4 * da;
            for (int i = 0; i <= teeth; i++) {
                double angle = i * 2.0 * PI / teeth;

                setCurrentNormal(-cos(angle), -sin(angle), 0.0);
                addVertex(r0 * cos(angle), r0 * sin(angle), -width * 0.5);
                addVertex(r0 * cos(angle), r0 * sin(angle), width * 0.5);
                setCurrentNormal(-cos(angle - da), -sin(angle - da), 0.0);
                addVertex(r0 * cos(angle - da), r0 * sin(angle - da), width * 0.5);

                setCurrentNormal(-cos(angle), -sin(angle), 0.0);
                addVertex(r0 * cos(angle), r0 * sin(angle), -width * 0.5);
                setCurrentNormal(-cos(angle - da), -sin(angle - da), 0.0);
                addVertex(r0 * cos(angle - da), r0 * sin(angle - da), width * 0.5);
                addVertex(r0 * cos(angle - da), r0 * sin(angle - da), -width * 0.5);


                setCurrentNormal(-cos(angle - da), -sin(angle - da), 0.0);
                addVertex(r0 * cos(angle - da), r0 * sin(angle - da), -width * 0.5);
                addVertex(r0 * cos(angle - da), r0 * sin(angle - da), width * 0.5);
                setCurrentNormal(-cos(angle - da3), -sin(angle - da3), 0.0);
                addVertex(r0 * cos(angle - da3), r0 * sin(angle - da3), width * 0.5);

                setCurrentNormal(-cos(angle - da), -sin(angle - da), 0.0);
                addVertex(r0 * cos(angle - da), r0 * sin(angle - da), -width * 0.5);
                setCurrentNormal(-cos(angle - da3), -sin(angle - da3), 0.0);
                addVertex(r0 * cos(angle - da3), r0 * sin(angle - da3), width * 0.5);
                addVertex(r0 * cos(angle - da3), r0 * sin(angle - da3), -width * 0.5);
            }

            // Build VAO and VBOs
            // Allocate and activate Vertex Array Object
            vao = glGenVertexArrays();
            glBindVertexArray(vao);
            // Allocate Vertex Buffer Objects
            int uboName = glGenBuffers();

            // VBO for vertex data
            FloatBuffer data = memAllocFloat(gVertices.size());
            for (int i = 0; i < gVertices.size(); i++) {
                data.put(i, gVertices.get(i).floatValue());
            }

            glBindBuffer(GL_ARRAY_BUFFER, uboName);
            glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
            glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
            glEnableVertexAttribArray(0);
            glEnableVertexAttribArray(1);

            memFree(data);
        }

        private void addVertex(double x, double y, double z) {
            gVertices.add(x);
            gVertices.add(y);
            gVertices.add(z);
            gVertices.add(currentNormalsX);
            gVertices.add(currentNormalsY);
            gVertices.add(currentNormalsZ);
            vertexCount += 1;
        }

        private void setCurrentNormal(double x, double y, double z) {
            currentNormalsX = x;
            currentNormalsY = y;
            currentNormalsZ = z;
        }
    }
}
