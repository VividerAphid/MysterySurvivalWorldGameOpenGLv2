package EngineTester;

//@author VividerAphid

import Entities.StaticCamera;
import Entities.Entity;
import Entities.Light;
import Entities.MovableCamera;
import Entities.Player;
import Entities.ThirdPersonCamera;
import Models.HuedModel;
import RenderEngine.DisplayManager;
import RenderEngine.Loader;
import Models.RawModel;
import Models.TexturedModel;
import RenderEngine.MasterRenderer;
import Shaders.HuedShader;
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
        
        String[] modelNames = {"firstTree", "basicStone", "grassModel", "firstCritter"};
        String[] modelTextures = {"huh", "genericStone", "basicGrass", "huh"};
        String[] plainGraySet = {"plainGray", "plainGray", "basicGrass-gray", "plainGray"};
        
        TexturedModel[] modelSet = createTexturedModelSet(modelNames, modelTextures);
        
        HuedModel[] hueTest = createHuedModelSet(modelNames, plainGraySet);
        
        Light light = new Light(new Vector3f(0,300,-10), new Vector3f(1,1,1));
        
        
        Entity[] entities;
        Terrain[] terrains = new Terrain[4];
        
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("genericGround"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("genericSand"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("genericStone"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("huh"));
        
        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMapTest"));
        
        terrains[0] = new Terrain(0,0, loader, texturePack, blendMap, "heightmap");
        terrains[1] = new Terrain(0,-1, loader, texturePack, blendMap, "heightmap");
        terrains[2] = new Terrain(-1,-1, loader, texturePack, blendMap, "heightmap");
        terrains[3] = new Terrain(-1,0, loader, texturePack, blendMap, "heightmap");

        int entityCount = 500;
        int range = 150;
        entities = fillTexturedEntities(entityCount, range, hueTest);
        
        MovableCamera camera = new MovableCamera(new Vector3f(0, 1, range));
        camera.setSpeed(10f);
        //StaticCamera camera = new StaticCamera(new Vector3f(0, 15, range), 20, 0, 0);

        //Player player = new Player(modelSet[3], new Vector3f(0,0,range-20), 0, 0, 0, 1);
        //ThirdPersonCamera camera = new ThirdPersonCamera(player);
        //entities[0] = player;
        HuedShader shader = new HuedShader();
        MasterRenderer renderer = new MasterRenderer(shader);
        while(!Display.isCloseRequested()){
            //player.move();
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
    
    public static TexturedModel[] createTexturedModelSet(String[] modelNames, String[] modelTextures){
        TexturedModel[] modelSet = new TexturedModel[modelNames.length];
        
        for(int r = 0; r < modelSet.length; r++){
            modelSet[r] = createTexturedModel(modelNames[r], modelTextures[r], 10, 0);
        }
        
        return modelSet;
    }
    
    public static HuedModel[] createHuedModelSet(String[] modelNames, String[] modelTextures){
        HuedModel[] modelSet = new HuedModel[modelNames.length];
        
        for(int r = 0; r < modelSet.length; r++){
            modelSet[r] = createHuedModel(modelNames[r], modelTextures[r], 10, 0, new Vector3f((float)Math.random(), (float)Math.random(), (float)Math.random()));
        }
        
        return modelSet;
    }
    
    public static Entity[] fillTexturedEntities(int count, int range, TexturedModel[] modelSet){
        
        Entity[] entities = new Entity[count];
        for(int r = 0; r < count; r++){
            int x = (int)(Math.random() * (range * 2));
            int z = (int)(Math.random() * (range * 2));
            int pick = (int)(Math.random()*3);
            TexturedModel model = modelSet[pick];
            entities[r] = new Entity(model, new Vector3f((x - range), 0, (z - range)), 0, 0, 0, 1);
        }
        return entities;
    }
    
    public static Entity[] fillHuedEntities(int count, int range, HuedModel[] modelSet){
        Entity[] entities = new Entity[count];
        for(int r = 0; r < count; r++){
            int x = (int)(Math.random() * (range * 2));
            int z = (int)(Math.random() * (range * 2));
            int pick = (int)(Math.random()*3);
            HuedModel model = modelSet[pick];
            entities[r] = new Entity(model, new Vector3f((x - range), 0, (z - range)), 0, 0, 0, 1);
        }
        return entities;
    }
    
    public static TexturedModel createTexturedModel(String modelName, String textureName, float damper, float reflectivity){
        Loader loader = new Loader();
        ModelData data = OBJFileLoader.loadOBJ(modelName);
        RawModel raw = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
        ModelTexture texture = new ModelTexture(loader.loadTexture(textureName));
        texture.setReflectivity(reflectivity);
        texture.setShineDamper(damper);
        
        return new TexturedModel(raw, texture);            
    }
    
    public static HuedModel createHuedModel(String modelName, String textureName, float damper, float reflectivity, Vector3f hue){
        Loader loader = new Loader();
        ModelData data = OBJFileLoader.loadOBJ(modelName);
        RawModel raw = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
        ModelTexture texture = new ModelTexture(loader.loadTexture(textureName));
        texture.setReflectivity(reflectivity);
        texture.setShineDamper(damper);
        
        return new HuedModel(raw, texture, hue);      
    }

}
