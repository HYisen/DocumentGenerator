package net.alexhyisen.dg.model;

import net.alexhyisen.dg.Utility;

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
                System.out.println(line);
                String[] args = line.substring(line.indexOf(",") + 1, line.lastIndexOf(")")).split(",");
                for (int i = 0; i < args.length; i++) {
                    args[i] = args[i].trim();
                    if (args[i].startsWith("\'") || args[i].startsWith("\"")) {
                        args[i] = args[i].subSequence(1, args[i].length() - 1).toString();
                    }
                }
                Arrays.stream(args).forEach(System.out::println);

                String id = args[0];
                Map<String, String> item = new LinkedHashMap<>();
                item.put("label", args[1]);
                if (args.length > 2) {
                    String option = args[2];
                    //canEdit to readonly
                    option = option.equals("true") ? "false" : "true";
                    item.put("readonly", option);
                }
                for (int i = 3; i < args.length; i++) {
                    if ("true".equals(args[i]) || "false".equals(args[i])) {
                        item.put("required", args[i]);
                    }
                }
                System.out.println("add " + id);
                rtn.put(id, item);
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
                    case "text":
                        item.put("label", define);
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
