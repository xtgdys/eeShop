package main.web.servlet;

import main.domain.User;
import main.utils.MailUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import main.service.impls.UserService;
import main.utils.CommonUtils;

import javax.mail.MessagingException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class RegisterServlet extends javax.servlet.http.HttpServlet {
    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // 获得表单数据
        Map<String, String[]> properties = request.getParameterMap();
        User user = new User();

        try {

            ConvertUtils.register(new Converter() {
                @Override
                public Object convert(Class aClass, Object o) {
                    // String -> Date
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = null;
                    try {
                        date = format.parse(o.toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return date;
                }
            }, Date.class);

            // 映射封装
            BeanUtils.populate(user, properties);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        // 补充user
        user.setUid(CommonUtils.getUUID());
        user.setTelephone(null);
        user.setState(0);
        user.setCode(CommonUtils.getUUID());

        // 将user传递给service层
        UserService service = new UserService();
        boolean isRegisterSuccess = service.regist(user);

        if (isRegisterSuccess) {
            // 发送激活邮件
            String activeCode = user.getCode();
            String msg = "这是一封激活邮件，请" +
                    "<a href='http://localhost:8080/eShop" +
                    "/active?activeCode=" + activeCode + "'>点击" +
                    "</a>";
            try {
                MailUtils.sendMail(user.getEmail(),msg);
            } catch (MessagingException e) {
                e.printStackTrace();
            }

            // 注册成功，跳转至成功页面
            response.sendRedirect(request.getContextPath() + "/registerSuccess.jsp");
        } else {
            // 注册失败，跳转至失败页面
            response.sendRedirect(request.getContextPath() + "/registerFail.jsp");
        }


    }
}
