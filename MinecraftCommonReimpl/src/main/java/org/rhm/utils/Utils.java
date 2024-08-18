package org.rhm.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.rhm.MinecraftTypes;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Map;

class ConsoleFormats {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_WHITE = "\u001B[97m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_DARK_RED = "\u001B[31m";
    public static final String ANSI_RED = "\u001B[91m";
    public static final String ANSI_DARK_GREEN = "\u001B[32m";
    public static final String ANSI_GREEN = "\u001B[92m";
    public static final String ANSI_GOLD = "\u001B[33m";
    public static final String ANSI_YELLOW = "\u001B[93m";
    public static final String ANSI_DARK_BLUE = "\u001B[34m";
    public static final String ANSI_BLUE = "\u001B[94m";
    public static final String ANSI_DARK_PURPLE = "\u001B[35m";
    public static final String ANSI_PURPLE = "\u001B[95m";
    public static final String ANSI_DARK_CYAN = "\u001B[36m";
    public static final String ANSI_CYAN = "\u001B[96m";
    public static final String ANSI_DARK_GRAY = "\u001B[90m";
    public static final String ANSI_GRAY = "\u001B[37m";

    public static final String SGR_STRIKETHROUGH = "\u001B[9m";
    public static final String SGR_BOLD = "\u001B[1m";
    public static final String SGR_ITALIC = "\u001B[3m";
    public static final String SGR_UNDERLINE = "\u001B[4m";
}

public class Utils implements MinecraftTypes {
    public static final String obfuscationChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()_+-=[]{}|;':\",.<>?/~`⌂ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜ¢£¥₧ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αßΓπΣσµτΦΘΩδ∞φε∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■ ";
    public static final String COLOR_PATTERN = "[§&]([0-9a-fA-FkKlLmMnNoOrR])";
    public static final Map<Character, String> colorMap = Map.ofEntries(
            Map.entry('4', ConsoleFormats.ANSI_DARK_RED),
            Map.entry('c', ConsoleFormats.ANSI_RED),
            Map.entry('6', ConsoleFormats.ANSI_YELLOW),
            Map.entry('e', ConsoleFormats.ANSI_GOLD),
            Map.entry('2', ConsoleFormats.ANSI_DARK_GREEN),
            Map.entry('a', ConsoleFormats.ANSI_GREEN),
            Map.entry('3', ConsoleFormats.ANSI_DARK_CYAN),
            Map.entry('b', ConsoleFormats.ANSI_CYAN),
            Map.entry('9', ConsoleFormats.ANSI_DARK_BLUE),
            Map.entry('1', ConsoleFormats.ANSI_BLUE),
            Map.entry('5', ConsoleFormats.ANSI_DARK_PURPLE),
            Map.entry('d', ConsoleFormats.ANSI_PURPLE),
            Map.entry('f', ConsoleFormats.ANSI_WHITE),
            Map.entry('8', ConsoleFormats.ANSI_DARK_GRAY),
            Map.entry('7', ConsoleFormats.ANSI_GRAY),
            Map.entry('0', ConsoleFormats.ANSI_BLACK),
            Map.entry('r', ConsoleFormats.ANSI_RESET),
            Map.entry('l', ConsoleFormats.SGR_BOLD),
            Map.entry('m', ConsoleFormats.SGR_STRIKETHROUGH),
            Map.entry('n', ConsoleFormats.SGR_UNDERLINE),
            Map.entry('o', ConsoleFormats.SGR_ITALIC),
            Map.entry('k', "")
    );
    public static Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    public final Map<String, Character> fancyColorMap = Map.ofEntries(
            Map.entry("dark_red", '4'),
            Map.entry("red", 'c'),
            Map.entry("yellow", '6'),
            Map.entry("gold", 'e'),
            Map.entry("dark_green", '2'),
            Map.entry("green", 'a'),
            Map.entry("dark_aqua", '3'),
            Map.entry("aqua", 'b'),
            Map.entry("dark_blue", '9'),
            Map.entry("blue", '1'),
            Map.entry("dark_purple", '5'),
            Map.entry("light_purple", 'd'),
            Map.entry("white", 'f'),
            Map.entry("dark_gray", '8'),
            Map.entry("gray", '7'),
            Map.entry("black", '0')
            //Map.entry('r', ConsoleFormats.ANSI_RESET),
            //Map.entry('l', ConsoleFormats.SGR_BOLD),
            //Map.entry('m', ConsoleFormats.SGR_STRIKETHROUGH),
            //Map.entry('n', ConsoleFormats.SGR_UNDERLINE),
            //Map.entry('o', ConsoleFormats.SGR_ITALIC),
            //Map.entry('k', "")
    );
    private SecureRandom rand = new SecureRandom();

