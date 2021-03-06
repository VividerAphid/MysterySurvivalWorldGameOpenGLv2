package Entities;

//@author VividerAphid

import Models.TexturedModel;
import RenderEngine.DisplayManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;
import terrains.Terrain;


public class Player extends Entity{
    
    private Vector3f position = new Vector3f(0,0,0);
    private float currentSpeed = 0;
    private float currentTurnSpeed = 0;
    private float upwardsSpeed = 0;
    private float sprintSpeed = 0;
    
    private boolean isInAir = false;
    
    private static final float RUN_SPEED = 20;
    private static final float TURN_SPEED = 160;
    private static final float GRAVITY = -50;
    private static final float JUMP_POWER = 30;
    
    

    public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(model, position, rotX, rotY, rotZ, scale);
    }

    public void move(Terrain terrain){
        checkInputs();
        super.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0);
        float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();
        float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
        float dy = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
        super.increasePosition(dx, 0, dy);
        upwardsSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();
        super.increasePosition(0, upwardsSpeed * DisplayManager.getFrameTimeSeconds(), 0);
        float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
        if(super.getPosition().y < terrainHeight){
            upwardsSpeed = 0;
            super.getPosition().y = terrainHeight;
            isInAir = false;
        }
    }
    
    private void jump(){
        if(!isInAir){
            this.upwardsSpeed = JUMP_POWER;
            isInAir = true;
        }
    }
    
    private void checkInputs(){
        if(Keyboard.isKeyDown(Keyboard.KEY_W) || Keyboard.isKeyDown(Keyboard.KEY_COMMA)){
            this.currentSpeed = (RUN_SPEED + this.sprintSpeed);
        }else if(Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_O)){
            this.currentSpeed = - (RUN_SPEED + this.sprintSpeed);
        }
        else{
            this.currentSpeed = 0;
        }
        
        if(Keyboard.isKeyDown(Keyboard.KEY_D) || Keyboard.isKeyDown(Keyboard.KEY_E)){
            this.currentTurnSpeed = -TURN_SPEED;
        }else if(Keyboard.isKeyDown(Keyboard.KEY_A)){
            this.currentTurnSpeed = TURN_SPEED;
        }
        else{
            this.currentTurnSpeed = 0;
        }
        
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
            this.sprintSpeed = 20;
        }
        else{
            this.sprintSpeed = 0;
        }
        
        if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
            jump();
        }
    }
}
