package com.zrzyyzt.runtimeviewer.Widgets.QueryWidget.Bean;

/**
 * 属性
 * Created by gis-luq on 2018/2/24.
 */

public class KeyAndValueBean {
    private String Key;
    private String Alias;
    private String Value;

    public String getAlias() {
        return Alias;
    }

    public void setAlias(String alias) {
        Alias = alias;
    }

    public String getKey() {
        return Key;
    }

    public String getValue() {
        return Value;
    }

    public void setKey(String key) {
        Key = key;
    }

    public void setValue(String value) {
        Value = value;
    }

     public String[] toStringArray(){
         if(Alias.equalsIgnoreCase("")){
             return new String[]{Key,Value};
         }else {
             return new String[]{Alias,Value};
         }
     }
}
