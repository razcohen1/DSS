package app;

import app.algorithm.CityTraverseAlgorithm;
import app.files.JsonReader;
import app.model.ProblemInput;
import app.model.ProblemOutput;
import app.visualization.ProblemOutputVisualizer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Collections.emptyList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BusinessLogicRunnerTest {
    private final String inputFilePath = "inputFilePath";
    private final ProblemInput parsedProblemInputFromFile = ProblemInput.builder()
            .junctionsList(emptyList())
            .streetsList(emptyList())
            .build();
    private final ProblemOutput problemOutputFromAlgorithm = ProblemOutput.builder().build();
    @Mock
    private JsonReader jsonReaderMock;
    @Mock
    private CityTraverseAlgorithm algorithmMock;
    @Mock
    private ProblemOutputVisualizer visualizerMock;
    private BusinessLogicRunner businessLogicRunner;

    @Before
    public void setUp() {
        businessLogicRunner = BusinessLogicRunner.builder()
                .inputFilepath(inputFilePath)
                .jsonReader(jsonReaderMock)
                .cityTraverseAlgorithm(algorithmMock)
                .visualizer(visualizerMock)
                .build();
        when(jsonReaderMock.read(inputFilePath, ProblemInput.class)).thenReturn(parsedProblemInputFromFile);
        when(algorithmMock.run(parsedProblemInputFromFile)).thenReturn(problemOutputFromAlgorithm);
    }

    @Test
    public void run() {
        businessLogicRunner.run();
        verify(visualizerMock).visualize(parsedProblemInputFromFile, problemOutputFromAlgorithm);
    }
}