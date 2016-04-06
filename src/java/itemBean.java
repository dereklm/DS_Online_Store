
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.sql.DataSource;

@SessionScoped
@Named
public class itemBean implements Serializable {

    // resource injection
    @Resource(name = "jdbc/db1")
    private DataSource ds;
    private String model;
    private String brand;
    private int quantity;
    private String description;
    private double price;
    private List<Item> list = new ArrayList<>();

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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
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

    public List<Item> getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }
    
    public void getItems() throws SQLException {
        list.clear();

        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();
        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT model, brand, stock, title, description, price from ITEM"
            );
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                Item c = new Item();
                c.setModel(result.getString("model"));
                c.setBrand(result.getString("brand"));
                c.setStock(Integer.valueOf(result.getString("stock")));
                c.setDescription(result.getString("description"));
                c.setPrice(Double.valueOf(result.getString("price")));
                list.add(c);
            }
        } finally {
            conn.close();
        }
        setList(list);
       
    }

}
