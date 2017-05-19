package entities;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.DisplayManager;

public class Camera {
	private Vector3f position = new Vector3f(0, 0, 0);
	private float pitch;
	private float yaw;
	private float roll;

	private float sensitivity = 5f;
	private float viewChange = 1.1f;
	private int offset = 15;

	private Player player;

	public Camera(Player player)
	 {
	  this.player = player;
	 }

	public void movement() {
		calculateCameraPosition();
		yaw = (float) (180 - player.getRotY());
		
		pitch += Mouse.getDY() * -sensitivity * DisplayManager.getFrameTimeSeconds();
		
		if (pitch > 80) {
			pitch = 80;
		} else if (pitch < -85) {
			pitch = -85;
		}
		
	}

	public void invertPitch() {
		pitch = -pitch;
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}

	private void calculateCameraPosition() {
		position.x = player.getPosition().x;
		position.z = player.getPosition().z;
		position.y = player.getPosition().y + 10;
	}
}
