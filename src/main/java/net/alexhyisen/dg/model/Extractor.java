package net.alexhyisen.dg.model;

import java.nio.file.Path;
import java.util.Map;

@FunctionalInterface
public interface Extractor {
    Map<String, Map<String, String>> extract(Path path) throws Exception;
}
