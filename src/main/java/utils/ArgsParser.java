package utils;

import java.util.HashMap;
import java.util.Map;

public class ArgsParser {

    public static Map<String, String> parse(String[] args) {
        Map<String, String> map = new HashMap<>();

        for (String arg : args) {
            if (!arg.startsWith("--")) {
                throw new IllegalArgumentException("Argumento inválido: " + arg);
            }

            String[] parts = arg.substring(2).split("=", 2);

            if (parts.length != 2) {
                throw new IllegalArgumentException("Formato inválido: " + arg);
            }

            map.put(parts[0], parts[1]);
        }

        return map;
    }
}