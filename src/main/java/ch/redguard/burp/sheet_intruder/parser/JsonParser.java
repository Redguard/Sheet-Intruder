package ch.redguard.burp.sheet_intruder.parser;

import burp.api.montoya.logging.Logging;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.Map;

public class JsonParser {
    private final byte[] tagContent;
    private final Logging logging;

    public JsonParser(byte[] tagContent, Logging logging) {
        this.tagContent = tagContent;
        this.logging = logging;
    }

    public final Map<String, String> parseJson() {
        Gson gson = new Gson();
        String jsonString = new String(tagContent).trim();

        Type mapType = new TypeToken<Map<String, String>>() {
        }.getType();

        JsonReader reader = new JsonReader(new StringReader(jsonString));
        reader.setLenient(true);

        try {
            return gson.fromJson(reader, mapType);
        } catch (JsonSyntaxException e) {
            logging.logToError("Invalid json in tag configuration: " + e.getMessage());
            return Map.of();
        }


    }
}
