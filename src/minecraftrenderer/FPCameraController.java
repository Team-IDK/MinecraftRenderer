/***************************************************************
* file: FPCameraController.java
* author: Team NULL
* class: CS 4450 - Computer Graphics
*
* assignment: final program
* date last modified: 4/16/2019
*
* purpose: This program is the main class for the FPCameraController and it
* controls all of the first person viewing functionality such as the camera,
* normalization, clipping, and etc.
*
****************************************************************/

package minecraftrenderer;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.Sys;

public class FPCameraController {
    
    private Vector3f position;
    private Vector3f lookPosition;
    private float yaw, pitch, dx, dy;
    
    private FPCameraController camera;
    private Chunk chunk;
    
    private float movementSpeed;
    private float mouseSensitivity;
    
    // method: FPCameraController
    // purpose: this constructor creates the new FPCameraController object
    public FPCameraController(float x, float y, float z) {
        
        position = new Vector3f(x, y, z);
        lookPosition = new Vector3f(100f, 0f, 100f);
        
        movementSpeed = 0.5f;
        mouseSensitivity = 0.075f;
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
        
        //Clamp vertical view angle to 180 degrees of motion
        if(pitch < -90) {
            pitch = -90;
        }
        if(pitch > 90) {
            pitch = 90;
        }
    }
    
    // method: walkForward
    // purpose: this method moves the camera's position forward
    public void walkForward(float distance) {
        
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw));
        position.x -= xOffset;
        position.z += zOffset;
        
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lookPosition.x-=xOffset).put(lookPosition.y).put(lookPosition.z+=zOffset).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }
    
    // method: walkBackwards
    // purpose: this method moves the camera's position backwards
    public void walkBackwards(float distance) {
        
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw));
        position.x += xOffset;
        position.z -= zOffset;
        
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lookPosition.x+=xOffset).put(lookPosition.y).put(lookPosition.z-=zOffset).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }
    
    // method: strafeRight
    // purpose: this method moves the camera's position to the right
    public void strafeRight(float distance) {
        
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw + 90));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw + 90));
        position.x -= xOffset;
        position.z += zOffset;
        
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lookPosition.x-=xOffset).put(lookPosition.y).put(lookPosition.z+=zOffset).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }
    
    // method: strafeLeft
    // purpose: this method moves the camera's position to the left
    public void strafeLeft(float distance) {
        
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw - 90));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw - 90));
        position.x -= xOffset;
        position.z += zOffset;
        
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lookPosition.x-=xOffset).put(lookPosition.y).put(lookPosition.z+=zOffset).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
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
        
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lookPosition.x).put(lookPosition.y).put(lookPosition.z).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }
    
    //method: processMouseInput
    //purpose: this method handles the user's mouse movements
    public void processMouseInput() {
        
        dx = Mouse.getDX();
        dy = Mouse.getDY();
        camera.rotateYaw(dx * mouseSensitivity);
        camera.rotatePitch(dy * mouseSensitivity);
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
        
        // Sprint Key on Left Mouse Click
        if(Mouse.isButtonDown(0)) {
            movementSpeed = 1.0f;
        } else if(!Mouse.isButtonDown(0)) {
            movementSpeed = 0.5f;
        }
        
        // Generate new chunk
        if(Keyboard.isKeyDown(Keyboard.KEY_F1))
            chunk = new Chunk(0, 0, 0);
    }
    
    // method: gameLoop
    // purpose: this method facilitates the main program loop
    public void gameLoop() {
        
        Mouse.setGrabbed(true);
        
        camera = new FPCameraController(-100, -250, -100);
        chunk = new Chunk(0, 0, 0);
        
        float lastTime;
        long time = 0;
        
        while(!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            
            time = Sys.getTime();
            lastTime = time;
            
            processMouseInput();
            processKeyboardInput();
            
            glLoadIdentity();
            camera.lookThrough();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glEnableClientState(GL_VERTEX_ARRAY);
            glEnableClientState(GL_COLOR_ARRAY);
            glEnable(GL_DEPTH_TEST);
            glDepthFunc(GL_LESS);
            
            chunk.render();
            
            Display.update();
            Display.sync(60);
        }
        
        Display.destroy();
    }
}