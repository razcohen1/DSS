package app.algorithm;

import app.model.ProblemInput;
import app.model.ProblemOutput;

public interface Algorithm {
    ProblemOutput run(ProblemInput problemInput);
}
