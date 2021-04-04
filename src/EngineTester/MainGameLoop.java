package EngineTester;

//@author VividerAphid

import Entities.Camera;
import Entities.Entity;
import Entities.Light;
import RenderEngine.DisplayManager;
import RenderEngine.Loader;
import Models.RawModel;
import Models.TexturedModel;
import RenderEngine.MasterRenderer;
import Shaders.StaticShader;
import Textures.ModelTexture;
import Textures.TerrainTexture;
import Textures.TerrainTexturePack;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import terrains.Terrain;


public class MainGameLoop {

    public static void main(String[] args) {

        DisplayManager.createDisplay();

        Loader loader = new Loader();
        StaticShader shader = new StaticShader();
        
        
        String[] modelNames = {"firstTree", "basicStone", "grassModel", "firstCritter"};
        String[] modelTextures = {"huh", "genericStone", "basicGrass", "huh"};
        
        TexturedModel[] modelSet = createModelSet(modelNames, modelTextures);
        System.out.println(modelSet.length);
        
        TexturedModel grassTest = createModel("grassModel", "basicGrass", 10, 0);
        grassTest.getTexture().setHasTransparency(true);
        grassTest.getTexture().setUseFakeLighting(true);

        
        Light light = new Light(new Vector3f(0,0,-10), new Vector3f(1,1,1));
        
        
        Entity[] entities;
        Terrain[] terrains = new Terrain[4];
        
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("genericGround"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("genericSand"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("genericStone"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("huh"));
        
        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMapTest"));
        
        terrains[0] = new Terrain(0,0, loader, texturePack, blendMap);
        terrains[1] = new Terrain(0,-1, loader, texturePack, blendMap);
        terrains[2] = new Terrain(-1,-1, loader, texturePack, blendMap);
        terrains[3] = new Terrain(-1,0, loader, texturePack, blendMap);

        int entityCount = 250;
        int range = 150;
        entities = fillEntities(entityCount, range, modelSet);
        
        Camera camera = new Camera(new Vector3f(0, 1, range));
        camera.setSpeed(0.08f);
        

        entities[0] = new Entity(grassTest, new Vector3f(0,0,0), 0, 0, 0, 1);
        entities[1] = new Entity(grassTest, new Vector3f(0,0,-20), 0, 0, 0, 1);
        
        MasterRenderer renderer = new MasterRenderer();
        while(!Display.isCloseRequested()){
            //entity.increasePosition(0, 0, 0);
            //entities[0].increaseRotation(0, 1, 0);
            camera.move();
            renderer.processEntity(entities[0]);
            for(int r = 0; r < entities.length; r++){
                renderer.processEntity(entities[r]);
            }
            for(int r = 0; r < terrains.length; r++){
                renderer.processTerrain(terrains[r]);
            }
            renderer.render(light, camera);
            DisplayManager.updateDisplay();
        }
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }
    
    public static TexturedModel[] createModelSet(String[] modelNames, String[] modelTextures){
        TexturedModel[] modelSet = new TexturedModel[modelNames.length];
        
        for(int r = 0; r < modelSet.length; r++){
            modelSet[r] = createModel(modelNames[r], modelTextures[r], 10, 0);
            System.out.println(modelSet[r]);
        }
        
        return modelSet;
    }
    
    public static Entity[] fillEntities(int count, int range, TexturedModel[] modelSet){
        
        Entity[] entities = new Entity[count];
        for(int r = 0; r < count; r++){
            int x = (int)(Math.random() * (range * 2));
            int z = (int)(Math.random() * (range * 2));
            int pick = (int)(Math.random()*3);
            System.out.println(pick);
            TexturedModel model = modelSet[pick];
            entities[r] = new Entity(model, new Vector3f((x - range), 0, (z - range)), 0, 0, 0, 1);
        }
        return entities;
    }
    
    public static TexturedModel createModel(String modelName, String textureName, float damper, float reflectivity){
        Loader loader = new Loader();
        ModelData data = OBJFileLoader.loadOBJ(modelName);
        RawModel raw = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
        //RawModel raw = OBJLoader.loadObjModel(modelName, loader);
        ModelTexture texture = new ModelTexture(loader.loadTexture(textureName));
        texture.setReflectivity(reflectivity);
        texture.setShineDamper(damper);
        
        return new TexturedModel(raw, texture);
        
        
        
    }

}
