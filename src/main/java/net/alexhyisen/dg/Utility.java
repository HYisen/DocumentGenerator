package net.alexhyisen.dg;

import javafx.collections.ObservableList;
import net.alexhyisen.dg.model.Item;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {
    //I do want to extend String with String.get().
    public static Optional<String> get(String key, String orig) {
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

    public static void transform(Map<String, Map<String, String>> src, ObservableList<Item> trg) {
        trg.clear();
        src.forEach((k, v) -> {
            String label = v.getOrDefault("label", "");
            String cls = v.getOrDefault("class", "");
            String id = k;
            String readonly = v.getOrDefault("readonly", "");
            String required = v.getOrDefault("required", "");

            String name;
            if (label.equals("") || cls.contains("button") || cls.contains("datagrid")) {
                name = "";
            } else {
                char[] chars = id.toCharArray();
                int index = 0;
                for (int i = 0; i < chars.length; i++) {
                    if (Character.isUpperCase(chars[i])) {
                        index = i;
                        break;
                    }
                }
                chars[index] = Character.toLowerCase(chars[index]);
                name = String.valueOf(chars).substring(index);
            }

            Item item = new Item(label, name, cls, id, readonly, required);
            trg.add(item);
        });
//        System.out.println("transform complete");
    }


    public static void main(String[] args) {
        String content = "<input type=\"text\" class=\"easyui-textbox\" id=\"tbDoctor\">";
        Optional<String> result = get("class", content);
        System.out.println(result.orElse("Not Found"));
    }
}
