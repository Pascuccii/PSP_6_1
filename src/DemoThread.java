import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.Random;

/*7. Движущегося слева направо парусника с постоянной скоростью.
Движение парусника начинается нажатием на кнопку «Старт».
Скорость всякий раз задается генератором случайных чисел.
В нижней части экрана  расположена пушка.
При нажатии на кнопку происходит выстрел торпедой с постоянной скоростью.
При попадании торпеды в пушку смоделировать взрыв парусника и его исчезновение.
При промахе парусник достигает правой границы экрана и
начинает движение сначала с новой постоянной скоростью.*/

public class DemoThread extends JFrame {

    private String message = "Press S to start";
    private String message1 = "";
    private String message2 = "move: <- ->           shoot: space";
    private Img1 ship;
    private Img2 torpedo;
    private boolean shipStop, torpedoStop;
    private int shipSpeed = 5, torpedoSpeed = 2;
    private Image buffImg1, buffImg2, field;
    private int buffImg1Width = 130, buffImg1Height = 55, buffImg2Width = 15, buffImg2Height = 45;
    private int buffImg1X = -130, buffImg1Y = 45, buffImg2X = 520, buffImg2Y = 230;
    private Random randomBuffImg1;
    private boolean fire = false, was = false;

    public DemoThread() {
        setTitle("Demo app");
        setSize(new Dimension(1080, 440));
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        field = Toolkit.getDefaultToolkit().createImage("C:\\Users\\HLEB\\IdeaProjects\\PSP_6_1\\src\\field.png");
        JPanel content = new JPanel(new BorderLayout()) {
            @Override
            public void paintComponent(Graphics page) {
                super.paintComponent(page);
                page.drawImage(field, 0, 0, null);
                page.setFont(new Font(Font.MONOSPACED, Font.BOLD, 24));
                page.drawString(message, 400, 50);
                page.drawString(message1, 30, 30);
                page.drawString(message2, 300, 200);
            }
        };
        setContentPane(content);
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (shipStop && torpedoStop)
                    System.exit(0);
                System.out.println(e.getKeyCode());
                if (e.getKeyCode() == 37 && buffImg2X > 30 && torpedo.isAlive() && !fire) {
                    buffImg2X -= 10;
                    repaint();
                }
                if (e.getKeyCode() == 39 && buffImg2X < 1030 && torpedo.isAlive() && !fire) {
                    buffImg2X += 10;
                    repaint();
                }
                if (e.getKeyCode() == 83) {
                    if (!was) {
                        ship.start();
                        torpedo.start();
                        message = "";
                        repaint();
                        was = true;
                    }
                }
                if (e.getKeyCode() == 32 && ship.isAlive()) {
                    fire = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        ship = new Img1();
        torpedo = new Img2();

        content.add(ship, BorderLayout.NORTH);
        content.add(torpedo, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        new DemoThread().setVisible(true);
    }

    private class Img1 extends JPanel {

        private Thread thread;

        public Img1() {
            setPreferredSize(new Dimension(1080, 100));
            setOpaque(false);

            buffImg1 = Toolkit.getDefaultToolkit().createImage("C:\\Users\\HLEB\\IdeaProjects\\PSP_6_1\\src\\ship\\ship.png");
            //buffImg1 = Toolkit.getDefaultToolkit().createImage("C:\\Users\\HLEB\\IdeaProjects\\PSP_6_1\\src\\ship\\ff.gif");
            //buffImg1 = Toolkit.getDefaultToolkit().createImage("C:\\Users\\HLEB\\IdeaProjects\\PSP_6_1\\src\\ship\\ff2.gif");


            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    message1 = "Speed: " + shipSpeed;
                    message2 = "";
                    randomBuffImg1 = new Random();
                    while (!shipStop) {
                        if (buffImg1X < 1100)
                            buffImg1X += shipSpeed;
                        else {
                            buffImg1X = -130;
                            shipSpeed = randomBuffImg1.nextInt(9 - 3) + 2;
                            message1 = "Speed: " + shipSpeed;
                        }
                        repaint();
                        try {
                            Thread.sleep(30);
                        } catch (Exception exc) {
                        }
                    }
                    System.out.println(thread.getName() + " is over");
                    message = "     YOU WON";
                    message1 = "";
                    buffImg1 =
                            Toolkit.getDefaultToolkit().createImage("C:\\Users\\HLEB\\IdeaProjects\\PSP_6_1\\src\\ship\\ship_blown1.gif");
                    repaint();
                    for (int i = 0; i < 50; i++) {
                        buffImg1Y++;
                        repaint();
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    buffImg1 =
                            Toolkit.getDefaultToolkit().createImage("C:\\Users\\HLEB\\IdeaProjects\\PSP_6_1\\src\\torpedo\\transparent.png");
                    repaint();
                }
            });
            thread.setName("ship");
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D graphics2D = (Graphics2D) g;
            graphics2D.drawImage(buffImg1, buffImg1X, buffImg1Y, buffImg1Width, buffImg1Height, this);
        }

        public void start() {
            System.out.println("ship isAlive: " + thread.isAlive());
            if (thread != null && !thread.isAlive())
                thread.start();
        }

        public boolean isAlive() {
            return thread.isAlive();
        }
    }

    private class Img2 extends JPanel {

        private Thread thread;

        public Img2() {
            setPreferredSize(new Dimension(180, 303));
            setOpaque(false);
            try {
                buffImg2 = ImageIO.read(new File("C:\\Users\\HLEB\\IdeaProjects\\PSP_6_1\\src\\torpedo\\torpedo.png"));
            } catch (IOException exc) {
            }

            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 10; i++) {

                        while (!fire) {
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        while (buffImg2Y > 0) {
                            buffImg2Y -= torpedoSpeed;
                            repaint();
                            try {
                                Thread.sleep(30);
                            } catch (Exception exc) {
                            }
                        }
                        fire = false;

                        System.out.println("ship x: " + buffImg1X);
                        System.out.println("torpedo x: " + buffImg2X);
                        if (buffImg2X >= buffImg1X && buffImg2X <= (buffImg1X + 120)) {
                            System.out.println("hit");
                            torpedoStop = true;
                            shipStop = true;
                            break;
                        } else {
                            System.out.println("miss");
                            buffImg2Y = 230;
                            repaint();
                        }
                    }
                    System.out.println(thread.getName() + " is over");
                    buffImg2 =
                            Toolkit.getDefaultToolkit().createImage("C:\\Users\\HLEB\\IdeaProjects\\PSP_6_1\\src\\ship\\transparent.png");
                    repaint();
                }
            });
            thread.setName("torpedo");
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D graphics2D = (Graphics2D) g;
            graphics2D.drawImage(buffImg2, buffImg2X, buffImg2Y, buffImg2Width, buffImg2Height, this);
        }

        public void start() {
            System.out.println("torpedo isAlive: " + thread.isAlive());
            if (thread != null && !thread.isAlive())
                thread.start();
        }

        public boolean isAlive() {
            return thread.isAlive();
        }
    }

}