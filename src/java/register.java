
import java.io.IOException;
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
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
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
    private String phone;
    
    private User user;

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

    public boolean checkLoginStatus() throws SQLException {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context.getExternalContext().getUserPrincipal() == null) {
            return false;
        } else {
            if (user != null) return true;
            else {
                user = new User();
                if (ds == null) {
                    throw new SQLException("ds is null; Can't get data source");
                }

                Connection conn = ds.getConnection();

                if (conn == null) {
                    throw new SQLException("conn is null; Can't get db connection");
                }

                try {
                    PreparedStatement ps = conn.prepareStatement("select * from customer where "
                            + "email = ?");
                    String em = FacesContext.getCurrentInstance().getExternalContext().
                            getUserPrincipal().getName();
                    ps.setString(1, em);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {                    
                        user.setUsername(em);
                        user.setPhone(rs.getString("phone"));
                        user.setBilling(rs.getString("billingAddress"));
                        user.setShipping(rs.getString("shippingAddress"));
                        user.setRealName(rs.getString("realName"));
                    }
                } finally {
                    conn.close();
                }                
                return true;
            }            
        }    
    }
    
    public User getUser() {
        return user;
    }
    
    public String getUserLogin() {
        return user.getRealName();
    }
    
    public void logout() throws IOException {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.invalidateSession();
        user = null;
        ec.redirect(ec.getRequestContextPath()+ "/faces/index.xhtml");
    }
    
    public void delete(String username) throws SQLException, IOException {
        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }
        
        try {
            PreparedStatement ps = conn.prepareStatement(""
                    + "delete from customer where email = ?");
            ps.setString(1, username);
           
            ps.executeUpdate();
            
            ps = conn.prepareStatement(""
                    + "delete from user_groups where username = ?");
            ps.setString(1, username);
            
            ps.executeUpdate();
        } finally {
            conn.close();
        }
        logout();
    }
    
    public String registerCheck() throws SQLException, NoSuchAlgorithmException, UnsupportedEncodingException {
        if (!(getPassword().equals(getPasswordConf()))) {
            FacesMessage lastMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "   The password does not match", null);
            FacesContext.getCurrentInstance().addMessage("register:passwordConf", lastMsg);
            passwordConf = "";
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
        }        
        if (!checkError) {
             password = "";
            passwordConf = "";
            return "welcome";
        }
        else {
            checkError = false;
            return "register";
        }
    }
    
    public void updateInfo() throws SQLException, NoSuchAlgorithmException, UnsupportedEncodingException {
        boolean pw = true;
        if (user.getPassword().equals("")) pw = false;
        
        if (user.getPassword() != null && !(user.getPassword().equals(user.getPasswordConf()))) {
            FacesMessage lastMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "   The password does not match", null);
            FacesContext.getCurrentInstance().addMessage("profile:passwordConf", lastMsg);
            passwordConf = "";
            checkError = true;
        }
        
        if (!(user.getPhone().matches("[0-9]+"))) {
            FacesMessage lastMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "   Phone # Must include only digits", null);
            FacesContext.getCurrentInstance().addMessage("profile:phone", lastMsg);
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
            if (!checkError) {
                if (pw) {
                    PreparedStatement ps = conn.prepareStatement("Update "
                            + "customer set password=?, realName=?"
                            + ", phone=?, billingAddress=?, shippingAddress=?"
                            + " where email=?");
                    MessageDigest digest = MessageDigest.getInstance("SHA-256");
                    byte[] hash = digest.digest(user.getPassword().getBytes("UTF-8"));
                    String pwString = DatatypeConverter.printHexBinary(hash);
                    ps.setString(1, pwString);
                    ps.setString(2, user.getRealName());
                    ps.setString(3, user.getPhone());
                    ps.setString(4, user.getBilling());
                    ps.setString(5, user.getShipping());
                    ps.setString(6, user.getUsername());
                    
                    ps.executeUpdate();
                } else {
                    PreparedStatement ps = conn.prepareStatement("Update "
                            + "Customer set realName=?"
                            + ", phone=?, billingAddress=?, shippingAddress=?"
                            + " where email=?");
                    ps.setString(1, user.getRealName());
                    ps.setString(2, user.getPhone());
                    ps.setString(3, user.getBilling());
                    ps.setString(4, user.getShipping());
                    ps.setString(5, user.getUsername());
                    
                    ps.executeUpdate();
                }
            }            
        } finally {
            conn.close();
            checkError = false;
            FacesMessage lastMsg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                "   Update Successful", null);
            FacesContext.getCurrentInstance().addMessage("profile:button", lastMsg);
        }  
    }
}
