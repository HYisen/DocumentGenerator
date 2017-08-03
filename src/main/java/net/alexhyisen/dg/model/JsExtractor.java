package net.alexhyisen.dg.model;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class JsExtractor implements Extractor {
    @Override
    public Map<String, Map<String, String>> extract(Path path) throws Exception {
        Map<String, Map<String, String>> rtn = new LinkedHashMap<>();
        List<String> text = Files.lines(path)
                .map(String::trim)
//                .peek(System.out::println)
                .collect(Collectors.toList());
        List<String> part = new LinkedList<>();
        boolean isInside = false;
        for (String line : text) {
            if (isInside) {
                if (line.equals("});")) {
                    isInside = false;
                    parsePart(part, rtn);
                } else {
                    part.add(line);
                }
            } else if (line.startsWith("options(")) {
                part.clear();
                part.add(line);
                isInside = true;
            } else if (line.startsWith("u.setup")) {
                Matcher m = Pattern.compile("u\\.setup(Combo|Text)Box\\(options," +
                        " ([\"\'])(.*?)\\2," +
                        " ([\"\'])(.*?)\\4(, )?" +
                        "(true|false)?.*").matcher(line);
                if (m.find()) {
//                    System.out.println(line);
                    String id = m.group(3);
//                    System.out.println(id);
                    Map<String, String> item = new LinkedHashMap<>();
                    item.put("label", m.group(5));
                    if (m.groupCount() > 7) {
                        String option = m.group(7);
                        //canEdit to readonly
                        option = option.equals("true") ? "false" : "true";
                        item.put("readonly", option);
                    }
                    rtn.put(id, item);
                }
            }
        }

        return rtn;
    }

    private void parsePart(List<String> orig, Map<String, Map<String, String>> data) {
//        System.out.println("\ndata");
//        orig.forEach(System.out::println);
//        System.out.println("end\n");

        Matcher matcher = Pattern.compile("options\\(([\"\'])(.*?)\\1, \\{").matcher(orig.get(0));
        if (!matcher.find()) {
            throw new RuntimeException("can not find id");
        }
        String id = matcher.group(2);
        if (Objects.equals(id, "")) {
            throw new RuntimeException("can not fine id from " + orig.get(0));
        }

        Map<String, String> item = new LinkedHashMap<>();
        Pattern p = Pattern.compile("(.*?): ([\"\'])(.*?)\\2,?");
        for (int i = 1; i < orig.size(); i++) {
            Matcher m = p.matcher(orig.get(i));
            if (m.find()) {
                String option = m.group(1);
//                System.out.println(option);
                String define = m.group(3);
                switch (option) {
                    case "label":
                        item.put("label", define);
                        break;
                    case "title":
                        item.put("label", define);
                        break;
                    case "required":
                        item.put("required", define);
                        break;
                    case "readonly":
                        item.put("readonly", define);
                        break;
                }
            }
        }
        data.put(id, item);
    }

    public static void main(String[] args) throws Exception {
        Path p = Paths.get(".", "sample.js");
        Utility.printData(new JsExtractor().extract(p));
    }
}
