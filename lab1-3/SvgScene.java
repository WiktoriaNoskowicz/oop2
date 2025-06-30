import java.awt.*;
import java.io.IOException;
import java.io.FileWriter;

public class SvgScene {
    private final Shape[] shapes;
    //private final Polygon[] pol; jako ze polygon dziedziczy po shape
    // to korzystamy z polimorfizmu aby sie dało dodac dowolny obiekt dziedziczacy po Shape; ellipse oraz Polygon
    private int indexPol;

    public SvgScene() {
        this.shapes = new Shape[3];
        this.indexPol = 0;

    }

    public Shape[] getShapes() {
        return this.shapes;
    }


    public void addShape(Shape s) {
        for (int i = 0; i < this.shapes.length; i++) {
            if (this.shapes[i] == null) {
                this.shapes[i] = s;
                return;
            }

        }
        this.shapes[this.indexPol] = s;
        this.indexPol++;
        if (this.indexPol == this.shapes.length) {
            this.indexPol = 0;
        }
    }


    public String toSvg() {
        String res = "";
        for (Shape s : this.shapes) {
            if (s != null) {
                res += s.toSvg(0, 0); //przesuwamy wzgl poczatku ukladu wspolrzednych
                res += "\n";
            }
        }
        return res;
    }
    public String toString(){
        String res = "";
        for (int i=0; i<this.shapes.length;i++){
            if(this.shapes[i] !=null) {
                res += "oto nasze shape: " + this.shapes[i].toString() + "\n";
            }else{
                System.out.println("shape == null ");
            }
        }
        return res;
    }

    public BoundingBox boundingBox() {
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE;

        for (Shape s : this.shapes) { //przechodzimy po calym shapes
            if (s instanceof Polygon poly){ //sprawdzamy czy mamy obiekt poly czy ellipse                  for (Point p : poly.getPoints()) {

                for (Point p : poly.getPoints()) {
                    minX = Math.min(minX, p.getX());
                    minY = Math.min(minY, p.getY());
                    maxX = Math.max(maxX, p.getX());
                    maxY = Math.max(maxY, p.getY());
                }
                    }

         else if (s instanceof Ellipse ellipse) {
                minX = Math.min(minX, ellipse.getCenter().getX() - ellipse.getRadiusX());
                minY = Math.min(minY, ellipse.getCenter().getY() - ellipse.getRadiusY());
                maxX = Math.max(maxX, ellipse.getCenter().getX() + ellipse.getRadiusX());
                maxY = Math.max(maxY, ellipse.getCenter().getY() + ellipse.getRadiusY());
            }


        }
//tworzenie najmniejszego prostokata ktory moze objac wszytskie elementy
        return new BoundingBox(minX, minY, maxX - minX, maxY - minY);
    }


    public void save(String filePath) {
        BoundingBox box = this.boundingBox();

        if (box == null) {
            System.out.println("No shapes to save.");
            return;
        }

        double width = box.width();
        double height = box.height();

        StringBuilder svgContent = new StringBuilder();
        svgContent.append(String.format(
                "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"%.2f\" height=\"%.2f\" viewBox=\"0 0 %.2f %.2f\">\n",
                width, height, width, height
        ));

        for (Shape s : this.shapes) {
            if (s == null) continue;
            svgContent.append(s.toSvg(0, 0)).append("\n");
        }

        svgContent.append("</svg>");

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(svgContent.toString());
            System.out.println("SVG file saved successfully!");
        } catch (IOException e) {
            System.err.println("Error saving SVG file: " + e.getMessage());
        }
    }
}
