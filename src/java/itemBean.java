
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.sql.DataSource;
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
    private List<Item> list = new ArrayList<>();
    private Item item;

    private UploadedFile file;

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
                list.add(c);
            }
        } finally {
            conn.close();
        }
        setList(list);
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

    public void uploadFile() throws IOException, SQLException {

        FacesContext facesContext = FacesContext.getCurrentInstance();

        Connection conn = ds.getConnection();

        InputStream inputStream;
        inputStream = null;
        try {
            inputStream = file.getInputstream();
            PreparedStatement insertQuery = conn.prepareStatement(
                    "INSERT INTO FILESTORAGE (FILE_NAME, FILE_TYPE, FILE_SIZE, FILE_CONTENTS) "
                    + "VALUES (?, ?, ?, ?)");
            insertQuery.setString(1, file.getFileName());
            insertQuery.setString(2, file.getContentType());
            insertQuery.setLong(3, file.getSize());
            insertQuery.setBinaryStream(4, inputStream);

            int result = insertQuery.executeUpdate();
            if (result == 1) {
                facesContext.addMessage("uploadForm:upload",
                        new FacesMessage(FacesMessage.SEVERITY_INFO,
                                file.getFileName()
                                + ": uploaded successfuly !!", null));
            } else {
                // if not 1, it must be an error.
                facesContext.addMessage("uploadForm:upload",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                result + " file uploaded", null));
            }
        } catch (IOException e) {
            facesContext.addMessage("uploadForm:upload",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "File upload failed !!", null));
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
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
}
