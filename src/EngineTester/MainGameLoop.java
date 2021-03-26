package EngineTester;

//@author VividerAphid

import Entities.Camera;
import Entities.Entity;
import Entities.Light;
import RenderEngine.DisplayManager;
import RenderEngine.Loader;
import Models.RawModel;
import Models.TexturedModel;
import RenderEngine.OBJLoader;
import RenderEngine.Renderer;
import Shaders.StaticShader;
import Textures.ModelTexture;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;


public class MainGameLoop {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        DisplayManager.createDisplay();

        Loader loader = new Loader();
        StaticShader shader = new StaticShader();
        Renderer renderer = new Renderer(shader);
        
        RawModel model = OBJLoader.loadObjModel("firstCritter", loader);


//       float[] vertices = {
//            -0.5f,0.5f,-0.5f,
//            -0.5f,-0.5f,-0.5f,
//            0.5f,-0.5f,-0.5f,
//            0.5f,0.5f,-0.5f,
//
//	    -0.5f,0.5f,0.5f,
//	    -0.5f,-0.5f,0.5f,
//				0.5f,-0.5f,0.5f,
//				0.5f,0.5f,0.5f,
//
//				0.5f,0.5f,-0.5f,
//				0.5f,-0.5f,-0.5f,
//				0.5f,-0.5f,0.5f,
//				0.5f,0.5f,0.5f,
//
//				-0.5f,0.5f,-0.5f,
//				-0.5f,-0.5f,-0.5f,
//				-0.5f,-0.5f,0.5f,
//				-0.5f,0.5f,0.5f,
//
//				-0.5f,0.5f,0.5f,
//				-0.5f,0.5f,-0.5f,
//				0.5f,0.5f,-0.5f,
//				0.5f,0.5f,0.5f,
//
//				-0.5f,-0.5f,0.5f,
//				-0.5f,-0.5f,-0.5f,
//				0.5f,-0.5f,-0.5f,
//				0.5f,-0.5f,0.5f
//
//		};
//
//		float[] textureCoords = {
//
//				0,0,
//				0,1,
//				1,1,
//				1,0,
//				0,0,
//				0,1,
//				1,1,
//				1,0,
//				0,0,
//				0,1,
//				1,1,
//				1,0,
//				0,0,
//				0,1,
//				1,1,
//				1,0,
//				0,0,
//				0,1,
//				1,1,
//				1,0,
//				0,0,
//				0,1,
//				1,1,
//				1,0
//
//
//		};
//
//		int[] indices = {
//				0,1,3,
//				3,1,2,
//				4,5,7,
//				7,5,6,
//				8,9,11,
//				11,9,10,
//				12,13,15,
//				15,13,14,
//				16,17,19,
//				19,17,18,
//				20,21,23,
//				23,21,22
//
//		};

        //RawModel model = loader.loadToVAO(vertices, textureCoords, indices);
        ModelTexture textureBase = new ModelTexture(loader.loadTexture("huh"));
        TexturedModel staticModel = new TexturedModel(model, textureBase);
        ModelTexture texture = staticModel.getTexture();
        texture.setShineDamper(10);
        texture.setReflectivity(1);

        Entity entity = new Entity(staticModel, new Vector3f(0,0,-25),0,0,0,1);
        Light light = new Light(new Vector3f(0,0,-20), new Vector3f(1,1,1));

        Camera camera = new Camera();

        while(!Display.isCloseRequested()){
            //entity.increasePosition(0, 0, 0);
            entity.increaseRotation(0, 1, 0);
            camera.move();
            renderer.prepare();
            shader.start();
            shader.loadLight(light);
            shader.loadViewMatrix(camera);
            renderer.render(entity, shader);
            shader.stop();
            DisplayManager.updateDisplay();
        }
        loader.cleanUp();
        shader.cleanUp();
        DisplayManager.closeDisplay();
    }

}
