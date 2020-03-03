package main.dao;

import main.domain.Category;
import main.domain.Order;
import main.domain.Product;
import main.domain.User;
import main.utils.CommonUtils;
import main.utils.DataSourceUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class AdminDao {
    private final QueryRunner qr = new QueryRunner(DataSourceUtils.getDataSource());

    public User login(String username, String password) {
        String sql = "SELECT * FROM user WHERE username=? AND password=?";
        User user = null;
        try {
            user = qr.query(sql, new BeanHandler<>(User.class), username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public void addCategory(String cname) {
        String sql = "INSERT INTO category VALUES (?, ?)";
        try {
            qr.update(sql, CommonUtils.getUUID(), cname);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Category> showCategories() {
        String sql = "SELECT * FROM category ORDER BY cid";
        try {
            return qr.query(sql, new BeanListHandler<>(Category.class));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Product> showProducts() {
        String sql = "SELECT * FROM product ORDER BY pid";
        try {
            return qr.query(sql, new BeanListHandler<>(Product.class));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteCategory(String cid) {
        String sql = "DELETE FROM category WHERE cid=?";
        try {
            qr.update(sql, cid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteProduct(String pid) {
        String sql = "DELETE FROM product WHERE pid=?";
        try {
            qr.update(sql, pid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addProduct(Product product) {
        String sql = "INSERT INTO product VALUES (?,?,?,?,?,?,?,?,?,?)";
        Object[] obj = {product.getPid(), product.getPname(), product.getMarket_price(),
                product.getShop_price(), product.getPimage(), product.getPdate(), product.getIs_hot(),
                product.getPdesc(), product.getPflag(), product.getCategory().getCid()};
        try {
            qr.update(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Order> showOrders() {
        String sql = "SELECT * FROM orders";
        List<Order> orders = null;
        try {
            orders = qr.query(sql, new BeanListHandler<>(Order.class));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public List<Map<String, Object>> findOrderInfoByOid(String oid) {
        String sql = "SELECT p.pimage, p.pname, p.shop_price, i.count, i.subtotal" +
                " FROM orderitem i, product p WHERE i.pid=p.pid AND i.oid=?";
        try {
            return qr.query(sql, new MapListHandler(), oid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}





























