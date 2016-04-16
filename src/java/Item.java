
import java.io.Serializable;

public class Item implements Serializable {

    private String model;
    private String brand;
    private int stock;
    private String description;
    private double price;
    private String title;
    private long ITEM_ID;

    public long getITEM_ID() {
        return ITEM_ID;
    }

    public void setITEM_ID(long ITEM_ID) {
        this.ITEM_ID = ITEM_ID;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
