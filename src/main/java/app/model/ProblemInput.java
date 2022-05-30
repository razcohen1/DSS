package app.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.MultiValueMap;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProblemInput {
    private MissionProperties missionProperties;
    private List<Junction> junctionsList;
    private List<Street> streetsList;
    private MultiValueMap<Long, ProceedableJunction> junctionToProceedableJunctions;
}
