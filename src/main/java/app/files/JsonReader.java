package app.files;

import app.exceptions.FileReadingException;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Type;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;

public class JsonReader {
    private final Gson gson = new Gson();

    public <T> T read(String filepath, Type resultType) {
        try {
            return gson.fromJson(new String(readAllBytes(get(filepath))), resultType);
        } catch (IOException e) {
            e.printStackTrace();
            throw new FileReadingException();
        }
    }
}
