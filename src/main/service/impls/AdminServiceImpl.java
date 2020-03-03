package main.service.impls;

import main.dao.AdminDao;
import main.domain.Category;
import main.domain.Order;
import main.domain.Product;
import main.domain.User;
import main.service.interfaces.AdminService;

import java.util.List;
import java.util.Map;

public class AdminServiceImpl implements AdminService {
    private final AdminDao dao = new AdminDao();

    public User login(String username, String password) {
        return dao.login(username, password);
    }

    public void addCategory(String cname) {
        dao.addCategory(cname);
    }

    public List<Category> showCategories() {
        return dao.showCategories();
    }

    public List<Product> showProducts() {
        return dao.showProducts();
    }

    public void deleteCategory(String cid) {
        dao.deleteCategory(cid);
    }

    public void deleteProduct(String pid) {
        dao.deleteProduct(pid);
    }

    public void addProduct(Product product) {
        dao.addProduct(product);
    }

    public List<Order> showOrders() {
        return dao.showOrders();
    }

    public List<Map<String, Object>> findOrderInfoByOid(String oid) {
        return dao.findOrderInfoByOid(oid);
    }
}
