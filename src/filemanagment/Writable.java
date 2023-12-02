package filemanagment;

import javax.swing.*;
import java.util.HashMap;

public interface Writable {
    /**
     * Provides with hashmap of components whose data will be written and their corresponding tags.
     * @return HashMap of components nad its tag.
     */
    HashMap<String, JComponent> getComponentMap();
    default String getClassName() {
        String res = this.getClass().getName();
        int pivot = res.indexOf('$');
        res = res.substring(pivot == -1 ? res.lastIndexOf('.') + 1 : pivot + 1).toLowerCase();
        return res;
    }
}
