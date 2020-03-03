package main.web.servlet;

import com.google.gson.Gson;
import main.domain.*;
import main.service.impls.ProductService;
import main.utils.CommonUtils;
import main.utils.JedisPoolUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import redis.clients.jedis.Jedis;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@WebServlet(name = "ProductServlet", urlPatterns = "/product")
public class ProductServlet extends BaseServlet {
    private final ProductService service = new ProductService();

    /**
     * 从缓存中获取categoryList，如果不存在，从数据库获取并保存于缓存
     */
    public void categoryList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ProductService service = new ProductService();

        // 1. 获得jedis对象，连接redis数据库
        Jedis jedis = JedisPoolUtils.getJedis();
        String categoryListJson = jedis.get("categoryListJson");

        // 2. 判断categoryListJson是否为空
        if (categoryListJson == null) {
            List<Category> categories = service.findAllCategory();
            Gson gson = new Gson();
            categoryListJson = gson.toJson(categories);
            jedis.set("categoryListJson", categoryListJson);
        }

        // 3.返回结果
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(categoryListJson);

    }

    /**
     * 显示首页
     */
    public void index(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //准备热门商品
        List<Product> hotProducts = service.findHotProduct();

        //准备最新商品
        List<Product> newProducts = service.findNewProduct();

        request.setAttribute("newProductList", newProducts);

        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }


    /**
     * 显示商品的详细信息
     */
    public void productInfo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获得pid
        String pid = request.getParameter("pid");

        // 获得当前页
        String currentPage = request.getParameter("currentPage");

        // 获得商品类别
        String cid = request.getParameter("cid");
        Product product = service.findProductByPid(pid);

        // 获得客户端携带的cookie 获得名字是pids的cookie
        String pidHistory = pid;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("pidHistory".equals(cookie.getName())) {
                    pidHistory = cookie.getValue();
                    String[] split = pidHistory.split("-");
                    LinkedList<String> list = new LinkedList(Arrays.asList(split));
                    if (list.contains(pid)) {  // 判断集合中是否存在当前pid
                        list.remove(pid);
                        list.addFirst(pid);
                    } else {
                        list.addFirst(pid);
                    }
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < list.size() && i <= 6; i++) {
                        sb.append(list.get(i));
                        sb.append("-");
                    }
                    pidHistory = sb.substring(0, sb.length() - 1);

                }
            }
        }
        Cookie newCookie = new Cookie("pidHistory", pidHistory);
        response.addCookie(newCookie);

        request.setAttribute("product", product);
        request.setAttribute("cid", cid);
        request.setAttribute("currentPage", currentPage);
        request.getRequestDispatcher("/product_info.jsp").forward(request, response);

    }

    /**
     * 根据cid获取产品的列表
     */
    public void productList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获得Cid
        String currentPageStr = request.getParameter("currentPage");
        if (currentPageStr == null) currentPageStr = "1";

        int currentPage = Integer.parseInt(currentPageStr);
        int currentCount = 12;

        String cid = request.getParameter("cid");
        PageBean pageBean = service.findProductListByCid(cid, currentPage, currentCount);

        request.setAttribute("pageBean", pageBean);
        request.setAttribute("cid", cid);

        List<Product> historyProductList = new ArrayList<>();

        // 获取历史记录
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("pidHistory".equals(cookie.getName())) {
                    String pidHistory = cookie.getValue();
                    String[] pids = pidHistory.split("-");
                    for (String pid : pids) {
                        Product product = service.findProductByPid(pid);
                        historyProductList.add(product);
                    }
                }
            }
        }
        request.setAttribute("historyProductList", historyProductList);

        request.getRequestDispatcher("/product_list.jsp").forward(request, response);
    }

    /**
     * 将商品加入到购物车
     */
    public void addProductToCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();

        String pid = request.getParameter("pid");        // 获取pid
        int buyNum = Integer.parseInt(request.getParameter("buyNum"));  // 获取购买数量


        // 判断session中是否已有购物车
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {  // 如果session中没有购物车，创建一个新的Cart
            cart = new Cart();
        }

        int newNum = 0;
        int oldNum = 0;
        if (cart.getCartItems().containsKey(pid)) {    // 如果购物车中已存在该商品, 累加购买总数
            oldNum = cart.getCartItems().get(pid).getBuyNum();
            newNum = oldNum + buyNum;
        } else {
            newNum = buyNum;
        }

        Product product = service.findProductByPid(pid);    // 获得Product对象
        double subtotal = product.getShop_price() * newNum;
        CartItem item = new CartItem(product, newNum, subtotal);

        cart.getCartItems().put(pid, item);    // 将CartItem放入购物车
        double total = cart.getTotal() + subtotal - oldNum * product.getShop_price();
        cart.setTotal(total);

        session.setAttribute("cart", cart);  // 将cart放回session

        // 直接跳转至购物车页面
        response.sendRedirect(request.getContextPath() + "/cart.jsp");
    }


    public void delProFromCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pid = request.getParameter("pid");

        HttpSession session = request.getSession();
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart != null) {
            cart.setTotal(cart.getTotal() - cart.getCartItems().get(pid).getSubtotal());
            cart.getCartItems().remove(pid);  // 删除指定商品
        }


        session.setAttribute("cart", cart);
        response.sendRedirect(request.getContextPath() + "/cart.jsp");
    }

    public void clearCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("cart") != null)
            session.removeAttribute("cart");

        response.sendRedirect(request.getContextPath() + "/cart.jsp");
    }

    /**
     * 提交订单
     * 把购物车中商品封装成为一个Order对象，传递给service层
     */
    public void submitOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        // private User user;//该订单属于哪个用户
        User user = (User) session.getAttribute("user");
        if (session.getAttribute("user") == null) { // 判断用户是否已登录，否则要求用户登录
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // private String oid;//该订单的订单号
        String oid = CommonUtils.getUUID();

        // private Date ordertime;//下单时间
        Date date = new Date();

        // private double total;//该订单的总金额
        Cart cart = (Cart) session.getAttribute("cart");
        double total = cart.getTotal();

        // private int state;//订单支付状态 1代表已付款 0代表未付款
        int state = 0;

        // private String address;//收货地址
        String address = null;

        // private String name;//收货人
        String name = null;

        // private String telephone;//收货人电话
        String telephone = null;

        // private User user;//该订单属于哪个用户
        //user;

        Order order = new Order(oid, date, total, state, address, name, telephone, user, null);

        // private List<OrderItem> orderItems = new ArrayList<>();
        List<OrderItem> orderItems = new ArrayList<>();

        Map<String, CartItem> cartItems = cart.getCartItems();
        for (Map.Entry<String, CartItem> entry : cartItems.entrySet()) {
            CartItem cartItem = entry.getValue();  // 获取cartItem对象
            OrderItem orderItem = new OrderItem(CommonUtils.getUUID(), cartItem.getBuyNum(),
                    cartItem.getSubtotal(), cartItem.getProduct(), order);
            orderItems.add(orderItem);
        }

        order.setOrderItems(orderItems);  // Order对象封装完成

        service.submitOrder(order);
        session.setAttribute("order", order);

        response.sendRedirect(request.getContextPath() + "/order_info.jsp");
    }

    public void confirmOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 更新收货人信息
        Map<String, String[]> parameterMap = request.getParameterMap();
        Order order = new Order();
        try {
            BeanUtils.populate(order, parameterMap);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        service.updateOrderAddr(order);
    }

    public void myOrders(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (session.getAttribute("user") == null) { // 判断用户是否已登录，否则要求用户登录
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        // 查询该用户所有的订单
        List<Order> orders = service.findAllOrders(user.getUid());
        // 循环所有订单，为每个订单填充List<OrderItem>;
        if (orders != null) {
            for (Order order : orders) {
                String oid = order.getOid();
                // 查询该订单所有的相关项目，并把mapList转换为OrderItemList
                List<Map<String, Object>> mapList = service.findAllOrderItemsByOid(oid);

                for (Map<String, Object> map : mapList) {
                    OrderItem orderItem = new OrderItem();
                    Product product = new Product();
                    try {
                        BeanUtils.populate(orderItem, map);
                        BeanUtils.populate(product, map);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    orderItem.setProduct(product);
                    order.getOrderItems().add(orderItem);
                }
            }
        }
        request.setAttribute("orderList", orders);
        request.getRequestDispatcher("/order_list.jsp").forward(request, response);
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        session.removeAttribute("user");

        Cookie cookie_username = new Cookie("cookie_username", "");
        cookie_username.setMaxAge(0);
        //创建存储密码的cookie
        Cookie cookie_password = new Cookie("cookie_password", "");
        cookie_password.setMaxAge(0);

        response.addCookie(cookie_username);
        response.addCookie(cookie_password);

        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }

    @SuppressWarnings("unchecked")
    public void fileUpload(HttpServletRequest request, HttpServletResponse response) throws FileUploadException, IOException {
        DiskFileItemFactory factory = new DiskFileItemFactory();

        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setHeaderEncoding("UTF-8");

        List<FileItem> parseRequest = upload.parseRequest(request);

        for (FileItem fileItem : parseRequest) {
            if (fileItem.isFormField()) {
                String fieldName = fileItem.getFieldName();
                String fieldValue = fileItem.getString("UTF_8");
                System.out.println(fieldName + ":" + fieldValue);
            } else {
                String fileName = fileItem.getName();
                InputStream inputStream = fileItem.getInputStream();
                String path = getServletContext().getRealPath("upload");
                OutputStream outputStream = new FileOutputStream(path + "/" + fileName);

                IOUtils.copy(inputStream, outputStream);
                inputStream.close();
                outputStream.close();
            }
        }
    }
}















