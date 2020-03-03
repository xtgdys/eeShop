package main.web.servlet;

import main.service.impls.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "ActiveServlet")
public class ActiveServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获得激活码
        String activeCode = request.getParameter("activeCode");

        UserService service = new UserService();
        service.active(activeCode);

        // 默认成功，跳转至登录页面
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }
}
