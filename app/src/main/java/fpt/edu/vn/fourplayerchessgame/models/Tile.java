package fpt.edu.vn.fourplayerchessgame.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fpt.edu.vn.fourplayerchessgame.BoardView;

/**
 * Created by Thanh on 22/09/2016.
 */

public class Tile {
    private int row;
    private int column;
    private String value;

    public Tile() {
    }

    public Tile(int row, int column, String value) {
        this.row = row;
        this.column = column;
        this.value = value;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Tile{" +
                "row=" + row +
                ", column=" + column +
                ", value='" + value + '\'' +
                '}';
    }

    public boolean isOccupied() {
        return !value.equals("xx") && !value.equals("  ");
    }

}
