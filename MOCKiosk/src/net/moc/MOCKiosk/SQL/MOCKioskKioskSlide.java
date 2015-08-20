package net.moc.MOCKiosk.SQL;

import java.awt.Image;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.imageio.ImageIO;

public class MOCKioskKioskSlide implements Comparable<MOCKioskKioskSlide> {
	private int id;
	private String name;
	private int indeck_id;
	private String title;
	private String text;
	private String url;
	private String image;
	private int image_size;
	
	private Image imageFile = null;
	
	public MOCKioskKioskSlide(int id, String name, int indeck_id, String title, String text, String url, String image, int image_size) {
		this.id = id;
		this.name = name;
		this.indeck_id = indeck_id;
		this.title = title;
		this.text = text;
		this.url = url;
		this.image = image;
		this.image_size = image_size;
		
		try { this.imageFile = ImageIO.read(new java.net.URL(image)); } catch (MalformedURLException e) {} catch (IOException e) {}
		
	}

	public int getId() { return id; }
	public void setId(int id) { this.id = id; }

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public int getIndeck_id() { return indeck_id; }
	public void setIndeck_id(int indeck_id) { this.indeck_id = indeck_id; }

	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }

	public String getText() { return text; }
	public void setText(String text) { this.text = text; }

	public String getUrl() { return url; }
	public void setUrl(String url) { this.url = url; }

	public String getImage() { return image; }
	public void setImage(String image) { this.image = image; try { this.imageFile = ImageIO.read(new java.net.URL(image)); } catch (MalformedURLException e) {} catch (IOException e) {} }

	public int getImage_size() { return image_size; }
	public void setImage_size(int image_size) { this.image_size = image_size; }

	public Image getImageFile() { return imageFile; }
	public int getImageX() {
		if (this.imageFile == null) return -1;
		return this.imageFile.getWidth(null);
	}
	public int getImageY() {
		if (this.imageFile == null) return -1;
		return this.imageFile.getHeight(null);
		
	}

	public int compareTo(MOCKioskKioskSlide another) { return this.name.compareTo(another.name); }
	
}
