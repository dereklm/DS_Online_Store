
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.DatatypeConverter;
import javax.servlet.http.Part;

@Named
@SessionScoped
public class addItem implements Serializable {

    @NotNull
    @Size(min = 1, message = "   At least 1 character")
    private String model;
    @NotNull
    @Size(min = 1, message = "   At least 1 character")
    private String brand;
    @NotNull
    private int stock;
    private String title;
    @NotNull
    @Size(min = 1, message = "   At least 1 character")
    private String description;
    @NotNull
    private double price;

    private Part part;

    private Item item;

    private Boolean checkError = false;

    @Resource(name = "jdbc/db1")
    private DataSource ds;

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

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Part getPart() {
        return part;
    }

    public void setPart(Part part) {
        this.part = part;
    }

    public String addItems() throws SQLException {

        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }

        try {
            PreparedStatement is = conn.prepareStatement(
                    "Insert into ITEM (model, brand, stock, title, description, price)"
                    + "VALUES(?, ?, ?, ?, ?, ?)"
            );
            // insert customer data from database
            is.setString(1, getModel());
            is.setString(2, getBrand());
            is.setString(3, String.valueOf(getStock()));
            is.setString(4, getTitle());
            is.setString(5, getDescription());
            is.setString(6, String.valueOf(getPrice()));
            is.executeUpdate();

        } finally {
            conn.close();
        }
        return "itemList";
    }

    public void uploadFile() throws IOException, SQLException {

        FacesContext facesContext = FacesContext.getCurrentInstance();

        Connection conn = ds.getConnection();

        InputStream inputStream;
        inputStream = null;
        try {
            inputStream = part.getInputStream();
            PreparedStatement insertQuery = conn.prepareStatement(
                    "INSERT INTO FILESTORAGE (model, brand, FILE_NAME, FILE_TYPE, FILE_SIZE, FILE_CONTENTS) "
                    + "VALUES (?, ?, ?, ?, ?, ?)");
            insertQuery.setString(1, getModel());
            insertQuery.setString(2, getBrand());
            insertQuery.setString(3, part.getSubmittedFileName());
            insertQuery.setString(4, part.getContentType());
            insertQuery.setLong(5, part.getSize());
            insertQuery.setBinaryStream(6, inputStream);

            int result = insertQuery.executeUpdate();
            if (result == 1) {
                facesContext.addMessage("uploadForm:upload",
                        new FacesMessage(FacesMessage.SEVERITY_INFO,
                                part.getSubmittedFileName()
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

    public void validateFile(FacesContext ctx, UIComponent comp, Object value) {
        if (value == null) {
            throw new ValidatorException(
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Select a file to upload", null));
        }
        Part file = (Part) value;
        long size = file.getSize();
        if (size <= 0) {
            throw new ValidatorException(
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "the file is empty", null));
        }
        if (size > 1024 * 1024 * 10) { // 10 MB limit
            throw new ValidatorException(
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            size + "bytes: file too big (limit 10MB)", null));
        }
    }
}
