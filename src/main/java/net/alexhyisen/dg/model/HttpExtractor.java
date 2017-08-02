package net.alexhyisen.dg.model;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpExtractor implements Extractor {
    @Override
    public Map<String, Map<String, String>> extract(Path path) throws Exception {
        Map<String, Map<String, String>> rtn = new LinkedHashMap<>();
        long cnt = Files.lines(path)
//                .peek(System.out::println)
                .map(String::trim)
                .filter(v -> v.matches("<.* id=.*>"))
                .peek(System.out::println)
                .peek(v -> {
                    Map<String, String> data = new LinkedHashMap<>();
                    String id = Utility.get("id", v)
                            .orElseThrow(() -> new RuntimeException("can not find id"));
                    String cls;
                    if (v.startsWith("<form ")) {
                        cls = "form";
                    } else if (v.contains("type=\"checkbox\"")) {
                        cls = "checkbox";
                    } else {
                        cls = Utility.get("class", v)
                                .orElseThrow(() -> new RuntimeException("can not find class"));
                    }
                    data.put("class", cls);
                    rtn.put(id, data);
                })
                .count();
        System.out.println("complete " + cnt + " line(s)");
        return rtn;
    }

    public static void main(String[] args) throws Exception {
        Path p = Paths.get(".","sample.jsp");
        new HttpExtractor().extract(p).forEach((k, v) -> System.out.println(k + " " + v.get("class")));
        System.out.println("<input type=\"text\" class=\"easyui-datebox\" id=\"dtSettleDate1\">"
                .matches("<.* id=.*>"));
    }
}