package app.jsonreader;

import app.model.ProblemInput;
import org.junit.Test;

public class JsonReaderTest {
    private JsonReader jsonReader = new JsonReader();

    @Test
    public void name() {
        String read = jsonReader.read("C:\\Users\\Raz\\Desktop\\DssProject\\DSS Project\\Input examples\\input_example_1.json", ProblemInput.class);
        System.out.println("test");
    }
}
