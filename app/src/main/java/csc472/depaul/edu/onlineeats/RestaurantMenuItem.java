package csc472.depaul.edu.onlineeats;

public class RestaurantMenuItem {

    private String name;
    private String description;
    private String category;
    private String imageURL;
    private float price;

    public RestaurantMenuItem() {}

    public RestaurantMenuItem(String name, String description, String category, String imageURL, float price) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.imageURL = imageURL;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
