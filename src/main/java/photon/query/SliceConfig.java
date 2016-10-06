package photon.query;

public class SliceConfig {
    public static final String DO_NOT_SLICE = "none";
    public static final String BY_RANK = "rank";
    public static final String BY_DEPTH = "depth";

    public String sliceMode;
    public int leftLimit;
    public int rightLimit;
    public boolean leftInclusive;
    public boolean rightInclusive;

    public SliceConfig(String sliceMode, int leftLimit, int rightLimit, boolean leftInclusive, boolean rightInclusive) {
        this.sliceMode = sliceMode;
        this.leftLimit = leftLimit;
        this.rightLimit = rightLimit;
        this.leftInclusive = leftInclusive;
        this.rightInclusive = rightInclusive;
    }
    
    public GraphSlice applyOn(GraphContainer container) {
        switch (sliceMode) {
            case SliceConfig.DO_NOT_SLICE:
                return container.export();
            case SliceConfig.BY_RANK:
                return container.sliceByRank(leftLimit, rightLimit, leftInclusive, rightInclusive).export();
            case SliceConfig.BY_DEPTH:
                return container.sliceByDepth(leftLimit, rightLimit, leftInclusive, rightInclusive).export();
            default:
                throw new RuntimeException("Unsupported applyOn sliceMode \"" + sliceMode + "\"!");
        }
    }
}