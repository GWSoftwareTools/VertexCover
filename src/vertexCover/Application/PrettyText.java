package vertexCover.Application;

import vertexCover.advanced.GraphUtil;
import vertexCover.advanced.SearchTree;
import core.Graph;

/**
 * Formats information about graphs and time-intervals for people to read.
 */
class PrettyText {
    private static final int colWidth = 20;
    static final String formatter = "%-" + colWidth + "s";

    /**
     * This method is used as a tool for human users. It is not needed for getting the results.
     * It only provides some context about the graphs.
     *
     * @param g The target graph.
     */
    static void printResult(Graph g, boolean print) {
        int safePoint = g.getSafePoint();
        int result = SearchTree.minVertexCover(g);
        g.restore(safePoint);
        if (print)
            System.out.println("\n" + String.format(formatter, "Result:") + result);
    }

    /**
     * Prints some main properties of the graph.
     *
     * @param g The analyzed graph.
     */
    static void printAnalysis(Graph g) {
        System.out.println(
                String.format(formatter, "Vertices:") + g.getVertices().size() + "\n" +
                        String.format(formatter, "Edges:") + g.getEdgeCount() + "\n" +
                        String.format(formatter, "Max-Degree:") + g.degree(GraphUtil.maxDegreeID(g)) + "\n" +
                        String.format(formatter, "Disjoint graphs:") + g.getDisjointGraphs().size());
    }

    /**
     * Formats a given amount of nanoseconds into a human-readable string.
     *
     * @param nanos A time interval in nanoseconds
     * @return A string with an equal time interval, but formatted in human-readable units
     */
    static String prettyTime(long nanos) {
        // hours, minutes, seconds, millis, micros, nanos
        int hours = 0;
        int mins = 0;
        int secs = 0;
        int millis = 0;
        int micros = 0;

        while (nanos >= 3_600_000_000_000L) {
            nanos -= 3_600_000_000_000L;
            hours++;
        }

        while (nanos >= 60_000_000_000L) {
            nanos -= 60_000_000_000L;
            mins++;
        }

        while (nanos >= 1_000_000_000L) {
            nanos -= 1_000_000_000L;
            secs++;
        }

        while (nanos >= 1_000_000L) {
            nanos -= 1_000_000L;
            millis++;
        }

        while (nanos >= 1_000L) {
            nanos -= 1_000L;
            micros++;
        }

        StringBuilder sb = new StringBuilder();
        boolean started = false;
        if (hours > 0) {
            started = true;
            sb.append(hours).append(" Hours, ");
        }

        if (started || mins > 0) {
            started = true;
            sb.append(mins).append(" Minutes, ");
        }

        if (started || secs > 0) {
            started = true;
            sb.append(secs).append(" Seconds, ");
        }

        if (started || millis > 0) {
            started = true;
            sb.append(millis).append(" Millis, ");
        }

        if (started || micros > 0) {
            sb.append(micros).append(" Micros");
        }
        return sb.toString();
    }
}
