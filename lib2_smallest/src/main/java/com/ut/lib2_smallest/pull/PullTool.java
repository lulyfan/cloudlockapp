package com.ut.lib2_smallest.pull;


import com.ut.lib2_smallest.entity.Dimen;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PullTool {

    public static List<Dimen> parserXml(InputStream is) throws Exception {
        List<Dimen> list = null;
        Dimen dimen = null;
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(new InputStreamReader(is));    //传入InputStream对象 并且设置解码规则需和XML文档中设置的一致
        int type = parser.getEventType();
        while (type != parser.END_DOCUMENT) {
            String name = parser.getName();
            switch (type) {
                case XmlPullParser.START_TAG:
                    if ("resources".equals(parser.getName())) {
                        list = new ArrayList<Dimen>();
                    } else if ("dimen".equals(parser.getName())) {
                        dimen = new Dimen();
                        String attribute = parser.getAttributeValue(null, "name");
                        dimen.setAttribute(attribute);
                        setVlueAndUnit(dimen, parser.nextText());
                        list.add(dimen);
                    }
                    break;
                case XmlPullParser.END_TAG:
//                    if ("dimen".equals(parser.getName())) {
//                        list.add(dimen);
//                        dimen = null;
//                    }
                    break;
                default:
                    break;
            }

            //让解析器向下解析一行,并返回改行的事件常量  这样配合while(type != parser.END_DOCUMENT)读取完整个文档
            type = parser.next();


        }
        return list;
    }

    private static void setVlueAndUnit(Dimen dimen, String temp) {
        float value = Float.parseFloat(temp.replaceAll("[a-z]", ""));
        String unit = temp.substring(temp.length() - 2);
        dimen.setValue(value);
        dimen.setUnit(unit);
    }

    public static void main(String[] args) {
        Dimen dimen = new Dimen();
        setVlueAndUnit(dimen, "1.2dp");
        System.out.println(dimen.toString());
    }

}