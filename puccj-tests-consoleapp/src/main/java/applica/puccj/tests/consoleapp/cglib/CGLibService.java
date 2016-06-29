package applica.puccj.tests.consoleapp.cglib;

/**
 * Created by bimbobruno on 27/10/15.
 */
public class CGLibService {

    private String name;

    public String getValue() {
        return "CGLibService";
    }

    public void print() {
        System.out.println("Not enhanced service");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
