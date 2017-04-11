import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.RawModel;
import models.TexturedModel;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import terrain.Terrain;
import texture.ModelTexture;
import utils.OBJLoader;

public class Main {

	public static void main(String[] args) {

		Random r = new Random();
		
		DisplayManager.createDisplay();
		Loader loader = new Loader();

		RawModel model = OBJLoader.loadObj("fighter-jet", loader);

		TexturedModel staticModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("model/fighter-jet-texture")));
		ModelTexture texture = staticModel.getTexture();
		texture.setShineDamper(20);
		texture.setReflectivity(1);

		Entity entity = new Entity(staticModel, new Vector3f(0, 2, 0), 0, 0, 0, 1);
		Light light = new Light(new Vector3f(-1, 4, -1), new Vector3f(1, 1, 1));

		Terrain terrain = new Terrain(0, 0, loader, new ModelTexture(loader.loadTexture("model/default-noise")));
		Terrain terrain2 = new Terrain(0, -1, loader, new ModelTexture(loader.loadTexture("model/default-noise")));
		Terrain terrain3 = new Terrain(-1, -1, loader, new ModelTexture(loader.loadTexture("model/default-noise")));
		Terrain terrain4 = new Terrain(-1, 0, loader, new ModelTexture(loader.loadTexture("model/default-noise")));

		// Gen grass
		
		ArrayList<Entity> grassObjects = new ArrayList<>();
		
		RawModel grassModel = OBJLoader.loadObj("grassModel", loader);

		TexturedModel staticGrassModel = new TexturedModel(grassModel, new ModelTexture(loader.loadTexture("model/grassTexture")));
		ModelTexture grassTexture = staticModel.getTexture();
		texture.setShineDamper(20);
		texture.setReflectivity(1);
		texture.setHasTransparency(true);
		texture.setUseFalseLighting(true);
		
		for (int i = 0; i < 10; i++) {
			Entity grass = new Entity(staticGrassModel, new Vector3f((r.nextFloat() - 0.5f) * 50, 0, (r.nextFloat() - 0.5f) * 50), 0, 0, 0, 1);
			grassObjects.add(grass);
		}
		
		//
		
		Camera camera = new Camera();

		MasterRenderer renderer = new MasterRenderer();

		while (!Display.isCloseRequested()) {

			camera.move();
			//entity.increaseRotation(0.0f, 0.1f, 0.0f);

			// Adding to render queue
			renderer.processEntity(entity);

			// grass
			
			for (Entity grass : grassObjects) {
				renderer.processEntity(grass);
			}
			
			renderer.processTerrain(terrain);
			renderer.processTerrain(terrain2);
			renderer.processTerrain(terrain3);
			renderer.processTerrain(terrain4);

			renderer.render(light, camera);
			DisplayManager.updateDisplay();
		}

		renderer.cleanUp();

		loader.cleanUp();
		DisplayManager.closeDisplay();
		quit();
	}
	
	public static void quit() {
		System.exit(0);
	}
	
}
