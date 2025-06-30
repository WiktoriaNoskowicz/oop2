public class Polygon extends Shape{
    private Point[] points;

    public Point[] getPoints() {
        return points;
    }

    public void setPoints(Point[] points) {
        this.points = points;
    }

    public Polygon(Point[] points,Style style) {
        super(style); //jako ze dziedziczymy po shape ktora ma pole style protected to mozna
        this.points = new Point[points.length];
        for (int i = 0; i < points.length; i++) {
            this.points[i] = new Point(points[i].getX(), points[i].getY());
        }
    }
    public Polygon(Point[] points) {
        this(points, new Style("none","black",1.0));
        // wywolanie konstruktora jesli nie podamy arg Style
    }
    public Polygon(Polygon stary) { //gleboka kopia
        super(new Style(stary.style.fillColor, stary.style.strokeColor,stary.style.strokeWidth ));
        Point[] zeStarego = stary.getPoints(); //wyciaga tablice z oryginalnego punktu i ulatwia czytelnosc, nie jest konieczne bo mozna samymi getterami
        this.points = new Point[zeStarego.length];
        for (int i = 0; i < points.length; i++) {
            this.points[i] = new Point(zeStarego[i].getX(), zeStarego[i].getY());
        }
    }

    @Override
    public String toString() {
        String res = "";
        for (int i = 0; i < this.points.length; i++) {
            res += " punkt nr " + (i + 1) + "\n" + this.points[i] + "\n";
        }
        return res;
    }
    @Override
    public String toSvg(float offsetX, float offsetY) {
        String res = "<polygon points= \"";
        for (int i = 0; i < this.points.length; i++) {
            res += (points[i].getX() + offsetX) + "," + (points[i].getY() + offsetY);
            if (i < points.length - 1) {
                res += " ";
            }
        }
        res += "\" style=\"fill:none;stroke:black;stroke-width:1";
        res += this.style.toSvg() + " />";;
        return res;
    }
    public static Polygon square(Segment diagonal, Style style){
        // Tworzymy prostopadły segment do przekątnej
        Segment perpendicular = diagonal.perpendicular(diagonal.len());

        // Tablica punktów z których zrobimi kwadrat
        Point[] points = new Point[4];

        // Środek przekątnej
        float midX = (diagonal.getStart().getX() + diagonal.getEnd().getX()) / 2;
        float midY = (diagonal.getStart().getY() + diagonal.getEnd().getY()) / 2;

        // Pierwsze dwa punkty to początek i koniec przekątnej
        points[0] = diagonal.getStart();
        points[1] = diagonal.getEnd();

        // Tworzymy pozostałe dwa punkty za pomocą segmentu prostopadłego do przekątnej
        points[2] = new Point(midX + perpendicular.getStart().getX(), midY + perpendicular.getStart().getY());
        points[3] = new Point(midX + perpendicular.getEnd().getX(), midY + perpendicular.getEnd().getY());

        // Zwracamy nowy obiekt Polygon
        return new Polygon(points, style);
    }
}
