package net.alexhyisen.dg.model;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpExtractor implements Extractor {
    @Override
    public Map<String, Map<String, String>> extract(Path path) throws Exception {
        Map<String, Map<String, String>> rtn = new LinkedHashMap<>();
        long cnt = Files.lines(path)
//                .peek(System.out::println)
                .map(String::trim)
                .filter(v -> v.matches("<.* id=.*>"))
//                .peek(System.out::println)
                .peek(v -> {
                    Map<String, String> item = new LinkedHashMap<>();

                    String id = Utility.get("id", v)
                            .orElseThrow(() -> new RuntimeException("can not find id"));

                    String cls;
                    if (v.startsWith("<form ") || v.startsWith("<div ")) {
//                        cls = "form";
                        return;
                    } else if (v.contains("type=\"checkbox\"")) {
                        cls = "checkbox";
                    } else {
                        try {
                            cls = Utility.get("class", v)
                                    .orElseThrow(() -> new RuntimeException("can not find class in\n"
                                    +v));
                        } catch (RuntimeException e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                    item.put("class", cls);

                    Matcher m = Pattern.compile("<.*>(.*)<.*>").matcher(v);
                    if (m.find()) {
                        String label = m.group(1);
                        if (!label.equals("")) {
                            item.put("label", label);
                        }
                    }

                    rtn.put(id, item);
                })
                .count();
//        System.out.println("complete " + cnt + " line(s)");
        return rtn;
    }

    public static void main(String[] args) throws Exception {
        Path p = Paths.get(".", "sample.jsp");
        Utility.printData(new HttpExtractor().extract(p));
    }
}