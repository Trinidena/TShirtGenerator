package model.shirt;

import java.lang.reflect.Field;

import model.shirt_attribute.Color;
import model.shirt_attribute.Material;
import model.shirt_attribute.NeckStyle;
import model.shirt_attribute.Size;

/**
 * Represents a T-Shirt, extending the Shirt class with specific behaviors
 * tailored for T-Shirts. This class provides implementations for customizing
 * the design and calculating the price, along with any additional T-Shirt
 * specific functionalities.
 * 
 * @author Trinidad
 */
public class TShirt extends Shirt {

	private String creatorName;
	private String status;
	private String businessName;

	/**
	 * Initializes a new TShirt object using provided shirt attributes.
	 *
	 * @param name          The name of the T-Shirt.
	 * @param hasPocket     Indicates if the T-Shirt has a pocket.
	 * @param shoulderWidth The shoulder width size of the T-Shirt.
	 * @param size          The overall size of the T-Shirt.
	 * @param sleeveLength  The length of the T-Shirt's sleeves.
	 * @param color         The color of the T-Shirt.
	 * @param neckStyle     The neck style of the T-Shirt.
	 * @param material      The material of the T-Shirt.
	 * @param backLength    The back length size of the T-Shirt.
	 * @param shirtText     The text printed on the T-Shirt, if any.
	 * @param creatorName   The name of the creator of the T-Shirt.
	 * @param status        The current status of the T-Shirt.
	 * @param businessName  The business associated with the status change of the
	 *                      shirt
	 */
	public TShirt(String name, boolean hasPocket, Size shoulderWidth, Size size, Size sleeveLength, Color color,
			NeckStyle neckStyle, Material material, Size backLength, String shirtText, String creatorName,
			String status, String businessName) {
		super(name, hasPocket, shoulderWidth, size, sleeveLength, color, neckStyle, material, backLength, shirtText);
		this.setCreatorName(creatorName);
		this.setStatus(status);
		this.businessName = businessName;
	}

	/**
	 * Gets the name of the creator of the T-Shirt.
	 * 
	 * @return The creator name.
	 */
	public String getCreatorName() {
		return this.creatorName;
	}

	/**
	 * Sets the name of the creator of the T-Shirt.
	 * 
	 * @param creatorName The creator name to set.
	 */
	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	/**
	 * Gets the current status of the T-Shirt.
	 * 
	 * @return The status.
	 */
	public String getStatus() {
		return this.status;
	}

	/**
	 * Sets the current status of the T-Shirt.
	 * 
	 * @param status The status to set.
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	public String getBusiness() {
		return this.businessName;
	}

	public String format() {
		StringBuilder result = new StringBuilder();
		String newLine = System.getProperty("line.separator");

		result.append(this.getClass().getName());
		result.append(" Object {");
		result.append(newLine);

		// determine fields declared in this class only (no fields of superclass)
		Field[] fields = this.getClass().getDeclaredFields();

		// print field names paired with their values
		for (Field field : fields) {
			result.append("  ");
			try {
				result.append(field.getName());
				result.append(": ");
				// requires access to private field:
				result.append(field.get(this));
			} catch (IllegalAccessException ex) {
				System.out.println(ex);
			}
			result.append(newLine);
		}
		result.append("}");

		return result.toString();
	}
}
