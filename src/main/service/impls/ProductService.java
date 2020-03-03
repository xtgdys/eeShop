package main.service.impls;

import main.dao.ProductDao;
import main.domain.*;
import main.utils.DataSourceUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ProductService {
    private final ProductDao dao = new ProductDao();

    public List<Product> findHotProduct() {
        return dao.findHotProduct();
    }

    public List<Product> findNewProduct() {
        return dao.findNewProduct();
    }

    public List<Category> findAllCategory() {
        return dao.findAllCategory();
    }

    /**
     * 封装一个PageBean
     *
     * @return 返回一个PageBean给web层
     */
    public PageBean findProductListByCid(String cid, int currentPage, int currentCount) {
        PageBean<Product> pageBean = new PageBean<>();

        int totalCount = dao.getProductCount(cid);
        int totalPage = (int) Math.ceil(1.0 * totalCount / currentCount);


        int index = (currentPage - 1) * currentCount;
        List<Product> list = dao.findProductByPage(cid, index, currentCount);

        // 封装
        pageBean.setCurrentPage(currentPage);
        pageBean.setCurrentCount(currentCount);
        pageBean.setTotalCount(totalCount);
        pageBean.setTotalPage(totalPage);
        pageBean.setList(list);

        return pageBean;
    }

    public Product findProductByPid(String pid) {
        return dao.findProductByPid(pid);
    }

    public void submitOrder(Order order) {
        try {
            DataSourceUtils.startTransaction();  //开启事务
            dao.addOrder(order);
            dao.addOrderItems(order.getOrderItems());

        } catch (SQLException e) {
            try {
                DataSourceUtils.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                DataSourceUtils.commitAndRelease();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateOrderAddr(Order order) {
        dao.updateOrderAddr(order);
    }

    public List<Order> findAllOrders(String uid) {
        return dao.findAllOrders(uid);
    }

    public List<Map<String, Object>> findAllOrderItemsByOid(String oid) {
        return dao.findAllOrderItemsByOid(oid);
    }
}
