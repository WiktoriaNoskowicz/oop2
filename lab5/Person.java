import java.time.LocalDate;
import java.util.*;

public class Person implements Comparable<Person> {
    private String imie;
    private String nazwisko;
    private LocalDate birthday;
    private Set<Person> children;

    public Person(String imie, String nazwisko, LocalDate birthday) {
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.birthday = birthday;
        this.children = new HashSet<>();
    }

    public String getImie() {
        return imie;
    }

    public String getNazwisko() {
        return nazwisko;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    @Override
    public String toString() {
    return this.imie + " " + this.nazwisko;
    }

    public boolean adopt(Person child) {
        if(child == null || child == this) return false;
        return this.children.add(child);}


    public Person getYoungestChild(){
        if (this.children.isEmpty()) return null;
        Person youngest = this.children.iterator().next();
        for (Person child : this.children) {
            if (youngest.compareTo(child) < 0) {
                youngest = child;
            }
        }
        return youngest;
    }

    @Override
    public int compareTo(Person other) {
//        return this.birthDate.compareTo(other.birthDate);
        if (this.birthday.isAfter(other.birthday)) {
            return 1; // other starszy
        } else if (this.birthday.isBefore(other.birthday)) {
            return -1; // other młodszy
        } else {
            return 0; // other równi
        }
    }
    public List<Person> getChildren() {
        List<Person> sortedChildren = new ArrayList<>(this.children);
        Collections.sort(sortedChildren, Collections.reverseOrder());
        //collection sort dziala przez to ze klasa impl comparable i na podst comapareTo() sortuje wywolujac to dla kazdego el
        return sortedChildren;
        }
    }

