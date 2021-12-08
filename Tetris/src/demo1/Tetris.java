package demo1;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Tetris extends JPanel {

    //当前四方格
    private Tetromino currentOne = Tetromino.randomOne();
    //下一个四方格
    private Tetromino nextOne = Tetromino.randomOne();
    //游戏区域
    private Cell[][] wall = new Cell[18][9];
    //声明单元格的像素
    private static final int CELL_SIZE = 48;
    //得分池
    int[] scores_poll = {0,1,2,5,10};
    //以获得的总分数
    private int totalScore = 0;
    //消除的行数
    private int totalLine = 0;
    //当前游戏状态的值
    private int game_state = 0;
    String[] show_state = {"P[pause]","C[continue]","S[repaly]"};
    //游戏的状态,游戏中、暂停、游戏结束
    public static final int PLAYING = 0;
    public static final int PAUSE = 1;
    public static final int GAMEOVER = 2;
    //载入四方格图片
    public static BufferedImage I;
    public static BufferedImage J;
    public static BufferedImage L;
    public static BufferedImage O;
    public static BufferedImage S;
    public static BufferedImage T;
    public static BufferedImage Z;
    //背景图片
    public static BufferedImage backImage;
    static {
        try {
            I = ImageIO.read(new File("images/I.png"));
            J = ImageIO.read(new File("images/J.png"));
            L = ImageIO.read(new File("images/L.png"));
            O = ImageIO.read(new File("images/O.png"));
            S = ImageIO.read(new File("images/S.png"));
            T = ImageIO.read(new File("images/T.png"));
            Z = ImageIO.read(new File("images/Z.png"));
            backImage = ImageIO.read(new File("images/backImage.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(backImage,0,0,null);
        //平移坐标轴
        g.translate(22,15);
        //绘制游戏主区域
        paintWall(g);
        //绘制正在下落的四方格
        paintCurrentOne(g);
        //绘制下一个要出现的四方格
        paintNextOne(g);
        //绘制游戏得分
        paintScore(g);
        //绘制游戏状态
        paintState(g);
    }

    private void paintState(Graphics g) {
        if (game_state == PLAYING){
            g.drawString(show_state[PLAYING],500,660);
        }else if(game_state == PAUSE){
            g.drawString(show_state[PAUSE],500,660);
        }else if(game_state == GAMEOVER){
            g.drawString(show_state[GAMEOVER],500,660);
            g.setColor(Color.red);
            g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,40));
            g.drawString("GAMEOVER!",30,400);
        }
    }

    private void paintScore(Graphics g) {
        g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,30));
        g.drawString("SCORES: " + totalScore,500,250);
        g.drawString("LINES: " + totalLine,500,435);
    }

    private void paintNextOne(Graphics g) {
        Cell[] cells = nextOne.cells;
        for(Cell cell : cells){
            int x = cell.getCol() * CELL_SIZE + 370;
            int y = cell.getRow() * CELL_SIZE + 18;
            g.drawImage(cell.getImage(),x,y,null);
        }
    }

    private void paintCurrentOne(Graphics g) {
        Cell[] cells = currentOne.cells;
        for(Cell cell : cells){
            int x = cell.getCol() * CELL_SIZE;
            int y = cell.getRow() * CELL_SIZE;
            g.drawImage(cell.getImage(),x,y,null);
        }
    }

    private void paintWall(Graphics g) {
        for (int i = 0; i < wall.length; i++) {
            for (int j = 0; j < wall[i].length; j++) {
                int x = j * CELL_SIZE;
                int y = i * CELL_SIZE;
                Cell cell = wall[i][j];

                if (cell == null) {
                    //绘制矩形
                    g.drawRect(x,y,CELL_SIZE,CELL_SIZE);
                }else{
                    g.drawImage(cell.getImage(),x,y,null);
                }
            }
        }
    }
    //判断游戏是否出界
    public boolean outOfBounds(){
        Cell[] cells = currentOne.cells;
        for (Cell cell : cells){
            int row = cell.getRow();
            int col = cell.getCol();
            if(row < 0 || row > wall.length -1 || col < 0 || col > wall[0].length - 1 ){
                return true;
            }
        }
        return false;
    }

    //判断方块是否重合
    public boolean coincide(){
        Cell[] cells = currentOne.cells;
        for (Cell cell : cells){
            int row = cell.getRow();
            int col = cell.getCol();
            if(wall[row][col] != null){
                return true;
            }
        }
        return false;
    }

    //四方格左移
    public void moveLeftAction(){
        currentOne.moveLeft();
        //判断是否越界或重合
        if(outOfBounds() || coincide()){
            currentOne.moveRight();
        }
    }

    //四方格右移
    public void moveRightAction(){
        currentOne.moveRight();
        //判断是否越界或重合
        if(outOfBounds() || coincide()){
            currentOne.moveLeft();
        }
    }

    //旋转
    public void clockwiseAction(){
        currentOne.clockwise();
        if(outOfBounds() || coincide()){
            currentOne.anticlockwise();
        }
    }

    //判断是否游戏结束
    public boolean isGameOver(){
        Cell[] cells = nextOne.cells;
        for (Cell cell : cells){
            int row = cell.getRow();
            int col = cell.getCol();
            if(wall[row][col] != null){
                return true;
            }
        }
        return false;
    }

    //判断能否下落
    public boolean canDrop(){
        Cell[] cells = currentOne.cells;
        for (Cell cell : cells){
            int row = cell.getRow();
            int col = cell.getCol();
            if(row == wall.length-1){
                return false;
            }else if(wall[row + 1][col] != null){
                return false;
            }
        }
        return true;
    }

    //下落一格
    public void dropOneAction(){
        if (canDrop()){
            currentOne.moveDrop();
        }else{
            //嵌入wall中
            landToWall();
            //消行得分
            destoryLine();
            //游戏能否结束
            if(isGameOver()){
                game_state = GAMEOVER;
            }else{
                currentOne = nextOne;
                nextOne = Tetromino.randomOne();
            }
        }
    }

    //快速下降
    public void dropFast(){
        while (canDrop()){
            currentOne.moveDrop();
        }

        landToWall();
        destoryLine();
        if(isGameOver()){
            game_state = GAMEOVER;
        }else{
            currentOne = nextOne;
            nextOne = Tetromino.randomOne();
        }
    }

    //嵌入墙体
    private void landToWall() {
        Cell[] cells = currentOne.cells;
        for (Cell cell : cells){
            wall[cell.getRow()][cell.getCol()] = cell;
        }
    }

    //判断当前行是否已满
    public boolean isFullLine(int row){
        Cell[] cells = wall[row];
        for (Cell cell : cells){
            if (cell == null) {
                return false;
            }
        }
        return true;
    }

    //消除已满行数并得分
    public void destoryLine(){
        int lines = 0;
        Cell[] cells = currentOne.cells;
        for (Cell cell : cells){
            int row = cell.getRow();
            if (isFullLine(row)){
                lines++;
                for (int i = row;i > 0;i--){
                    System.arraycopy(wall[i - 1],0,wall[i],0,wall[0].length);
                    wall[0] = new Cell[9];
                }
            }
        }
        totalLine += lines;
        // int[] scores_poll = {0,1,2,5,10};
        if (lines < scores_poll.length){
            totalScore += scores_poll[lines];
        }else{
            totalScore += ((lines - scores_poll.length) * 5 + 15);
        }
    }

    public void start(){
        game_state = PLAYING;
        KeyListener l = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                switch(keyCode){
                    case KeyEvent.VK_LEFT:
                        moveLeftAction(); //左移
                        break;
                    case KeyEvent.VK_RIGHT:
                        moveRightAction(); //右移
                        break;
                    case KeyEvent.VK_DOWN:
                        dropOneAction(); //下落一行
                        break;
                    case KeyEvent.VK_UP:
                        clockwiseAction(); //旋转
                        break;
                    case KeyEvent.VK_SPACE:
                        dropFast(); //瞬间下落
                        break;
                    case KeyEvent.VK_P:
                        if(game_state == PLAYING){
                            game_state = PAUSE;
                        }
                        break;
                    case KeyEvent.VK_C:
                        if(game_state == PAUSE){
                            game_state = PLAYING;
                        }
                        break;
                    case KeyEvent.VK_S:
                        //重新开始
                        game_state = PLAYING;
                        wall = new Cell[18][9];
                        currentOne = Tetromino.randomOne();
                        nextOne = Tetromino.randomOne();
                        totalLine = 0;
                        totalScore = 0;
                        break;
                }
            }
        };
        this.addKeyListener(l);
        //将俄罗斯方块窗口获得焦点
        this.requestFocus();
        while(true){
            //每0.5s自动下落
            if (game_state == PLAYING){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //下降一格
                dropOneAction();
            }
            //重新绘制图像
            repaint();
        }
    }

    public static void main(String[] args) {
        //创建一个窗口对象
        JFrame frame = new JFrame("俄罗斯方块");
        //设置可见
        frame.setVisible(true);
        //设置窗口尺寸
        frame.setSize(810,940);
        //设置窗口居中
        frame.setLocationRelativeTo(null);
        //设置窗口关闭时程序终止
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //创建游戏界面
        Tetris tetris = new Tetris();
        //将面板嵌入窗口
        frame.add(tetris);
        //游戏开始
        tetris.start();
    }
}

