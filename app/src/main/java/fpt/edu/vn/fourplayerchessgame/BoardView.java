package fpt.edu.vn.fourplayerchessgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fpt.edu.vn.fourplayerchessgame.models.Move;
import fpt.edu.vn.fourplayerchessgame.models.Tile;

/**
 * Created by Thanh on 19/09/2016.
 */
public class BoardView extends View {

    private Paint mPaint;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private float mX, mY;
    private float border = 10;
    private float squareSize;
    Context context;
    private Tile selectedPiece = null; // no piece on board is selected
    private List<Move> possibleMoves = new ArrayList<>();

    // "  ":  not occupied, "xx": out of board
    private static String chessBoard[][] = {
            {"xx","xx","xx","r1","k1","b1","a1","q1","b1","k1","r1","xx","xx","xx"},
            {"xx","xx","xx","p1","p1","p1","p1","p1","p1","p1","p1","xx","xx","xx"},
            {"xx","xx","xx","  ","  ","  ","  ","  ","  ","  ","  ","xx","xx","xx"},
            {"r4","p4","  ","  ","  ","  ","  ","  ","  ","  ","  ","  ","p2","r2"},
            {"k4","p4","  ","  ","  ","  ","  ","  ","  ","  ","  ","  ","p2","k2"},
            {"b4","p4","  ","  ","  ","  ","  ","  ","  ","  ","  ","  ","p2","b2"},
            {"a4","p4","  ","  ","  ","  ","  ","  ","  ","  ","  ","  ","p2","q2"},
            {"q4","p4","  ","p3","  ","  ","  ","  ","  ","  ","  ","  ","p2","a2"},
            {"b4","p4","  ","  ","  ","  ","  ","  ","  ","  ","  ","  ","p2","b2"},
            {"k4","p4","  ","  ","  ","  ","  ","  ","  ","  ","  ","  ","p2","k2"},
            {"r4","p4","  ","  ","  ","  ","  ","  ","  ","  ","  ","  ","p2","r2"},
            {"xx","xx","xx","  ","p3","  ","p3","  ","  ","  ","  ","xx","xx","xx"},
            {"xx","xx","xx","  ","  ","p3","  ","p3","p3","p3","p3","xx","xx","xx"},
            {"xx","xx","xx","r3","k3","b3","q3","a3","b3","k3","r3","xx","xx","xx"},
    };

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        mX = 0;
        mY = 0;
        // set a new Paint with the desired attributes
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.parseColor("#CD5C5C"));
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(4f);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Canvas will draw onto the defined Bitmap
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        squareSize = (Math.min(w,h) - 2*border) / 14;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // draw here
        canvas.drawColor(Color.parseColor("#E0E0E0"));
        //canvas.drawRect(mX, mY, mX + 400, mY + 400, mPaint);
        drawChessBoard(canvas);

    }

    private void drawChessBoard(Canvas canvas) {
        //draw chess board
        for (int i=0;i<14;i++) {
            for (int j=0;j<14;j++) {
                if (!chessBoard[i][j].equals("xx")) {
                    if ((i+j)%2 != 0) {
                        mPaint.setColor(Color.parseColor("#F5F5F5"));
                        canvas.drawRect(j*squareSize+border, i*squareSize+border,
                                (j+1)*squareSize+border, (i+1)*squareSize+border, mPaint);
                    } else {
                        mPaint.setColor(Color.parseColor("#9E9E9E"));
                        canvas.drawRect(j*squareSize+border, i*squareSize+border,
                                (j+1)*squareSize+border, (i+1)*squareSize+border, mPaint);
                    }
                }
            }
        }

        // draw possible move
        for (Move move: possibleMoves) {
            int row = move.getRow();
            int column = move.getColumn();
            mPaint.setColor(Color.parseColor("#FF5722"));
            mPaint.setAlpha(80);
            canvas.drawRect(column*squareSize+border, row*squareSize+border,
                    (column+1)*squareSize+border, (row+1)*squareSize+border, mPaint);
        }

        // draw chess pieces
        for (int i=0;i<14;i++) {
            for (int j = 0; j < 14; j++) {
                if (!chessBoard[i][j].equals("xx") && !chessBoard[i][j].equals("  ")) {
                    RectF dst = new RectF();
                    dst.set(j*squareSize+border, i*squareSize+border, (j+1)*squareSize+border, (i+1)*squareSize+border);

                    String piece = chessBoard[i][j];
                    String packageName = context.getPackageName();
                    int resourceID = getResources().getIdentifier(piece, "drawable", packageName);

                    canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), resourceID), null, dst, null);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        int row = (int) Math.floor((y - border)/squareSize);
        int column = (int) Math.floor((x - border)/squareSize);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("BoardView", "touchedTile: " + row + " , " + column);

                if (row > 13 || column > 13) {
                    break;
                }

                Tile touchedTile = new Tile(row, column, chessBoard[row][column]); // touched tile
                if (selectedPiece == null) { // if no piece is selected
                    if (touchedTile.isOccupied()) { // touched tile is occupied
                        // set selectedPiece as touchedTile
                        selectedPiece = touchedTile;
                        getPossibleMoves();
                    }
                } else { // if a piece is selected
//                    if (touchedTile.isOccupied()) { // touched tile is occupied
//                        if (selectedPiece.getValue().charAt(1) == touchedTile.getValue().charAt(1)) { // touched tile is an ally
//                            // set selectedPiece as touchedTile
//                            selectedPiece = touchedTile;
//                            getPossibleMoves();
//                        } else { // touched tile is an enemy
//                            // move selectedPiece to touchedTile position (capture the enemy)
//                            moveSelectedPiece(row, column);
//                        }
//                    } else { // touched tile is not occupied
//                        // move selectedPiece to touchedTile position
//                        moveSelectedPiece(row, column);
//                    }
                    getPossibleMoves();
                    if (isPossibleMove(row, column)) {
                        moveSelectedPiece(row, column); // includes moving to empty tile and capturing an enemy
                    } else {
                        if (touchedTile.isOccupied() && isAllyWithSelectedPiece(row, column)) { // select another ally piece
                            selectedPiece = touchedTile;
                            getPossibleMoves();
                        } else { // touch outside to cancel selection
                            selectedPiece = null;
                            possibleMoves = new ArrayList<>();
                        }
                    }
                }
                invalidate();
                break;
        }
        return true;
    }

    private void moveSelectedPiece(int row, int column) {
        chessBoard[selectedPiece.getRow()][selectedPiece.getColumn()] = "  ";
        chessBoard[row][column] = selectedPiece.getValue();
        selectedPiece = null;
        possibleMoves = new ArrayList<>();
    }

    private void getPossibleMoves() {
        possibleMoves = new ArrayList<>();
        String type = selectedPiece.getValue().charAt(0) + "";

        switch (type) {
            case "k": //Knight
                possibleMovesKnight();
                break;
            case "b": //Bishop
                possibleMovesBishop();
                break;
            case "r": //Rook
                possibleMovesRook();
                break;
            case "p": //Pawn
                possibleMovesPawn();
                break;
            case "q": //Queen
                possibleMovesQueen();
                break;
            case "a": //King
                possibleMovesKing();
                break;
            default:
        }
    }

    private void possibleMovesKnight() {
        int selectedRow = selectedPiece.getRow();
        int selectedColumn = selectedPiece.getColumn();

        possibleMoves.add(new Move(selectedRow - 2, selectedColumn - 1));
        possibleMoves.add(new Move(selectedRow - 2, selectedColumn + 1));
        possibleMoves.add(new Move(selectedRow + 2, selectedColumn - 1));
        possibleMoves.add(new Move(selectedRow + 2, selectedColumn + 1));
        possibleMoves.add(new Move(selectedRow - 1, selectedColumn - 2));
        possibleMoves.add(new Move(selectedRow - 1, selectedColumn + 2));
        possibleMoves.add(new Move(selectedRow + 1, selectedColumn - 2));
        possibleMoves.add(new Move(selectedRow + 1, selectedColumn + 2));

        // remove invalid moves
        Iterator<Move> iterator = possibleMoves.iterator();
        while(iterator.hasNext()) {
            Move move = iterator.next();
            if(invalidMove(move.getRow(),move.getColumn())
                    || isAllyWithSelectedPiece(move.getRow(), move.getColumn())) {
                iterator.remove();
            }
        }
    }

    private void possibleMovesBishop() {
        int selectedRow = selectedPiece.getRow();
        int selectedColumn = selectedPiece.getColumn();

        int x;
        int y;

        // North - West direction
        x = selectedRow - 1;
        y = selectedColumn - 1;
        while (!invalidMove(x, y)) {
            if (isOccupied(x, y)) {
                if (isAllyWithSelectedPiece(x, y)) {
                    break;
                } else {
                    possibleMoves.add(new Move(x, y));
                    break;
                }
            } else {
                possibleMoves.add(new Move(x, y));
                x--;
                y--;
            }
        }

        // North - East direction
        x = selectedRow - 1;
        y = selectedColumn + 1;
        while (!invalidMove(x, y)) {
            if (isOccupied(x, y)) {
                if (isAllyWithSelectedPiece(x, y)) {
                    break;
                } else {
                    possibleMoves.add(new Move(x, y));
                    break;
                }
            } else {
                possibleMoves.add(new Move(x, y));
                x--;
                y++;
            }
        }

        // South - East direction
        x = selectedRow + 1;
        y = selectedColumn + 1;
        while (!invalidMove(x, y)) {
            if (isOccupied(x, y)) {
                if (isAllyWithSelectedPiece(x, y)) {
                    break;
                } else {
                    possibleMoves.add(new Move(x, y));
                    break;
                }
            } else {
                possibleMoves.add(new Move(x, y));
                x++;
                y++;
            }
        }

        // South - West direction
        x = selectedRow + 1;
        y = selectedColumn - 1;
        while (!invalidMove(x, y)) {
            if (isOccupied(x, y)) {
                if (isAllyWithSelectedPiece(x, y)) {
                    break;
                } else {
                    possibleMoves.add(new Move(x, y));
                    break;
                }
            } else {
                possibleMoves.add(new Move(x, y));
                x++;
                y--;
            }
        }
    }

    private void possibleMovesRook() {
        int selectedRow = selectedPiece.getRow();
        int selectedColumn = selectedPiece.getColumn();

        int x;
        int y;

        // UP direction
        x = selectedRow - 1;
        y = selectedColumn;
        while (!invalidMove(x, y)) {
            if (isOccupied(x, y)) {
                if (isAllyWithSelectedPiece(x, y)) {
                    break;
                } else {
                    possibleMoves.add(new Move(x, y));
                    break;
                }
            } else {
                possibleMoves.add(new Move(x, y));
                x--;
            }
        }

        // DOWN direction
        x = selectedRow + 1;
        y = selectedColumn;
        while (!invalidMove(x, y)) {
            if (isOccupied(x, y)) {
                if (isAllyWithSelectedPiece(x, y)) {
                    break;
                } else {
                    possibleMoves.add(new Move(x, y));
                    break;
                }
            } else {
                possibleMoves.add(new Move(x, y));
                x++;
            }
        }

        // LEFT direction
        x = selectedRow;
        y = selectedColumn - 1;
        while (!invalidMove(x, y)) {
            if (isOccupied(x, y)) {
                if (isAllyWithSelectedPiece(x, y)) {
                    break;
                } else {
                    possibleMoves.add(new Move(x, y));
                    break;
                }
            } else {
                possibleMoves.add(new Move(x, y));
                y--;
            }
        }

        // RIGHT direction
        x = selectedRow;
        y = selectedColumn + 1;
        while (!invalidMove(x, y)) {
            if (isOccupied(x, y)) {
                if (isAllyWithSelectedPiece(x, y)) {
                    break;
                } else {
                    possibleMoves.add(new Move(x, y));
                    break;
                }
            } else {
                possibleMoves.add(new Move(x, y));
                y++;
            }
        }
    }

    private void possibleMovesPawn() {
        int selectedRow = selectedPiece.getRow();
        int selectedColumn = selectedPiece.getColumn();
        char side = selectedPiece.getValue().charAt(1);

        switch (side) {
            case '1': // UP side
                if (selectedRow == 1) { // the Pawn is at starting position
                    if (!isOccupied(selectedRow + 2, selectedColumn)) {
                        possibleMoves.add(new Move(selectedRow + 2, selectedColumn));
                    }
                }
                if (!invalidMove(selectedRow + 1, selectedColumn) && !isOccupied(selectedRow + 1, selectedColumn)) {
                    possibleMoves.add(new Move(selectedRow + 1, selectedColumn));
                }
                // if there is enemy to capture
                if (isEnemyWithSelectedPiece(selectedRow + 1, selectedColumn + 1)) {
                    possibleMoves.add(new Move(selectedRow + 1, selectedColumn + 1));
                }
                if (isEnemyWithSelectedPiece(selectedRow + 1, selectedColumn - 1)) {
                    possibleMoves.add(new Move(selectedRow + 1, selectedColumn - 1));
                }
                break;
            case '2': // RIGHT side
                if (selectedColumn == 12) { // the Pawn is at starting position
                    if (!isOccupied(selectedRow, selectedColumn - 2)) {
                        possibleMoves.add(new Move(selectedRow, selectedColumn - 2));
                    }
                }
                if (!invalidMove(selectedRow, selectedColumn - 1) && !isOccupied(selectedRow, selectedColumn - 1)) {
                    possibleMoves.add(new Move(selectedRow, selectedColumn - 1));
                }
                // if there is enemy to capture
                if (isEnemyWithSelectedPiece(selectedRow - 1, selectedColumn - 1)) {
                    possibleMoves.add(new Move(selectedRow - 1, selectedColumn - 1));
                }
                if (isEnemyWithSelectedPiece(selectedRow + 1, selectedColumn - 1)) {
                    possibleMoves.add(new Move(selectedRow + 1, selectedColumn - 1));
                }
                break;
            case '3': // DOWN side
                if (selectedRow == 12) { // the Pawn is at starting position
                    if (!isOccupied(selectedRow - 2, selectedColumn)) {
                        possibleMoves.add(new Move(selectedRow - 2, selectedColumn));
                    }
                }
                if (!invalidMove(selectedRow - 1, selectedColumn) && !isOccupied(selectedRow - 1, selectedColumn)) {
                    possibleMoves.add(new Move(selectedRow - 1, selectedColumn));
                }
                // if there is enemy to capture
                if (isEnemyWithSelectedPiece(selectedRow - 1, selectedColumn - 1)) {
                    possibleMoves.add(new Move(selectedRow - 1, selectedColumn - 1));
                }
                if (isEnemyWithSelectedPiece(selectedRow - 1, selectedColumn + 1)) {
                    possibleMoves.add(new Move(selectedRow - 1, selectedColumn + 1));
                }
                break;
            case '4': // LEFT side
                if (selectedColumn == 1) { // the Pawn is at starting position
                    if (!isOccupied(selectedRow, selectedColumn + 2)) {
                        possibleMoves.add(new Move(selectedRow, selectedColumn + 2));
                    }
                }
                if (!invalidMove(selectedRow, selectedColumn + 1) && !isOccupied(selectedRow, selectedColumn + 1)) {
                    possibleMoves.add(new Move(selectedRow, selectedColumn + 1));
                }
                // if there is enemy to capture
                if (isEnemyWithSelectedPiece(selectedRow - 1, selectedColumn + 1)) {
                    possibleMoves.add(new Move(selectedRow - 1, selectedColumn + 1));
                }
                if (isEnemyWithSelectedPiece(selectedRow + 1, selectedColumn + 1)) {
                    possibleMoves.add(new Move(selectedRow + 1, selectedColumn + 1));
                }
                break;
            default:
        }

    }

    private void possibleMovesQueen() {
        possibleMovesBishop();
        possibleMovesRook();
    }

    private void possibleMovesKing() {
        int selectedRow = selectedPiece.getRow();
        int selectedColumn = selectedPiece.getColumn();

        possibleMoves.add(new Move(selectedRow - 1, selectedColumn - 1));
        possibleMoves.add(new Move(selectedRow - 1, selectedColumn));
        possibleMoves.add(new Move(selectedRow - 1, selectedColumn + 1));
        possibleMoves.add(new Move(selectedRow + 1, selectedColumn - 1));
        possibleMoves.add(new Move(selectedRow + 1, selectedColumn));
        possibleMoves.add(new Move(selectedRow + 1, selectedColumn + 1));
        possibleMoves.add(new Move(selectedRow, selectedColumn - 1));
        possibleMoves.add(new Move(selectedRow, selectedColumn + 1));

        // remove invalid moves
        Iterator<Move> iterator = possibleMoves.iterator();
        while(iterator.hasNext()) {
            Move move = iterator.next();
            if(invalidMove(move.getRow(),move.getColumn())
                    || isAllyWithSelectedPiece(move.getRow(), move.getColumn())) {
                iterator.remove();
            }
        }
    }

    private boolean invalidMove(int row, int column) {
        return row < 0 || row > 13 || column < 0 || column > 13 || chessBoard[row][column].equals("xx");
    }

    private boolean isOccupied(int row, int column) {
        return !invalidMove(row, column) && !chessBoard[row][column].equals("  ");
    }

    private boolean isAllyWithSelectedPiece(int row, int column) {
        return isOccupied(row, column) && selectedPiece.getValue().charAt(1) == chessBoard[row][column].charAt(1);
    }

    private boolean isEnemyWithSelectedPiece(int row, int column) {
        return isOccupied(row, column) && selectedPiece.getValue().charAt(1) != chessBoard[row][column].charAt(1);
    }

    private boolean isPossibleMove(int row, int column) {
        for(Move move: possibleMoves) {
            if (row == move.getRow() && column == move.getColumn()) {
                return true;
            }
        }
        return false;
    }
}