    public Utils() {

    }

    public static boolean isColorSupported() {
        return true; //FEAT: actually make this check if its supported
    }

    public static String formatColorCodes(String inp) {
        boolean isObfuscated = false;
        char c2 = 'a';
        for (int i = 0; i < inp.length(); i++) {
            char c = inp.charAt(i);

            if ((c2 == '&' || c2 == '§')) {
                inp = colorMap.containsKey(c) ? inp.replaceFirst("" + inp.charAt(i - 1) + c, colorMap.get(c)) : inp;
                if (c == 'k') {
                    isObfuscated = true;
                    i -= 2;
                } else if (c == 'r' && isObfuscated) {
                    isObfuscated = false;
                }
            } else {
                inp = isObfuscated && c != 'r' ? inp.substring(0, i) + obfuscationChars.charAt((int) (Math.random() * obfuscationChars.length())) + inp.substring(i + 1) : inp;
            }
            c2 = c;
        }
        return inp;
    }

    public static byte[] getFirstNElements(byte[] array, int n) {
        if (array.length <= n) {
            return array;
        } else {
            return Arrays.copyOfRange(array, 0, n);
        }
    }

    public String formatJsonCompound(JsonObject compound) {
        compound = compound.getAsJsonObject("value");
        compound.addProperty("type", "CompoundTag");
        return formatColorCodes(formatJsonCompound(compound, ""));
    }

    private String formatJsonCompound(JsonObject compound, String existing) {
        String type = compound.get("type").getAsString();

        switch (type) {
            case "CompoundTag":
                if (compound.has("color")) {
                    JsonObject color = compound.getAsJsonObject("color");
                    if (fancyColorMap.containsKey(color.get("value").getAsString())) {
                        existing += "&" + fancyColorMap.get(color.get("value").getAsString());
                    }
                }
                JsonElement text = compound.get("text");
                if (text instanceof JsonObject) existing = formatJsonCompound(((JsonObject) text), existing);
                JsonElement extra = compound.get("extra");
                if (extra instanceof JsonObject) existing = formatJsonCompound(((JsonObject) extra), existing);
                JsonElement empty = compound.get("");
                if (empty instanceof JsonObject) existing = formatJsonCompound(((JsonObject) empty), existing);
                break;
            case "ListTag":
                JsonObject val = compound.getAsJsonObject("value");
                for (JsonElement je : val.getAsJsonArray("list")) {
                    if (je instanceof JsonObject) {
                        JsonObject jo = (JsonObject) je;
                        jo.addProperty("type", val.get("type").getAsString());
                        existing = formatJsonCompound(jo, existing);
                    }
                }
                break;
            case "StringTag":
                existing += compound.get("value").getAsString();
                break;
            default:
                existing += compound.getAsString();
        }

        return existing;
    }

    public String formatColorAll(String s) {
        try {
            JsonObject elem = gson.fromJson(s, JsonObject.class);
            if (elem.isJsonPrimitive()) return elem.toString();
            return formatJsonCompound(elem);
        } catch (Exception e) {
            Logger.error(e, "Error while trying to format color codes:");
            return s;
        }
    }

    public String removeColorCodes(String inp) {
        return inp.replaceAll(COLOR_PATTERN, "");
    }

    public long getNewSalt() {
        return rand.nextLong();
    }

    public byte[] prepend(byte[] array, byte data) {
        byte[] newArray = Arrays.copyOf(array, array.length + 1);
        newArray[0] = data;

        System.arraycopy(array, 0, newArray, 1, array.length);
        return newArray;
    }
}
