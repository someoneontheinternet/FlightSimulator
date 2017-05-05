package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import renderEngine.DisplayManager;
import terrain.Terrain;

public class Player extends Entity {

	private static float RUN_SPEED = -45;
	private static final float TURN_SPEED = 45;
	private static final float GRAVITY = -9.8f;

	private static final float DRAG = -20;
	
	public float velocity = 0;
	public float rotY = 0;
	public float uVelocity = 0;
	
	public float acceleration = 0;
	
	public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
	}

	private float calculateLift(float velocity) {
		
		velocity = Math.abs(velocity);
		return (float) (Math.log(velocity + 1) + 0.02 * velocity);
		
	}
	
	public void move(Terrain terrain) {
		checkInputs();
		
		float frameSeconds = DisplayManager.getFrameTimeSeconds();
		
		velocity += acceleration * frameSeconds;
		velocity -= DRAG * frameSeconds;
		
		if (velocity >= 0)
			velocity = 0;
		
		if (velocity < -600) {
			velocity = -600;
		}
		
		// Calculate Lift
		//uVelocity = calculateLift(velocity) * frameSeconds;
		
		super.increaseRotation(0, rotY * frameSeconds, 0);
		float distance = velocity * frameSeconds;

		float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
		super.increasePosition(dx, 0, dz);

		float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
		
		uVelocity += GRAVITY * frameSeconds;
		
		super.increasePosition(0, uVelocity, 0);
		if (super.getPosition().y < terrainHeight + 2) {
			super.getPosition().y = terrainHeight + 2;
			uVelocity = 0;
		}

	}

	private void checkInputs() {
		
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			RUN_SPEED = -75;
		} else {
			RUN_SPEED = -45;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			this.acceleration = RUN_SPEED;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			this.acceleration = (float) ((-0.5) * RUN_SPEED);
		} else {
			this.acceleration = 0;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			this.rotY = TURN_SPEED;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			this.rotY = -TURN_SPEED;
		} else {
			this.rotY = 0;
		}
		
	}

}
