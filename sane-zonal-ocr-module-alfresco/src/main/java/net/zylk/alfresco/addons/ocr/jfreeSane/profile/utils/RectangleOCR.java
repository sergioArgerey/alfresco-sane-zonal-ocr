package net.zylk.alfresco.addons.ocr.jfreeSane.profile.utils;

public class RectangleOCR {

	private int x;
	private int y;
	private int width;
	private int height;

	private String metadata;
	private String language;
	private String typography;
	private int psm;
	/*
	 * Set Tesseract to only run a subset of layout
	 * layout analysis and assume a certain form of image
	 * ----------------------------------------------------------
	 * 0 = Orientation and script detection (OSD) only.
	 * 1 = Automatic page segmentation with OSD.
	 * 2 = Automatic page segmentation, but no OSD, or OCR.
	 * 3 = Fully automatic page segmentation, but no OSD. (Default)
	 * 4 = Assume a single column of text of variable sizes.
	 * 5 = Assume a single uniform block of vertically aligned text.
	 * 6 = Assume a single uniform block of text.
	 * 7 = Treat the image as a single text line.
	 * 8 = Treat the image as a single word. 
	 * 9 = Treat the image as a single word in a circle.
	 * 10 = Treat the image as a single character.
	 */

	private static final int Orientation_and_script_detection_OSD_only = 0;
	private static final int Automatic_page_segmentation_with_OSD = 1;
	private static final int Automatic_page_segmentation_but_no_OSD_or_OCR = 2;
	private static final int Fully_automatic_page_segmentation_but_no_OSD = 3;
	private static final int Assume_a_single_column_of_text_of_variable_sizes = 4;
	private static final int Assume_a_single_uniform_block_of_vertically_aligned_text = 5;
	private static final int Assume_a_single_uniform_block_of_text = 6;
	private static final int Treat_the_image_as_a_single_text_line = 7;
	private static final int Treat_the_image_as_a_single_word = 8;
	private static final int Treat_the_image_as_a_single_word_in_a_circle = 9;
	private static final int Treat_the_image_as_a_single_character = 10;

	public RectangleOCR(int x, int y, int width, int height, String metadata, String language, String typography, int psm) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.metadata = metadata;
		this.language = language;
		this.typography = typography;
		this.psm=psm;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	
	public String getMetadata() {
		return metadata;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getTypography() {
		return typography;
	}

	public void setTypography(String typography) {
		this.typography = typography;
	}
	
	public int getPsm() {
		return psm;
	}

	public void setPsm(int psm) {
		this.psm = psm;
	}

	public RectangleOCR getBounds() {
		return new RectangleOCR(this.x, this.y, this.width, this.height, this.metadata, this.language, this.typography, this.psm);
	}
}
