package menu;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тест для інтерфейсу Command.
 */
class CommandTest {

    @Test
    void testCommandExecutes() {
        // Перевіряємо що лямбда-реалізація Command працює
        final boolean[] executed = {false};
        Command cmd = () -> executed[0] = true;
        cmd.execute();
        assertTrue(executed[0]);
    }

    @Test
    void testCommandMultipleExecutions() {
        final int[] counter = {0};
        Command cmd = () -> counter[0]++;
        cmd.execute();
        cmd.execute();
        cmd.execute();
        assertEquals(3, counter[0]);
    }
}
