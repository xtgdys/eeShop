package main.dao;

import main.domain.Category;
import main.domain.Order;
import main.domain.OrderItem;
import main.domain.Product;
import main.utils.DataSourceUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ProductDao {
    private final QueryRunner qr = new QueryRunner(DataSourceUtils.getDataSource());

    public List<Product> findHotProduct() {
        String sql = "SELECT * FROM product WHERE is_hot=? LIMIT ?,?";
        List<Product> products = getData(sql);
        return products;
    }

    public List<Product> findNewProduct() {
        String sql = "SELECT * FROM product ORDER BY pdate DESC LIMIT ?,?";
        List<Product> products = null;
        try {
            products = qr.query(sql, new BeanListHandler<>(Product.class), 0, 9);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    private List<Product> getData(String sql) {
        List<Product> products = null;
        try {
            products = qr.query(sql, new BeanListHandler<>(Product.class), 1, 0, 9);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public List<Category> findAllCategory() {
        String sql = "SELECT * FROM category";
        List<Category> categories = null;
        try {
            categories = qr.query(sql, new BeanListHandler<>(Category.class));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    public int getProductCount(String cid) {
        String sql = "SELECT COUNT(*) FROM product WHERE cid=?";
        Long count = null;
        try {
            count = (Long) qr.query(sql, new ScalarHandler(), cid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        assert count != null;
        return count.intValue();
    }

    public List<Product> findProductByPage(String cid, int index, int currentCount) {
        String sql = "SELECT * FROM product WHERE cid=? LIMIT ?,?";
        List<Product> list = null;
        try {
            list = qr.query(sql, new BeanListHandler<>(Product.class), cid, index, currentCount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Product findProductByPid(String pid) {
        String sql = "SELECT * FROM product WHERE pid=?";
        Product product = null;
        try {
            product = qr.query(sql, new BeanHandler<>(Product.class), pid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return product;
    }

    public void addOrder(Order order) {
        try {
            QueryRunner runner = new QueryRunner();
            String sql = "INSERT INTO orders VALUES(?,?,?,?,?,?,?,?)";
            Connection conn = DataSourceUtils.getConnection();
            Object[] obj = {order.getOid(), order.getOrdertime(), order.getTotal(), order.getState(),
                    order.getAddress(), order.getName(), order.getTelephone(), order.getUser().getUid()};
            runner.update(conn, sql, obj);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addOrderItems(List<OrderItem> orderItems) {
        try {
            QueryRunner runner = new QueryRunner();
            String sql = "INSERT INTO orders VALUES(?,?,?,?,?)";
            Connection conn = DataSourceUtils.getConnection();
            // 使用for循环，把所有orderItem加入到数据库
            for (OrderItem orderItem : orderItems) {
                Object[] obj = {orderItem.getItemid(), orderItem.getCount(), orderItem.getSubtotal(),
                        orderItem.getProduct().getPid(), orderItem.getOrder().getOid()};
                runner.update(conn, sql, obj);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateOrderAddr(Order order) {
        String sql = "UPDATE orders SET address=?,name=?,telephone=? WHERE oid=?";
        try {
            qr.update(sql, order.getAddress(), order.getName(),
                    order.getTelephone(), order.getOid());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Order> findAllOrders(String uid) {
        String sql = "SELECT * FROM orders WHERE uid=?";
        List<Order> orderList = null;
        try {
            orderList = qr.query(sql, new BeanListHandler<>(Order.class), uid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderList;
    }

    public List<Map<String, Object>> findAllOrderItemsByOid(String oid) {
        String sql = "SELECT  i.count,i.subtotal,p.pimage,p.pname,p.shop_price" +
                " FROM orderitem i, product p WHERE i.pid=p.pid AND i.oid=?";
        List<Map<String, Object>> mapList = null;
        try {
            mapList = qr.query(sql, new MapListHandler(), oid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mapList;
    }
}
