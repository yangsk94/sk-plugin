package com.sk.plugin.task;

import org.gradle.api.DefaultTask;
import org.gradle.api.Task;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import groovy.lang.Closure;

/**
 * @author ysk
 * @class describe  {@link #}
 * @time 2022/1/19 5:03 下午
 */
public class CheckTask extends DefaultTask {


    @Override
    public Task doLast(Closure action) {
        checkJava();

        System.out.println("\n");
        System.out.println("\n");

        checkString();

        System.out.println("\n");
        System.out.println("\n");

        checkImage();

        System.out.println("\n");
        System.out.println("\n");
        return super.doLast(action);

    }

    private void checkJava() {
        ArrayList<String> excludeFiles = new ArrayList<>();
        excludeFiles.add("ImageLoader.kt");
        excludeFiles.add("CommonLayoutManager.java");
        excludeFiles.add("CommonGridLayoutManager.java");
        excludeFiles.add("ToastCompat.kt");
        excludeFiles.add("CompatTextView.kt");
        excludeFiles.add("NumberUtil.kt");
        excludeFiles.add("SearchFragment.kt");
        excludeFiles.add("Logger.kt");
        excludeFiles.add("user_splash_svg.xml");
        excludeFiles.add("BaseCommonPopupWindow.java");
        excludeFiles.add("common_shadow_border.xml");
        excludeFiles.add("shadow_notification_bottom.xml");
        excludeFiles.add("AndroidManifest.xml");
        excludeFiles.add("ic_camera.xml");
        excludeFiles.add("user_spalsh_svg.xml");

        LinkedHashMap<Integer, ArrayList<File>> listHashMap = new LinkedHashMap<>();

        StringBuilder rootDir = new StringBuilder().append(getProject().getRootDir()).append(File.separator).append("app").
                append(File.separator).append("src").append(File.separator).append("main");

        checkContent(new File(rootDir.toString()), listHashMap, excludeFiles);

        if (listHashMap.size() > 0) {

            String start = "";
            String end = "";

            for (Map.Entry<Integer, ArrayList<File>> entry : listHashMap.entrySet()) {
                int key = entry.getKey();
                ArrayList<File> value = entry.getValue();

                System.out.println("key:" + entry.getKey() + "   value:" + entry.getValue());

                if (1 == key) {
                    start = "you have already used system ImageView !!!";
                    end = "please use custom ImageView !!!";
                } else if (2 == key) {
                    start = "you should avoid directly use new Handler();.postXXX method !!!";
                    end = "please use instance of Handler !!!";
                } else if (3 == key) {
                    start = "you have already used system Toast !!!";
                    end = "please use custom ToastCompat !!!";
                } else if (4 == key) {
                    start = "you maybe directly use getResources(); in Fragment !!!";
                    end = "please use getContext();.getResources(); method !!!";
                } else if (5 == key) {
                    start = "you maybe directly use string resource !!!";
                    end = "please use string.xml resource !!!";
                } else if (6 == key) {
                    start = "you maybe directly use android.support.v7.widget.LinearLayoutManager !!!";
                    end = "please use custom CommonLayoutManager !!!";
                } else if (7 == key) {
                    start = "you maybe directly use android.support.v7.widget.GridLayoutManager !!!";
                    end = "please use custom CommonGridLayoutManager !!!";
                } else if (8 == key) {
                    start = "you maybe directly use ImageView.setImageResource(0); !!!";
                    end = "please use ImageView.setImageDrawable(null); method !!!";
                } else if (9 == key) {
                    start = "you maybe directly use Integer.parseInt(); or Long.parseLong();!!!";
                    end = "please use NumberUtil.parseInt(); or NumberUtil.parseLong(); method !!!";
                } else if (10 == key) {
                    start = "you have already used system android.support.v7.widget.RecyclerView !!!";
                    end = "please use custom MultiTypeRecyclerView !!!";
                } else if (11 == key) {
                    start = "you have already used system TextView !!!";
                    end = "please use custom CompatTextView !!!";
                } else if (12 == key) {
                    start = "you have already used system Logger !!!";
                    end = "please use custom com.utils.Logger !!!";
                } else if (13 == key) {
                    start = "you have already used system dimens !!!";
                    end = "please use custom dimens.xml !!!";
                } else if (14 == key) {
                    start = "you have already used hard coded(#......); colors !!!";
                    end = "please use custom colors.xml !!!";
                }

                if (value.size() > 0) {
                    System.err.println("==================" + start + "==================");

                    for (File file : value) {
                        String s = file.getAbsolutePath().substring(rootDir.toString().length() - 5);
                        System.err.println(s);
                    }

                    System.err.println("\n");
                    System.err.println("\n");

                    throw new RuntimeException(end);
                }
            }


        }
    }

