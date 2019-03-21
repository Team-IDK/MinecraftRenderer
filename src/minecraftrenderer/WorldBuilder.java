/***************************************************************
* file: WorldBuilder.java
* author: Team NULL - Kyle Hubbard
* class: CS 4450 - Computer Graphics
*
* assignment: final program
* date last modified: 3/12/2019
*
* purpose: This program handles rendering the primitives to be used
* in the FPCameraController class.
*
****************************************************************/

package minecraftrenderer;

import static org.lwjgl.opengl.GL11.*;

public class WorldBuilder {
    
    // method: drawCube
    // purpose: this method draws a uniform, multi-colored cube outlined in black
    public void drawCube(float xOffset, float yOffset, float zOffset) {
        
        float x = 1.0f + xOffset;
        float y = 1.0f + yOffset;
        float z = 1.0f + zOffset;
        float x2 = x - 2f;
        float y2 = y - 2f;
        float z2 = z - 2f;
        
        try {
            glBegin(GL_QUADS);
                //Top Face
                glColor3f(0.835f, 0.133f, 0.322f);
                glVertex3f(x, y, z2);
                glVertex3f(x2, y, z2);
                glVertex3f(x2, y, z);
                glVertex3f(x, y, z);
                
                //Bottom Face
                glColor3f(0.416f, 0.816f, 0.129f);
                glVertex3f(x, y2, z);
                glVertex3f(x2, y2, z);
                glVertex3f(x2, y2, z2);
                glVertex3f(x, y2, z2);

                //Front Face
                glColor3f(0.114f, 0.439f, 0.58f);
                glVertex3f(x, y, z);
                glVertex3f(x2, y, z);
                glVertex3f(x2, y2, z);
                glVertex3f(x, y2, z);

                //Back Face
                glColor3f(0.925f, 0.455f, 0.149f);
                glVertex3f(x, y2, z2);
                glVertex3f(x2, y2, z2);
                glVertex3f(x2, y, z2);
                glVertex3f(x, y, z2);
                
                //Left Face
                glColor3f(0.955f, 0.839f, 0.149f);
                glVertex3f(x2, y, z);
                glVertex3f(x2, y, z2);
                glVertex3f(x2, y2, z2);
                glVertex3f(x2, y2, z);
                
                //Right Face
                glColor3f(0.337f, 0.149f, 0.327f);
                glVertex3f(x, y, z2);
                glVertex3f(x, y, z);
                glVertex3f(x, y2, z);
                glVertex3f(x, y2, z2);
            glEnd();
            
            //Top Outline
            glBegin(GL_LINE_LOOP);
                glColor3f(0.0f,0.0f,0.0f);
                glVertex3f(x, y, z2);
                glVertex3f(x2, y, z2);
                glVertex3f(x2, y, z);
                glVertex3f(x, y, z);
            glEnd();
            
            //Bottom Outline
            glBegin(GL_LINE_LOOP);
                glVertex3f(x, y2, z);
                glVertex3f(x2, y2, z);
                glVertex3f(x2, y2, z2);
                glVertex3f(x, y2, z2);
            glEnd();

            //Front Outline
            glBegin(GL_LINE_LOOP);
                glVertex3f(x, y, z);
                glVertex3f(x2, y, z);
                glVertex3f(x2, y2, z);
                glVertex3f(x, y2, z);
            glEnd();

            //Back Outline
            glBegin(GL_LINE_LOOP);
                glVertex3f(x, y2, z2);
                glVertex3f(x2, y2, z2);
                glVertex3f(x2, y, z2);
                glVertex3f(x, y, z2);
            glEnd();
            
            //Left Outline
            glBegin(GL_LINE_LOOP);
                glVertex3f(x2, y, z);
                glVertex3f(x2, y, z2);
                glVertex3f(x2, y2, z2);
                glVertex3f(x2, y2, z);
            glEnd();

            //Right Outline
            glBegin(GL_LINE_LOOP);
                glVertex3f(x, y, z2);
                glVertex3f(x, y, z);
                glVertex3f(x, y2, z);
                glVertex3f(x, y2, z2);
            glEnd();
        } catch(Exception e) {
            System.out.println(e);
        }
    }
    
    // method: drawGrid
    // purpose: this method draws a reference grid
    public void drawGrid() {
        
        try {
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
    
    // method: drawPoint
    // purpose: this method draws a single point
    public void drawPoint(float x, float y, float z) {
        
        try {
            glBegin(GL_POINTS);
                glColor3f(1.0f, 1.0f, 1.0f);
                glVertex3f(x, y, z);
            glEnd();
        } catch(Exception e) {
            System.out.println(e);
        }
    }
}
