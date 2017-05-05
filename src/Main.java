import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import guis.GUITexture;
import guis.GuiRenderer;
import models.RawModel;
import models.TexturedModel;
import renderEngine.DisplayManager;
import renderEngine.MasterRenderer;
import terrain.Terrain;
import texture.ModelTexture;
import texture.TerrainTexture;
import texture.TerrainTexturePack;
import utils.Loader;
import utils.ModelData;
import utils.OBJLoader;

public class Main {

	public static void main(String[] args) {

		String title = "Flight Simulator | v0.0.1";
		Random r = new Random();

		// Creating Window
		System.out.println("Creating Window...");
		DisplayManager.createDisplay();
		Display.setTitle(title);
		Loader loader = new Loader();

		System.out.println("Loading Models");

		// Tree Model
		ModelData dTree = OBJLoader.loadOBJ("cherry-tree");
		RawModel dTreeModel = loader.loadToVAO(dTree.getVertices(), dTree.getTextureCoords(), dTree.getNormals(),
				dTree.getIndices());
		TexturedModel staticDTree = new TexturedModel(dTreeModel,
				new ModelTexture(loader.loadTexture("cherry-texture")));
		ModelTexture dTreeTexture = staticDTree.getTexture();
		dTreeTexture.setShineDamper(50);
		dTreeTexture.setReflectivity(1);

		// Fighter Jet
		ModelData fj = OBJLoader.loadOBJ("fighter-jet");
		RawModel fjModel = loader.loadToVAO(fj.getVertices(), fj.getTextureCoords(), fj.getNormals(), fj.getIndices());

		TexturedModel staticModel = new TexturedModel(fjModel,
				new ModelTexture(loader.loadTexture("fighter-jet-texture")));
		ModelTexture texture = staticModel.getTexture();
		texture.setShineDamper(20);
		texture.setReflectivity(1);

		Player player = new Player(staticModel, new Vector3f(0, 2, 0), 0, 0, 0, 1);

		System.out.println("Setting up renderers...");

		// Light
		List<Light> lights = new ArrayList<>();
		lights.add(new Light(new Vector3f(500, 5000, 500), new Vector3f(1f, 1f, 1f)));
		lights.add(new Light(new Vector3f(-500, -5000, -500), new Vector3f(0.3f, 0.3f, 0.3f)));

		Camera camera = new Camera(player);
		MasterRenderer renderer = new MasterRenderer();

		// Terrain
		System.out.println("Loading Terrain texture...");
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
		TerrainTexturePack ttp = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

		// Terrain Generation

		System.out.println("Generating Terrain...");
		Display.setTitle(title + " | Generating Terrain: 0%");
		ArrayList<Terrain> terrainList = new ArrayList<>();
		int terrainCount = 1;
		int totalTerrainCount = (terrainCount * 2 + 1) * (terrainCount * 2 + 1);
		
		for (int i = -terrainCount; i <= terrainCount; i++) {
			for (int j = -terrainCount; j <= terrainCount; j++) {
				Terrain terrain = new Terrain(i, j, loader, ttp, blendMap, "height-map");
				terrainList.add(terrain);
				String percent = "" + (((float) terrainList.size() / (float) totalTerrainCount)) * 100;
	
				if (percent.length() > 5) 
					percent = percent.substring(0, 5);
				 
				Display.setTitle(title + " | " + "Generating Terrain: " + percent + "%"); 
				
			}
		}

		// Trees
		ArrayList<Entity> treeList = new ArrayList<>();

		for (int i = 0; i < 1000; i++) {
			float rx = (float) ((Math.random() - 0.5f) * 1000);
			float rz = (float) ((Math.random() - 0.5f) * 1000);

			float y = onTerrain(terrainList, rx, rz).getHeightOfTerrain(rx, rz);
			Entity treeModel = new Entity(staticDTree, new Vector3f(rx, y - 1f, rz), 0, (float) (Math.random() * 360),
					0, (float) ((Math.random() * 0.2) + 0.9f));

			treeList.add(treeModel);
		}

		System.out.println("Starting Gameloop:");

		List<Float> averageFrameTime = new ArrayList<>();

		Display.setTitle(title);
		
		while (!Display.isCloseRequested()) {

			// find the terrain that player is on

			float playerX = player.getPosition().x;
			float playerZ = player.getPosition().z;
			float playerY = player.getPosition().y;
			
			// set render distance origin
			renderer.setOrigin(playerX, playerZ);
			
			for (Terrain t : terrainList) {
				// Adding to terrain render queue
				renderer.processTerrain(t);
			}

			Terrain playerOn = onTerrain(terrainList, playerX, playerZ);
			//System.out.println(player);
			//System.out.println(playerOn.getX() + " " + playerOn.getZ());
			player.move(playerOn);
			camera.move();

			// Adding to render queue
			renderer.processEntity(player);

			// Trees
			for (Entity e : treeList) {
				renderer.processEntity(e);
			}

			renderer.render(lights, camera);

			if (averageFrameTime.size() < 10) {
				averageFrameTime.add(DisplayManager.getFrameTimeSeconds());
			} else {
				float total = 0;
				for (float time : averageFrameTime)
					total += time;
				// System.out.println("Frame time: " + (total / 10) * 1000 +
				// "ms");

				averageFrameTime.clear();
			}

			DisplayManager.updateDisplay();
		}

		System.out.println("Exiting Game Loop.");

		renderer.cleanUp();

		loader.cleanUp();
		DisplayManager.closeDisplay();
		quit();
	}

	public static Terrain onTerrain(List<Terrain> terrainList, float x, float z) {
		Terrain playerOn = terrainList.get(0);
		for (Terrain t : terrainList) {
			if (t.getX() <= x && t.getX() - t.SIZE <= x && t.getZ() <= z && t.getZ() - t.SIZE <= z) {
				playerOn = t;
			}
		}
		return playerOn;
	}

	public static void quit() {
		System.exit(0);
	}

}
