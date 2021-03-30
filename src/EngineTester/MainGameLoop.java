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
import RenderEngine.OBJLoader;
import RenderEngine.EntityRenderer;
import Shaders.StaticShader;
import Textures.ModelTexture;
import java.util.HashSet;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import terrains.Terrain;


public class MainGameLoop {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        DisplayManager.createDisplay();

        Loader loader = new Loader();
        StaticShader shader = new StaticShader();
        
        TexturedModel staticModel = createModel("firstTree", "huh", 10, 0);

        
        Light light = new Light(new Vector3f(0,0,-20), new Vector3f(1,1,1));
        
        
        Entity[] entities = new Entity[25];
        Terrain[] terrains = new Terrain[4];
        
        terrains[0] = new Terrain(0,0, loader, new ModelTexture(loader.loadTexture("genericGround")));
        terrains[1] = new Terrain(0,-1, loader, new ModelTexture(loader.loadTexture("genericGround")));
        terrains[2] = new Terrain(-1,-1, loader, new ModelTexture(loader.loadTexture("genericGround")));
        terrains[3] = new Terrain(-1,0, loader, new ModelTexture(loader.loadTexture("genericGround")));

        int entityCount = 100;
        int range = 150;
        entities = fillEntities(entityCount, range, staticModel);
        //entities[0] = new Entity(staticModel, new Vector3f(0,0,-25),0,0,0,1);
        
        Camera camera = new Camera(new Vector3f(0, 1, range));
        camera.setSpeed(0.08f);
        

        MasterRenderer renderer = new MasterRenderer();
        while(!Display.isCloseRequested()){
            //entity.increasePosition(0, 0, 0);
            //entities[0].increaseRotation(0, 1, 0);
            camera.move();
            renderer.processEntity(entities[0]);
            for(int r = 0; r < entities.length; r++){
                System.out.println(r);
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
    
    public static Entity[] fillEntities(int count, int range, TexturedModel model){
        
        Entity[] entities = new Entity[count];
        for(int r = 0; r < count; r++){
            int x = (int)(Math.random() * (range * 2));
            int z = (int)(Math.random() * (range * 2));
            System.out.println(x);
            entities[r] = new Entity(model, new Vector3f((x - range), 0, (z - range)), 0, 0, 0, 1);
        }
        return entities;
    }
    
    public static TexturedModel createModel(String modelName, String textureName, float damper, float reflectivity){
        Loader loader = new Loader();
        RawModel raw = OBJLoader.loadObjModel(modelName, loader);
        ModelTexture texture = new ModelTexture(loader.loadTexture(textureName));
        texture.setReflectivity(reflectivity);
        texture.setShineDamper(damper);
        
        return new TexturedModel(raw, texture);
        
        
        
    }

}
