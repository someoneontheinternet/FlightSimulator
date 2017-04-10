package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class OBJSorter {

	public static void sort(String filename, String outputName) {
		
		System.out.println("Sorting..");
		
		try {
			FileReader fr = new FileReader("res/model/" + filename + ".obj");
			BufferedReader reader = new BufferedReader(fr);
			
			String line = reader.readLine();
			
			ArrayList<String> v = new ArrayList<>();
			ArrayList<String> vt = new ArrayList<>();
			ArrayList<String> vn = new ArrayList<>();
			ArrayList<String> f = new ArrayList<>();
			
			while (line != null) {
				
				if (line.startsWith("v")) {
					v.add(line);
				} else if (line.startsWith("vt")) {
					vt.add(line);
				} else if (line.startsWith("vn")) {
					vn.add(line);
				}  else if (line.startsWith("f")) {
					line = line.replaceAll("//", "/1/");
					f.add(line);
				}
				
				line = reader.readLine();
			}
			
			reader.close();
			
			FileWriter fw = new FileWriter("res/model/" + outputName + ".obj");
			BufferedWriter writer = new BufferedWriter(fw);
			
			for (String vert : v) {
				writer.write(vert + "\n");
			}

			writer.write("\n");
			
			for (String verttext : vt) {
				writer.write(verttext + "\n");
			}
			
			writer.write("\n");
			
			for (String vertnorm : vn) {
				writer.write(vertnorm + "\n");
			}
			
			writer.write("\n");
			
			for (String face : f) {
				writer.write(face + "\n");
			}

			writer.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		System.out.println("Done!");
		
	}
	
}
