package entities;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class Camera {

	private Vector3f position = new Vector3f(0, 0, 0);
	private float pitch = 0;
	private float yaw = 0;
	private float playerYaw = 0;

	private float roll = 0;

	private float distance = 50;

	private Player player;

	public Camera(Player player) {
		this.player = player;
	}

	public void move() {
		calculateZoom();
		calculatePitch();
		calculateYaw();
		float hd = calHDistance();
		float vd = calVDistance();

		calculateCameraPosition(hd, vd);
		this.yaw = 180 - (player.getRotY() + playerYaw);

	}

	private void calculateCameraPosition(float hd, float vd) {
		float theta = player.getRotY() + playerYaw;
		float offsetX = (float) (hd * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (hd * Math.cos(Math.toRadians(theta)));

		position.x = player.getPosition().x - offsetX;
		position.z = player.getPosition().z - offsetZ;
		position.y = player.getPosition().y + vd;

	}

	private float calHDistance() {
		return (float) (distance * Math.cos(Math.toDegrees(pitch)));
	}

	private float calVDistance() {
		return (float) (distance * Math.sin(Math.toDegrees(pitch)));
	}

	private void calculateZoom() {
		float zoomLevel = Mouse.getDWheel() * 0.005f;
		distance -= zoomLevel;
		if (distance < 10f) {
			distance = 10f;
		}
	}

	private void calculatePitch() {
		if (Mouse.isButtonDown(0)) {
			float pitchChange = Mouse.getDY() * 0.00001f;
			pitch -= pitchChange;
		}
		
		if (pitch < 0.001f)
			pitch = 0.001f;
		if (pitch > 0.007) 
			pitch = 0.007f;
		
	}

	private void calculateYaw() {
		if (Mouse.isButtonDown(0)) {
			float yawChange = Mouse.getDX() * 0.1f;
			playerYaw -= yawChange;
		}
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
}
