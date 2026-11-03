package com.ollie.mc3d;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {
    private Context context;
    public World world;
    private int program;
    private int aPosition, aNormal, aColor;
    private int uMVPMatrix, uMVMatrix, uLightPos;

    private float[] mvpMatrix = new float[16];
    private float[] viewMatrix = new float[16];
    private float[] projectionMatrix = new float[16];

    // Игрок
    public float camX = 32f, camY = 40f, camZ = 32f;
    public float yaw = 0f, pitch = -0.5f;
    public float velY = 0f;
    public boolean isGrounded = false;
    
    private FloatBuffer vertexBuffer;
    private int vertexCount = 0;
    private boolean needsRebuild = true;

    public MyGLRenderer(Context context) {
        this.context = context;
        this.world = new World();
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        GLES20.glClearColor(0.5f, 0.7f, 1.0f, 1.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_CULL_FACE); // Отсекаем невидимые грани

        int vShader = loadShader(GLES20.GL_VERTEX_SHADER, ShaderUtils.readTextFileFromRawResource(context, R.raw.vertex_shader));
        int fShader = loadShader(GLES20.GL_FRAGMENT_SHADER, ShaderUtils.readTextFileFromRawResource(context, R.raw.fragment_shader));
        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vShader);
        GLES20.glAttachShader(program, fShader);
        GLES20.glLinkProgram(program);

        aPosition = GLES20.glGetAttribLocation(program, "a_Position");
        aNormal = GLES20.glGetAttribLocation(program, "a_Normal");
        aColor = GLES20.glGetAttribLocation(program, "a_Color");
        uMVPMatrix = GLES20.glGetUniformLocation(program, "u_MVPMatrix");
        uMVMatrix = GLES20.glGetUniformLocation(program, "u_MVMatrix");
        uLightPos = GLES20.glGetUniformLocation(program, "u_LightPos");
    }

    public void rebuildMesh() {
        ArrayList<Float> vertices = new ArrayList<>();
        for (int x = 0; x < World.SIZE; x++) {
            for (int y = 0; y < World.HEIGHT; y++) {
                for (int z = 0; z < World.SIZE; z++) {
                    int block = world.blocks[x][y][z];
                    if (block != 0) {
                        // Face Culling: рисуем грань только если рядом воздух
                        if (y == World.HEIGHT-1 || world.blocks[x][y+1][z] == 0) addFace(vertices, x, y, z, 0, 1, 0, block);
                        if (y == 0 || world.blocks[x][y-1][z] == 0) addFace(vertices, x, y, z, 0, -1, 0, block);
                        if (x == 0 || world.blocks[x-1][y][z] == 0) addFace(vertices, x, y, z, -1, 0, 0, block);
                        if (x == World.SIZE-1 || world.blocks[x+1][y][z] == 0) addFace(vertices, x, y, z, 1, 0, 0, block);
                        if (z == 0 || world.blocks[x][y][z-1] == 0) addFace(vertices, x, y, z, 0, 0, -1, block);
                        if (z == World.SIZE-1 || world.blocks[x][y][z+1] == 0) addFace(vertices, x, y, z, 0, 0, 1, block);
                    }
                }
            }
        }
        vertexCount = vertices.size() / 9;
        float[] vArray = new float[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) vArray[i] = vertices.get(i);
        
        vertexBuffer = ByteBuffer.allocateDirect(vArray.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(vArray).position(0);
        needsRebuild = false;
    }

    private void addFace(ArrayList<Float> v, float x, float y, float z, float nx, float ny, float nz, int type) {
        float r = 0, g = 0, b = 0;
        if (type == 1) { r = 0.3f; g = 0.8f; b = 0.3f; } // Трава
        else if (type == 2) { r = 0.5f; g = 0.3f; b = 0.1f; } // Земля
        else if (type == 3) { r = 0.5f; g = 0.5f; b = 0.5f; } // Камень
        else if (type == 4) { r = 0.4f; g = 0.2f; b = 0.1f; } // Дерево
        else if (type == 5) { r = 0.1f; g = 0.6f; b = 0.1f; } // Листва

        float size = 0.5f;
        float[] verts;
        if (nx != 0) {
            float dx = nx * size;
            verts = new float[]{x+dx,y-size,z-size, x+dx,y-size,z+size, x+dx,y+size,z+size, x+dx,y-size,z-size, x+dx,y+size,z+size, x+dx,y+size,z-size};
        } else if (ny != 0) {
            float dy = ny * size;
            verts = new float[]{x-size,y+dy,z-size, x-size,y+dy,z+size, x+size,y+dy,z+size, x-size,y+dy,z-size, x+size,y+dy,z+size, x+size,y+dy,z-size};
        } else {
            float dz = nz * size;
            verts = new float[]{x-size,y-size,z+dz, x+size,y-size,z+dz, x+size,y+size,z+dz, x-size,y-size,z+dz, x+size,y+size,z+dz, x-size,y+size,z+dz};
        }
        for (int i = 0; i < 6; i++) {
            v.add(verts[i*3]); v.add(verts[i*3+1]); v.add(verts[i*3+2]);
            v.add(nx); v.add(ny); v.add(nz);
            v.add(r); v.add(g); v.add(b);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 0.1f, 100f);
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        if (needsRebuild) rebuildMesh();
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Физика (Гравитация)
        velY -= 0.01f;
        camY += velY;
        
        // Коллизия с землей
        int px = (int)camX; int py = (int)camY; int pz = (int)camZ;
        if (py >= 0 && py < World.HEIGHT && px >= 0 && px < World.SIZE && pz >= 0 && pz < World.SIZE) {
            if (world.blocks[px][py][pz] != 0) {
                camY = py + 1;
                velY = 0;
                isGrounded = true;
            } else {
                isGrounded = false;
            }
        }

        float lookX = (float) (Math.sin(yaw) * Math.cos(pitch));
        float lookY = (float) Math.sin(pitch);
        float lookZ = (float) (-Math.cos(yaw) * Math.cos(pitch));

        Matrix.setLookAtM(viewMatrix, 0, camX, camY, camZ, camX + lookX, camY + lookY, camZ + lookZ, 0f, 1f, 0f);
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        GLES20.glUseProgram(program);
        GLES20.glUniformMatrix4fv(uMVPMatrix, 1, false, mvpMatrix, 0);
        GLES20.glUniformMatrix4fv(uMVMatrix, 1, false, viewMatrix, 0);
        GLES20.glUniform3f(uLightPos, 10f, 30f, 10f);

        if (vertexBuffer != null) {
            vertexBuffer.position(0);
            GLES20.glVertexAttribPointer(aPosition, 3, GLES20.GL_FLOAT, false, 9*4, vertexBuffer);
            GLES20.glEnableVertexAttribArray(aPosition);
            vertexBuffer.position(3);
            GLES20.glVertexAttribPointer(aNormal, 3, GLES20.GL_FLOAT, false, 9*4, vertexBuffer);
            GLES20.glEnableVertexAttribArray(aNormal);
            vertexBuffer.position(6);
            GLES20.glVertexAttribPointer(aColor, 3, GLES20.GL_FLOAT, false, 9*4, vertexBuffer);
            GLES20.glEnableVertexAttribArray(aColor);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
        }
    }

    // Логика копания (Рейкаст)
    public void breakBlock() {
        float lookX = (float) (Math.sin(yaw) * Math.cos(pitch));
        float lookY = (float) Math.sin(pitch);
        float lookZ = (float) (-Math.cos(yaw) * Math.cos(pitch));
        
        for (float i = 0.5f; i < 5f; i += 0.2f) {
            int tx = (int)(camX + lookX * i);
            int ty = (int)(camY + lookY * i);
            int tz = (int)(camZ + lookZ * i);
            if (tx >= 0 && tx < World.SIZE && ty >= 0 && ty < World.HEIGHT && tz >= 0 && tz < World.SIZE) {
                if (world.blocks[tx][ty][tz] != 0) {
                    world.blocks[tx][ty][tz] = 0;
                    needsRebuild = true;
                    break;
                }
            }
        }
    }

    private int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }
}
