package moc.DreamCrafter.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;

public class YAMLHelper {

	@SuppressWarnings("unchecked")
	public static void copyNode(YamlConfiguration yaml, String originalPath, String newPath) {
		// Get the data for the node of the original path
		MemorySection m = (MemorySection) yaml.get(originalPath);
		if(m != null) {
			
			// For each child of the node, get the path and value.
			// Store it under the new path
			for(Map.Entry<String, Object> entry : m.getValues(true).entrySet()) {
				String path = entry.getKey();
				Object value = entry.getValue();
				String valueClass = value.getClass().toString();
				
				if(valueClass.equals("class java.lang.Integer"))
					yaml.set(newPath + "." + path, (Integer)value);
				
				if(valueClass.equals("class java.lang.String"))
					yaml.set(newPath + "." + path, (String)value);
				
				if(valueClass.equals("class java.lang.Boolean"))
					yaml.set(newPath + "." + path, (Boolean)value);
					
				else if(valueClass.equals("class java.util.ArrayList"))
					yaml.set(newPath + "." + path, Arrays.asList(((ArrayList<String>)value).toArray()));
				
			}
		}
	}
	
	/**
	 * Helper - Loads YAML configuration from File
	 * @param f		File containing yaml
	 * @return		YamlConfiguration object of file
	 */
	public static YamlConfiguration loadYAML(File f) {
		YamlConfiguration yaml = new YamlConfiguration();
		try {
			yaml.load(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		return yaml;
	}
	
	/**
	 * Helper - Save YAML to file
	 * @param yaml	YAMLConfiguration
	 * @param file	Desired File to save to
	 */
	public static void saveYaml(YamlConfiguration yaml, File file) {
		try {
			yaml.save(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void saveYaml(YamlConfiguration yaml, String filepath) {
		YAMLHelper.saveYaml(yaml, new File(filepath));
	}
	
	public static YamlConfiguration initYaml(String fileNamePath) {
		YamlConfiguration yaml;
		
		File file = new File(fileNamePath);
		if (file.exists())
			yaml = YAMLHelper.loadYAML(file);
		else
			yaml = new YamlConfiguration();
		
		return yaml;
	}
	
//	public static String pathToString(YamlConfiguration yaml, String yamlPath) {
//		MemorySection m = (MemorySection) yaml.get(yamlPath);
//		if(m != null) {
//			
//			String s = "";
//			
//			for(Map.Entry<String, Object> entry : m.getValues(true).entrySet()) {
//				String path = entry.getKey();
//				Object value = entry.getValue();
//				
//				s += "\n" + yamlPath + "." + path + ": ";
//				
//				if(value instanceof Integer) s += String.valueOf((Integer)value);
//				
//				if(value instanceof String) s += (String)value;
//				
//				if(value instanceof Boolean) s += String.valueOf((Boolean)value);
//					
//				else if(value instanceof ArrayList) s += String.valueOf(Arrays.asList(((ArrayList<String>)value).toArray()));
//				
//			}
//			
//			return s;
//			
//		}
//		
//		return "null";
//		
//	}
	
}
