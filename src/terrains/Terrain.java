package terrains;

//@author VividerAphid

import Models.RawModel;
import RenderEngine.Loader;
import Textures.TerrainTexture;
import Textures.TerrainTexturePack;
import ToolBox.Maths;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;


public class Terrain {
    private static final float SIZE = 800;
    
    private static final int VERTEX_COUNT = 120;
    
    private static final float MAX_HEIGHT = 50;
    private static final float MIN_HEIGHT = -20;
    
    private static final float MAX_PIXEL_COLOUR = 256 * 256 * 256;
    
    private float[][] heights;
    
    private float x;
    private float z;
    private RawModel model;
    private TerrainTexture blendMap;
    private TerrainTexturePack texturePack;
    
    public Terrain(int gridX, int gridZ, Loader loader, TerrainTexturePack texturePack, TerrainTexture blendMap){
        this.blendMap = blendMap;
        this.texturePack = texturePack;
        this.x = gridX * SIZE;
        this.z = gridZ * SIZE;
        this.model = generateAphidTerrain(loader);
    }
    
    public Terrain(int gridX, int gridZ, Loader loader, TerrainTexturePack texturePack, TerrainTexture blendMap, String heightMap){
        this.blendMap = blendMap;
        this.texturePack = texturePack;
        this.x = gridX * SIZE;
        this.z = gridZ * SIZE;
        this.model = generateHeightMapTerrain(loader, heightMap);
    }
    
    public Terrain(int gridX, int gridZ, Loader loader, TerrainTexturePack texturePack, TerrainTexture blendMap, float[][] noise){
        this.blendMap = blendMap;
        this.texturePack = texturePack;
        this.x = gridX * SIZE;
        this.z = gridZ * SIZE;
        this.model = generateNoiseTerrain(loader, noise);
    }
    
