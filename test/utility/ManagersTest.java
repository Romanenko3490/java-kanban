package utility;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    Managers managers = new Managers();

    @Test
    public void shellReturnNotNull() {
        assertNotNull(managers.getDefault());
        assertNotNull(managers.getDefaultHistory());
    }

}