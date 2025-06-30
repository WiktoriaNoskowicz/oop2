import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
//        List<Person> persons = new ArrayList<>();
//        persons.add(new Person("ola","nowak", LocalDate.of(2022,2,11)));
//        persons.add(new Person("ala","nowak", LocalDate.of(2012,6,17)));
//        persons.add(new Person("maja","kowalska", LocalDate.of(2002,4,13)));
//
//        for(Person p : persons){
//            System.out.println(p);
//        }
        Person p1 = new Person("Jan", "Kowalski", LocalDate.of(1980, 1, 12));
        Person p2 = new Person("Kamil", "Nowak", LocalDate.of(1990, 11, 22));
        Person p3 = new Person("Anna", "Polak", LocalDate.of(1999, 12, 17));

        List<Person> people = new ArrayList<>();
        people.add(p1);
        people.add(p2);
        people.add(p3);

        p1.adopt(p2);
        p1.adopt(p3);

        System.out.println(p1.getYoungestChild());
        System.out.println(p1.getChildren().get(0));

        Family fam1 = new Family();
        fam1.add(p1, p2, p3);

        System.out.println(Arrays.toString(fam1.get("Jan Kowalski")));

    }
}