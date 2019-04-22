/***************************************************************
* file: Chunk.java
* author: Team NULL
* class: CS 4450 - Computer Graphics
*
* assignment: final program
* date last modified: 4/22/2019
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
    
    public static final int CHUNK_SIZE = 100;
    static final int CUBE_LENGTH = 2;
    private int startX, startY, startZ;
    
    public Block[][][] Blocks;
    private Texture texture;
    private int VBOVertexHandle;
    private int VBOColorHandle;
    private int VBOTextureHandle;
    
    private Random r;
    private int seed;
    private SimplexNoise noise;
    boolean isNether, portalBuilt;

    // method: Chunk
    // purpose: this constructor generates the chunk data
    public Chunk(int initX, int initY, int initZ, boolean isNether) {
        
        this.isNether = isNether;
        portalBuilt = false;
        
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
                    if (y <= 2) {
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Bedrock);
                        Blocks[x][y][z].setCoords(x, y, z);
                    } else if ((y < (CHUNK_SIZE / 4)) && (y >= 3)) {
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Stone);
                        Blocks[x][y][z].setCoords(x, y, z);
                    } else if ((y < CHUNK_SIZE)  && (y >= (CHUNK_SIZE / 4))) {
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Dirt);
                        Blocks[x][y][z].setCoords(x, y, z);
                    } else {
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Bedrock);
                        Blocks[x][y][z].setCoords(x, y, z);
                    }
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
        
        if(!isNether)
            noise = new SimplexNoise(8000, 0.5, seed);
        else
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
        
        float seaLevel = 53f;
        float sandLevel = 55f;
        if(isNether)
            sandLevel++;
        
        for(float x = 0; x < CHUNK_SIZE; x += 1) {
            for (float z = 0; z < CHUNK_SIZE; z += 1) {
                double height = (startY + (100 * noise.getNoise((int) x + startX, startY, (int) z + startZ)) * CUBE_LENGTH);
                float maxHeight = (float) Math.max(1, Math.min(Math.abs(height) + 47, CHUNK_SIZE));
                for(float y = 0; y <= maxHeight; y++) {
                    if (maxHeight < seaLevel && y > maxHeight - 5) {
                        for (float i = y; i < seaLevel; i++) {
                            
                            if(!isNether)
                                Blocks[(int) x][(int) i][(int) z].setID(Block.BlockType.BlockType_Water);
                            else
                                Blocks[(int) x][(int) i][(int) z].setID(Block.BlockType.BlockType_Lava);
                            
                            VertexPositionData.put(createCube(
                                (float) (startX + x * CUBE_LENGTH),
                                (float) (i * CUBE_LENGTH + (int) (CHUNK_SIZE * .8)),
                                (float) (startZ + z * CUBE_LENGTH)));
                            VertexColorData.put(createCubeVertexCol(getCubeColor(
                                Blocks[(int) x][(int) i][(int) z])));
                            VertexTextureData.put(createTexCube((float) 0, (float) 0,
                                Blocks[(int) x][(int) i][(int) z]));
                        }
                    } else if (maxHeight < seaLevel && y > maxHeight - 9) {
                        for (float i = y; i <= maxHeight - 5; i++) {
                            
                            if(!isNether)
                                Blocks[(int) x][(int) i][(int) z].setID(Block.BlockType.BlockType_Sand);
                            else
                                Blocks[(int) x][(int) i][(int) z].setID(Block.BlockType.BlockType_SoulSand);
                            
                            VertexPositionData.put(createCube(
                                (float) (startX + x * CUBE_LENGTH),
                                (float) (i * CUBE_LENGTH + (int) (CHUNK_SIZE * .8)),
                                (float) (startZ + z * CUBE_LENGTH)));
                            VertexColorData.put(createCubeVertexCol(getCubeColor(
                                Blocks[(int) x][(int) i][(int) z])));
                            VertexTextureData.put(createTexCube((float) 0, (float) 0,
                                Blocks[(int) x][(int) i][(int) z]));
                        }
                    } else if (maxHeight < sandLevel && y > maxHeight - 8) {
                        for (float i = y; i <= maxHeight; i++) {
                            
                            if(!isNether)
                                Blocks[(int) x][(int) i][(int) z].setID(Block.BlockType.BlockType_Sand);
                            else
                                Blocks[(int) x][(int) i][(int) z].setID(Block.BlockType.BlockType_SoulSand);

                            VertexPositionData.put(createCube(
                                (float) (startX + x * CUBE_LENGTH),
                                (float) (i * CUBE_LENGTH + (int) (CHUNK_SIZE * .8)),
                                (float) (startZ + z * CUBE_LENGTH)));
                            VertexColorData.put(createCubeVertexCol(getCubeColor(
                                Blocks[(int) x][(int) i][(int) z])));
                            VertexTextureData.put(createTexCube((float) 0, (float) 0,
                                Blocks[(int) x][(int) i][(int) z]));
                            
                            if(y > maxHeight - 2 && (r.nextInt(400) + 1 == 1)) {
                                
                                if(!isNether)
                                    buildCactus((int) x,(int) y + 1,(int) z, Blocks, VertexPositionData, VertexColorData, VertexTextureData);
                                else if(isNether && r.nextInt(75) + 1 == 1) {
                                    if(!portalBuilt && x > 10 && x < 90 && z > 10 && z < 90)
                                    buildPortal((int) x,(int) y + 1,(int) z, Blocks, VertexPositionData, VertexColorData, VertexTextureData);
                                }
                            }
                        }
                    } else if (y > maxHeight-1) {
                        
                        if(!isNether)
                            Blocks[(int) x][(int) y][(int) z].setID(Block.BlockType.BlockType_Grass);
                        else
                            Blocks[(int) x][(int) y][(int) z].setID(Block.BlockType.BlockType_Netherrack);
                        
                        if(!isNether && r.nextInt(100) + 1 == 1)
                            buildTree((int) x,(int) y + 1,(int) z, Blocks, VertexPositionData, VertexColorData, VertexTextureData);
                        else if(isNether && r.nextInt(75) + 1 == 1) {
                            if(r.nextInt(100) + 1 > 25)
                                Blocks[(int) x][(int) y][(int) z].setID(Block.BlockType.BlockType_Glowstone);
                            else
                                Blocks[(int) x][(int) y][(int) z].setID(Block.BlockType.BlockType_NetherQuartz);
                                             
                            if(!portalBuilt && x > 10 && x < 90 && z > 10 && z < 90)
                                buildPortal((int) x,(int) y + 1,(int) z, Blocks, VertexPositionData, VertexColorData, VertexTextureData);
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
    
    // method: buildTree
    // purpose: this method spawns a tree at the given location
    private void buildTree(int x, int y, int z, Block[][][] Blocks, FloatBuffer VertexPositionData, FloatBuffer VertexColorData, FloatBuffer VertexTextureData) {
        
        int xOffset = 0, zOffset = 0;
        Random r = new Random();
        
        if(y > 94) {
            //Don't spawn
        } else if(r.nextInt(100) >= 40) {
            
            // Large Tree
            for(int i = y; i <= y + 6; i++) {

                if(i >= y + 3) {

                    if(i == y + 3 || i == y + 4) {
                        xOffset = -2;
                        zOffset = -2;
                    } else if(i == y + 5 || i == y + 6) {
                        xOffset = -1;
                        zOffset = -1;
                    }

                    for(int j = xOffset; j <= xOffset * -1; j++) {

                        if(x + j < 0 || x + j > 99)
                            continue;

                        for(int k = zOffset; k <= zOffset * -1; k++) {

                            if(z + k < 0 || z + k > 99)
                                continue;
                            if(i == y + 3) {
                                if((j == -2 && k == -2) || (j == 2 && k == -2) || (j == -2 && k == 2) || (j == 2 && k == 2))
                                    continue;
                            }
                            if(i == y + 6) {
                                if((j == -1 && k == -1) || (j == 1 && k == -1) || (j == -1 && k == 1) || (j == 1 && k == 1))
                                    continue;
                            }

                            Blocks[(int) x + j][(int) i][(int) z + k].setID(Block.BlockType.BlockType_Leaf);

                            VertexPositionData.put(createCube(
                                (float) (startX + (x + j) * CUBE_LENGTH),
                                (float) (i * CUBE_LENGTH + (int) (CHUNK_SIZE * .8)),
                                (float) (startZ + (z + k) * CUBE_LENGTH)));
                            VertexColorData.put(createCubeVertexCol(getCubeColor(
                                Blocks[(int) x + j][(int) i][(int) z + k])));
                            VertexTextureData.put(createTexCube((float) 0, (float) 0,
                                Blocks[(int) x + j][(int) i][(int) z + k]));
                        }
                    }
                }

                Blocks[(int) x][(int) i][(int) z].setID(Block.BlockType.BlockType_Log);    

                VertexPositionData.put(createCube(
                    (float) (startX + x * CUBE_LENGTH),
                    (float) (i * CUBE_LENGTH + (int) (CHUNK_SIZE * .8)),
                    (float) (startZ + z * CUBE_LENGTH)));
                VertexColorData.put(createCubeVertexCol(getCubeColor(
                    Blocks[(int) x][(int) i][(int) z])));
                VertexTextureData.put(createTexCube((float) 0, (float) 0,
                    Blocks[(int) x][(int) i][(int) z])); 
            }    
        } else {

            // Small Tree
            for(int i = y; i <= y + 4; i++) {

                if(i >= y + 2) {

                    if(i == y + 2 || i == y + 3) {
                        xOffset = -1;
                        zOffset = -1;
                    } else if(i == y + 4) {
                        xOffset = 0;
                        zOffset = 0;
                    }

                    for(int j = xOffset; j <= xOffset * -1; j++) {

                        if(x + j < 0 || x + j > 99)
                            continue;

                        for(int k = zOffset; k <= zOffset * -1; k++) {

                            if(z + k < 0 || z + k > 99)
                                continue;
                            if(i == y + 2) {
                                if((j == -1 && k == -1) || (j == 1 && k == -1) || (j == -1 && k == 1) || (j == 1 && k == 1))
                                    continue;
                            }
                            if(i == y + 6) {
                                if((j == -1 && k == -1) || (j == 1 && k == -1) || (j == -1 && k == 1) || (j == 1 && k == 1))
                                    continue;
                            }

                            Blocks[(int) x + j][(int) i][(int) z + k].setID(Block.BlockType.BlockType_Leaf);

                            VertexPositionData.put(createCube(
                                (float) (startX + (x + j) * CUBE_LENGTH),
                                (float) (i * CUBE_LENGTH + (int) (CHUNK_SIZE * .8)),
                                (float) (startZ + (z + k) * CUBE_LENGTH)));
                            VertexColorData.put(createCubeVertexCol(getCubeColor(
                                Blocks[(int) x + j][(int) i][(int) z + k])));
                            VertexTextureData.put(createTexCube((float) 0, (float) 0,
                                Blocks[(int) x + j][(int) i][(int) z + k]));
                        }
                    }
                }

                Blocks[(int) x][(int) i][(int) z].setID(Block.BlockType.BlockType_Log);    

                VertexPositionData.put(createCube(
                    (float) (startX + x * CUBE_LENGTH),
                    (float) (i * CUBE_LENGTH + (int) (CHUNK_SIZE * .8)),
                    (float) (startZ + z * CUBE_LENGTH)));
                VertexColorData.put(createCubeVertexCol(getCubeColor(
                    Blocks[(int) x][(int) i][(int) z])));
                VertexTextureData.put(createTexCube((float) 0, (float) 0,
                    Blocks[(int) x][(int) i][(int) z])); 
            }    
        }
    }
    
    // method: buildCactus
    // purpose: this method spawns a cactus at the given location
    private void buildCactus(int x, int y, int z, Block[][][] Blocks, FloatBuffer VertexPositionData, FloatBuffer VertexColorData, FloatBuffer VertexTextureData) {
        
        for(int i = y; i < y + 3; i++) {
                
            Blocks[(int) x][(int) i][(int) z].setID(Block.BlockType.BlockType_Cactus);

            VertexPositionData.put(createCube(
                (float) (startX + x * CUBE_LENGTH),
                (float) (i * CUBE_LENGTH + (int) (CHUNK_SIZE * .8)),
                (float) (startZ + z * CUBE_LENGTH)));
            VertexColorData.put(createCubeVertexCol(getCubeColor(
                Blocks[(int) x][(int) i][(int) z])));
            VertexTextureData.put(createTexCube((float) 0, (float) 0,
                Blocks[(int) x][(int) i][(int) z]));      
        }              
    }
    
    // method: buildTree
    // purpose: this method spawns a nether portal at the given location
    private void buildPortal(int x, int y, int z, Block[][][] Blocks, FloatBuffer VertexPositionData, FloatBuffer VertexColorData, FloatBuffer VertexTextureData) {

        Random r = new Random();
        
        if(y > 90) {
            //Don't spawn
        } else {

            for(int i = y; i <= y + 5; i++) {
                
                for(int j = x; j <= x + 3; j++) {
                
                    if(j > x && j < x + 3 && i > y && i < y + 5)
                        Blocks[(int) j][(int) i][(int) z].setID(Block.BlockType.BlockType_Portal);
                    else
                        Blocks[(int) j][(int) i][(int) z].setID(Block.BlockType.BlockType_Obsidian);  

                    VertexPositionData.put(createCube(
                        (float) (startX + j * CUBE_LENGTH),
                        (float) (i * CUBE_LENGTH + (int) (CHUNK_SIZE * .8)),
                        (float) (startZ + z * CUBE_LENGTH)));
                    VertexColorData.put(createCubeVertexCol(getCubeColor(
                        Blocks[(int) j][(int) i][(int) z])));
                    VertexTextureData.put(createTexCube((float) 0, (float) 0,
                        Blocks[(int) j][(int) i][(int) z]));   
                }
            }  
            
            portalBuilt = true;
        }
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
        float clip = 0.00095f;
        
        switch(block.getID()) {

            // Grass
            case 0:
                return new float[] {
                    // BOTTOM QUAD(DOWN=+Y)
                    x + offset*3 - clip, y + offset*10 - clip,
                    x + offset*2 + clip, y + offset*10 - clip,
                    x + offset*2 + clip, y + offset*9 + clip,
                    x + offset*3 - clip, y + offset*9 + clip,
                    // TOP!
                    x + offset*3 - clip, y + offset*1 - clip,
                    x + offset*2 + clip, y + offset*1 - clip,
                    x + offset*2 + clip, y + offset*0 + clip,
                    x + offset*3 - clip, y + offset*0 + clip,
                    // FRONT QUAD
                    x + offset*3 + clip, y + offset*0 + clip,
                    x + offset*4 - clip, y + offset*0 + clip,
                    x + offset*4 - clip, y + offset*1 - clip,
                    x + offset*3 + clip, y + offset*1 - clip,
                    // BACK QUAD
                    x + offset*4 - clip, y + offset*1 - clip,
                    x + offset*3 + clip, y + offset*1 - clip,
                    x + offset*3 + clip, y + offset*0 + clip,
                    x + offset*4 - clip, y + offset*0 + clip,
                    // LEFT QUAD
                    x + offset*3 + clip, y + offset*0 + clip,
                    x + offset*4 - clip, y + offset*0 + clip,
                    x + offset*4 - clip, y + offset*1 - clip,
                    x + offset*3 + clip, y + offset*1 - clip,
                    // RIGHT QUAD
                    x + offset*3 + clip, y + offset*0 + clip,
                    x + offset*4 - clip, y + offset*0 + clip,
                    x + offset*4 - clip, y + offset*1 - clip,
                    x + offset*3 + clip, y + offset*1 - clip
                };
            
            // Sand
            case 1:
                return new float[] {
                    // BOTTOM QUAD (DOWN=+Y)
                    x + offset*3 - clip, y + offset*2 - clip,
                    x + offset*2 + clip, y + offset*2 - clip,
                    x + offset*2 + clip, y + offset*1 + clip,
                    x + offset*3 - clip, y + offset*1 + clip,
                    // TOP
                    x + offset*3 - clip, y + offset*2 - clip,
                    x + offset*2 + clip, y + offset*2 - clip,
                    x + offset*2 + clip, y + offset*1 + clip,
                    x + offset*3 - clip, y + offset*1 + clip,
                    // FRONT QUAD
                    x + offset*2 + clip, y + offset*1 + clip,
                    x + offset*3 - clip, y + offset*1 + clip,
                    x + offset*3 - clip, y + offset*2 - clip,
                    x + offset*2 + clip, y + offset*2 - clip,
                    // BACK QUAD
                    x + offset*3 - clip, y + offset*2 - clip,
                    x + offset*2 + clip, y + offset*2 - clip,
                    x + offset*2 + clip, y + offset*1 + clip,
                    x + offset*3 - clip, y + offset*1 + clip,
                    // LEFT QUAD
                    x + offset*2 + clip, y + offset*1 + clip,
                    x + offset*3 - clip, y + offset*1 + clip,
                    x + offset*3 - clip, y + offset*2 - clip,
                    x + offset*2 + clip, y + offset*2 - clip,
                    // RIGHT QUAD
                    x + offset*2 + clip, y + offset*1 + clip,
                    x + offset*3 - clip, y + offset*1 + clip,
                    x + offset*3 - clip, y + offset*2 - clip,
                    x + offset*2 + clip, y + offset*2 - clip
                };
            
            // Water
            case 2:
                return new float[] {
                    // BOTTOM QUAD (DOWN=+Y)
                    x + offset*14 - clip, y + offset*13 - clip,
                    x + offset*13 + clip, y + offset*13 - clip,
                    x + offset*13 + clip, y + offset*12 + clip,
                    x + offset*14 - clip, y + offset*12 + clip,
                    // TOP
                    x + offset*14 - clip, y + offset*13 - clip,
                    x + offset*13 + clip, y + offset*13 - clip,
                    x + offset*13 + clip, y + offset*12 + clip,
                    x + offset*14 - clip, y + offset*12 + clip,
                    // FRONT QUAD
                    x + offset*13 + clip, y + offset*12 + clip,
                    x + offset*14 - clip, y + offset*12 + clip,
                    x + offset*14 - clip, y + offset*13 - clip,
                    x + offset*13 + clip, y + offset*13 - clip,
                    // BACK QUAD
                    x + offset*14 - clip, y + offset*13 - clip,
                    x + offset*13 + clip, y + offset*13 - clip,
                    x + offset*13 + clip, y + offset*12 + clip,
                    x + offset*14 - clip, y + offset*12 + clip,
                    // LEFT QUAD
                    x + offset*13 + clip, y + offset*12 + clip,
                    x + offset*14 - clip, y + offset*12 + clip,
                    x + offset*14 - clip, y + offset*13 - clip,
                    x + offset*13 + clip, y + offset*13 - clip,
                    // RIGHT QUAD
                    x + offset*13 + clip, y + offset*12 + clip,
                    x + offset*14 - clip, y + offset*12 + clip,
                    x + offset*14 - clip, y + offset*13 - clip,
                    x + offset*13 + clip, y + offset*13 - clip
                };
            
            // Dirt
            case 3:
                return new float[] {
                    // BOTTOM QUAD (DOWN=+Y)
                    x + offset*3 - clip, y + offset*1 - clip,
                    x + offset*2 + clip, y + offset*1 - clip,
                    x + offset*2 + clip, y + offset*0 + clip,
                    x + offset*3 - clip, y + offset*0 + clip,
                    // TOP
                    x + offset*3 - clip, y + offset*1 - clip,
                    x + offset*2 + clip, y + offset*1 - clip,
                    x + offset*2 + clip, y + offset*0 + clip,
                    x + offset*3 - clip, y + offset*0 + clip,
                    // FRONT QUAD
                    x + offset*2 + clip, y + offset*0 + clip,
                    x + offset*3 - clip, y + offset*0 + clip,
                    x + offset*3 - clip, y + offset*1 - clip,
                    x + offset*2 + clip, y + offset*1 - clip,
                    // BACK QUAD
                    x + offset*3 - clip, y + offset*1 - clip,
                    x + offset*2 + clip, y + offset*1 - clip,
                    x + offset*2 + clip, y + offset*0 + clip,
                    x + offset*3 - clip, y + offset*0 + clip,
                    // LEFT QUAD
                    x + offset*2 + clip, y + offset*0 + clip,
                    x + offset*3 - clip, y + offset*0 + clip,
                    x + offset*3 - clip, y + offset*1 - clip,
                    x + offset*2 + clip, y + offset*1 - clip,
                    // RIGHT QUAD
                    x + offset*2 + clip, y + offset*0 + clip,
                    x + offset*3 - clip, y + offset*0 + clip,
                    x + offset*3 - clip, y + offset*1 - clip,
                    x + offset*2 + clip, y + offset*1 - clip
                };
                
            // Stone    
            case 4:
                return new float[] {
                    // BOTTOM QUAD (DOWN=+Y)
                    x + offset*2 - clip, y + offset*1 - clip,
                    x + offset*1 + clip, y + offset*1 - clip,
                    x + offset*1 + clip, y + offset*0 + clip,
                    x + offset*2 - clip, y + offset*0 + clip,
                    // TOP
                    x + offset*2 - clip, y + offset*1 - clip,
                    x + offset*1 + clip, y + offset*1 - clip,
                    x + offset*1 + clip, y + offset*0 + clip,
                    x + offset*2 - clip, y + offset*0 + clip,
                    // FRONT QUAD
                    x + offset*1 + clip, y + offset*0 + clip,
                    x + offset*2 - clip, y + offset*0 + clip,
                    x + offset*2 - clip, y + offset*1 - clip,
                    x + offset*1 + clip, y + offset*1 - clip,
                    // BACK QUAD
                    x + offset*2 - clip, y + offset*1 - clip,
                    x + offset*1 + clip, y + offset*1 - clip,
                    x + offset*1 + clip, y + offset*0 + clip,
                    x + offset*2 - clip, y + offset*0 + clip,
                    // LEFT QUAD
                    x + offset*1 + clip, y + offset*0 + clip,
                    x + offset*2 - clip, y + offset*0 + clip,
                    x + offset*2 - clip, y + offset*1 - clip,
                    x + offset*1 + clip, y + offset*1 - clip,
                    // RIGHT QUAD
                    x + offset*1 + clip, y + offset*0 + clip,
                    x + offset*2 - clip, y + offset*0 + clip,
                    x + offset*2 - clip, y + offset*1 - clip,
                    x + offset*1 + clip, y + offset*1 - clip
                };
            
            // Bedrock
            case 5:
                return new float[] {
                    // BOTTOM QUAD (DOWN=+Y)
                    x + offset*2 - clip, y + offset*2 - clip,
                    x + offset*1 + clip, y + offset*2 - clip,
                    x + offset*1 + clip, y + offset*1 + clip,
                    x + offset*2 - clip, y + offset*1 + clip,
                    // TOP
                    x + offset*2 - clip, y + offset*2 - clip,
                    x + offset*1 + clip, y + offset*2 - clip,
                    x + offset*1 + clip, y + offset*1 + clip,
                    x + offset*2 - clip, y + offset*1 + clip,
                    // FRONT QUAD
                    x + offset*1 + clip, y + offset*1 + clip,
                    x + offset*2 - clip, y + offset*1 + clip,
                    x + offset*2 - clip, y + offset*2 - clip,
                    x + offset*1 + clip, y + offset*2 - clip,
                    // BACK QUAD
                    x + offset*2 - clip, y + offset*2 - clip,
                    x + offset*1 + clip, y + offset*2 - clip,
                    x + offset*1 + clip, y + offset*1 + clip,
                    x + offset*2 - clip, y + offset*1 + clip,
                    // LEFT QUAD
                    x + offset*1 + clip, y + offset*1 + clip,
                    x + offset*2 - clip, y + offset*1 + clip,
                    x + offset*2 - clip, y + offset*2 - clip,
                    x + offset*1 + clip, y + offset*2 - clip,
                    // RIGHT QUAD
                    x + offset*1 + clip, y + offset*1 + clip,
                    x + offset*2 - clip, y + offset*1 + clip,
                    x + offset*2 - clip, y + offset*2 - clip,
                    x + offset*1 + clip, y + offset*2 - clip
                };
                
            // Log
            case 6:
                return new float[] {
                    // BOTTOM QUAD (DOWN=+Y)
                    x + offset*6 - clip, y + offset*2 - clip,
                    x + offset*5 + clip, y + offset*2 - clip,
                    x + offset*5 + clip, y + offset*1 + clip,
                    x + offset*6 - clip, y + offset*1 + clip,
                    // TOP
                    x + offset*6 - clip, y + offset*2 - clip,
                    x + offset*5 + clip, y + offset*2 - clip,
                    x + offset*5 + clip, y + offset*1 + clip,
                    x + offset*6 - clip, y + offset*1 + clip,
                    // FRONT QUAD
                    x + offset*4 + clip, y + offset*1 + clip,
                    x + offset*5 - clip, y + offset*1 + clip,
                    x + offset*5 - clip, y + offset*2 - clip,
                    x + offset*4 + clip, y + offset*2 - clip,
                    // BACK QUAD
                    x + offset*5 - clip, y + offset*2 - clip,
                    x + offset*4 + clip, y + offset*2 - clip,
                    x + offset*4 + clip, y + offset*1 + clip,
                    x + offset*5 - clip, y + offset*1 + clip,
                    // LEFT QUAD
                    x + offset*4 + clip, y + offset*1 + clip,
                    x + offset*5 - clip, y + offset*1 + clip,
                    x + offset*5 - clip, y + offset*2 - clip,
                    x + offset*4 + clip, y + offset*2 - clip,
                    // RIGHT QUAD
                    x + offset*4 + clip, y + offset*1 + clip,
                    x + offset*5 - clip, y + offset*1 + clip,
                    x + offset*5 - clip, y + offset*2 - clip,
                    x + offset*4 + clip, y + offset*2 - clip
                };
            
            // Leaf
            case 7:
                return new float[] {
                    // BOTTOM QUAD (DOWN=+Y)
                    x + offset*5 + clip, y + offset*8 + clip,
                    x + offset*6 - clip, y + offset*8 + clip,
                    x + offset*6 - clip, y + offset*9 - clip,
                    x + offset*5 + clip, y + offset*9 - clip,
                    // TOP
                    x + offset*5 + clip, y + offset*8 + clip,
                    x + offset*6 - clip, y + offset*8 + clip,
                    x + offset*6 - clip, y + offset*9 - clip,
                    x + offset*5 + clip, y + offset*9 - clip,
                    // FRONT QUAD
                    x + offset*5 + clip, y + offset*8 + clip,
                    x + offset*6 - clip, y + offset*8 + clip,
                    x + offset*6 - clip, y + offset*9 - clip,
                    x + offset*5 + clip, y + offset*9 - clip,
                    // BACK QUAD
                    x + offset*6 - clip, y + offset*9 - clip,
                    x + offset*5 + clip, y + offset*9 - clip,
                    x + offset*5 + clip, y + offset*8 + clip,
                    x + offset*6 - clip, y + offset*8 + clip,
                    // LEFT QUAD
                    x + offset*5 + clip, y + offset*8 + clip,
                    x + offset*6 - clip, y + offset*8 + clip,
                    x + offset*6 - clip, y + offset*9 - clip,
                    x + offset*5 + clip, y + offset*9 - clip,
                    // RIGHT QUAD
                    x + offset*5 + clip, y + offset*8 + clip,
                    x + offset*6 - clip, y + offset*8 + clip,
                    x + offset*6 - clip, y + offset*9 - clip,
                    x + offset*5 + clip, y + offset*9 - clip
                };
                
            // Cactus
            case 8:
                return new float[] {
                    // BOTTOM QUAD (DOWN=+Y)
                    x + offset*5 + clip, y + offset*4 + clip,
                    x + offset*6 - clip, y + offset*4 + clip,
                    x + offset*6 - clip, y + offset*5 - clip,
                    x + offset*5 + clip, y + offset*5 - clip,
                    // TOP
                    x + offset*7 + clip, y + offset*4 + clip,
                    x + offset*8 - clip, y + offset*4 + clip,
                    x + offset*8 - clip, y + offset*5 - clip,
                    x + offset*7 + clip, y + offset*5 - clip,
                    // FRONT QUAD
                    x + offset*6 + clip, y + offset*4 + clip,
                    x + offset*7 - clip, y + offset*4 + clip,
                    x + offset*7 - clip, y + offset*5 - clip,
                    x + offset*6 + clip, y + offset*5 - clip,
                    // BACK QUAD
                    x + offset*7 - clip, y + offset*5 - clip,
                    x + offset*6 + clip, y + offset*5 - clip,
                    x + offset*6 + clip, y + offset*4 + clip,
                    x + offset*7 - clip, y + offset*4 + clip,
                    // LEFT QUAD
                    x + offset*6 + clip, y + offset*4 + clip,
                    x + offset*7 - clip, y + offset*4 + clip,
                    x + offset*7 - clip, y + offset*5 - clip,
                    x + offset*6 + clip, y + offset*5 - clip,
                    // RIGHT QUAD
                    x + offset*6 + clip, y + offset*4 + clip,
                    x + offset*7 - clip, y + offset*4 + clip,
                    x + offset*7 - clip, y + offset*5 - clip,
                    x + offset*6 + clip, y + offset*5 - clip
                };
                
            // Netherrack
            case 9:
                return new float[] {
                    // BOTTOM QUAD (DOWN=+Y)
                    x + offset*7 + clip, y + offset*6 + clip,
                    x + offset*8 - clip, y + offset*6 + clip,
                    x + offset*8 - clip, y + offset*7 - clip,
                    x + offset*7 + clip, y + offset*7 - clip,
                    // TOP
                    x + offset*7 + clip, y + offset*6 + clip,
                    x + offset*8 - clip, y + offset*6 + clip,
                    x + offset*8 - clip, y + offset*7 - clip,
                    x + offset*7 + clip, y + offset*7 - clip,
                    // FRONT QUAD
                    x + offset*7 + clip, y + offset*6 + clip,
                    x + offset*8 - clip, y + offset*6 + clip,
                    x + offset*8 - clip, y + offset*7 - clip,
                    x + offset*7 + clip, y + offset*7 - clip,
                    // BACK QUAD
                    x + offset*8 - clip, y + offset*7 - clip,
                    x + offset*7 + clip, y + offset*7 - clip,
                    x + offset*7 + clip, y + offset*6 + clip,
                    x + offset*8 - clip, y + offset*6 + clip,
                    // LEFT QUAD
                    x + offset*7 + clip, y + offset*6 + clip,
                    x + offset*8 - clip, y + offset*6 + clip,
                    x + offset*8 - clip, y + offset*7 - clip,
                    x + offset*7 + clip, y + offset*7 - clip,
                    // RIGHT QUAD
                    x + offset*7 + clip, y + offset*6 + clip,
                    x + offset*8 - clip, y + offset*6 + clip,
                    x + offset*8 - clip, y + offset*7 - clip,
                    x + offset*7 + clip, y + offset*7 - clip
                };
                
            // SoulSand
            case 10:
                return new float[] {
                    // BOTTOM QUAD (DOWN=+Y)
                    x + offset*8 + clip, y + offset*6 + clip,
                    x + offset*9 - clip, y + offset*6 + clip,
                    x + offset*9 - clip, y + offset*7 - clip,
                    x + offset*8 + clip, y + offset*7 - clip,
                    // TOP
                    x + offset*8 + clip, y + offset*6 + clip,
                    x + offset*9 - clip, y + offset*6 + clip,
                    x + offset*9 - clip, y + offset*7 - clip,
                    x + offset*8 + clip, y + offset*7 - clip,
                    // FRONT QUAD
                    x + offset*8 + clip, y + offset*6 + clip,
                    x + offset*9 - clip, y + offset*6 + clip,
                    x + offset*9 - clip, y + offset*7 - clip,
                    x + offset*8 + clip, y + offset*7 - clip,
                    // BACK QUAD
                    x + offset*9 - clip, y + offset*7 - clip,
                    x + offset*8 + clip, y + offset*7 - clip,
                    x + offset*8 + clip, y + offset*6 + clip,
                    x + offset*9 - clip, y + offset*6 + clip,
                    // LEFT QUAD
                    x + offset*8 + clip, y + offset*6 + clip,
                    x + offset*9 - clip, y + offset*6 + clip,
                    x + offset*9 - clip, y + offset*7 - clip,
                    x + offset*8 + clip, y + offset*7 - clip,
                    // RIGHT QUAD
                    x + offset*8 + clip, y + offset*6 + clip,
                    x + offset*9 - clip, y + offset*6 + clip,
                    x + offset*9 - clip, y + offset*7 - clip,
                    x + offset*8 + clip, y + offset*7 - clip
                };
                
            // Glowstone
            case 11:
                return new float[] {
                    // BOTTOM QUAD (DOWN=+Y)
                    x + offset*9 + clip, y + offset*6 + clip,
                    x + offset*10 - clip, y + offset*6 + clip,
                    x + offset*10 - clip, y + offset*7 - clip,
                    x + offset*9 + clip, y + offset*7 - clip,
                    // TOP
                    x + offset*9 + clip, y + offset*6 + clip,
                    x + offset*10 - clip, y + offset*6 + clip,
                    x + offset*10 - clip, y + offset*7 - clip,
                    x + offset*9 + clip, y + offset*7 - clip,
                    // FRONT QUAD
                    x + offset*9 + clip, y + offset*6 + clip,
                    x + offset*10 - clip, y + offset*6 + clip,
                    x + offset*10 - clip, y + offset*7 - clip,
                    x + offset*9 + clip, y + offset*7 - clip,
                    // BACK QUAD
                    x + offset*10 - clip, y + offset*7 - clip,
                    x + offset*9 + clip, y + offset*7 - clip,
                    x + offset*9 + clip, y + offset*6 + clip,
                    x + offset*10 - clip, y + offset*6 + clip,
                    // LEFT QUAD
                    x + offset*9 + clip, y + offset*6 + clip,
                    x + offset*10 - clip, y + offset*6 + clip,
                    x + offset*10 - clip, y + offset*7 - clip,
                    x + offset*9 + clip, y + offset*7 - clip,
                    // RIGHT QUAD
                    x + offset*9 + clip, y + offset*6 + clip,
                    x + offset*10 - clip, y + offset*6 + clip,
                    x + offset*10 - clip, y + offset*7 - clip,
                    x + offset*9 + clip, y + offset*7 - clip
                };
                
            // Lava
            case 12:
                return new float[] {
                    // BOTTOM QUAD (DOWN=+Y)
                    x + offset*10 + clip, y + offset*10 + clip,
                    x + offset*11 - clip, y + offset*10 + clip,
                    x + offset*11 - clip, y + offset*11 - clip,
                    x + offset*10 + clip, y + offset*11 - clip,
                    // TOP
                    x + offset*10 + clip, y + offset*10 + clip,
                    x + offset*11 - clip, y + offset*10 + clip,
                    x + offset*11 - clip, y + offset*11 - clip,
                    x + offset*10 + clip, y + offset*11 - clip,
                    // FRONT QUAD
                    x + offset*10 + clip, y + offset*10 + clip,
                    x + offset*11 - clip, y + offset*10 + clip,
                    x + offset*11 - clip, y + offset*11 - clip,
                    x + offset*10 + clip, y + offset*11 - clip,
                    // BACK QUAD
                    x + offset*11 - clip, y + offset*11 - clip,
                    x + offset*10 + clip, y + offset*11 - clip,
                    x + offset*10 + clip, y + offset*10 + clip,
                    x + offset*11 - clip, y + offset*10 + clip,
                    // LEFT QUAD
                    x + offset*10 + clip, y + offset*10 + clip,
                    x + offset*11 - clip, y + offset*10 + clip,
                    x + offset*11 - clip, y + offset*11 - clip,
                    x + offset*10 + clip, y + offset*11 - clip,
                    // RIGHT QUAD
                    x + offset*10 + clip, y + offset*10 + clip,
                    x + offset*11 - clip, y + offset*10 + clip,
                    x + offset*11 - clip, y + offset*11 - clip,
                    x + offset*10 + clip, y + offset*11 - clip
                };
                
            // NetherQuartz
            case 13:
                return new float[] {
                    // BOTTOM QUAD (DOWN=+Y)
                    x + offset*7 + clip, y + offset*5 + clip,
                    x + offset*8 - clip, y + offset*5 + clip,
                    x + offset*8 - clip, y + offset*6 - clip,
                    x + offset*7 + clip, y + offset*6 - clip,
                    // TOP
                    x + offset*7 + clip, y + offset*5 + clip,
                    x + offset*8 - clip, y + offset*5 + clip,
                    x + offset*8 - clip, y + offset*6 - clip,
                    x + offset*7 + clip, y + offset*6 - clip,
                    // FRONT QUAD
                    x + offset*7 + clip, y + offset*5 + clip,
                    x + offset*8 - clip, y + offset*5 + clip,
                    x + offset*8 - clip, y + offset*6 - clip,
                    x + offset*7 + clip, y + offset*6 - clip,
                    // BACK QUAD
                    x + offset*8 - clip, y + offset*6 - clip,
                    x + offset*7 + clip, y + offset*6 - clip,
                    x + offset*7 + clip, y + offset*5 + clip,
                    x + offset*8 - clip, y + offset*5 + clip,
                    // LEFT QUAD
                    x + offset*7 + clip, y + offset*5 + clip,
                    x + offset*8 - clip, y + offset*5 + clip,
                    x + offset*8 - clip, y + offset*6 - clip,
                    x + offset*7 + clip, y + offset*6 - clip,
                    // RIGHT QUAD
                    x + offset*7 + clip, y + offset*5 + clip,
                    x + offset*8 - clip, y + offset*5 + clip,
                    x + offset*8 - clip, y + offset*6 - clip,
                    x + offset*7 + clip, y + offset*6 - clip
                };
                
            // Obsidian
            case 14:
                return new float[] {
                    // BOTTOM QUAD (DOWN=+Y)
                    x + offset*6 - clip, y + offset*3 - clip,
                    x + offset*5 + clip, y + offset*3 - clip,
                    x + offset*5 + clip, y + offset*2 + clip,
                    x + offset*6 - clip, y + offset*2 + clip,
                    // TOP
                    x + offset*6 - clip, y + offset*3 - clip,
                    x + offset*5 + clip, y + offset*3 - clip,
                    x + offset*5 + clip, y + offset*2 + clip,
                    x + offset*6 - clip, y + offset*2 + clip,
                    // FRONT QUAD
                    x + offset*5 + clip, y + offset*2 + clip,
                    x + offset*6 - clip, y + offset*2 + clip,
                    x + offset*6 - clip, y + offset*3 - clip,
                    x + offset*5 + clip, y + offset*3 - clip,
                    // BACK QUAD
                    x + offset*6 - clip, y + offset*3 - clip,
                    x + offset*5 + clip, y + offset*3 - clip,
                    x + offset*5 + clip, y + offset*2 + clip,
                    x + offset*6 - clip, y + offset*2 + clip,
                    // LEFT QUAD
                    x + offset*5 + clip, y + offset*2 + clip,
                    x + offset*6 - clip, y + offset*2 + clip,
                    x + offset*6 - clip, y + offset*3 - clip,
                    x + offset*5 + clip, y + offset*3 - clip,
                    // RIGHT QUAD
                    x + offset*5 + clip, y + offset*2 + clip,
                    x + offset*6 - clip, y + offset*2 + clip,
                    x + offset*6 - clip, y + offset*3 - clip,
                    x + offset*5 + clip, y + offset*3 - clip
                };
                
            // Portal
            case 15:
                return new float[] {
                    // BOTTOM QUAD (DOWN=+Y)
                    x + offset*7 - clip, y + offset*3 - clip,
                    x + offset*6 + clip, y + offset*3 - clip,
                    x + offset*6 + clip, y + offset*2 + clip,
                    x + offset*7 - clip, y + offset*2 + clip,
                    // TOP
                    x + offset*7 - clip, y + offset*3 - clip,
                    x + offset*6 + clip, y + offset*3 - clip,
                    x + offset*6 + clip, y + offset*2 + clip,
                    x + offset*7 - clip, y + offset*2 + clip,
                    // FRONT QUAD
                    x + offset*6 + clip, y + offset*2 + clip,
                    x + offset*7 - clip, y + offset*2 + clip,
                    x + offset*7 - clip, y + offset*3 - clip,
                    x + offset*6 + clip, y + offset*3 - clip,
                    // BACK QUAD
                    x + offset*7 - clip, y + offset*3 - clip,
                    x + offset*6 + clip, y + offset*3 - clip,
                    x + offset*6 + clip, y + offset*2 + clip,
                    x + offset*7 - clip, y + offset*2 + clip,
                    // LEFT QUAD
                    x + offset*6 + clip, y + offset*2 + clip,
                    x + offset*7 - clip, y + offset*2 + clip,
                    x + offset*7 - clip, y + offset*3 - clip,
                    x + offset*6 + clip, y + offset*3 - clip,
                    // RIGHT QUAD
                    x + offset*6 + clip, y + offset*2 + clip,
                    x + offset*7 - clip, y + offset*2 + clip,
                    x + offset*7 - clip, y + offset*3 - clip,
                    x + offset*6 + clip, y + offset*3 - clip
                };
                
            default:
                return new float[] {
                    // BOTTOM QUAD (DOWN=+Y)
                    x + offset*1 - clip, y + offset*1 - clip,
                    x + offset*0 + clip, y + offset*1 - clip,
                    x + offset*0 + clip, y + offset*0 + clip,
                    x + offset*1 - clip, y + offset*0 + clip,
                    // TOP
                    x + offset*1 - clip, y + offset*1 - clip,
                    x + offset*0 + clip, y + offset*1 - clip,
                    x + offset*0 + clip, y + offset*0 + clip,
                    x + offset*1 - clip, y + offset*0 + clip,
                    // FRONT QUAD
                    x + offset*0 + clip, y + offset*0 + clip,
                    x + offset*1 - clip, y + offset*0 + clip,
                    x + offset*1 - clip, y + offset*1 - clip,
                    x + offset*0 + clip, y + offset*1 - clip,
                    // BACK QUAD
                    x + offset*1 - clip, y + offset*1 - clip,
                    x + offset*0 + clip, y + offset*1 - clip,
                    x + offset*0 + clip, y + offset*0 + clip,
                    x + offset*1 - clip, y + offset*0 + clip,
                    // LEFT QUAD
                    x + offset*0 + clip, y + offset*0 + clip,
                    x + offset*1 - clip, y + offset*0 + clip,
                    x + offset*1 - clip, y + offset*1 - clip,
                    x + offset*0 + clip, y + offset*1 - clip,
                    // RIGHT QUAD
                    x + offset*0 + clip, y + offset*0 + clip,
                    x + offset*1 - clip, y + offset*0 + clip,
                    x + offset*1 - clip, y + offset*1 - clip,
                    x + offset*0 + clip, y + offset*1 - clip
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