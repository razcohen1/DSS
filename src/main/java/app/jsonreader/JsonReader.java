package app.jsonreader;

import app.exceptions.FileReadingException;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonReader {
    private final Gson gson = new Gson();

    public String read(String filepath, Type resultType) {
        try {
            String json = new String(Files.readAllBytes(Paths.get(filepath)));
            return gson.fromJson(json, resultType);
        } catch (IOException e) {
            throw new FileReadingException();
        }
    }
}
