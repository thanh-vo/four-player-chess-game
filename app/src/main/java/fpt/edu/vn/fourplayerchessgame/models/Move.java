package fpt.edu.vn.fourplayerchessgame.models;

/**
 * Created by Thanh on 22/09/2016.
 */

public class Move {
    private int row;
    private int column;

    public Move(int row, int column) {
        this.row = row;
        this.column = column;
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

    @Override
    public String toString() {
        return "Move{" +
                "row=" + row +
                ", column=" + column +
                '}';
    }
}
