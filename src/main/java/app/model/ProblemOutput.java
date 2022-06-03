package app.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ProblemOutput {
    List<PathDetails> bestPaths;
    double totalScore;
}
