package app.visualization;

import app.model.Path;
import app.model.ProblemInput;
import app.model.ProblemOutput;
import app.model.Street;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.DefaultEdgeLabelRenderer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProblemOutputVisualizer {
    @Value(value = "${frame.width:1920}")
    private int frameWidth;
    @Value(value = "${frame.height:1080}")
    private int frameHeight;
    private final int graphSpaceFromWidthEdges = 20;
    private final int graphSpaceFromHeightEdges = 80;
    private final int edgeLengthLabelFontSize = 12;
    private final Color edgeLengthLabelColor = Color.BLACK;

    public void visualize(ProblemInput problemInput, ProblemOutput problemOutput) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(createVisualizationViewer(problemInput, problemOutput));
        frame.setSize(new Dimension(frameWidth, frameHeight));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private VisualizationViewer<Long, Street> createVisualizationViewer(ProblemInput problemInput, ProblemOutput problemOutput) {
        VisualizationViewer<Long, Street> visualizationViewer = initializeVisualizationViewer(problemInput);
        setLabelsAndColors(problemInput, problemOutput, visualizationViewer);

        return visualizationViewer;
    }

    private void setLabelsAndColors(ProblemInput problemInput, ProblemOutput problemOutput, VisualizationViewer<Long, Street> visualizationViewer) {
        visualizationViewer.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<>());
        visualizationViewer.getRenderContext().setVertexFillPaintTransformer(junctionId -> chooseJunctionColor(problemInput.getMissionProperties().getInitialJunctionId(), junctionId));
        visualizationViewer.getRenderContext().setEdgeFontTransformer(__ -> createEdgeLengthFont());
        visualizationViewer.getRenderContext().setEdgeLabelRenderer(createEdgeLabelRendererByColor(edgeLengthLabelColor));
        Map<Integer, Color> carIndexToRandomColorMap = createCarIndexToRandomColorMap(problemOutput.getBestPaths().size());
        visualizationViewer.getRenderContext().setEdgeFillPaintTransformer(street -> chooseStreetColor(carIndexToRandomColorMap, problemOutput.getBestPaths(), street));
    }

    private VisualizationViewer<Long, Street> initializeVisualizationViewer(ProblemInput problemInput) {
        return new VisualizationViewer<>(
                new ISOMLayout<>(convertToVisualizableGraph(problemInput)),
                new Dimension(frameWidth - graphSpaceFromWidthEdges, frameHeight - graphSpaceFromHeightEdges));
    }

    private Map<Integer, Color> createCarIndexToRandomColorMap(int numberOfCars) {
        Map<Integer, Color> carIndexToColor = new HashMap<>();
        for (int carIndex = 0; carIndex < numberOfCars; carIndex++) {
            carIndexToColor.put(carIndex, new Color((int) (Math.random() * 0x1000000)));
        }
        return carIndexToColor;
    }

    private Paint chooseStreetColor(Map<Integer, Color> carIndexToColor, List<Path> bestPaths, Street street) {
        for (int carIndex = 0; carIndex < bestPaths.size(); carIndex++) {
            List<Street> streets = bestPaths.get(carIndex).getStreets();
            if (streets.contains(street)) {
                return carIndexToColor.get(carIndex);
            }
        }

        return null;
    }

    private DefaultEdgeLabelRenderer createEdgeLabelRendererByColor(Color edgeLabelColor) {
        return new DefaultEdgeLabelRenderer(edgeLabelColor) {
            @Override
            public <V> Component getEdgeLabelRendererComponent(
                    JComponent vv, Object value, Font font,
                    boolean isSelected, V vertex) {
                super.getEdgeLabelRendererComponent(
                        vv, value, font, isSelected, vertex);
                setForeground(edgeLabelColor);
                return this;
            }
        };
    }

    private Font createEdgeLengthFont() {
        return new Font("Helvetica", Font.BOLD, edgeLengthLabelFontSize);
    }

    private Paint chooseJunctionColor(long initialJunctionId, Long aLong) {
        if (aLong.equals(initialJunctionId))
            return Color.GREEN;
        else
            return Color.CYAN;
    }

    private Graph<Long, Street> convertToVisualizableGraph(ProblemInput problemInput) {
        Graph<Long, Street> g = new DirectedSparseGraph<Long, Street>();
        problemInput.getJunctionsList().forEach(junction -> g.addVertex(junction.getJunctionId()));
        problemInput.getStreetsList().forEach(street -> g.addEdge(street,
                street.getJunctionFromId(),
                street.getJunctionToId()));

        return g;
    }
}