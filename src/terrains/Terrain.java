package terrains;

//@author VividerAphid

import Models.RawModel;
import RenderEngine.Loader;
import Textures.TerrainTexture;
import Textures.TerrainTexturePack;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.lwjgl.util.vector.Vector3f;


public class Terrain {
    private static final float SIZE = 800;
    
    private static final int VERTEX_COUNT = 120;
    
    private static final float MAX_HEIGHT = 40;
    private static final float MAX_PIXEL_COLOUR = 256 * 256 * 256;
    
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

            BufferedImage image = null;
        try {
            image = ImageIO.read(new File("res/" + heightMap + ".png"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        int vertexCount = image.getHeight();
        
            int count = vertexCount * vertexCount;
            float[] vertices = new float[count * 3];
            float[] normals = new float[count * 3];
            float[] textureCoords = new float[count*2];
            int[] indices = new int[6*(vertexCount-1)*(vertexCount-1)];
            int vertexPointer = 0;
            for(int i=0;i<vertexCount;i++){
                    for(int j=0;j<vertexCount;j++){
                            vertices[vertexPointer*3] = (float)j/((float)vertexCount - 1) * SIZE;
                            vertices[vertexPointer*3+1] = getHeight(j, i, image);
                            vertices[vertexPointer*3+2] = (float)i/((float)vertexCount - 1) * SIZE;
                            Vector3f normal = calculateNormal(j, i, image);
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
    
    private float getHeight(int x, int z, BufferedImage image){
        if(x < 0 || x >= image.getHeight() ||  z < 0 || z >=image.getHeight()){
            return 0;
        }
        float height = image.getRGB(x,z);
        height += MAX_PIXEL_COLOUR / 2f;
        height /= MAX_PIXEL_COLOUR / 2f;
        height *= MAX_HEIGHT;
        return height;
    }
    
    private Vector3f calculateNormal(int x, int z, BufferedImage image){
        float heightL = getHeight(x-1, z, image);
        float heightR = getHeight(x+1, z, image);
        float heightD = getHeight(x, z-1, image);
        float heightU = getHeight(x, z+1, image);
        Vector3f normal = new Vector3f(heightL-heightR, 2f, heightD-heightU);
        normal.normalise();
        return normal;
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
