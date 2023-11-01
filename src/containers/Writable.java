package containers;

import javax.swing.*;
import java.util.HashMap;

public interface Writable {
    HashMap<String, JComponent> getComponentMap();
    default String getClassName() {
        String res = this.getClass().getName();
        res = res.substring(res.lastIndexOf('.') + 1).toLowerCase();
        return res;
    }
}
