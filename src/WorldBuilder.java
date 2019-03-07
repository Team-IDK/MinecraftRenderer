/***************************************************************
* file: WorldBuilder.java
* author: Team NULL - Kyle Hubbard
* class: CS 4450 - Computer Graphics
*
* assignment: final program
* date last modified: 3/7/2019
*
* purpose: This program handles the rendering of primitives to be used
* in the FPCameraController class.
*
****************************************************************/

import static org.lwjgl.opengl.GL11.*;

public class WorldBuilder {
    
    // method: drawCube
    // purpose: this method draws a multi-colored cube outlined in black
    public void drawCube() {
        
        try {
            glBegin(GL_QUADS);
                //Top Face
                glColor3f(0.835f, 0.133f, 0.322f);
                glVertex3f( 1.0f, 1.0f,-1.0f);
                glVertex3f(-1.0f, 1.0f,-1.0f);
                glVertex3f(-1.0f, 1.0f, 1.0f);
                glVertex3f( 1.0f, 1.0f, 1.0f);
                
                //Bottom Face
                glColor3f(0.416f, 0.816f, 0.129f);
                glVertex3f( 1.0f,-1.0f, 1.0f);
                glVertex3f(-1.0f,-1.0f, 1.0f);
                glVertex3f(-1.0f,-1.0f,-1.0f);
                glVertex3f( 1.0f,-1.0f,-1.0f);

                //Front Face
                glColor3f(0.114f, 0.439f, 0.58f);
                glVertex3f( 1.0f, 1.0f, 1.0f);
                glVertex3f(-1.0f, 1.0f, 1.0f);
                glVertex3f(-1.0f,-1.0f, 1.0f);
                glVertex3f( 1.0f,-1.0f, 1.0f);
                
                //Back Face
                glColor3f(0.925f, 0.455f, 0.149f);
                glVertex3f( 1.0f,-1.0f,-1.0f);
                glVertex3f(-1.0f,-1.0f,-1.0f);
                glVertex3f(-1.0f, 1.0f,-1.0f);
                glVertex3f( 1.0f, 1.0f,-1.0f);
                
                //Left Face
                glColor3f(0.955f, 0.839f, 0.149f);
                glVertex3f(-1.0f, 1.0f,1.0f);
                glVertex3f(-1.0f, 1.0f,-1.0f);
                glVertex3f(-1.0f,-1.0f,-1.0f);
                glVertex3f(-1.0f,-1.0f, 1.0f);
                
                //Right Face
                glColor3f(0.337f, 0.149f, 0.327f);
                glVertex3f( 1.0f, 1.0f,-1.0f);
                glVertex3f( 1.0f, 1.0f, 1.0f);
                glVertex3f( 1.0f,-1.0f, 1.0f);
                glVertex3f( 1.0f,-1.0f,-1.0f);
            glEnd();
            
            //Top Outline
            glBegin(GL_LINE_LOOP);
                glColor3f(0.0f,0.0f,0.0f);
                glVertex3f( 1.0f, 1.0f,-1.0f);
                glVertex3f(-1.0f, 1.0f,-1.0f);
                glVertex3f(-1.0f, 1.0f, 1.0f);
                glVertex3f( 1.0f, 1.0f, 1.0f);
            glEnd();
            
            //Bottom Outline
            glBegin(GL_LINE_LOOP);
                glVertex3f( 1.0f,-1.0f, 1.0f);
                glVertex3f(-1.0f,-1.0f, 1.0f);
                glVertex3f(-1.0f,-1.0f,-1.0f);
                glVertex3f( 1.0f,-1.0f,-1.0f);
            glEnd();

            //Front Outline
            glBegin(GL_LINE_LOOP);
                glVertex3f( 1.0f, 1.0f, 1.0f);
                glVertex3f(-1.0f, 1.0f, 1.0f);
                glVertex3f(-1.0f,-1.0f, 1.0f);
                glVertex3f( 1.0f,-1.0f, 1.0f);
            glEnd();

            //Back Outline
            glBegin(GL_LINE_LOOP);
                glVertex3f( 1.0f,-1.0f,-1.0f);
                glVertex3f(-1.0f,-1.0f,-1.0f);
                glVertex3f(-1.0f, 1.0f,-1.0f);
                glVertex3f( 1.0f, 1.0f,-1.0f);
            glEnd();
            
            //Left Outline
            glBegin(GL_LINE_LOOP);
                glVertex3f(-1.0f, 1.0f, 1.0f);
                glVertex3f(-1.0f, 1.0f,-1.0f);
                glVertex3f(-1.0f,-1.0f,-1.0f);
                glVertex3f(-1.0f,-1.0f, 1.0f);
            glEnd();

            //Right Outline
            glBegin(GL_LINE_LOOP);
                glVertex3f( 1.0f, 1.0f,-1.0f);
                glVertex3f( 1.0f, 1.0f, 1.0f);
                glVertex3f( 1.0f,-1.0f, 1.0f);
                glVertex3f( 1.0f,-1.0f,-1.0f);
            glEnd();
        } catch(Exception e) {
            System.out.println(e);
        }
    }
    
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
}
