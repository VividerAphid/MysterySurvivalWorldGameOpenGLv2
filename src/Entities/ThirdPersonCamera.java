package Entities;

//@author VividerAphid

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;


public class ThirdPersonCamera extends StaticCamera{
    
    private float distanceFromPlayer = 50;
    private float angleAroundPlayer = 0;
    
    private Entity targetEntity;
    
    public ThirdPersonCamera(){}
    public ThirdPersonCamera(Entity targetEntity){
        this.targetEntity = targetEntity;
        this.pitch = 20;
        this.position.y = 15;
    }
    public ThirdPersonCamera(Vector3f position){
        this.position = position;
    }
    public ThirdPersonCamera(Vector3f position, float pitch, float yaw, float roll){
        this.position = position;
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
    }
    
    public void move(){
        calculateZoom();
        calculatePitch();
        calculateAngleAroundPlayer();
        float horizontalDistance = calculateHorizontalDistance();
        float verticalDistance = calculateVerticalDistance();
        calculateCameraPosition(horizontalDistance, verticalDistance);
        this.yaw = 180 - (targetEntity.getRotY() + angleAroundPlayer);
    }

    private void calculateZoom(){
        float zoomLevel = Mouse.getDWheel() * 0.1f;
        distanceFromPlayer -= zoomLevel;
    }
    
    private void calculatePitch(){
        if(Mouse.isButtonDown(1)){
            float pitchChange = Mouse.getDY() * 0.1f;
            pitch -= pitchChange;
        }
    }
    
    private void calculateAngleAroundPlayer(){
        if(Mouse.isButtonDown(0)){
            float angleChange = Mouse.getDX() * 0.3f;
            angleAroundPlayer -= angleChange;
        }
    }
    
    private float calculateHorizontalDistance(){
        return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
    }
    
    private float calculateVerticalDistance(){
        return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
    }
   
    private void calculateCameraPosition(float horizDistance, float verticDistance){
        float theta = targetEntity.getRotY() + angleAroundPlayer;
        float offsetX = (float) (horizDistance * Math.sin(Math.toRadians(theta)));
        float offsetZ = (float) (horizDistance * Math.cos(Math.toRadians(theta)));
        position.x = targetEntity.getPosition().x - offsetX;
        position.z = targetEntity.getPosition().z - offsetZ;
        position.y = targetEntity.getPosition().y + verticDistance;
    }
        
}
