import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class TestGsonArrayList {
    public static void main(String[] args) {
        Gson gson = new Gson();
        List<String> list = new ArrayList<>();

        list.add("w");
        list.add("w");

        String s = gson.toJson(list);
        System.out.println(s);
    }
}
