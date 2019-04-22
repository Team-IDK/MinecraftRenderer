/***************************************************************
* file: Block.java
* author: Team NULL
* class: CS 4450 - Computer Graphics
*
* assignment: final program
* date last modified: 4/22/2019
*
* purpose: Data structure to store basic information for each block,
* specifically the block's Type, active state, and coordinates.
* 
****************************************************************/

package minecraftrenderer;

public class Block {
    
    private boolean IsActive;
    private BlockType Type;
    public float x,y,z;
    
    // enum: BlockType
    // purpose: this method constructs the block with an intial type
    public enum BlockType {
        
        BlockType_Grass(0),
        BlockType_Sand(1),
        BlockType_Water(2),
        BlockType_Dirt(3),
        BlockType_Stone(4),
        BlockType_Bedrock(5),
        BlockType_Log(6),
        BlockType_Leaf(7),
        BlockType_Cactus(8),
        BlockType_Netherrack(9),
        BlockType_SoulSand(10),
        BlockType_Glowstone(11),
        BlockType_Lava(12),
        BlockType_NetherQuartz(13),
        BlockType_Obsidian(14),
        BlockType_Portal(15);
        
        private int BlockID;
        
        // method: BlockType
        // purpose: this constructor sets the block ID
        BlockType(int i) {
            BlockID = i;
        }
        
        // method: getID
        // purpose: this method gets the block ID
        public int getID() {
            return BlockID;
        }
        
        // method: setID
        // purpose: this method sets the block ID
        public void setID(int i) {
            BlockID = i;
        }
    }
    
    // method: Block
    // purpose: this constructor sets the block with an intial type
    public Block(BlockType t) {
        Type = t;
    }
    
    // method: setCoords
    // purpose: this method sets the block's coordinates
    public void setCoords(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    // method: setActive
    // purpose: this method sets the block's active state
    public void setActive(boolean a) {
        IsActive = a;
    }
    
    // method: isActive
    // purpose: this method checks the block's active state
    public boolean isActive() {
        return IsActive;
    }
    
    // method: getID
    // purpose: this method returns the block's Type ID
    public int getID() {
        return Type.getID();
    }
    
    // method: setID
    // purpose: this method sets the block ID
    public void setID(BlockType t) {
        Type = t;
    }
}