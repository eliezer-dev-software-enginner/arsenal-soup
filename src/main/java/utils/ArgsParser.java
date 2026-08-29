package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ArgsParser {

    private static final Logger log = LoggerFactory.getLogger(ArgsParser.class);

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

    /** Lê um argumento obrigatório; encerra o processo com uma mensagem clara se estiver ausente. */
    public static String require(Map<String, String> args, String key) {
        String value = args.get(key);
        if (value == null || value.isBlank()) {
            log.error("Argumento obrigatório: --{}", key);
            System.exit(1);
        }
        return value;
    }
}