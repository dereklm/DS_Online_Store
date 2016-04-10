import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

@Named(value = "adminBean")
@SessionScoped
public class adminBean implements Serializable {

    @Resource(name = "jdbc/db1")
    private DataSource ds;
    
    public List<User> getUsers() throws SQLException {
        List<User> list = new ArrayList<>();
        
        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }
        
        try {
            PreparedStatement ps = conn.prepareStatement(""
                    + "select * from customer");
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if(!rs.getString("email").equals(FacesContext.
                        getCurrentInstance().getExternalContext().
                        getUserPrincipal().getName())) {
                    User u = new User();
                    u.setUsername(rs.getString("email"));
                    u.setRealName(rs.getString("realName"));
                    u.setBilling(rs.getString("billingAddress"));
                    u.setShipping(rs.getString("shippingAddress"));
                    u.setPhone(rs.getString("phone"));
                    list.add(u);
                }
            }
        } finally {
            conn.close();
        }
        
        return list;
    }
    
    public void deleteUser(String user) throws SQLException, IOException {
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
            ps.setString(1, user);
           
            ps.executeUpdate();
            
            ps = conn.prepareStatement(""
                    + "delete from user_groups where username = ?");
            ps.setString(1, user);
            
            ps.executeUpdate();
        } finally {
            conn.close();
        }
        
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.redirect(((HttpServletRequest) ec.getRequest()).getRequestURI());
    }
    
}
