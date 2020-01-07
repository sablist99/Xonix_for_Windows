import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class Game extends JFrame {

    private final int POINT_SIZE = 10;
    private final int FIELD_WIDTH = 960 / POINT_SIZE;
    private final int FIELD_HEIGHT = 640 / POINT_SIZE;
    private final int FIELD_DX = 6;
    private final int FIELD_DY = 80;
    private final int START_LOCATION = 150;
    private final int LEFT = 37; // Направления
    private final int UP = 38;
    private final int RIGHT = 39;
    private final int DOWN = 40;
    private final int ESC = 27;
    private final int PERCENT_OF_WATER_CAPTURE = 75;
    private final String FORMAT_STRING = "Score: %d %20s %d %20s %2.0f%% %20s %d";

    private final Font font = new Font("", Font.BOLD, 21);
    private Random random = new Random();
    private Canvas canvas = new Canvas();
    private JLabel board = new JLabel();
    private Delay delay = new Delay();
    private Field field = new Field();
    private Xonix xonix = new Xonix();
    private Balls balls = new Balls();
    private Cube cube = new Cube();
    private GameOver gameover = new GameOver();
    private Pause pause = new Pause();
    private Bonuses bonuses = new Bonuses();
    private Records records = new Records();

    private Color COLOR_LAND = new Color(23, 171, 117, 255);
    private Color COLOR_WATER = new Color(1, 0, 28, 255);
    private Color COLOR_TRACK = new Color(255, 212, 30, 255);
    private Color COLOR_BONUS = new Color(100, 109, 237, 255);

    private int show_delay = 35;
    private boolean check_pause = false;

    public static void main(String[] args) throws IOException {
        Game game = new Game();
        game.go();
    }

    private Game() {
        //Dimension sSize = Toolkit.getDefaultToolkit ().getScreenSize ();
        //final int WIDTH = sSize.width;
        //final int HEIGHT = sSize.height;
        String TITLE = "Xonix";
        setTitle(TITLE);
        setBounds(START_LOCATION, START_LOCATION, FIELD_WIDTH * POINT_SIZE + FIELD_DX, FIELD_HEIGHT * POINT_SIZE + FIELD_DY); //Параметры окна
        //setAlwaysOnTop(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        //Информационная строка
        board.setFont(font);//Установка шрифта
        board.setOpaque(true);//От прозрачности
        board.setBackground(Color.black);
        board.setForeground(Color.white);
        board.setHorizontalAlignment(JLabel.CENTER);

        JMenuBar menuBar = new JMenuBar();

        JMenu game_menu = new JMenu("Игра");
        JMenu speed_menu = new JMenu("Скорость");
        JMenu records_menu = new JMenu("Рекорды");
        JMenu help_menu = new JMenu("Помощь");

        JMenuItem new_game_item = new JMenuItem("Новая игра (N)");
        JMenuItem play_pause_item = new JMenuItem("Пуск/Пауза (Space)");
        JMenuItem exit_item = new JMenuItem("Выход (ESC)");
        JMenuItem easy_item = new JMenuItem("Легко (1)");
        JMenuItem normal_item = new JMenuItem("Нормально (2)");
        JMenuItem hard_item = new JMenuItem("Сложно (3)");
        JMenuItem records_item = new JMenuItem("Рекорды");
        JMenuItem help_item = new JMenuItem("Помощь");

        game_menu.add(new_game_item);
        game_menu.add(play_pause_item);
        game_menu.addSeparator();
        game_menu.add(exit_item);
        speed_menu.add(easy_item);
        speed_menu.add(normal_item);
        speed_menu.add(hard_item);
        records_menu.add(records_item);
        help_menu.add(help_item);

        menuBar.add(game_menu);
        menuBar.add(speed_menu);
        menuBar.add(records_menu);
        menuBar.add(help_menu);

        setJMenuBar(menuBar);//Установка меню
        add(BorderLayout.CENTER, canvas);//Размещение поля на JPanel
        add(BorderLayout.SOUTH, board);//Размещение строки на JPanel

        addKeyListener(new KeyAdapter() {//Обработка нажатий
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() >= LEFT && e.getKeyCode() <= DOWN)//Установка направления хода
                    xonix.setDirection(e.getKeyCode());
                if (e.getKeyCode() == ESC)//Завершение игры
                    System.exit(0);
                if (e.getKeyCode() == 49)//Легкий режим
                    show_delay = 50;
                if (e.getKeyCode() == 50)//Нормальный режим
                    show_delay = 35;
                if (e.getKeyCode() == 51)//Сложный режим
                    show_delay = 20;
                if (e.getKeyCode() == 78) {//Новая игра
                    field.init();
                    xonix.init();
                    xonix.setLevel(1);
                    xonix.setCountLives(3);
                    balls.remove();
                    balls.add();
                    bonuses.remove();
                    cube.init();
                    field.setCountScore(0);
                    gameover.setGameOver(false);
                    gameover.setFlag(true);
                }
                if (e.getKeyCode() == 32) {//Пауза
                    check_pause = !check_pause;
                }
                //System.out.println(e.getKeyCode());
            }
        });

        new_game_item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                field.init();
                xonix.init();
                xonix.setLevel(1);
                xonix.setCountLives(3);
                balls.remove();
                balls.add();
                bonuses.remove();
                cube.init();
                field.setCountScore(0);
                gameover.setGameOver(false);
                gameover.setFlag(true);
            }
        });

        play_pause_item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                check_pause = !check_pause;
            }
        });

        exit_item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        easy_item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                show_delay = 50;
            }
        });

        normal_item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                show_delay = 35;
            }
        });

        hard_item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                show_delay = 20;
            }
        });

        records_item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                records.output();
            }
        });

        help_item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "По всем вопросам - sablist99@bk.ru", "Помощь", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        setVisible(true);
    }

    private void go() { // Игровой цикл
        while (true) {
            while (check_pause) {//Пауза
                canvas.repaint();
                delay.wait(show_delay);
            }
            if (!gameover.isGameOver()) {//Если не конец игры
                xonix.move();
                balls.move();
                cube.move();
                bonuses.add();
                bonuses.check_collect_bonus();
                canvas.repaint();
                delay.wait(show_delay);
                if (xonix.isSelfCrosed() || balls.isHitTrackOrXonix() || cube.isHitXonix()) {//Если Xonix ранен
                    xonix.decreaseCountLives();
                    if (xonix.getCountLives() > 0) {
                        xonix.init();
                        field.clearTrack();
                        delay.wait(show_delay * 10);
                    }
                }
                if (field.getCurrentPercent() >= PERCENT_OF_WATER_CAPTURE) {
                    field.init();
                    xonix.init();
                    xonix.level_up();
                    cube.init();
                    balls.add();
                    delay.wait(show_delay * 10);
                }
            }
            gameover.record();
            board.setText(String.format(FORMAT_STRING, field.getCountScore(), "Xn:", xonix.getCountLives(), "Full:", field.getCurrentPercent(), "Lvl:", xonix.getLevel()));
        }

    }

    class Xonix {

        private int x, y, direction, countLives = 3, level = 1;
        private boolean isWater, isSelfCross;

        Xonix() {
            init();
        }

        void init() {
            y = 0;
            x = FIELD_WIDTH / 2;
            direction = 0;
            isWater = false;
        }

        int getX() { return x; }
        int getY() { return y; }
        int getCountLives() { return countLives; }
        int getLevel() {return level;}

        void decreaseCountLives() { countLives--; }

        void setCountLives(int c) { countLives = c;}

        void setDirection(int direction) { this.direction = direction; }

        void setLevel(int l) { level = l;};

        void level_up() {level++;}

        void addCountLives() {countLives++;}

        void move() {
            if (direction == LEFT) x--;
            if (direction == RIGHT) x++;
            if (direction == UP) y--;
            if (direction == DOWN) y++;
            if (x < 0) x = 0;
            if (y < 0) y = 0;
            if (y > FIELD_HEIGHT - 1) y = FIELD_HEIGHT - 1;
            if (x > FIELD_WIDTH - 1) x = FIELD_WIDTH - 1;
            isSelfCross = field.getColor(x, y) == 3;
            if (field.getColor(x, y) == 1 && isWater) {
                direction = 0;
                isWater = false;
                field.tryToFill();
            }
            if (field.getColor(x, y) == 2) {
                isWater = true;
                field.setColor(x, y, 3);
            }
        }

        boolean isSelfCrosed() { return isSelfCross; }

        void paint(Graphics g) {
            g.setColor((field.getColor(x, y) == 1) ? COLOR_TRACK : Color.white);
            g.fillRect(x*POINT_SIZE, y*POINT_SIZE, POINT_SIZE, POINT_SIZE);
            g.setColor((field.getColor(x, y) == 1) ? Color.white : COLOR_TRACK);
            g.fillRect(x*POINT_SIZE + 3, y*POINT_SIZE + 3, POINT_SIZE - 6, POINT_SIZE - 6);
        }
    }

    class Balls {
        private ArrayList<Ball> balls = new ArrayList<Ball>();

        Balls() {
            add();
        }

        int sizeOfArray () {
            return balls.size();
        }

        void add() { balls.add(new Ball()); }

        void move() { for (Ball ball : balls) ball.move(); }

        void remove() {
            if (balls.size() > 0) {
                balls.subList(0, balls.size()).clear();
            }
        }

        void removeOne() {
            balls.remove(1);
        }

        ArrayList<Ball> getBalls() { return balls; }

        boolean isHitTrackOrXonix() {
            for (Ball ball : balls) if (ball.isHitTrackOrXonix()) return true;
            return false;
        }

        void paint(Graphics g) { for (Ball ball : balls) ball.paint(g); }
    }

    class Ball {
        private int x, y, dx, dy;

        Ball() {
            do {
                x = random.nextInt(FIELD_WIDTH);
                y = random.nextInt(FIELD_HEIGHT);
            } while (field.getColor(x, y) == 1);
            dx = random.nextBoolean()? 1 : -1;
            dy = random.nextBoolean()? 1 : -1;
        }

        void updateDXandDY() {
            if (field.getColor(x + dx, y) == 1) dx = -dx;
            if (field.getColor(x, y + dy) == 1) dy = -dy;
        }

        void move() {
            updateDXandDY();
            x += dx;
            y += dy;
        }

        int getX() { return x; }
        int getY() { return y; }

        boolean isHitTrackOrXonix() {
            updateDXandDY();
            if (field.getColor(x + dx, y + dy) == 3) return true;
            return x + dx == xonix.getX() && y + dy == xonix.getY();
        }

        void paint(Graphics g) {
            g.setColor(Color.white);
            g.fillOval(x*POINT_SIZE, y*POINT_SIZE, POINT_SIZE, POINT_SIZE);
            g.setColor(COLOR_LAND);
            g.fillOval(x*POINT_SIZE + 2, y*POINT_SIZE + 2, POINT_SIZE - 4, POINT_SIZE - 4);
        }
    }

    class Cube {
        private int x, y, dx, dy;

        Cube() {
            init();
        }

        void init() { x = dx = dy = 1; }

        void updateDXandDY() {
            //System.out.println(" " + x + " " + (x + dx) + " " + y + " " + (y + dy));
            if (field.getColor(x + dx, y) != 1) dx = -dx;
            if (field.getColor(x, y + dy) != 1) dy = -dy;
        }

        void move() {
            updateDXandDY();
            x += dx;
            y += dy;
        }

        boolean isHitXonix() {
            updateDXandDY();
            return x + dx == xonix.getX() && y + dy == xonix.getY();
        }

        void paint(Graphics g) {
            g.setColor(COLOR_WATER);
            g.fillRect(x*POINT_SIZE, y*POINT_SIZE, POINT_SIZE, POINT_SIZE);
            g.setColor(COLOR_LAND);
            g.fillRect(x*POINT_SIZE + 2, y*POINT_SIZE + 2, POINT_SIZE - 4, POINT_SIZE - 4);
        }
    }

    class Bonuses {
        /*
        type = 1 - добавление жизни
        type = 2 - удаление врага на море
         */
        private ArrayList<Bonus> bonuses = new ArrayList<Bonus>();
        private int step = 0;

        void add() {
            if (field.getCountScore() > 30000 * step + 30000) {
                int type, count = 1;
                for (Bonus bonus : bonuses) {
                    if (bonus.getType() == 2) {
                        count++;
                    }
                }
                if (balls.sizeOfArray() == count) {
                    type = 1;
                } else {
                    Random random = new Random();
                    type = random.nextInt(2) + 1;
                }
                bonuses.add(new Bonus(type));
                step++;
            }
        }

        void remove() {
            if (bonuses.size() > 0) {
                bonuses.subList(0, bonuses.size()).clear();
            }
            step = 0;
        }

        int sizeOfArray () {
            return bonuses.size();
        }

        void check_collect_bonus() {
            for (Bonus bonus : bonuses) {
                if (field.getColor(bonus.getX(),bonus.getY()) == 1) {
                    if (bonus.getType() == 1) {
                        xonix.addCountLives();
                        bonuses.remove(bonus);
                        break;
                    }
                    if (bonus.getType() == 2) {
                        balls.removeOne();
                        bonuses.remove(bonus);
                        break;
                    }
                }
            }
        }

        void paint(Graphics g) {
            g.setColor(COLOR_BONUS);
            for (Bonus bonus : bonuses) {
                int x = bonus.getX();
                int y = bonus.getY();
                switch (bonus.getType()) {
                    case 1:
                        g.drawLine(x * POINT_SIZE + POINT_SIZE / 2, y * POINT_SIZE, x * POINT_SIZE + POINT_SIZE / 2, (y + 1) * POINT_SIZE);
                        g.drawLine(x * POINT_SIZE, y * POINT_SIZE + POINT_SIZE / 2, (x + 1) * POINT_SIZE, y * POINT_SIZE + POINT_SIZE / 2);

                        break;
                    case 2:
                        g.drawLine(x * POINT_SIZE, y * POINT_SIZE + POINT_SIZE / 2, (x + 1) * POINT_SIZE, y * POINT_SIZE + POINT_SIZE / 2);
                        break;
                }
            }
        }
    }

    class Bonus {
        private int x, y, type;

        Bonus (int type) {
            Random random = new Random();
            do {
                x = random.nextInt(FIELD_WIDTH);
                y = random.nextInt(FIELD_HEIGHT);
            } while (field.getColor(x, y) == 1);
            this.type = type;
        }

        int getX() {return x;}
        int getY() {return y;}
        int getType() {return type;}
    }

    class Field {
        /*
        1 - Земля
        2 - Вода
        3 - Трек
        4 - Temp
        */
        private final int WATER_AREA = (FIELD_WIDTH - 4)*(FIELD_HEIGHT - 4);
        private int[][] field = new int[FIELD_WIDTH][FIELD_HEIGHT];
        private float currentWaterArea;
        private int countScore = 0;

        Field() {
            init();
        }

        void init() {
            for (int y = 0; y < FIELD_HEIGHT; y++)
                for (int x = 0; x < FIELD_WIDTH; x++)
                    if(x < 2 || x > FIELD_WIDTH - 3 || y < 2 || y > FIELD_HEIGHT - 3)
                        field[x][y] = 1;
                    else field[x][y] = 2;
            currentWaterArea = WATER_AREA;
        }

        int getColor(int x, int y) {
            if (x >= 0 && x < FIELD_WIDTH && y >= 0 && y < FIELD_HEIGHT)
                return field[x][y];
            else return 0;
        }

        void setColor(int x, int y, int color) { field[x][y] = color; }

        void setCountScore(int c) { countScore = c; }

        int getCountScore() { return countScore; }
        float getCurrentPercent() { return 100f - currentWaterArea / WATER_AREA * 100; }

        void clearTrack() { // clear track of Xonix
            for (int y = 0; y < FIELD_HEIGHT; y++)
                for (int x = 0; x < FIELD_WIDTH; x++)
                    if (field[x][y] == 3) field[x][y] = 2;
        }

        void fillTemporary(int x, int y) {
            if (field[x][y] != 2) return;
            field[x][y] = 4; //Отделение области с шариком
            for (int dx = -1; dx < 2; dx++)
                for (int dy = -1; dy < 2; dy++) fillTemporary(x + dx, y + dy);
        }

        void tryToFill() {//Заполняем зону
            currentWaterArea = 0;
            for (Ball ball : balls.getBalls()) fillTemporary(ball.getX(), ball.getY());
            for (int y = 0; y < FIELD_HEIGHT; y++)
                for (int x = 0; x < FIELD_WIDTH; x++) {
                    if (field[x][y] == 3 || field[x][y] == 2) {
                        field[x][y] = 1;
                        countScore += 10;
                    }
                    if (field[x][y] == 4) {
                        field[x][y] = 2;
                        currentWaterArea++;
                    }
                }
        }

        void paint(Graphics g) {
            for (int y = 0; y < FIELD_HEIGHT; y++)
                for (int x = 0; x < FIELD_WIDTH; x++) {
                    switch (field[x][y]){
                        case 0:
                            break;
                        case 1:
                            g.setColor(COLOR_LAND);
                            break;
                        case 2:
                            g.setColor(COLOR_WATER);
                            break;
                        case 3:
                            g.setColor(COLOR_TRACK);
                            break;
                        default:
                            break;
                    }
                    g.fillRect(x*POINT_SIZE, y*POINT_SIZE, POINT_SIZE, POINT_SIZE);
                }
        }
    }

    class Canvas extends JPanel { // класс для рисования
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            field.paint(g);
            xonix.paint(g);
            balls.paint(g);
            cube.paint(g);
            gameover.paint(g);
            pause.paint(g);
            bonuses.paint(g);
        }
    }

    class GameOver {
        private boolean gameOver, flag = true;

        boolean isGameOver() { return gameOver; }

        void setGameOver(boolean b) { gameOver = b; }

        void setFlag(boolean b) { flag = b;}

        void record() {
            if(gameOver && flag) {
                flag = false;
                try {
                    records.update();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        void paint(Graphics g){
            if (xonix.getCountLives() == 0) {
                gameOver = true;
                g.setColor(Color.white);
                g.setFont(new Font("", Font.BOLD, 60));
                FontMetrics fm = g.getFontMetrics();
                String MESSAGE = "GAME OVER";
                g.drawString(MESSAGE, (FIELD_WIDTH*POINT_SIZE + FIELD_DX - fm.stringWidth(MESSAGE))/2, (FIELD_HEIGHT*POINT_SIZE + FIELD_DY)/2);

            }
        }
    }

    class Pause {


        void paint(Graphics g) {
            if (check_pause) {
                g.setColor(Color.white);
                g.setFont(new Font("", Font.BOLD, 60));
                FontMetrics fm = g.getFontMetrics();
                String MESSAGE = "PAUSE";
                g.drawString(MESSAGE, (FIELD_WIDTH*POINT_SIZE + FIELD_DX - fm.stringWidth(MESSAGE))/2, (FIELD_HEIGHT*POINT_SIZE + FIELD_DY)/2);
            }
        }
    }

    class Delay {
        void wait(int milliseconds) {
            try {
                Thread.sleep(milliseconds);
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    class Records extends JFrame {
        private String[] columnNames = {"Имя", "Уровень", "Рекорд"};

        private String[][] data = new String[5][3];

        private JButton jButton = new JButton();

        Records(){
            String TITLE = "Records";
            setTitle(TITLE);
            setBounds(0, 0, 300, 132); //Параметры окна
            setAlwaysOnTop(true);
            setResizable(false);
        }

        public void output() {
            fill_table();
            JTable table = new JTable(data, columnNames) {
                @Override
                public boolean isCellEditable(int i, int i1) {
                    return false;//Переопределяем метод, чтобы нельзя было редактировать
                }
            };
            JScrollPane scrollPane = new JScrollPane(table);
            getContentPane().add(scrollPane);
            setVisible(true);
        }

        public void fill_table() {
            for (int i = 0; i < 5; i++)
                for (int j = 0; j < 3; j++)
                    data[i][j] = "";
            File rec = new File("records.txt");
            //создаем объект FileReader для объекта File
            FileReader fr = null;
            try {
                fr = new FileReader(rec);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //создаем BufferedReader с существующего FileReader для построчного считывания
            BufferedReader reader = new BufferedReader(fr);
            // считаем сначала первую строку
            String line = null;
            try {
                line = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            int j, i = 0;
            while (line != null) {
                int k, p = 1;
                j = 0;
                while (p == 1) {
                    k = line.indexOf(" ");
                    if (k >= 0) {
                        data[i][j] = line.substring(0, k);
                        line = line.substring(k + 1);
                    } else {
                        p = 0;
                        data[i][j] = line;
                        line = "";
                    }
                    j++;
                }
                try {
                    line = reader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                i++;
            }
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void update() throws IOException {
            fill_table();
            for (int i = 0; i < 5; i++) {
                if (field.getCountScore() > Integer.parseInt(data[i][2]))
                {
                    for (int j = 4; j > i; j--) {
                        data[j][0] = data[j - 1][0];
                        data[j][1] = data[j - 1][1];
                        data[j][2] = data[j - 1][2];
                    }
                    String name = null;
                    name = JOptionPane.showInputDialog(null, "Введите имя:", "Вы побили рекорд!", JOptionPane.INFORMATION_MESSAGE);
                    data[i][0] = name;
                    data[i][1] = Integer.toString(xonix.getLevel());
                    data[i][2] = Integer.toString(field.getCountScore());
                    FileWriter wr = new FileWriter("records.txt");
                    for (int j = 0; j < 5; j++) {
                        wr.write(data[j][0] + " " + data[j][1] + " " + data[j][2] + "\n");
                    }
                    wr.close();
                    break;
                }
            }
        }
    }
}
