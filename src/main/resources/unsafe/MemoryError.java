
import java.util.ArrayList;
import java.util.List;
public class Main {

    public static void main(String[] args) {
        ArrayList<Byte[]> list = new ArrayList<>();
        while (true) {
            list.add(new Byte[100000]);
        }
    }
}