package app.visualization;

import app.algorithm.AlgorithmImpl;
import app.algorithm.JunctionToProceedableJunctionsCreator;
import app.files.JsonReader;
import app.model.ProblemInput;
import app.model.ProblemOutput;
import app.model.Street;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.BasicRenderer;
import edu.uci.ics.jung.visualization.renderers.DefaultEdgeLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import org.apache.commons.collections15.Transformer;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ProblemOutputVisualizer {
//    private static AlgorithmImpl algorithm = new AlgorithmImpl();
//    private static JsonReader jsonReader = new JsonReader();

//    public static void main(String[] args) {
//        ProblemInput problemInput = jsonReader.read("C:\\Users\\Raz\\Desktop\\DssProject\\DSS Project\\Input examples\\input_example_1.json", ProblemInput.class);
//        problemInput.setJunctionToProceedableJunctions(JunctionToProceedableJunctionsCreator.createJunctionToProceedableJunctionsMap(problemInput.getJunctionsList(), problemInput.getStreetsList()));
//        ProblemOutput problemOutput = algorithm.run(problemInput);
//        visualize(problemInput, problemOutput);
//    }

    public static void visualize(ProblemInput problemInput, ProblemOutput problemOutput) {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Graph<Long, Street> g = convertToVisualizableGraph(problemInput);
//        Graph<String, String> g = createGraph();

        Dimension frameSize = new Dimension(1920, 1080);
        Dimension visualizationSize = new Dimension(1900, 1000);
        Renderer<Long, Street> renderer = new BasicRenderer<Long, Street>();
        VisualizationViewer<Long, Street> vv =
                new VisualizationViewer<Long, Street>(
                        new ISOMLayout<>(g), visualizationSize);
//        DefaultModalGraphMouse<String, Double> graphMouse =
//                new DefaultModalGraphMouse<String, Double>();
//        vv.setGraphMouse(graphMouse);

//        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<>());
        vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<>());
        vv.getRenderContext().setVertexFillPaintTransformer(aLong -> {
            if (aLong.equals(problemInput.getMissionProperties().getInitialJunctionId()))
                return Color.GREEN;
            else
                return Color.CYAN;
        });
        vv.getRenderContext().setEdgeFontTransformer(new Transformer<Street, Font>(){
            @Override
            public Font transform(Street customObject) {
                return new Font("Helvetica", Font.BOLD, 12);
            }
        });

        final Color edgeLabelColor = Color.BLACK;
        DefaultEdgeLabelRenderer edgeLabelRenderer =
                new DefaultEdgeLabelRenderer(edgeLabelColor)
                {
                    @Override
                    public <V> Component getEdgeLabelRendererComponent(
                            JComponent vv, Object value, Font font,
                            boolean isSelected, V vertex)
                    {
                        super.getEdgeLabelRendererComponent(
                                vv, value, font, isSelected, vertex);
                        setForeground(edgeLabelColor);
                        return this;
                    }
                };
        vv.getRenderContext().setEdgeLabelRenderer(edgeLabelRenderer);

        Map<Integer, Color> carIndexToColor = new HashMap<>();
        for (int carIndex = 0; carIndex < problemOutput.getBestCarsPaths().size(); carIndex++) {
            carIndexToColor.put(carIndex, new Color((int) (Math.random() * 0x1000000)));
        }

        vv.getRenderContext().setEdgeFillPaintTransformer(street -> {
            List<List<Street>> bestCarsPaths = problemOutput.getBestCarsPaths();
            for (int carIndex = 0; carIndex < bestCarsPaths.size(); carIndex++) {
                List<Street> streets = bestCarsPaths.get(carIndex);
                if (streets.contains(street)) {
                    return carIndexToColor.get(carIndex);
                }
            }
            return null;
        });
//        renderer.setVertexRenderer((rc, layout, aLong) -> rc.setVertexFillPaintTransformer(aLong1 -> {
//            if(aLong1.equals(problemInput.getMissionProperties().getInitialJunctionId()))
//                return Color.YELLOW;
//            else
//                return Color.BLACK;
//        }));
//        improvePerformance(vv);
        f.getContentPane().add(vv);
        f.setSize(frameSize);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    private static Graph<Long, Street> convertToVisualizableGraph(ProblemInput problemInput) {

        Graph<Long, Street> g = new DirectedSparseGraph<Long, Street>();
        problemInput.getJunctionsList().forEach(junction -> g.addVertex(junction.getJunctionId()));
        problemInput.getStreetsList().forEach(street -> g.addEdge(street,
                street.getJunctionFromId(),
                street.getJunctionToId()));
//        for (int i=0; i<numVertices; i++)
//        {
//            g.addVertex("v"+i);
//        }
//        for (int i=0; i<numEdges; i++)
//        {
//            int v0 = random.nextInt(numVertices);
//            int v1 = random.nextInt(numVertices);
//            g.addEdge("e"+i, "v"+v0, "v"+v1);
//        }
        return g;
    }

//    // This method summarizes several options for improving the painting
//    // performance. Enable or disable them depending on which visual features
//    // you want to sacrifice for the higher performance.
//    private static <V, E> void improvePerformance(
//            VisualizationViewer<V, E> vv)
//    {
//        // Probably the most important step for the pure rendering performance:
//        // Disable anti-aliasing
//        vv.getRenderingHints().remove(RenderingHints.KEY_ANTIALIASING);
//
//        // Skip vertices that are not inside the visible area.
////        doNotPaintInvisibleVertices(vv);
//
//        // May be helpful for performance in general, but not appropriate
//        // when there are multiple edges between a pair of nodes: Draw
//        // the edges not as curves, but as straight lines:
//        vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line<V,E>());
//
//        // May be helpful for painting performance: Omit the arrow heads
//        // of directed edges
//        Predicate<Context<Graph<V, E>, E>> edgeArrowPredicate =
//                new Predicate<Context<Graph<V,E>,E>>()
//                {
//                    @Override
//                    public boolean evaluate(Context<Graph<V, E>, E> arg0)
//                    {
//                        return false;
//                    }
//                };
//        vv.getRenderContext().setEdgeArrowPredicate(edgeArrowPredicate);
//
//    }

//    // Skip all vertices that are not in the visible area.
//    // NOTE: See notes at the end of this method!
//    private static <V, E> void doNotPaintInvisibleVertices(
//            VisualizationViewer<V, E> vv)
//    {
//        Predicate<Context<Graph<V, E>, V>> vertexIncludePredicate =
//                new Predicate<Context<Graph<V,E>,V>>()
//                {
//                    Dimension size = new Dimension();
//
//                    @Override
//                    public boolean evaluate(Context<Graph<V, E>, V> c)
//                    {
//                        vv.getSize(size);
//                        Point2D point = vv.getGraphLayout().transform(c.element);
//                        Point2D transformed =
//                                vv.getRenderContext().getMultiLayerTransformer()
//                                        .transform(point);
//                        if (transformed.getX() < 0 || transformed.getX() > size.width)
//                        {
//                            return false;
//                        }
//                        if (transformed.getY() < 0 || transformed.getY() > size.height)
//                        {
//                            return false;
//                        }
//                        return true;
//                    }
//                };
//        vv.getRenderContext().setVertexIncludePredicate(vertexIncludePredicate);
//
//        // NOTE: By default, edges will NOT be included in the visualization
//        // when ONE of their vertices is NOT included in the visualization.
//        // This may look a bit odd when zooming and panning over the graph.
//        // Calling the following method will cause the edges to be skipped
//        // ONLY when BOTH their vertices are NOT included in the visualization,
//        // which may look nicer and more intuitive
//        doPaintEdgesAtLeastOneVertexIsVisible(vv);
//    }
//
//    // See note at end of "doNotPaintInvisibleVertices"
//    private static <V, E> void doPaintEdgesAtLeastOneVertexIsVisible(
//            VisualizationViewer<V, E> vv)
//    {
//        vv.getRenderer().setEdgeRenderer(new BasicEdgeRenderer<V, E>()
//        {
//            @Override
//            public void paintEdge(RenderContext<V,E> rc, Layout<V, E> layout, E e)
//            {
//                GraphicsDecorator g2d = rc.getGraphicsContext();
//                Graph<V,E> graph = layout.getGraph();
//                if (!rc.getEdgeIncludePredicate().evaluate(
//                        Context.<Graph<V,E>,E>getInstance(graph,e)))
//                    return;
//
//                Pair<V> endpoints = graph.getEndpoints(e);
//                V v1 = endpoints.getFirst();
//                V v2 = endpoints.getSecond();
//                if (!rc.getVertexIncludePredicate().evaluate(
//                        Context.<Graph<V,E>,V>getInstance(graph,v1)) &&
//                        !rc.getVertexIncludePredicate().evaluate(
//                                Context.<Graph<V,E>,V>getInstance(graph,v2)))
//                    return;
//
//                Stroke new_stroke = rc.getEdgeStrokeTransformer().transform(e);
//                Stroke old_stroke = g2d.getStroke();
//                if (new_stroke != null)
//                    g2d.setStroke(new_stroke);
//
//                drawSimpleEdge(rc, layout, e);
//
//                // restore paint and stroke
//                if (new_stroke != null)
//                    g2d.setStroke(old_stroke);
//            }
//        });
//    }


    public static Graph<String, String> createGraph() {
        Random random = new Random(0);
        int numVertices = 100;
        int numEdges = 100;
        Graph<String, String> g = new DirectedSparseGraph<String, String>();
        for (int i = 0; i < numVertices; i++) {
            g.addVertex("v" + i);
        }
        for (int i = 0; i < numEdges; i++) {
            int v0 = random.nextInt(numVertices);
            int v1 = random.nextInt(numVertices);
            g.addEdge("e" + i, "v" + v0, "v" + v1);
        }
        return g;
    }
}