/***************************************************************
* file: FPCameraController.java
* author: Team NULL - Kyle Hubbard
* class: CS 4450 - Computer Graphics
*
* assignment: final program
* date last modified: 3/5/2019
*
* purpose: This program is the main class for the FPCameraController and it
* controls all of the first person viewing functionality such as the camera,
* normalization, clipping, and etc.
*
****************************************************************/

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.Sys;

public class FPCameraController {
    
    private Vector3f position;
    private Vector3f lastPosition;
    private Vector3Float player;
    private float yaw, pitch;
    
    private FPCameraController camera;
    private float movementSpeed;
    private float mouseSensitivity;
    
    // method: FPCameraController
    // purpose: this constructor creates the new FPCameraController object
    public FPCameraController(float x, float y, float z) {
        
        position = new Vector3f(x, y, z);
        lastPosition = new Vector3f(0f, 15f, 0f);
        movementSpeed = 0.05f;
        mouseSensitivity = 0.09f;
    }
    
    // method: rotateYaw
    // purpose: this method rotates the camera about the v axis
    public void rotateYaw(float amount) {
        yaw += amount;
    }
    
    // method: rotatePitch
    // purpose: this method rotates about the u axis
    public void rotatePitch(float amount) {
        pitch -= amount;
    }
    
    // method: walkForward
    // purpose: this method moves the camera's position forward
    public void walkForward(float distance) {
        
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw));
        position.x -= xOffset;
        position.z += zOffset;
    }
    
    // method: walkBackwards
    // purpose: this method moves the camera's position backwards
    public void walkBackwards(float distance) {
        
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw));
        position.x += xOffset;
        position.z -= zOffset;
    }
    
    // method: strafeRight
    // purpose: this method moves the camera's position to the right
    public void strafeRight(float distance) {
        
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw + 90));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw + 90));
        position.x -= xOffset;
        position.z += zOffset;
    }
    
    // method: strafeLeft
    // purpose: this method moves the camera's position to the left
    public void strafeLeft(float distance) {
        
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw - 90));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw - 90));
        position.x -= xOffset;
        position.z += zOffset;
    }
    
    // method: moveUp
    // purpose: this method moves the camera's position upwards
    public void moveUp(float distance) {
        position.y -= distance;
    }
    
    // method: moveDown
    // purpose: this method moves the camera's position downwards
    public void moveDown(float distance) {
        position.y += distance;
    }
    
    // method: lookThrough
    // purpose: this method transforms the identity matrix to look through the camera
    public void lookThrough() {
        
        glRotatef(pitch, 1.0f, 0.0f, 0.0f);
        glRotatef(yaw, 0.0f, 1.0f, 0.0f);
        glTranslatef(position.x, position.y, position.z);
    }
    
    // method: processKeyboardInput
    // purpose: this method handles the user's keyboard inputs
    public void processKeyboardInput() {

        if(Keyboard.isKeyDown(Keyboard.KEY_W) || Keyboard.isKeyDown(Keyboard.KEY_UP))
            camera.walkForward(movementSpeed);
        
        if(Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_DOWN))
            camera.walkBackwards(movementSpeed);
        
        if(Keyboard.isKeyDown(Keyboard.KEY_A) || Keyboard.isKeyDown(Keyboard.KEY_LEFT))
            camera.strafeLeft(movementSpeed);
        
        if(Keyboard.isKeyDown(Keyboard.KEY_D) || Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
            camera.strafeRight(movementSpeed);
        
        if(Keyboard.isKeyDown(Keyboard.KEY_SPACE))
            camera.moveUp(movementSpeed);
        
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
            camera.moveDown(movementSpeed);
        
        //Sprint Key on Left Mouse Click
        if(Mouse.isButtonDown(0)) {
            movementSpeed = 0.2f;
        } else if(!Mouse.isButtonDown(0)) {
            movementSpeed = 0.05f;
        }
    }
        
    // method: render
    // purpose: this method contains all of the primitives to be drawn
    private void render() {

        try {
            glBegin(GL_QUADS);
                glColor3f(1.0f, 0.0f, 1.0f);
                glVertex3f( 1.0f,-1.0f, -1.0f);
                glVertex3f(-1.0f,-1.0f, -1.0f);
                glVertex3f(-1.0f, 1.0f, -1.0f);
                glVertex3f( 1.0f, 1.0f, -1.0f);
            glEnd();
            
            glBegin(GL_LINES);
                glColor3f(1.0f, 0.0f, 0.0f);
                glVertex3f(20.0f, 0.0f, 0.0f);
                glVertex3f(-20.0f, 0.0f, 0.0f);
                
                glColor3f(0.0f, 1.0f, 0.0f);
                glVertex3f(0.0f, 20.0f, 0.0f);
                glVertex3f(0.0f, -20.0f, 0.0f);
                
                glColor3f(0.0f, 0.0f, 1.0f);
                glVertex3f(0.0f, 0.0f, 20.0f);
                glVertex3f(0.0f, 0.0f, -20.0f);
            glEnd();
        } catch(Exception e) {
            System.out.println(e);
        }
    }
    
    // method: gameLoop
    // purpose: this method facilitates the main rendering loop
    public void gameLoop() {
        
        camera = new FPCameraController(0, 0, 0);
        float dx, dy, dt, lastTime;
        long time = 0;
        Mouse.setGrabbed(true);
        
        while(!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            
            time = Sys.getTime();
            lastTime = time;
            
            dx = Mouse.getDX();
            dy = Mouse.getDY();
            camera.rotateYaw(dx * mouseSensitivity);
            camera.rotatePitch(dy * mouseSensitivity);
            
            processKeyboardInput();
            
            glLoadIdentity();
            camera.lookThrough();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            
            render();
            
            Display.update();
            Display.sync(60);
        }
        
        Display.destroy();
    }
    
    // class: Vector3Float
    // purpose: this class acts as a vector object
    public class Vector3Float {
        
        public float x, y, z;
        
        public Vector3Float(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}