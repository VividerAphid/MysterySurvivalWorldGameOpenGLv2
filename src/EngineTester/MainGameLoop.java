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
        
        RawModel model = OBJLoader.loadObjModel("basicStone", loader);


        ModelTexture textureBase = new ModelTexture(loader.loadTexture("huh"));
        TexturedModel staticModel = new TexturedModel(model, textureBase);
        ModelTexture texture = staticModel.getTexture();
        texture.setShineDamper(10);
        texture.setReflectivity(0);

        
        Light light = new Light(new Vector3f(0,0,-20), new Vector3f(1,1,1));
        
        
        Entity[] entities = new Entity[3];
        Terrain[] terrains = new Terrain[2];
        
        terrains[0] = new Terrain(0,-1, loader, new ModelTexture(loader.loadTexture("huh")));
        terrains[1] = new Terrain(-1,-1, loader, new ModelTexture(loader.loadTexture("huh")));

        entities[0] = new Entity(staticModel, new Vector3f(0,0,-25),0,0,0,1);
        
        Camera camera = new Camera();

        MasterRenderer renderer = new MasterRenderer();
        while(!Display.isCloseRequested()){
            //entity.increasePosition(0, 0, 0);
            //entities[0].increaseRotation(0, 1, 0);
            camera.move();
            renderer.processEntity(entities[0]);
            renderer.processTerrain(terrains[0]);
            renderer.processTerrain(terrains[1]);
            renderer.render(light, camera);
            DisplayManager.updateDisplay();
        }
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }

}
