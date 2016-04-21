
import com.sun.rowset.CachedRowSetImpl;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.sql.DataSource;
import javax.sql.rowset.CachedRowSet;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

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
    private long ITEM_ID;
    private List<Item> itemList = new ArrayList<>();
    private Item item;
    private List<String> images;
    private UploadedFile file;

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public List<String> getImages() {
        return images;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

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

    public List<Item> getitemList() {
        return itemList;
    }

    public void setitemList(List itemList) {
        this.itemList = itemList;
    }

    public void getItems() throws SQLException {
        itemList.clear();

        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();
        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT ITEM_ID, model, brand, stock, title, description, price from ITEM"
            );
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                Item c = new Item();
                c.setITEM_ID(Long.valueOf(result.getString("ITEM_ID")));
                c.setModel(result.getString("model"));
                c.setBrand(result.getString("brand"));
                c.setStock(Integer.valueOf(result.getString("stock")));
                c.setTitle(result.getString("title"));
                c.setDescription(result.getString("description"));
                c.setPrice(Double.valueOf(result.getString("price")));
                itemList.add(c);
            }
        } finally {
            conn.close();
        }
        setitemList(itemList);
    }

    public String deleteItem(Item s) throws SQLException {

        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }

        try {
            PreparedStatement is = conn.prepareStatement(
                    "delete from ITEM where ITEM_ID = ?"
            );

            // insert customer data from database
            is.setString(1, String.valueOf(s.getITEM_ID()));

            is.executeUpdate();

        } finally {
            conn.close();
        }
        return null;
    }

    public String updateItem(String s) throws SQLException {

        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }

        try {
            PreparedStatement is = conn.prepareStatement(
                    "Update ITEM Set model = ?, brand = ?, stock = ?, title = ?, description = ?, price = ? Where ITEM_ID = ?"
            );
            // insert customer data from database
            is.setString(1, item.getModel());
            is.setString(2, item.getBrand());
            is.setString(3, String.valueOf(item.getStock()));
            is.setString(4, item.getTitle());
            is.setString(5, item.getDescription());
            is.setString(6, String.valueOf(item.getPrice()));
            is.setString(7, s);
            is.executeUpdate();

        } finally {
            conn.close();
        }
        return "itemList";
    }

    public String goToEdit(Item s) {
        setItem(s);
        return "editItem";
    }

    public ResultSet getList() throws SQLException {
        Connection conn = ds.getConnection();
        try {
            PreparedStatement is = conn.prepareStatement(
                    "SELECT ITEM_ID, FILE_ID, FILE_NAME, FILE_TYPE, FILE_SIZE, FILE_CONTENTS FROM FILESTORAGE WHERE ITEM_ID = ?"
            );
            CachedRowSet crs = new CachedRowSetImpl();

            is.setLong(1, item.getITEM_ID());

            ResultSet result = is.executeQuery();
            crs.populate(result);
            return crs;
        } finally {
            conn.close();
        }
    }

    public ResultSet getPic() throws SQLException {
        Connection conn = ds.getConnection();
        try {
            PreparedStatement is = conn.prepareStatement(
                    "SELECT ITEM_ID, FILE_ID, FILE_NAME, FILE_TYPE, FILE_SIZE, FILE_CONTENTS FROM FILESTORAGE"
            );

            CachedRowSet cache = new CachedRowSetImpl();

            ResultSet result = is.executeQuery();
            cache.populate(result);
            return cache;
        } finally {
            conn.close();
        }
    }
}
