
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.servlet.http.Part;
import javax.sql.rowset.CachedRowSet;
import org.primefaces.model.UploadedFile;

@Named
@RequestScoped
public class manageItem implements Serializable {

    @Resource(name = "jdbc/db1")
    private DataSource ds;

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

    private UploadedFile file;

    private Boolean checkError = false;

    private String ITEM_ID;

    private List <String> images;
    
    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public String getITEM_ID() {
        return ITEM_ID;
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
            is.setString(1, getModel());
            is.setString(2, getBrand());
            is.setString(3, String.valueOf(getStock()));
            is.setString(4, getTitle());
            is.setString(5, getDescription());
            is.setString(6, String.valueOf(getPrice()));
            is.setString(7, s);
            is.executeUpdate();

        } finally {
            conn.close();
        }
        return "itemList";
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
        return "editItem" ;
    }

    public String removeFile(long s) throws SQLException {

        Connection conn = ds.getConnection();

        try {
            PreparedStatement deleteQuery = conn.prepareStatement(
                    "delete from FILESTORAGE where FILE_ID = ?"
            );

              deleteQuery.setString(1, String.valueOf(s));
            deleteQuery.executeUpdate();

        } finally {
            conn.close();
        }
        return "itemList";
    }

    public void uploadFile(long s) throws IOException, SQLException {

        FacesContext facesContext = FacesContext.getCurrentInstance();

        Connection conn = ds.getConnection();

        InputStream inputStream;
        inputStream = null;

        int count = 0;
        try {

            PreparedStatement countQuery = conn.prepareStatement(
                    "Select FILE_ID From FILESTORAGE Where ITEM_ID = ?"
            );
            countQuery.setLong(1, s);
            ResultSet outcome = countQuery.executeQuery();

            while (outcome.next()) {
                count++;
            }

            if (count < 5) {
                inputStream = file.getInputstream();
                PreparedStatement insertQuery = conn.prepareStatement(
                        "INSERT INTO FILESTORAGE (ITEM_ID, FILE_NAME, FILE_TYPE, FILE_SIZE, FILE_CONTENTS) "
                        + "VALUES (?, ?, ?, ?, ?)");

                insertQuery.setLong(1, s);
                insertQuery.setString(2, file.getFileName());
                insertQuery.setString(3, file.getContentType());
                insertQuery.setLong(4, file.getSize());
                insertQuery.setBinaryStream(5, inputStream);

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
            }
            else {
                   facesContext.addMessage("uploadForm:upload",
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                    "You can't uploade more than 5 pictures", null));
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
    
    /*
    public void processValidations() {
        FacesContext context = FacesContext.getCurrentInstance();
        UIInput fileUploadComponent = fileUploadsBean.getFileUploadComponent();
        if (fileUploadComponent!=null && !isFileUploaded()) {
            fileUploadComponent.setValid(false);
            context.addMessage(fileUploadComponent.getClientId(context), new FacesMessage(FacesMessage.SEVERITY_ERROR, messageSummary, messageDetail));
            context.validationFailed();
        }
    }
    */
}
