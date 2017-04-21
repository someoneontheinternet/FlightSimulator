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
import utils.ModelData;
import utils.OBJLoader;

public class Main {

	public static void main(String[] args) {

		Random r = new Random();
		
		DisplayManager.createDisplay();
		Loader loader = new Loader();

		
		ModelData fj = OBJLoader.loadOBJ("fighter-jet");
		RawModel fjModel = loader.loadToVAO(fj.getVertices(), fj.getTextureCoords(), fj.getNormals(), fj.getIndices());

		TexturedModel staticModel = new TexturedModel(fjModel, new ModelTexture(loader.loadTexture("model/fighter-jet-texture")));
		ModelTexture texture = staticModel.getTexture();
		texture.setShineDamper(20);
		texture.setReflectivity(1);

		Entity entity = new Entity(staticModel, new Vector3f(0, 2, 0), 0, 0, 0, 1);
		Light light = new Light(new Vector3f(-1, 4, -1), new Vector3f(1, 1, 1));

		ArrayList<Terrain> terrainList = new ArrayList<>();
		
		terrainList.add(new Terrain(0, 0, loader, new ModelTexture(loader.loadTexture("model/default-red"))));
		terrainList.add(new Terrain(0, -1, loader, new ModelTexture(loader.loadTexture("model/default-blue"))));
		terrainList.add(new Terrain(-1, -1, loader, new ModelTexture(loader.loadTexture("model/default-green"))));
		terrainList.add(new Terrain(-1, 0, loader, new ModelTexture(loader.loadTexture("model/default-grey"))));

		// Gen grass
		
		ArrayList<Entity> grassObjects = new ArrayList<>();
		
		
		ModelData grassData = OBJLoader.loadOBJ("grassModel");
		RawModel grassModel = loader.loadToVAO(grassData.getVertices(), grassData.getTextureCoords(), grassData.getNormals(), grassData.getIndices());

		TexturedModel staticGrassModel = new TexturedModel(grassModel, new ModelTexture(loader.loadTexture("model/grassTexture")));
		ModelTexture grassTexture = staticModel.getTexture();
		texture.setShineDamper(20);
		texture.setReflectivity(1);
		texture.setHasTransparency(true);
		texture.setUseFalseLighting(true);
		
		//for (int i = 0; i < 1000; i++) {
			//Entity grass = new Entity(staticGrassModel, new Vector3f((r.nextFloat() - 0.5f) * 500, 0, (r.nextFloat() - 0.5f) * 500), 0, 0, 0, 1);
			//grassObjects.add(grass);
		//}
		
		Camera camera = new Camera();

		MasterRenderer renderer = new MasterRenderer();
		
		while (!Display.isCloseRequested()) {

			camera.move();
			//entity.increaseRotation(0.0f, 0.1f, 0.0f);

			// Adding to render queue
			renderer.processEntity(entity);

			for (Entity grass : grassObjects) {
				renderer.processEntity(grass);
			}
			
			terrainList.forEach(t -> {
				renderer.processTerrain(t);
			});
			
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
