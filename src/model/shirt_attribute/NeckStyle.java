package model.shirt_attribute;

/**
 * Enum representing various neck styles for clothing.
 * This enumeration defines a set of constants for different neck styles that can be used in the design of tops, such as shirts.
 */
public enum NeckStyle {
    CREW("Crew Neck"),
    V_NECK("V-Neck"),
    POLO("Polo"),
    SCOOP_NECK("Scoop Neck"),
    HENLEY("Henley"),
    TURTLENECK("Turtleneck");

    private final String style; // The description of the neck style.

    /**
     * Constructor for the NeckStyle enum.
     * Initializes the enum instance with the specified neck style description.
     *
     * @param style The description of the neck style.
     */
    NeckStyle(String style) {
        this.style = style;
    }

    /**
     * Gets the description of the neck style.
     * 
     * @return The description of the neck style.
     */
    public String getStyle() {
        return style;
    }
}
