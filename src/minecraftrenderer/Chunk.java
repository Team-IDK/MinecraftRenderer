/***************************************************************
* file: Chunk.java
* author: Team NULL - Kyle Hubbard
* class: CS 4450 - Computer Graphics
*
* assignment: final program
* date last modified: 4/15/2019
*
* purpose: Data structure to bundle up a number of blocks together
* and then only make a single call to the renderer for each chunk.
* 
****************************************************************/

package minecraftrenderer;

import java.nio.FloatBuffer;
import java.util.Random;
import minecraftrenderer.noise.SimplexNoise;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Chunk {
    
    static final int CHUNK_SIZE = 100;
    static final int CUBE_LENGTH = 2;
    private int startX, startY, startZ;
    
    private Block[][][] Blocks;
    private Texture texture;
    private int VBOVertexHandle;
    private int VBOColorHandle;
    private int VBOTextureHandle;
    
    private Random r;
    private int seed;
    private SimplexNoise noise;

    // method: Chunk
    // purpose: this constructor generates the chunk data
    public Chunk(int initX, int initY, int initZ) {
        
        try {
            texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/terrain.png"));
        } catch(Exception e) {
            System.out.print("Error loading texture source file: " + e);
        }
        
        seed = generateSeed();
        r = new Random();
        
        Blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        for(int x = 0; x < CHUNK_SIZE; x++) {
            for(int y = 0; y < CHUNK_SIZE; y++) {
                for(int z = 0; z < CHUNK_SIZE; z++) {
                    if (y == 0) {
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Bedrock);
                    } else if ((y < (CHUNK_SIZE / 4)) && (y >= 1)) {
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Stone);
                    }  else if ((y < CHUNK_SIZE)  && (y >= (CHUNK_SIZE / 4))) {
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Dirt);
                    } else {
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Bedrock);
                    }
                    
//                    if (r.nextFloat() > 0.6f) {
//                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Grass);
//                    } else if (r.nextFloat() > 0.5f) {
//                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Dirt);
//                    } else if (r.nextFloat() > 0.4f) {
//                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Sand);
//                    } else if (r.nextFloat() > 0.3f) {
//                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Water);
//                    } else if (r.nextFloat() > 0.2f) {
//                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Stone);
//                    } else {
//                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Bedrock);
//                    }
                }
            }
        }
        
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        
        startX = initX;
        startY = initY;
        startZ = initZ;
        rebuildMesh(); 
    }
    
    // method: render
    // purpose: this method renders the various buffers
    public void render() {
        
        glPushMatrix();
            glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
            glVertexPointer(3, GL_FLOAT, 0, 0L);
            glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
            glColorPointer(3, GL_FLOAT, 0, 0L);
            glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
            glBindTexture(GL_TEXTURE_2D, 1);
            glTexCoordPointer(2, GL_FLOAT, 0, 0L);
            glDrawArrays(GL_QUADS, 0, CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 24);
        glPopMatrix();
    }
    
    // method: rebuildMesh
    // purpose: this method updates the block buffers and facilitates noise generation
    public void rebuildMesh() {
        
        seed = generateSeed();
        noise = new SimplexNoise(8000, 0.5, seed);
        
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers(); 
        VBOTextureHandle = glGenBuffers();
        
        FloatBuffer VertexPositionData = BufferUtils.createFloatBuffer(
                (CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexColorData = BufferUtils.createFloatBuffer(
                (CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexTextureData = BufferUtils.createFloatBuffer(
                (CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        
        for(float x = 0; x < CHUNK_SIZE; x += 1) {
            for (float z = 0; z < CHUNK_SIZE; z += 1) {
                
                double height = (startY + (100 * noise.getNoise((int) x + startX, startY, (int) z + startZ)) * CUBE_LENGTH);
                double maxHeight = Math.max(1, Math.min(Math.abs(height) + 50, CHUNK_SIZE));
                for(float y = 0; y < maxHeight; y++) {
                    if (y <= maxHeight && y > maxHeight - 1) {
                        if (r.nextFloat() > .7f) {
                            Blocks[(int) x][(int) y][(int) z].setID(Block.BlockType.BlockType_Water);
                        } else if (r.nextFloat() > .5f) {
                            Blocks[(int) x][(int) y][(int) z].setID(Block.BlockType.BlockType_Sand);
                        } else {
                            Blocks[(int) x][(int) y][(int) z].setID(Block.BlockType.BlockType_Grass);
                        }
                    }
                    VertexPositionData.put(createCube(
                        (float) (startX + x * CUBE_LENGTH),
                        (float) (y * CUBE_LENGTH + (int) (CHUNK_SIZE * .8)),
                        (float) (startZ + z * CUBE_LENGTH)));
                    VertexColorData.put(createCubeVertexCol(getCubeColor(
                        Blocks[(int) x][(int) y][(int) z])));
                    VertexTextureData.put(createTexCube((float) 0, (float) 0,
                        Blocks[(int) x][(int) y][(int) z]));
                }
            }
        }
        
        // End of for loops
        VertexColorData.flip();
        VertexPositionData.flip();
        VertexTextureData.flip();
        
        glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexPositionData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexColorData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexTextureData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
    
    // method: createCubeVertexCol
    // purpose: this method generates the vertex color data for each cube
    private float[] createCubeVertexCol(float[] CubeColorArray) {
        
        float[] cubeColors = new float[CubeColorArray.length * 4 * 6];
        
        for (int i = 0; i < cubeColors.length; i++) {
            cubeColors[i] = CubeColorArray[i % CubeColorArray.length];
        }
        
        return cubeColors;
    }
    
    // method: createCube
    // purpose: this method creates cubes using the given coordinates
    public static float[] createCube(float x, float y, float z) {
        
        int offset = CUBE_LENGTH / 2;
        
        return new float [] {
            // TOP QUAD
            x + offset, y + offset, z,
            x - offset, y + offset, z,
            x - offset, y + offset, z - CUBE_LENGTH,
            x + offset, y + offset, z - CUBE_LENGTH,
            // BOTTOM QUAD
            x + offset, y - offset, z - CUBE_LENGTH,
            x - offset, y - offset, z - CUBE_LENGTH,
            x - offset, y - offset, z,
            x + offset, y - offset, z,
            // FRONT QUAD
            x + offset, y + offset, z - CUBE_LENGTH,
            x - offset, y + offset, z - CUBE_LENGTH,
            x - offset, y - offset, z - CUBE_LENGTH,
            x + offset, y - offset, z - CUBE_LENGTH,
            // BACK QUAD
            x + offset, y - offset, z,
            x - offset, y - offset, z,
            x - offset, y + offset, z,
            x + offset, y + offset, z,
            // LEFT QUAD
            x - offset, y + offset, z - CUBE_LENGTH,
            x - offset, y + offset, z,
            x - offset, y - offset, z,
            x - offset, y - offset, z - CUBE_LENGTH,
            // RIGHT QUAD
            x + offset, y + offset, z,
            x + offset, y + offset, z - CUBE_LENGTH,
            x + offset, y - offset, z - CUBE_LENGTH,
            x + offset, y - offset, z
        };
    }
    
    // method: getCubeColor
    // purpose: this method returns the cube's color
    private float[] getCubeColor(Block block) {
        return new float[] {1, 1, 1};
    }
    
    // method: createTexCube
    // purpose: this method maps the texture sheet data to the cube based on the block ID
    public static float[] createTexCube(float x, float y, Block block) {
        
        float offset = (1024f/16) / 1024f;
        
        switch(block.getID()) {
            
            // Grass
            case 0:
                return new float[] {
                    // BOTTOM QUAD(DOWN=+Y)
                    x + offset*3, y + offset*10,
                    x + offset*2, y + offset*10,
                    x + offset*2, y + offset*9,
                    x + offset*3, y + offset*9,
                    // TOP!
                    x + offset*3, y + offset*1,
                    x + offset*2, y + offset*1,
                    x + offset*2, y + offset*0,
                    x + offset*3, y + offset*0,
                    // FRONT QUAD
                    x + offset*3, y + offset*0,
                    x + offset*4, y + offset*0,
                    x + offset*4, y + offset*1,
                    x + offset*3, y + offset*1,
                    // BACK QUAD
                    x + offset*4, y + offset*1,
                    x + offset*3, y + offset*1,
                    x + offset*3, y + offset*0,
                    x + offset*4, y + offset*0,
                    // LEFT QUAD
                    x + offset*3, y + offset*0,
                    x + offset*4, y + offset*0,
                    x + offset*4, y + offset*1,
                    x + offset*3, y + offset*1,
                    // RIGHT QUAD
                    x + offset*3, y + offset*0,
                    x + offset*4, y + offset*0,
                    x + offset*4, y + offset*1,
                    x + offset*3, y + offset*1
                };
            
            // Sand
            case 1:
                return new float[] {
                    // BOTTOM QUAD (DOWN=+Y)
                    x + offset*3, y + offset*2,
                    x + offset*2, y + offset*2,
                    x + offset*2, y + offset*1,
                    x + offset*3, y + offset*1,
                    // TOP
                    x + offset*3, y + offset*2,
                    x + offset*2, y + offset*2,
                    x + offset*2, y + offset*1,
                    x + offset*3, y + offset*1,
                    // FRONT QUAD
                    x + offset*2, y + offset*1,
                    x + offset*3, y + offset*1,
                    x + offset*3, y + offset*2,
                    x + offset*2, y + offset*2,
                    // BACK QUAD
                    x + offset*3, y + offset*2,
                    x + offset*2, y + offset*2,
                    x + offset*2, y + offset*1,
                    x + offset*3, y + offset*1,
                    // LEFT QUAD
                    x + offset*2, y + offset*1,
                    x + offset*3, y + offset*1,
                    x + offset*3, y + offset*2,
                    x + offset*2, y + offset*2,
                    // RIGHT QUAD
                    x + offset*2, y + offset*1,
                    x + offset*3, y + offset*1,
                    x + offset*3, y + offset*2,
                    x + offset*2, y + offset*2
                };
            
            // Water
            case 2:
                return new float[] {
                    // BOTTOM QUAD (DOWN=+Y)
                    x + offset*14, y + offset*13,
                    x + offset*13, y + offset*13,
                    x + offset*13, y + offset*12,
                    x + offset*14, y + offset*12,
                    // TOP
                    x + offset*14, y + offset*13,
                    x + offset*13, y + offset*13,
                    x + offset*13, y + offset*12,
                    x + offset*14, y + offset*12,
                    // FRONT QUAD
                    x + offset*13, y + offset*12,
                    x + offset*14, y + offset*12,
                    x + offset*14, y + offset*13,
                    x + offset*13, y + offset*13,
                    // BACK QUAD
                    x + offset*14, y + offset*13,
                    x + offset*13, y + offset*13,
                    x + offset*13, y + offset*12,
                    x + offset*14, y + offset*12,
                    // LEFT QUAD
                    x + offset*13, y + offset*12,
                    x + offset*14, y + offset*12,
                    x + offset*14, y + offset*13,
                    x + offset*13, y + offset*13,
                    // RIGHT QUAD
                    x + offset*13, y + offset*12,
                    x + offset*14, y + offset*12,
                    x + offset*14, y + offset*13,
                    x + offset*13, y + offset*13
                };
            
            // Dirt
            case 3:
                return new float[] {
                    // BOTTOM QUAD (DOWN=+Y)
                    x + offset*3, y + offset*1,
                    x + offset*2, y + offset*1,
                    x + offset*2, y + offset*0,
                    x + offset*3, y + offset*0,
                    // TOP
                    x + offset*3, y + offset*1,
                    x + offset*2, y + offset*1,
                    x + offset*2, y + offset*0,
                    x + offset*3, y + offset*0,
                    // FRONT QUAD
                    x + offset*2, y + offset*0,
                    x + offset*3, y + offset*0,
                    x + offset*3, y + offset*1,
                    x + offset*2, y + offset*1,
                    // BACK QUAD
                    x + offset*3, y + offset*1,
                    x + offset*2, y + offset*1,
                    x + offset*2, y + offset*0,
                    x + offset*3, y + offset*0,
                    // LEFT QUAD
                    x + offset*2, y + offset*0,
                    x + offset*3, y + offset*0,
                    x + offset*3, y + offset*1,
                    x + offset*2, y + offset*1,
                    // RIGHT QUAD
                    x + offset*2, y + offset*0,
                    x + offset*3, y + offset*0,
                    x + offset*3, y + offset*1,
                    x + offset*2, y + offset*1
                };
                
            // Stone    
            case 4:
                return new float[] {
                    // BOTTOM QUAD (DOWN=+Y)
                    x + offset*2, y + offset*1,
                    x + offset*1, y + offset*1,
                    x + offset*1, y + offset*0,
                    x + offset*2, y + offset*0,
                    // TOP
                    x + offset*2, y + offset*1,
                    x + offset*1, y + offset*1,
                    x + offset*1, y + offset*0,
                    x + offset*2, y + offset*0,
                    // FRONT QUAD
                    x + offset*1, y + offset*0,
                    x + offset*2, y + offset*0,
                    x + offset*2, y + offset*1,
                    x + offset*1, y + offset*1,
                    // BACK QUAD
                    x + offset*2, y + offset*1,
                    x + offset*1, y + offset*1,
                    x + offset*1, y + offset*0,
                    x + offset*2, y + offset*0,
                    // LEFT QUAD
                    x + offset*1, y + offset*0,
                    x + offset*2, y + offset*0,
                    x + offset*2, y + offset*1,
                    x + offset*1, y + offset*1,
                    // RIGHT QUAD
                    x + offset*1, y + offset*0,
                    x + offset*2, y + offset*0,
                    x + offset*2, y + offset*1,
                    x + offset*1, y + offset*1
                };
            
            // Bedrock
            case 5:
                return new float[] {
                    // BOTTOM QUAD (DOWN=+Y)
                    x + offset*2, y + offset*2,
                    x + offset*1, y + offset*2,
                    x + offset*1, y + offset*1,
                    x + offset*2, y + offset*1,
                    // TOP
                    x + offset*2, y + offset*2,
                    x + offset*1, y + offset*2,
                    x + offset*1, y + offset*1,
                    x + offset*2, y + offset*1,
                    // FRONT QUAD
                    x + offset*1, y + offset*1,
                    x + offset*2, y + offset*1,
                    x + offset*2, y + offset*2,
                    x + offset*1, y + offset*2,
                    // BACK QUAD
                    x + offset*2, y + offset*2,
                    x + offset*1, y + offset*2,
                    x + offset*1, y + offset*1,
                    x + offset*2, y + offset*1,
                    // LEFT QUAD
                    x + offset*1, y + offset*1,
                    x + offset*2, y + offset*1,
                    x + offset*2, y + offset*2,
                    x + offset*1, y + offset*2,
                    // RIGHT QUAD
                    x + offset*1, y + offset*1,
                    x + offset*2, y + offset*1,
                    x + offset*2, y + offset*2,
                    x + offset*1, y + offset*2
                };
                    
            default:
                return new float[] {
                    // BOTTOM QUAD (DOWN=+Y)
                    x + offset*1, y + offset*1,
                    x + offset*0, y + offset*1,
                    x + offset*0, y + offset*0,
                    x + offset*1, y + offset*0,
                    // TOP
                    x + offset*1, y + offset*1,
                    x + offset*0, y + offset*1,
                    x + offset*0, y + offset*0,
                    x + offset*1, y + offset*0,
                    // FRONT QUAD
                    x + offset*0, y + offset*0,
                    x + offset*1, y + offset*0,
                    x + offset*1, y + offset*1,
                    x + offset*0, y + offset*1,
                    // BACK QUAD
                    x + offset*1, y + offset*1,
                    x + offset*0, y + offset*1,
                    x + offset*0, y + offset*0,
                    x + offset*1, y + offset*0,
                    // LEFT QUAD
                    x + offset*0, y + offset*0,
                    x + offset*1, y + offset*0,
                    x + offset*1, y + offset*1,
                    x + offset*0, y + offset*1,
                    // RIGHT QUAD
                    x + offset*0, y + offset*0,
                    x + offset*1, y + offset*0,
                    x + offset*1, y + offset*1,
                    x + offset*0, y + offset*1
                };
        }
    }

    // method: generateSeed
    // purpose: this method generates a random seed based on time
    private int generateSeed() {
        
        Random rand = new Random();
        return (int) System.currentTimeMillis() + rand.nextInt(10000);
    }
}