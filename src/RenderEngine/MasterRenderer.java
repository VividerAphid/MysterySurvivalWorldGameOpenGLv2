package RenderEngine;

//@author VividerAphid

import Entities.StaticCamera;
import Entities.Entity;
import Entities.Light;
import Models.TexturedModel;
import Shaders.ShaderProgram;
import Shaders.StaticShader;
import Shaders.TerrainShader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import terrains.Terrain;


public class MasterRenderer {
    private Matrix4f projectionMatrix;
    
    private static final float FOV = 70;
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 1000f;
    
    private static final float SKY_RED = 0.5f;
    private static final float SKY_GREEN = 0;
    private static final float SKY_BLUE = 0.5f;

    private ShaderProgram shader;
    private EntityRenderer renderer;
    
    private TerrainRenderer terrainRenderer;
    private TerrainShader terrainShader = new TerrainShader();
    
    private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
    private List<Terrain> terrains = new ArrayList<Terrain>();
    
    public MasterRenderer(ShaderProgram shader){
        this.shader = shader;
        createProjectionMatrix();
        renderer = new EntityRenderer(this.shader, projectionMatrix);
        enableCulling();
        terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
    }
    
    public static void enableCulling(){
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }
    public static void disableCulling(){
        GL11.glDisable(GL11.GL_CULL_FACE);
    }
    public void render(Light sun, StaticCamera camera){
        prepare();
        shader.start();
        shader.loadSkyColour(SKY_RED, SKY_GREEN, SKY_BLUE);
        shader.loadLight(sun);
        shader.loadViewMatrix(camera);
        renderer.render(entities);
        shader.stop();
        entities.clear();
        terrainShader.start();
        terrainShader.loadSkyColour(SKY_RED, SKY_GREEN, SKY_BLUE);
        terrainShader.loadLight(sun);
        terrainShader.loadViewMatrix(camera);
        terrainRenderer.render(terrains);
        terrains.clear();
        terrainShader.stop();
    }
    
    public void prepare(){
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(SKY_RED, SKY_GREEN ,SKY_BLUE, 1);
    }
    
    private void createProjectionMatrix(){
        float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
	float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
	float x_scale = y_scale / aspectRatio;
	float frustum_length = FAR_PLANE - NEAR_PLANE;

	projectionMatrix = new Matrix4f();
	projectionMatrix.m00 = x_scale;
	projectionMatrix.m11 = y_scale;
	projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
	projectionMatrix.m23 = -1;
	projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
	projectionMatrix.m33 = 0;
    }
    
    public void processEntity(Entity entity){
        TexturedModel entityModel = entity.getModel();
        List<Entity> batch = entities.get(entityModel);
        if(batch!=null){
            batch.add(entity);
        }else{
            List<Entity> newBatch = new ArrayList<Entity>();
            newBatch.add(entity);
            entities.put(entityModel, newBatch);
        }
    }
    
    public void processTerrain(Terrain terrain){
        terrains.add(terrain);
    }
    
    public void cleanUp(){
        shader.cleanUp();
        terrainShader.cleanUp();
    }

}
