import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
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

	private static volatile boolean wPressed = false;

	public static boolean isWPressed() {
		synchronized (Main.class) {
			return wPressed;
		}
	}

	public static void configure() {

		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {

			@Override
			public boolean dispatchKeyEvent(KeyEvent ke) {
				synchronized (Main.class) {
					switch (ke.getID()) {
					case KeyEvent.KEY_PRESSED:
						if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
							wPressed = true;
						}
						break;

					case KeyEvent.KEY_RELEASED:
						if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
							wPressed = false;
						}
						break;
					}
					return false;
				}
			}
		});

		JFrame frame = new JFrame("Launcher");
		frame.setSize(1024, 640);
		frame.setLocationRelativeTo(null);

		// Add components

		try {
			frame.setContentPane(new JLabel(new ImageIcon(ImageIO.read(new File("res/launcher-bg.jpg")))));
		} catch (IOException e) {
			e.printStackTrace();
		}

		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		long before = System.currentTimeMillis();
		
		while (frame.isVisible()) {
			
			long now = System.currentTimeMillis();
			
			if (isWPressed() || now - before > 20 * 1000) {
				frame.setVisible(false);
				break;
			}
		}
	}

	public static void main(String[] args) {

		configure();

		// Code to configure
		
		BufferedReader br = null;
		try {
			FileReader fr = new FileReader("settings.conf");
			br = new BufferedReader(fr);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			quit();
		}
		
		String settings = "";
		String line = "";
		
		try {
			while ((line = br.readLine()) != null) {
				settings += line;
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			quit();
		}
		
		/// end of configuration
		
		int vWidth = settings.indexOf("=", settings.indexOf("vWidth=")) + 1;
		int vHeight = settings.indexOf("=", settings.indexOf("vHeight=")) + 1;
		int fpsCap = settings.indexOf("=", settings.indexOf("fpsCap=")) + 1;
		int tCount = settings.indexOf("=", settings.indexOf("terrainSize=")) + 1;
		
		int width = Integer.parseInt(settings.substring(vWidth, settings.indexOf(";", vWidth)).trim());
		int height = Integer.parseInt(settings.substring(vHeight, settings.indexOf(";", vHeight)).trim());
		int fps = Integer.parseInt(settings.substring(fpsCap, settings.indexOf(";", fpsCap)).trim());
		int tSize = Integer.parseInt(settings.substring(tCount, settings.indexOf(";", tCount)).trim());
		
		DisplayManager.FPS_CAP = fps;
		DisplayManager.WIDTH = width;
		DisplayManager.HEIGHT = height;
		
		float modelScaleOffset = 3.8f;
		float globalEntityGenSpread = 30000f;

		String title = "Explorer.exe | v0.0.1";
		Random r = new Random();

		// Creating Window
		System.out.println("Creating Window...");
		DisplayManager.createDisplay();
		Display.setTitle(title);
		Loader loader = new Loader();

		System.out.println("Loading Models");

		// Fighter Jet
		ModelData fj = OBJLoader.loadOBJ("sphere");
		RawModel fjModel = loader.loadToVAO(fj.getVertices(), fj.getTextureCoords(), fj.getNormals(), fj.getIndices());

		TexturedModel staticModel = new TexturedModel(fjModel, new ModelTexture(loader.loadTexture("default-grey")));
		ModelTexture texture = staticModel.getTexture();
		texture.setShineDamper(20);
		texture.setReflectivity(1);

		Player player = new Player(staticModel, new Vector3f(0, 2, 0), 0, 0, 0, 0.05f);

		System.out.println("Setting up renderers...");

		// Light
		List<Light> lights = new ArrayList<>();
		lights.add(new Light(new Vector3f(500, 50000, 500), new Vector3f(1f, 1f, 1f)));
		lights.add(new Light(new Vector3f(-500, -50000, -500), new Vector3f(0.3f, 0.3f, 0.3f)));

		Camera camera = new Camera(player);
		MasterRenderer renderer = new MasterRenderer();

		// Terrain
		System.out.println("Loading Terrain texture...");
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("green-grass"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("darkgreen-grass"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("brown-dirt"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("fadedgreen-grass"));
		TerrainTexturePack ttp = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap2"));

		// Terrain Generation
		System.out.println("Generating Terrain...");
		Display.setTitle(title + " | Generating Terrain: 0%");
		ArrayList<Terrain> terrainList = new ArrayList<>();
		
		// Set Terrain Count
		int terrainCount = tSize;
		
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

		// Tree Model
		ModelData dTree = OBJLoader.loadOBJ("cherry-tree");
		RawModel dTreeModel = loader.loadToVAO(dTree.getVertices(), dTree.getTextureCoords(), dTree.getNormals(),
				dTree.getIndices());
		TexturedModel staticDTree = new TexturedModel(dTreeModel,
				new ModelTexture(loader.loadTexture("cherry-texture")));
		ModelTexture dTreeTexture = staticDTree.getTexture();
		dTreeTexture.setShineDamper(50);
		dTreeTexture.setReflectivity(1);

		// Trees
		ArrayList<Entity> treeList = new ArrayList<>();

		for (int i = 0; i < 40000; i++) {
			float rx = (float) ((Math.random() - 0.5f) * globalEntityGenSpread);
			float rz = (float) ((Math.random() - 0.5f) * globalEntityGenSpread);

			float y = onTerrain(terrainList, rx, rz).getHeightOfTerrain(rx, rz);
			Entity treeModel = new Entity(staticDTree, new Vector3f(rx, y - 2f, rz), 0, (float) (Math.random() * 360),
					0, ((float) (Math.random() * 0.5 + 1.2)) * modelScaleOffset);

			treeList.add(treeModel);
		}

		// Grass Model
		ModelData grass = OBJLoader.loadOBJ("grass");
		RawModel grassModel = loader.loadToVAO(grass.getVertices(), grass.getTextureCoords(), grass.getNormals(),
				grass.getIndices());
		TexturedModel staticGrassModel = new TexturedModel(grassModel,
				new ModelTexture(loader.loadTexture("grass-texture")));
		ModelTexture grassModelTexture = staticGrassModel.getTexture();
		grassModelTexture.setShineDamper(50);
		grassModelTexture.setReflectivity(1);
		grassModelTexture.setUseFalseLighting(true);

		// Grass
		ArrayList<Entity> grassList = new ArrayList<>();

		for (int i = 0; i < 700000; i++) {
			float rx = (float) ((Math.random() - 0.5f) * globalEntityGenSpread);
			float rz = (float) ((Math.random() - 0.5f) * globalEntityGenSpread);

			float y = onTerrain(terrainList, rx, rz).getHeightOfTerrain(rx, rz);
			Entity grassObj = new Entity(staticGrassModel, new Vector3f(rx, y, rz), 0, (float) (Math.random() * 360), 0,
					((float) (2 + (Math.random() * 0.1)) * modelScaleOffset / 1.8f));

			grassList.add(grassObj);
		}

		// large tree
		ModelData spruceTree = OBJLoader.loadOBJ("spruce-tree");
		RawModel spruceModel = loader.loadToVAO(spruceTree.getVertices(), spruceTree.getTextureCoords(),
				spruceTree.getNormals(), spruceTree.getIndices());
		TexturedModel staticSpruceModel = new TexturedModel(spruceModel,
				new ModelTexture(loader.loadTexture("spruce-texture")));
		ModelTexture spruceTextureModel = staticSpruceModel.getTexture();
		spruceTextureModel.setShineDamper(50);
		spruceTextureModel.setReflectivity(1);

		ArrayList<Entity> spruceList = new ArrayList<>();

		for (int i = 0; i < 80000; i++) {
			float rx = (float) ((Math.random() - 0.5f) * globalEntityGenSpread);
			float rz = (float) ((Math.random() - 0.5f) * globalEntityGenSpread);

			float y = onTerrain(terrainList, rx, rz).getHeightOfTerrain(rx, rz);
			Entity spruceObj = new Entity(staticSpruceModel, new Vector3f(rx, y - 1f, rz), 0,
					(float) (Math.random() * 360), 0, modelScaleOffset * ((float) ((Math.random() * 0.5) + 2.3)));

			spruceList.add(spruceObj);
		}

		// rock
		ModelData rockData = OBJLoader.loadOBJ("rock");
		RawModel rockModel = loader.loadToVAO(rockData.getVertices(), rockData.getTextureCoords(),
				rockData.getNormals(), rockData.getIndices());
		TexturedModel staticRockModel = new TexturedModel(rockModel,
				new ModelTexture(loader.loadTexture("rock-texture")));
		ModelTexture rockTextureModel = staticRockModel.getTexture();
		rockTextureModel.setShineDamper(50);
		rockTextureModel.setReflectivity(1);

		ArrayList<Entity> rockList = new ArrayList<>();

		for (int i = 0; i < 50000; i++) {
			float rx = (float) ((Math.random() - 0.5f) * globalEntityGenSpread);
			float rz = (float) ((Math.random() - 0.5f) * globalEntityGenSpread);

			float y = onTerrain(terrainList, rx, rz).getHeightOfTerrain(rx, rz);
			Entity rockObj = new Entity(staticRockModel, new Vector3f(rx, y + 0.1f, rz), 0,
					(float) (Math.random() * 360), 0, ((float) (Math.random() + 5.3)) * modelScaleOffset / 1.5f);

			spruceList.add(rockObj);
		}

		System.out.println("Starting Gameloop:");

		Display.setTitle(title + " | Exit with: ESC");

		while (!Display.isCloseRequested()) {

			if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
				break;
			}
			
			// find the terrain that player is on

			float playerX = player.getPosition().x;
			float playerZ = player.getPosition().z;

			// set render distance origin
			renderer.setOrigin(playerX, playerZ);

			for (Terrain t : terrainList) {
				renderer.processTerrain(t);
			}

			Terrain playerOn = onTerrain(terrainList, playerX, playerZ);
			camera.movement();
			player.move(playerOn);

			// Adding to render queue
			renderer.processEntity(player);

			// Grass
			for (Entity e : grassList)
				renderer.processEntity(e);
			// Trees
			for (Entity e : treeList)
				renderer.processEntity(e);
			// Spruce
			for (Entity e : spruceList)
				renderer.processEntity(e);
			// Rock
			for (Entity e : rockList)
				renderer.processEntity(e);

			renderer.render(lights, camera);
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
