package net.alexhyisen.dg.view;

import net.alexhyisen.dg.model.HttpExtractor;
import net.alexhyisen.dg.model.JsExtractor;
import net.alexhyisen.dg.model.Utility;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        Path pJ = Paths.get(".","sample.js");
        Path pH = Paths.get(".","sample.jsp");
        Map<String, Map<String, String>> result = Utility.merge(
                new HttpExtractor().extract(pH),
                new JsExtractor().extract(pJ)
        );
        Utility.printData(result);
    }
}
