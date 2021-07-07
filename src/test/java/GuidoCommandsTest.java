import me.googas.guido.commands.GuidoCommands;

import java.util.Arrays;
import java.util.List;

public class GuidoCommandsTest {

    public static void main(String[] args) {
        GuidoCommands commands = new GuidoCommands();
        List<Integer> list = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        System.out.println(commands.getRandom(list, 3));
    }
}
