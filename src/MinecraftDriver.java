/***************************************************************
* file: MinecraftDriver.java
* author: Team NULL - Kyle Hubbard
* class: CS 4450 - Computer Graphics
*
* assignment: final program
* date last modified: 3/5/2019
*
* purpose: This program is the main class for the MincraftRenderer and it
* acts as the entry point for the entire rendering process
*
****************************************************************/

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.glu.GLU;

public class MinecraftDriver {
    
    private FPCameraController fp = new FPCameraController(0, 0, 0);
    private DisplayMode displayMode;
    
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
    // purpose: this method initializes the matrix mode, matrix model, and perspective attributes
    private void initGL() {
        
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        GLU.gluPerspective(100.0f, (float)displayMode.getWidth() / (float)displayMode.getHeight(), 0.1f, 300.0f);
        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
    }
    
<<<<<<< HEAD
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
                    glVertex3f( 1.0f,-1.0f,-1.0f);
                    glVertex3f(-1.0f,-1.0f,-1.0f);
                    glVertex3f(-1.0f, 1.0f,-1.0f);
                    glVertex3f( 1.0f, 1.0f,-1.0f);
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
    
=======
>>>>>>> master
    // method: main
    // purpose: this method creates a new MinecraftDriver object and calls its start method
    public static void main(String[] args) {

        MinecraftDriver renderer = new MinecraftDriver();
        renderer.start();
    }
}
