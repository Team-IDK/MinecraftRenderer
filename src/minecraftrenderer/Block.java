package minecraftrenderer;

public class Block {
    private boolean IsActive;
    private BlockType Type;
    private float x,y,z;
    
    /**
     * Enum to hold block type and ID
     */
    public enum BlockType {
        BlockType_Grass(0),
        BlockType_Sand(1),
        BlockType_Water(2),
        BlockType_Dirt(3),
        BlockType_Stone(4),
        BlockType_Bedrock(5);
        
        private int BlockID;
        /**
         * Constructor sets Block ID
         * @param i ID of Block
         */
        BlockType(int i) {
            BlockID = i;
        }
        /**
         * @return Block ID
         */
        public int GetID() {
            return BlockID;
        }
        /**
         * @param i new ID of Block
         */
        public void SetID(int i) {
            BlockID = i;
        }
    }
    /**
     * Initialize new Block
     * @param t type of Block
     */
    public Block(BlockType t) {
        Type = t;
    }
    /**
     * Block coordinates
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     */
    public void SetCoords(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    /**
     * @param a Set if Block is active or not
     */
    public void SetActive(boolean a) {
        IsActive = a;
    }
    /**
     * Returns active state of Block for rendering purposes
     * @return True if Block is active, false if not
     */
    public boolean IsActive() {
        return IsActive;
    }
    /**
     * ID of Block
     * @return Block ID
     */
    public int GetID() {
        return Type.GetID();
    }
}
