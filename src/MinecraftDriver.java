/***************************************************************
* file: MinecraftDriver.java
* author: Team IDK
* class: CS 4450 - Computer Graphics
*
* assignment: final program
* date last modified: 2/27/2019
*
* purpose: This program is the main class for the MincraftRenderer and it
* handles controlling the entire rendering process
*
****************************************************************/

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;

public class MinecraftDriver {
    
    private final int SCREEN_WIDTH = 640, SCREEN_HEIGHT = 480;
    private int mouseX, mouseY;
    private float dx, dy, dz, pitch, yaw, roll;

    // method: start
    // purpose: this method calls all of the necessary rendering functions
    public void start() {
        
        try {
            createWindow();
            initGL();
            render();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // method: createWindow
    // purpose: this method creates the display stage and sets its dimensions
    private void createWindow() throws Exception{
        
        Display.setFullscreen(false);
        Display.setDisplayMode(new DisplayMode(640, 480));
        Display.setTitle("Team IDK - Minecraft");
        Display.create();
    }

    // method: initGL
    // purpose: this method initializes the background color, camera, ortho matrix, and etc
    private void initGL() {
        
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, SCREEN_WIDTH, 0, SCREEN_HEIGHT, 1, -1);
        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
    }
    
    // method: render
    // purpose: this method handles calling the appropriate rendering functions
    private void render() {
        
        while(!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            try {
                processKeyboardInput();
                processMouseInput();
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                glLoadIdentity();
                glTranslatef(320f + dx, 240f + dy, 0f);
                glRotatef(0f + roll, 0 + yaw, 0 + pitch, 1);
                glBegin(GL_QUADS);
                    glColor3f(1.0f,1.0f,0.0f);
                    glVertex3f( 10.0f,-10.0f,-10.0f);
                    glVertex3f(-10.0f,-10.0f,-10.0f);
                    glVertex3f(-10.0f, 10.0f,-10.0f);
                    glVertex3f( 10.0f, 10.0f,-10.0f);
                glEnd();
                
                glBegin(GL_POINTS);
                    int halfWidth = SCREEN_WIDTH / 2;
                    int x = -halfWidth, y = -halfWidth, z = -halfWidth;
                    
                    while(x < halfWidth) {
                        glColor3f(1.0f,0.0f,0.0f);
                        glVertex3f(x, 0, 0);
                        x++;
                        
                        glColor3f(0.0f,1.0f,0.0f);
                        glVertex3f(0, y, 0);
                        y++;
                        
                        glColor3f(0.0f,0.0f,1.0f);
                        glVertex3f(0, 0, z);
                        z++;
                    }
                glEnd();
                Display.update();
                Display.sync(60);
            } catch(Exception e) {
            }
        }

        Display.destroy();
    }
    
    // method: processKeyboardInput
    // purpose: this method handles user's keyboard inputs
    private void processKeyboardInput() {
        
        if(Keyboard.isKeyDown(Keyboard.KEY_W) || Keyboard.isKeyDown(Keyboard.KEY_UP))
            dz++;
        
        if(Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_DOWN))
            dz--;
        
        if(Keyboard.isKeyDown(Keyboard.KEY_A) || Keyboard.isKeyDown(Keyboard.KEY_LEFT))
            dx++;
        
        if(Keyboard.isKeyDown(Keyboard.KEY_D) || Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
            dx--;
        
        if(Keyboard.isKeyDown(Keyboard.KEY_SPACE))
            dy--;
        
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
            dy++;
        
        System.out.printf("dX: %2f | dY: %2f | Roll: %2f\n", dx, dy, roll);
    }
    
    // method: processMouseInput
    // purpose: this method handles user's mouse inputs
    private void processMouseInput() {
        
        mouseX = Mouse.getX() - (SCREEN_WIDTH / 2);
        mouseY = Mouse.getY() - (SCREEN_HEIGHT / 2);
        
        if(Keyboard.isKeyDown(Keyboard.KEY_Q))
            roll--;
        
        if(Keyboard.isKeyDown(Keyboard.KEY_E))
            roll++;
        
        System.out.printf("Mouse X: %d | Mouse Y: %d\n", mouseX, mouseY);
    }
    
    // method: main
    // purpose: this method creates a new MinecraftDriver object and calls its start method
    public static void main(String[] args) {

        MinecraftDriver renderer = new MinecraftDriver();
        renderer.start();
    }
}
