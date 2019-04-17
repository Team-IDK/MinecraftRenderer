/***************************************************************
* file: MinecraftDriver.java
* author: Team NULL
* class: CS 4450 - Computer Graphics
*
* assignment: final program
* date last modified: 4/16/2019
*
* purpose: This program is the main class for the MincraftRenderer and it
* acts as the entry point for the entire rendering process.
*
****************************************************************/

package minecraftrenderer;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.glu.GLU;

public class MinecraftDriver {
    
    private FPCameraController fp = new FPCameraController(0, 0, 0);
    private DisplayMode displayMode;
    private FloatBuffer lightPos;
    private FloatBuffer whiteLight;
    
    // method: start
    // purpose: this method calls all of the necessary rendering functions
    public void start() {
        
        try {
            createWindow();
            initGL();
            fp.gameLoop();
        } catch(Exception e) {
            System.out.println(e);
        }
    }

    // method: createWindow
    // purpose: this method creates the display stage and assigns the display mode
    private void createWindow() throws Exception {
        
        Display.setFullscreen(false);
        DisplayMode d[] = Display.getAvailableDisplayModes();
        for(int i = 0; i < d.length; i++) {
            if(d[i].getWidth() == 640 && d[i].getHeight() == 480 && d[i].getBitsPerPixel() == 32) {
                displayMode = d[i];
                break;
            }
        }
        Display.setDisplayMode(displayMode);
        Display.setTitle("Team NULL - Minecraft Renderer");
        Display.create();
    }

    // method: initGL
    // purpose: this method initializes the matrix mode, light source, matrix model, and perspective attributes
    private void initGL() {
        
        glClearColor(0.443f, 0.737f, 0.867f, 0.0f);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        GLU.gluPerspective(100.0f, (float)displayMode.getWidth() / (float)displayMode.getHeight(), 0.1f, 300.0f);

        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        glEnable(GL_TEXTURE_2D);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        
        initLightArrays();
        glLight(GL_LIGHT0, GL_POSITION, lightPos);
        glLight(GL_LIGHT0, GL_SPECULAR, whiteLight);
        glLight(GL_LIGHT0, GL_DIFFUSE, whiteLight);
        glLight(GL_LIGHT0, GL_AMBIENT, whiteLight);
        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);
    }
    
    // method: initLightArrays
    // purpose: Initializes buffers for light position and white light
    public void initLightArrays() {
        
        lightPos = BufferUtils.createFloatBuffer(4);
        lightPos.put(0f).put(0f).put(0f).put(1f).flip();
        
        whiteLight = BufferUtils.createFloatBuffer(4);
        whiteLight.put(1f).put(1f).put(1f).put(0f).flip();
    }
    
    // method: main
    // purpose: this method creates a new MinecraftDriver object and calls its start method
    public static void main(String[] args) {

        MinecraftDriver renderer = new MinecraftDriver();
        renderer.start();
    }
}