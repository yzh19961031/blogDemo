package com.yzh;


import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Test;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

/**
 * xml注入测试代码
 *
 * @author Y
 * @since 2020/8/29
 */
public class XmlTest {

    @Test
    public void testDom4j() {
        String replace = "user1</user><user role=\"admin\">user2";
        Document document = DocumentHelper.createDocument();
        Element user = document.addElement("user");
        user.addAttribute("role","guest");
        user.setText(replace);
        String xml = document.asXML();
        System.out.println(xml);
    }

    @Test
    public void testTrans() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<user role=\"guest\">%s</user>";
        String replace = "user1</user><user role=\"admin\">user2";
        String format = String.format(xml, xmlConversion(replace));
        System.out.println(format);
    }

    /**
     * 转义xml中不支持的特殊字符
     *
     * @param strXml
     * @return
     */
    private String xmlConversion(String strXml){
        String conversionStr = "";
        if(strXml == null){
            return null;
        }
        conversionStr = strXml.replaceAll("&","&amp;").replaceAll("<","&lt;").replaceAll(">","&gt;").
                replaceAll("'","&apos;").replaceAll("\"","&quot;");
        return conversionStr;
    }

    @Test
    public void testXXE1() throws DocumentException {
        File file = new File("d://xml//demo.xml");
        SAXReader reader = new SAXReader();
        Document document = reader.read(file);
        Element rootElement = document.getRootElement();
        String lastname = rootElement.element("lastname").getText();
        System.out.println("get the password "+ lastname);// 这边代码会输出password.txt文件里面的内容
    }


    @Test
    public void testXXE2() throws DocumentException, SAXException {
        File file = new File("d://xml//demo.xml");
        SAXReader reader = new SAXReader();
        // 禁止解析DTDS
        reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true); //禁止包含doctype
        reader.setFeature("http://xml.org/sax/features/external-general-entities", false); //禁止外部实体解析
        reader.setFeature("http://xml.org/sax/features/external-parameter-entities", false); //禁止外部参数解析
        Document document = reader.read(file);
        Element rootElement = document.getRootElement();
        String lastname = rootElement.element("lastname").getText();
        System.out.println("get the password "+lastname);
    }

    @Test
    public void testXXE3() throws DocumentException, SAXException {
        File file = new File("d://xml//demo.xml");
        SAXReader reader = new SAXReader();
        // 禁止解析DTDS
        reader.setFeature("http://xml.org/sax/features/external-general-entities", false); //禁止外部实体解析
        reader.setFeature("http://xml.org/sax/features/external-parameter-entities", false); //禁止外部参数解析
        Document document = reader.read(file);
        Element rootElement = document.getRootElement();
        String lastname = rootElement.element("lastname").getText();
        System.out.println("get the password "+lastname);
    }

    // 自定义外部实体处理
    private class CustomResolver implements EntityResolver {
        // 自定义的一个白名单
        String whitePath = "file:///d:/xml/password.txt";

        @Override
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            if (systemId.equals(whitePath)) {
                System.out.println("Resolving entity: " + publicId + " " + systemId);
                return new InputSource(whitePath);
            } else {
                // 解析输入恶意的xml内容时，返回空
                return new InputSource();
            }
        }
    }

    @Test
    public void testXXE() throws DocumentException, SAXException {
        File file = new File("d://xml//demo.xml");
        SAXReader reader = new SAXReader();
        // 设置自定义的外部实体处理
        reader.setEntityResolver(new CustomResolver());
        Document document = reader.read(file);
        Element rootElement = document.getRootElement();
        String lastname = rootElement.element("lastname").getText();
        System.out.println("get the password "+lastname);
    }

    @Test
    public void testXmlDos() throws SAXException, DocumentException {
        File file = new File("d://xml//dos.xml");
        SAXReader reader = new SAXReader();
        // 这边设置实体个数不超过10000个
        reader.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
        Document document = reader.read(file);
        String xml = document.asXML();
        System.out.println(xml);
    }


}
