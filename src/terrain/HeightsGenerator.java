package terrain;

import java.util.Random;

public class HeightsGenerator {

	public static final float AMPLITUDE = 70f;
	
	private Random random = new Random();
	private int seed;
	
	public HeightsGenerator() {
		this.seed = random.nextInt(10000);
	}
	
	public float generateHeight(int x, int z) {
		return 1;
	}
	
}
