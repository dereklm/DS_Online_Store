
import java.io.Serializable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


public class User implements Serializable {
    private String username;
    
    @NotNull
    @Size(min = 3, message = "  At least 3 characters")
    private String realName;
    
    @NotNull
    @Size(min = 10, max = 10, message = "   Must be 10 digits.")
    private String phone;
    private String password;  
    private String passwordConf;
    private String billing;
    private String shipping;

    public String getPassword() {
        return password;
    }

    public String getPasswordConf() {
        return passwordConf;
    }

    public void setPasswordConf(String passwordConf) {
        this.passwordConf = passwordConf;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBilling() {
        return billing;
    }

    public void setBilling(String billing) {
        this.billing = billing;
    }

    public String getShipping() {
        return shipping;
    }

    public void setShipping(String shipping) {
        this.shipping = shipping;
    }
    
}
