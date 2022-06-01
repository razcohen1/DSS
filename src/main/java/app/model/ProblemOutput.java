package app.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
public class ProblemOutput {
    List<List<Street>> bestCarsPaths;
    List<PathDetails> bestPaths;
    double totalScore;
}
