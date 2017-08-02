package net.alexhyisen.dg.model;

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

    public static void main(String[] args) {
        String content = "<input type=\"text\" class=\"easyui-textbox\" id=\"tbDoctor\">";
        Optional<String> result = get("class", content);
        System.out.println(result.orElse("Not Found"));
    }
}
