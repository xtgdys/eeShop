package main.service.interfaces;

import main.domain.Category;
import main.domain.Order;
import main.domain.Product;
import main.domain.User;

import java.util.List;
import java.util.Map;

public interface AdminService {
    User login(String username, String password);

    void addCategory(String cname);

    List<Category> showCategories();

    List<Product> showProducts();

    void deleteCategory(String cid);

    void deleteProduct(String pid);

    void addProduct(Product product);

    List<Order> showOrders();

    List<Map<String, Object>> findOrderInfoByOid(String oid);
}
