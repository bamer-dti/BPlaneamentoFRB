package utils;

import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * by miguel.silva on 17/07/2017.
 */
public class SceneWithId extends Scene {
    private int id;

    public SceneWithId(Parent p) {
        super(p);
        this.id = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
