/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.inject.Named;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.sql.DataSource;

/**
 *
 * @author Derek
 */
@Named(value = "searchBean")
@SessionScoped
public class searchBean implements Serializable {
    @Resource(name = "jdbc/db1")
    private DataSource ds;
    
    private Item item;
    
    private String searchTxt;
    
    private List<Item> searchResults;
    
    @PostConstruct
    public void postInit() {
        searchResults = new ArrayList<>();
    }
    
    public List<String> autoComplete(String query) throws SQLException {
        List<String> results = new ArrayList<>();
        
        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }
        
        try {
            PreparedStatement ps = conn.prepareStatement("select title from item"
                    + " where upper(title) like upper('%"+ query + "%')");
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                results.add(rs.getString("title"));
            }
        } finally {
            conn.close();
        }
        
        return results;
    }
    
    public void search() throws SQLException {
        searchResults.clear();
        
        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }
        
        try {
            PreparedStatement ps = conn.prepareStatement("select * from item"
                    + " where upper(title) like upper('%"+ searchTxt + "%')");
            
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                Item i = new Item();
                i.setITEM_ID(rs.getLong("ITEM_ID"));
                i.setTitle(rs.getString("title"));
                i.setPrice(rs.getDouble("price"));
                i.setBrand(rs.getString("brand"));
                i.setStock(rs.getInt("stock"));
                i.setDescription(rs.getString("description"));
                i.setModel(rs.getString("model"));
                searchResults.add(i);
            }
        } finally {
            conn.close();
        }
    }
    
    public void itemDetails(Item i) throws IOException {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        this.item = i;        
        ec.redirect(ec.getRequestContextPath()+ "/faces/itemInfo.xhtml");
    }

    public String getSearchTxt() {
        return searchTxt;
    }

    public void setSearchTxt(String searchTxt) {
        this.searchTxt = searchTxt;
    }

    public List<Item> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(List<Item> searchResults) {
        this.searchResults = searchResults;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
    
}