    private RawModel generateFlatTerrain(Loader loader){
		int count = VERTEX_COUNT * VERTEX_COUNT;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count*2];
		int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
		int vertexPointer = 0;
		for(int i=0;i<VERTEX_COUNT;i++){
			for(int j=0;j<VERTEX_COUNT;j++){
				vertices[vertexPointer*3] = (float)j/((float)VERTEX_COUNT - 1) * SIZE;
				vertices[vertexPointer*3+1] = 0;
				vertices[vertexPointer*3+2] = (float)i/((float)VERTEX_COUNT - 1) * SIZE;
				normals[vertexPointer*3] = 0;
				normals[vertexPointer*3+1] = 1;
				normals[vertexPointer*3+2] = 0;
				textureCoords[vertexPointer*2] = (float)j/((float)VERTEX_COUNT - 1);
				textureCoords[vertexPointer*2+1] = (float)i/((float)VERTEX_COUNT - 1);
				vertexPointer++;
			}
		}
		int pointer = 0;
		for(int gz=0;gz<VERTEX_COUNT-1;gz++){
			for(int gx=0;gx<VERTEX_COUNT-1;gx++){
				int topLeft = (gz*VERTEX_COUNT)+gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		return loader.loadToVAO(vertices, textureCoords, normals, indices);
	}
    
    private RawModel generateHeightMapTerrain(Loader loader, String heightMap){
        boolean withTrees = false;
        float treeChance = 0.98f;
        float treeHeight = 0;
            BufferedImage image = null;
        try {
            image = ImageIO.read(new File("res/" + heightMap + ".png"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        int vertexCount = image.getHeight();
        System.out.println(vertexCount);
        System.out.println(image.getRGB(40, 40));
        
            int count = vertexCount * vertexCount;
            float[] vertices = new float[count * 3];
            float[] normals = new float[count * 3];
            float[] textureCoords = new float[count*2];
            int[] indices = new int[6*(vertexCount-1)*(vertexCount-1)];
            int vertexPointer = 0;
            for(int i=0;i<vertexCount;i++){
                    for(int j=0;j<vertexCount;j++){
                            vertices[vertexPointer*3] = (float)j/((float)vertexCount - 1) * SIZE;
                            if(withTrees){if(Math.random() > treeChance){treeHeight = 10;}else{treeHeight=0;}}
                            vertices[vertexPointer*3+1] = getHeightFromHeightMap(j, i, image) + treeHeight;
                            vertices[vertexPointer*3+2] = (float)i/((float)vertexCount - 1) * SIZE;
                            Vector3f normal = calculateNormalFromHeightMap(j, i, image);
                            normals[vertexPointer*3] = normal.x;
                            normals[vertexPointer*3+1] = normal.y;
                            normals[vertexPointer*3+2] = normal.z;
                            textureCoords[vertexPointer*2] = (float)j/((float)vertexCount - 1);
                            textureCoords[vertexPointer*2+1] = (float)i/((float)vertexCount - 1);
                            vertexPointer++;
                    }
            }
            int pointer = 0;
            for(int gz=0;gz<vertexCount-1;gz++){
                    for(int gx=0;gx<vertexCount-1;gx++){
                            int topLeft = (gz*vertexCount)+gx;
                            int topRight = topLeft + 1;
                            int bottomLeft = ((gz+1)*vertexCount)+gx;
                            int bottomRight = bottomLeft + 1;
                            indices[pointer++] = topLeft;
                            indices[pointer++] = bottomLeft;
                            indices[pointer++] = topRight;
                            indices[pointer++] = topRight;
                            indices[pointer++] = bottomLeft;
                            indices[pointer++] = bottomRight;
                    }
            }
            return loader.loadToVAO(vertices, textureCoords, normals, indices);
    }
    
    private float getHeightFromHeightMap(int x, int z, BufferedImage image){
        if(x < 0 || x >= image.getHeight() ||  z < 0 || z >=image.getHeight()){
            return 0;
        }
        float height = image.getRGB(x,z);
        height += MAX_PIXEL_COLOUR / 2f;
        height /= MAX_PIXEL_COLOUR / 2f;
        height *= MAX_HEIGHT;
        return height;
    }
    
    private Vector3f calculateNormalFromHeightMap(int x, int z, BufferedImage image){
        float heightL = getHeightFromHeightMap(x-1, z, image);
        float heightR = getHeightFromHeightMap(x+1, z, image);
        float heightD = getHeightFromHeightMap(x, z-1, image);
        float heightU = getHeightFromHeightMap(x, z+1, image);
        Vector3f normal = new Vector3f(heightL-heightR, 2f, heightD-heightU);
        normal.normalise();
        return normal;
    }
    
    private float getHeightFromNoise(int x, int z, float[][] noise, int scaleY){
        if(x < 0 || x >= noise.length  ||  z < 0 || z >=noise.length){
            return 0;
        }
        Color color = new Color(noise[x][z], noise[x][z], noise[x][z]);
        float height = color.getRGB() ;
        height += MAX_PIXEL_COLOUR / 2f;
        height /= MAX_PIXEL_COLOUR / 2f;
        height *= scaleY;
        if(height > MAX_HEIGHT){
            height = MAX_HEIGHT;
        }
        else if(height < MIN_HEIGHT){
            height = MIN_HEIGHT;
        }
        return height;
    }
    
    private Vector3f calculateNormalFromNoise(int x, int z, float[][] noise, int scaleY){
        float heightL = getHeightFromNoise(x-1, z, noise, scaleY);
        float heightR = getHeightFromNoise(x+1, z, noise, scaleY);
        float heightD = getHeightFromNoise(x, z-1, noise, scaleY);
        float heightU = getHeightFromNoise(x, z+1, noise, scaleY);
        Vector3f normal = new Vector3f(heightL-heightR, 2f, heightD-heightU);
        normal.normalise();
        return normal;
    }
    
    private RawModel generateNoiseTerrain(Loader loader, float[][] noise){
        boolean withTrees = false;
        float treeChance = 0.98f;
        float treeHeight = 0;
        int scaleY = 100;
        
        int vertexCount = noise.length ;
        //System.out.println(vertexCount);
        //System.out.println(getHeightFromNoise(0,0, noise));
        
            int count = vertexCount * vertexCount;
            heights = new float[vertexCount][vertexCount];
            float[] vertices = new float[count * 3];
            float[] normals = new float[count * 3];
            float[] textureCoords = new float[count*2];
            int[] indices = new int[6*(vertexCount-1)*(vertexCount-1)];
            int vertexPointer = 0;
            for(int i=0;i<vertexCount;i++){
                    for(int j=0;j<vertexCount;j++){
                            vertices[vertexPointer*3] = (float)j/((float)vertexCount - 1) * SIZE;
                            if(withTrees){if(Math.random() > treeChance){treeHeight = 10;}else{treeHeight=0;}}
                            float height = getHeightFromNoise(j, i, noise, scaleY) + treeHeight;
                            heights[j][i] = height;
                            vertices[vertexPointer*3+1] = height;
                            vertices[vertexPointer*3+2] = (float)i/((float)vertexCount - 1) * SIZE;
                            Vector3f normal = calculateNormalFromNoise(j, i, noise, scaleY);
                            normals[vertexPointer*3] = normal.x;
                            normals[vertexPointer*3+1] = normal.y;
                            normals[vertexPointer*3+2] = normal.z;
                            textureCoords[vertexPointer*2] = (float)j/((float)vertexCount - 1);
                            textureCoords[vertexPointer*2+1] = (float)i/((float)vertexCount - 1);
                            vertexPointer++;
                    }
            }
            int pointer = 0;
            for(int gz=0;gz<vertexCount-1;gz++){
                    for(int gx=0;gx<vertexCount-1;gx++){
                            int topLeft = (gz*vertexCount)+gx;
                            int topRight = topLeft + 1;
                            int bottomLeft = ((gz+1)*vertexCount)+gx;
                            int bottomRight = bottomLeft + 1;
                            indices[pointer++] = topLeft;
                            indices[pointer++] = bottomLeft;
                            indices[pointer++] = topRight;
                            indices[pointer++] = topRight;
                            indices[pointer++] = bottomLeft;
                            indices[pointer++] = bottomRight;
                    }
            }
            return loader.loadToVAO(vertices, textureCoords, normals, indices);
    }
    
    private RawModel generateAphidTerrain(Loader loader){
		int count = VERTEX_COUNT * VERTEX_COUNT;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count*2];
		int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
		int vertexPointer = 0;
		for(int i=0;i<VERTEX_COUNT;i++){
			for(int j=0;j<VERTEX_COUNT;j++){
				vertices[vertexPointer*3] = (float)j/((float)VERTEX_COUNT - 1) * SIZE;
				vertices[vertexPointer*3+1] = (float)(Math.random() * 10);
				vertices[vertexPointer*3+2] = (float)i/((float)VERTEX_COUNT - 1) * SIZE;
				normals[vertexPointer*3] = 0;
				normals[vertexPointer*3+1] = 1;
				normals[vertexPointer*3+2] = 0;
				textureCoords[vertexPointer*2] = (float)j/((float)VERTEX_COUNT - 1);
				textureCoords[vertexPointer*2+1] = (float)i/((float)VERTEX_COUNT - 1);
				vertexPointer++;
			}
		}
		int pointer = 0;
		for(int gz=0;gz<VERTEX_COUNT-1;gz++){
			for(int gx=0;gx<VERTEX_COUNT-1;gx++){
				int topLeft = (gz*VERTEX_COUNT)+gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		return loader.loadToVAO(vertices, textureCoords, normals, indices);
	}
    
    public float getHeightOfTerrain(float worldX, float worldZ){
        float terrainX = worldX - this.x;
        float terrainZ = worldZ - this.z;
        float gridSquareSize = SIZE / ((float)heights.length - 1);
        int gridX = (int) Math.floor(terrainX / gridSquareSize);
        int gridZ = (int) Math.floor(terrainZ / gridSquareSize);
        if(gridX >= heights.length - 1 || gridZ >= heights.length - 1 || gridX < 0 || gridZ < 0){
            return 0;
        }
        float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
        float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;
        float answer;
        if (xCoord <= (1-zCoord)) {
            answer = Maths.barryCentric(new Vector3f(0, heights[gridX][gridZ], 0), new Vector3f(1,heights[gridX + 1][gridZ], 0), new Vector3f(0,heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
        }
        else{
            answer = Maths.barryCentric(new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(1,heights[gridX + 1][gridZ + 1], 1), new Vector3f(0,heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
        }
        return answer;
    }
    public float getX() {
        return x;
    }

    public float getZ() {
        return z;
    }

    public RawModel getModel() {
        return model;
    }
    
    public TerrainTexture getBlendMap() {
        return blendMap;
    }

    public TerrainTexturePack getTexturePack() {
        return texturePack;
    }
    
}
