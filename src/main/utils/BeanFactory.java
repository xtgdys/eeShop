package main.utils;

import org.apache.commons.beanutils.BeanUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public class BeanFactory {
    public static Object getBean(String id) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {

        // 根据配置文件中的清单生产对象，使用dom4j的xml解析技术
        SAXReader reader = new SAXReader();
        String path = Objects.requireNonNull(BeanFactory.class.getClassLoader().getResource("bean.xml")).getPath();
        Document doc = null;
        try {
            doc = reader.read(path);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        assert doc != null;
        Element element = (Element) doc.selectSingleNode("//bean[@id='" + id + "']");
        String className = element.attributeValue("class");

        Class clazz = Class.forName(className);

        return clazz.getDeclaredConstructor().newInstance();
    }
}
