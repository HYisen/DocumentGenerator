package net.alexhyisen.dg.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {
    //I do want to extend String with String.get().
    static Optional<String> get(String key, String orig) {
        Matcher m = Pattern.compile(".* " + key + "=([\"\'])(.*?)\\1.*").matcher(orig);
        String result = null;
        if (m.find()) {
            result = m.group(2);
        }
        return Optional.ofNullable(result);
    }

    public static void printData(Map<String, Map<String, String>> orig) {
        orig.forEach((k, v) -> {
            System.out.println(k);
            v.forEach((key, val) -> System.out.println("\t" + key + " " + val));
        });
    }

    //lhs LEFT JOIN rhs
    public static Map<String, Map<String, String>> merge(Map<String, Map<String, String>> lhs,
                                                         Map<String, Map<String, String>> rhs) {
        Map<String, Map<String, String>> rtn = new LinkedHashMap<>();
        lhs.forEach((k, v) -> {
            Map<String, String> item = new LinkedHashMap<>();
            v.forEach(item::put);
            if (rhs.containsKey(k)) {
                rhs.get(k).forEach(item::put);
            }
            rtn.put(k, item);
        });
        return rtn;
    }


    public static void main(String[] args) {
        String content = "<input type=\"text\" class=\"easyui-textbox\" id=\"tbDoctor\">";
        Optional<String> result = get("class", content);
        System.out.println(result.orElse("Not Found"));
    }
}
