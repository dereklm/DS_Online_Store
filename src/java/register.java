
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.DatatypeConverter;

@Named
@SessionScoped
public class register implements Serializable {

    @NotNull
    @Size(min = 3, message = "  At least 3 characters")
    private String name;

    @NotNull
    private String email;
    @NotNull
    @Size(min = 8, message = "   Password must be at least 8 characters.")
    private String password;
    private String passwordConf;
    @NotNull
    @Size(min = 10, max = 10, message = "   Must be 10 digits.")
    private String phone;

    private Boolean checkError = false;

    @Resource(name = "jdbc/db1")
    private DataSource ds;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConf() {
        return passwordConf;
    }

    public void setPasswordConf(String passwordConf) {
        this.passwordConf = passwordConf;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String registerCheck() throws SQLException, NoSuchAlgorithmException, UnsupportedEncodingException {
        if (!(getPassword().equals(getPasswordConf()))) {
            FacesMessage lastMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "   The password does not match", null);
            FacesContext.getCurrentInstance().addMessage("register:passwordConf", lastMsg);
            checkError = true;
        }

        if (!(getPhone().matches("[0-9]+"))) {
            FacesMessage lastMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "   Phone # Must include only digits", null);
            FacesContext.getCurrentInstance().addMessage("register:phone", lastMsg);
            checkError = true;
        }        
        
        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }

        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * from CUSTOMER where email = ?");
            ps.setString(1, getEmail());
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                FacesMessage lastMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "   The email address is already in use", null);
                FacesContext.getCurrentInstance().addMessage("register:email", lastMsg);
                checkError = true;
            }

            if (!checkError) {
                PreparedStatement is = conn.prepareStatement(
                        "INSERT into CUSTOMER(email, password, phone, realName)"
                        + "VALUES(?, ?, ?, ?)"
                );

                // insert customer data from database
                is.setString(1, getEmail());

                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(getPassword().getBytes("UTF-8"));
                String pw = DatatypeConverter.printHexBinary(hash);
                is.setString(2, pw);
                is.setString(3, getPhone());
                is.setString(4, getName());
                is.executeUpdate();

                PreparedStatement ms = conn.prepareStatement("insert into user_groups(username, groupname)"
                        + "values(?, ?)");
                ms.setString(1, getEmail());
                ms.setString(2, "usergroup");
                ms.executeUpdate();
            }
        } finally {
            conn.close();
            name = "";
            email = "";
            password = "";
            passwordConf = "";
            phone = "";
        }        
        if (!checkError) return "welcome";
        else return "register";
    }
}
