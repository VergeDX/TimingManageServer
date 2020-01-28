import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TestMap {
    public static void main(String[] args) {
        Map<Integer, String> integerStringMap = new HashMap<>();

        integerStringMap.put(1, "a");
        System.out.println(Objects.isNull(integerStringMap.get(2)));
    }
}
