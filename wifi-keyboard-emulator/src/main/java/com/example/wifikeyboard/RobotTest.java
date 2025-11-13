import java.awt.*;
import java.awt.event.KeyEvent;

public class RobotTest {
    public static void main(String[] args) throws Exception {
        Robot robot = new Robot();
        System.out.println("⏳ You have 3 seconds to focus Notepad...");
        Thread.sleep(3000);
        String text = "Hello from Robot!";
        for (char c : text.toCharArray()) {
            boolean upper = Character.isUpperCase(c);
            int keyCode = KeyEvent.getExtendedKeyCodeForChar(Character.toUpperCase(c));
            if (keyCode == KeyEvent.VK_UNDEFINED) continue;
            if (upper) robot.keyPress(KeyEvent.VK_SHIFT);
            robot.keyPress(keyCode);
            robot.keyRelease(keyCode);
            if (upper) robot.keyRelease(KeyEvent.VK_SHIFT);
            robot.delay(50);
        }
    }
}
