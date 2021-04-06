package Entities;

//@author VividerAphid

import RenderEngine.DisplayManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;


public class MovableCamera extends StaticCamera{
    private float speed = 5f;
    
    private float currentFBSpeed = 0;
    private float currentLRSpeed = 0;
    private float currentVerticalSpeed = 0;
    
    public MovableCamera(){super();}
    public MovableCamera(Vector3f position){
        super();
        this.position = position;
    }

    public void move(){
        checkInputs();
        calculatePitchAndYaw();
        float hFBDistance = currentFBSpeed * DisplayManager.getFrameTimeSeconds();
        float hLRDistance = currentLRSpeed * DisplayManager.getFrameTimeSeconds();
        float vDistance = currentVerticalSpeed * DisplayManager.getFrameTimeSeconds();
        float dx = (float) (hLRDistance * Math.cos(Math.toRadians(yaw))) - (float)(hFBDistance * Math.sin(Math.toRadians(yaw))) ;
        float dz = (float) (hFBDistance * Math.cos(Math.toRadians(yaw))) + (float)(hLRDistance * Math.sin(Math.toRadians(yaw)));
        this.position.x += dx;
        this.position.z += dz;
        this.position.y += vDistance;
    }
    private void checkInputs(){
        if(Keyboard.isKeyDown(Keyboard.KEY_W) || Keyboard.isKeyDown(Keyboard.KEY_COMMA)){
            this.currentFBSpeed = -speed;
        }else if(Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_O)){
            this.currentFBSpeed = speed;
        }
        else{
            this.currentFBSpeed = 0;
        }
        
        if(Keyboard.isKeyDown(Keyboard.KEY_D) || Keyboard.isKeyDown(Keyboard.KEY_E)){
            this.currentLRSpeed = speed;
        }else if(Keyboard.isKeyDown(Keyboard.KEY_A)){
            this.currentLRSpeed = -speed;
        }
        else{
            this.currentLRSpeed = 0;
        }
        
        if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
            this.currentVerticalSpeed = speed;
        }
        else if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
            this.currentVerticalSpeed = -speed;
        }
        else{
            this.currentVerticalSpeed = 0;
        }
    }
    
    private void calculatePitchAndYaw(){
        if(Mouse.isButtonDown(1)){
            float pitchChange = Mouse.getDY() * 0.1f;
            pitch -= pitchChange;
            float yawChange = Mouse.getDX() * 0.1f;
            yaw -= yawChange;
        }
    }
    
    public void setSpeed(float speed){
        this.speed = speed;
    }
        
}
