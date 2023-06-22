package org.l3ger0j.simpledimpledraw.utils;

import android.graphics.Path;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class SpecialPath extends Path implements Serializable {

    private static final long serialVersionUID = -7642039629858339221L;
    private final List<Action> actions = new LinkedList<>();

    private void readObject(@NonNull ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        for (Action action : actions) {
            action.perform(this);
        }
    }

    @Override
    public void lineTo(float x, float y) {
        actions.add(new Line(x, y));
        super.lineTo(x, y);
    }

    @Override
    public void moveTo(float x, float y) {
        actions.add(new Move(x, y));
        super.moveTo(x, y);
    }

    @Override
    public void quadTo(float x1, float y1, float x2, float y2) {
        actions.add(new Quad(x1, y1, x2, y2));
        super.quadTo(x1, y1, x2, y2);
    }

    private interface Action extends Serializable {

        void perform(Path path);
    }

    private static final class Move implements Action {

        private final float x, y;

        public Move(float x, float y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public void perform(@NonNull Path path) {
            path.moveTo(x, y);
        }
    }

    private static final class Line implements Action {

        private final float x, y;

        public Line(float x, float y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public void perform(@NonNull Path path) {
            path.lineTo(x, y);
        }
    }

    private static final class Quad implements Action {

        private final float x1, y1, x2, y2;

        private Quad(float x1, float y1, float x2, float y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }

        @Override
        public void perform(@NonNull Path path) {
            path.quadTo(x1, y1, x2, y2);
        }
    }

}