package demo1;

import java.awt.image.BufferedImage;

/*
小方块类
    属性：行、列、每个小方格图片
    方法：左移、右移、下移
 */
public class Cell {
    private int row;
    private int col;
    private BufferedImage image;

    public Cell() {
    }

    public Cell(int row, int col, BufferedImage image) {
        this.row = row;
        this.col = col;
        this.image = image;
    }

    @Override
    public String toString() {
        return "cell{" +
                "row=" + row +
                ", col=" + col +
                ", image=" + image +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cell cell = (Cell) o;

        if (row != cell.row) return false;
        if (col != cell.col) return false;
        return image != null ? image.equals(cell.image) : cell.image == null;
    }

    @Override
    public int hashCode() {
        int result = row;
        result = 31 * result + col;
        result = 31 * result + (image != null ? image.hashCode() : 0);
        return result;
    }

    //左移一格
    public void left(){
        col--;
    }
    //右移一格
    public void right(){
       col++;
    }
    //下降一格
    public void drop(){
        row++;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }
}