    public static ArrayList<String> toArrayByRandomAccessFile(String name) {
        // 使用ArrayList来存储每行读取到的字符串
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            File file = new File(name);
            RandomAccessFile fileR = new RandomAccessFile(file, "r");
            // 按行读取字符串
            String str = null;
            while ((str = fileR.readLine()) != null) {
                arrayList.add(str);
            }
            fileR.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    private void checkContent(File file, LinkedHashMap<Integer, ArrayList<File>> listHashMap, ArrayList<String> excludeFiles) {
        if (null == file) {
            return;
        }

        if (file.isDirectory()) {
            if (file.listFiles() != null && file.listFiles().length > 0)
                for (File f : file.listFiles()) {
                    checkContent(f, listHashMap, excludeFiles);
                }
        } else {
            if (excludeFiles.contains(file.getName())) {
                return;
            }
            if (file.getName().endsWith(".xml") || file.getName().endsWith(".java") || file.getName().endsWith(".kt")) {

                ArrayList<String> arr = toArrayByRandomAccessFile(file.getName());
                for (String line : arr) {
                    if (line != null) {

                        if (line.trim().startsWith("<ImageView") || line.trim().
                                startsWith("import android.widget.ImageView")) {
                            checkContent(file, listHashMap, 1);
                        }

                        if (line.trim().startsWith("new Handler()")) {
                            checkContent(file, listHashMap, 2);
                        }

                        if (line.trim().contains("Toast.makeText")) {
                            checkContent(file, listHashMap, 3);
                        }

                        if (line.trim().contains("getResources()") && (file.getName().endsWith("ment.java") ||
                                file.getName().endsWith("ment.kt"))) {
                            String subString = line.substring(0, line.indexOf("getResources()")).trim();
                            if (!subString.endsWith(".")) {
                                checkContent(file, listHashMap, 4);
                            }
                        }

                        if (isContainChinese(line.trim(), file)) {
                            checkContent(file, listHashMap, 5);
                        }

                        if (line.trim().startsWith("import android.support.v7.widget.LinearLayoutManager")) {
                            checkContent(file, listHashMap, 6);
                        }

                        if (line.trim().startsWith("import android.support.v7.widget.GridLayoutManager")) {
                            checkContent(file, listHashMap, 7);
                        }

                        if (line.trim().contains("setImageResource(0);")) {
                            checkContent(file, listHashMap, 8);
                        }

                        if (line.trim().contains("Integer.parseInt") || line.trim().contains("Long.parseLong")) {
                            checkContent(file, listHashMap, 9);
                            System.err.println(line.trim() + "     " + file.getAbsolutePath());
                        }

                        if (line.trim().contains("<android.support.v7.widget.RecyclerView") || line.trim().
                                contains("<androidx.recyclerview.widget.RecyclerView")) {
                            checkContent(file, listHashMap, 10);
                        }

                        if ((line.trim().startsWith("<TextView") || line.trim().
                                startsWith("import android.widget.TextView")))
                            checkContent(file, listHashMap, 11);

                        if (isContainLog(line.trim()))
                            checkContent(file, listHashMap, 12);

//                    if (file.name.endsWith(".xml"); && isXmlUseDimens(line.trim();););
//                        checkContent(file, listHashMap, 13);
//
//                    if (isUseColors(line.trim();););
//                        checkContent(file, listHashMap, 14);
                    }
                }

            }
        }
    }


    static void checkContent(File file, LinkedHashMap<Integer, ArrayList<File>> listHashMap, int type) {
        ArrayList<File> fileArrayList = listHashMap.get(type);
        if (null == fileArrayList) {
            fileArrayList = new ArrayList<>();
            listHashMap.put(type, fileArrayList);
        }

        if (!fileArrayList.contains(file)) {
            fileArrayList.add(file);
        }
    }


    private void checkImage() {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");

            HashMap<String, String> hashMap = new HashMap<String, String>();

            for (File file : getProject().getRootDir().listFiles()) {
                checkImage(file, md5, hashMap);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private static boolean isContainLog(String str) {
        return str.contains("Log.v") || str.contains("Log.d") || str.contains("Log.i") || str.contains("Log.w") ||
                str.contains("Log.e");
    }

    private static boolean isUseColors(String str) {
        if (str.contains("</color>")) return false;
        if (str.contains("parseColor")) return true;

        Pattern p = Pattern.compile("\"#[A-Za-z0-9]{3,8}\"");
        Matcher m = p.matcher(str);
        return m.find();
    }

    static boolean isXmlUseDimens(String str) {
        Pattern p = Pattern.compile("\"[0-9]dp\"");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }

        Pattern sp = Pattern.compile("\"[0-9]sp\"");
        Matcher spm = sp.matcher(str);
        return spm.find();
    }

    static boolean isContainChinese(String str, File file) {
        if (str.contains("tools:text") || str.contains("<item>") || str.contains("//") || str.contains("<!--") || str.contains("*") || str.contains("<string") || str.contains("Deprecated")) {
            return false;
        }
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
//    Pattern p = Pattern.compile("\"[\u4e00-\u9fa5][^%&',;=?\$\\x22]+\"")
        Matcher m = p.matcher(str);
        if (m.find()) {
            System.err.println("isContainChinese " + str + p.pattern());
            return true;
        }
        return false;
    }

    void checkImage(File file, MessageDigest md5, HashMap<String, String> map) {
        if (file.isDirectory() && !file.getName().equals("build")) {
            if (file.listFiles() != null && file.listFiles().length > 0) {
                for (File f : file.listFiles()) {
                    checkImage(f, md5, map);
                }
            }
        } else {
            if ((file.getName().endsWith(".webp") || file.getName().endsWith(".png") || file.getName().endsWith(".jpg"))) {
                String fileMd5 = getMD5(file, md5);
                String value = file.getAbsolutePath().substring(file.getAbsolutePath().indexOf("xcbb_client_android/") + "xcbb_client_android/".length());
                if (map.containsKey(fileMd5)) {
                    System.err.println(value + generateSpace(value, 80) + "   和   " + map.get(fileMd5) + generateSpace(map.get(fileMd5), 90) + "文件相同");
                } else {
                    map.put(fileMd5, value);
                }
            }
        }
    }

    static String getMD5(File file, MessageDigest md5) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[8192];
            int length;
            while ((length = fileInputStream.read(buffer)) != -1) {
                md5.update(buffer, 0, length);
            }

            return convertToHexString(md5.digest());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fileInputStream != null)
                    fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static String convertToHexString(byte[] data) {
        StringBuilder strBuffer = new StringBuilder();
        for (byte datum : data) {
            strBuffer.append(Integer.toHexString(0xff & datum));
        }
        return strBuffer.toString();
    }

    private void checkString() {
        String xmlFile = getProject().getRootDir() + File.separator + "app" + File.separator + "src" + File.separator + "main" + File.separator + "res" + File.separator + "values" + File.separator + "strings.xml";
//        Node xmlParser = null;
//        try {
//            xmlParser = new XmlParser().parse(xmlFile);
//            HashMap<String, String> map = new HashMap<String, String>();
//
//
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (SAXException e) {
//            e.printStackTrace();
//        } catch (ParserConfigurationException e) {
//            e.printStackTrace();
//        }
//
//        xmlParser.string.each {
//            if (map.containsKey(it.text())) {
//                System.err.println(it.attribute("name") + generateSpace(it.attribute("name"), 30) + "和   " + map.get(it.text()) + generateSpace(map.get(it.text()), 30) + "的值相同");
//            } else {
//                map.put(it.text(), it.attribute("name"));
//            }
//        }

        //1.创建DocumentBuilderFactory对象
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        //2.创建DocumentBuilder对象
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document d = builder.parse(xmlFile);
            NodeList list = d.getElementsByTagName("string");
            //element(sList);
            HashMap<String, String> map = new HashMap<>();
            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                NodeList childNodes = node.getChildNodes();
                for (int j = 0; j < childNodes.getLength(); j++) {
                    if (childNodes.item(j).getNodeType() == Node.ELEMENT_NODE) {
                        System.out.print(childNodes.item(j).getNodeName() + ":");
                        System.out.println(childNodes.item(j).getFirstChild().getNodeValue());

                        if (map.containsKey(childNodes.item(j).getNodeName())) {
                            System.err.println(childNodes.item(j).getNodeName() + generateSpace(childNodes.item(j).getNodeName(), 30) + "和   "
                                    + map.get(childNodes.item(j).getNodeName()) + generateSpace(childNodes.item(j).getNodeName(), 30) + "的值相同");

//                            System.err.println(childNodes.item(j).getNodeName() + "、" + childNodes.item(j).getFirstChild().getNodeValue() + "重复");
                        } else {
                            map.put(childNodes.item(j).getNodeName(), childNodes.item(j).getFirstChild().getNodeValue());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String generateSpace(String value, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count - value.length(); i++) {
            sb.append(" ");
        }
        return sb.toString();
    }
}
