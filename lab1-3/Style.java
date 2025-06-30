public class Style {
    public final String fillColor;
    public final String strokeColor;
    public Double strokeWidth;

    public Style(String fillColor, String strokeColor, Double strokeWidth) {
        this.fillColor = fillColor;
        this.strokeColor = strokeColor;
        this.strokeWidth = strokeWidth;
    }
    public String toSvg(){
        return "Style=\"fillColor=\"" + this.fillColor + "\" strokeColor =\"" + this.strokeColor + "strokeWidth =\"" +  strokeWidth;

    }
}
