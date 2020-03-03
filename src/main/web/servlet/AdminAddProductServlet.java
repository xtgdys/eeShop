package main.web.servlet;

import main.domain.Category;
import main.domain.Product;
import main.service.impls.AdminServiceImpl;
import main.utils.CommonUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "AdminAddProductServlet", urlPatterns = "/adminAddProduct")
public class AdminAddProductServlet extends HttpServlet {
    /**
     * 收集表单数据，并封装成Product实体，同时将上传的图片保存在磁盘
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    @SuppressWarnings("unchecked")
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        Product product = new Product();
        Map<String, Object> map = new HashMap<>();
        try {
            List<FileItem> parseRequest = upload.parseRequest(request);
            for (FileItem item : parseRequest) {
                if (item.isFormField()) {
                    String fieldName = item.getFieldName();
                    String fieldValue = item.getString("UTF-8");
                    map.put(fieldName, fieldValue);
                } else {
                    String fileName = item.getName();
                    String path = this.getServletContext().getRealPath("upload");
                    InputStream in = item.getInputStream();
                    OutputStream out = new FileOutputStream(path + "/" + fileName);
                    IOUtils.copy(in, out);
                    map.put("pimage", "upload/" + fileName);
                    in.close();
                    out.close();
                    item.delete();
                }
            }
            BeanUtils.populate(product, map);
            product.setPid(CommonUtils.getUUID());
            product.setPdate(new Date());
            product.setPflag(1);
            Category category = new Category(map.get("cid").toString(), null);
            product.setCategory(category);

            AdminServiceImpl service = new AdminServiceImpl();
            service.addProduct(product);



        } catch (FileUploadException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

    }
}
