//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
//        System.out.printf("Hello and welcome!\n");

        Point p = new Point(4,7);
        //System.out.println(p.toSvg());
        //p.translate(1,1);
        System.out.println(p);
        Point nowy = p.translated(4,1);
        System.out.println(p);
        System.out.println(nowy);

        Point p1 = new Point(1,1);
        Point p2 = new Point(2,2);
        Point p3 = new Point(3,4);
        Point p4 = new Point(6,5);

        Point p5 = new Point(7,8);
        Point p6 = new Point(9,10);

        Point p7 = new Point(11,12);
        Point p8 = new Point(13,14);

        Segment s1 = new Segment(p1,p2);
        System.out.println("s1 = "+ s1.len());
        Segment s2= new Segment(p3,p4);
        System.out.println("s2 = "+  s2.len());
        Segment s3= new Segment(p5,p6);
        System.out.println("s3 = "+ s3.len());
        Segment s4= new Segment(p7,p8);
        System.out.println("s4 = "+ s4.len());

        Segment [] num = new Segment[4];
        num[0] = s1;
        num[1] = s2;
        num[2] = s3;
        num[3] = s4;

        System.out.println(Segment.max(num));  //static method -> w celu jej wywolania nie musimy tworzyc obiektu

        Point[] toPol = new Point[2];
        toPol[0] = p1;
        toPol[1] = p2;


        Polygon pol1 = new Polygon(toPol);
        System.out.println("pol1 test  = "+ pol1.toString());

       // SvgScene scene = new SvgScene();
       // scene.addPolygon(pol1);
       // System.out.println(scene);
       // scene.toSvg();

      // scene.boundingBox();
      // scene.save("C:\\sciezka\\nazwaIstniejacegoPliku.txt");

        Point pp = new Point(11,12);
        Point ppp = new Point(21,22);
        Point pppp = new Point(31,32);

        Point[] points = {pp,ppp,pppp};

        Polygon polygon1 = new Polygon(points);
        Ellipse ellipse1 = new Ellipse(pp, 10, 11, new Style("none", "black", 1.0));
        SvgScene svgScene1 = new SvgScene();

        svgScene1.addShape(ellipse1);
        svgScene1.addShape(polygon1);

        System.out.println(svgScene1.toSvg());




    }
}