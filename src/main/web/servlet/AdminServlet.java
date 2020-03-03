package main.web.servlet;

import com.google.gson.Gson;
import main.domain.*;
import main.service.impls.AdminServiceImpl;
import main.service.interfaces.AdminService;
import main.utils.BeanFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "AdminServlet", urlPatterns = "/admin")
public class AdminServlet extends BaseServlet {

    private AdminService service;
    {
        try {
            service = (AdminService) BeanFactory.getBean("adminService");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();

        }
    }


    public void login(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        User user = service.login(username, password);

        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("user", username);
            response.sendRedirect(request.getContextPath() + "/admin/home.jsp");
        } else {
            request.setAttribute("loginError", "用户名或密码错误");
            request.getRequestDispatcher("/admin/index.jsp").forward(request, response);
        }
    }

    public void addCategory(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String cname = request.getParameter("cname");
        service.addCategory(cname);
    }

    public void showCategories(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        List<Category> categories = service.showCategories();
        request.setAttribute("categories", categories);
        request.getRequestDispatcher("/admin/category/list.jsp").forward(request, response);

    }

    public void findAllCategories(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        List<Category> categories = service.showCategories();
        Gson gson = new Gson();
        String json = gson.toJson(categories);
        response.setContentType("text/json; charset=UTF-8");
        response.getWriter().write(json);
    }

    public void showProducts(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        List<Product> products = service.showProducts();
        request.setAttribute("products", products);
        request.getRequestDispatcher("/admin/product/list.jsp").forward(request, response);
    }

    public void showOrders(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        List<Order> orders = service.showOrders();
        request.setAttribute("orders", orders);
        request.getRequestDispatcher("/admin/order/list.jsp").forward(request, response);
    }

    public void deleteCategory(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String cid = request.getParameter("cid");
        service.deleteCategory(cid);
        response.sendRedirect(request.getContextPath() + "/admin/home.jsp");
    }

    public void deleteProduct(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pid = request.getParameter("pid");
        service.deleteProduct(pid);
    }

    public void findOrderInfoByOid(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String oid = request.getParameter("oid");
        List<Map<String, Object>> maps = service.findOrderInfoByOid(oid);
        Gson gson = new Gson();
        String json = gson.toJson(maps);

        response.setContentType("text/html; charset=UTF-8");
        response.getWriter().write(json
        );
    }
}
