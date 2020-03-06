import java.awt.Graphics;
import java.lang.reflect.Array;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class RobotSoccerSimulation extends JPanel {
    private static final long serialVersionUID = -5228718339006830546L;

    private static double WIDTH = 400;
    private static double HEIGHT = 600;

    private static double PLAYER_RADIUS = 15;
    private static double ENEMY_RADIUS = 20;
    private static double PLAYER_SPEED = 1.3;
    private static double ENEMY_SPEED = 1.8;
    private static double FRICTION = 0.0009;

    private static String input = "";

    private volatile String endMessage;
    
    static class Ball {
        private double x;
        private double y;
        private double radius;
        private double speed;
        private Color color;

        Ball(double x, double y, double radius, double speed, Color color) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.speed = speed;
            this.color = color;
        }

        void moveToward(double targetX, double targetY) {
            var dx = targetX - this.x;
            var dy = targetY - this.y;
            var v = this.speed / Math.hypot(dx, dy);
            this.x = constrain(this.x + v * dx, this.radius, WIDTH - this.radius);
            this.y = constrain(this.y + v * dy, this.radius, HEIGHT - this.radius);
        }
        
        public static void adjustIfCollisions() {
            for (var b1 : balls) {
                for (var b2 : balls) {
                    if (b1 != b2) {
                        Array[] mark = [dx, dy];
                    }
                }
            }
        }

        public static void endSimulationIfNecessary() {
            if (balls[0].speed <= 0) {
                endMessage = "Uh oh! Game Over!";
            }
            else if (balls[0].inside(goal)) {
                endMessage = "GOOAALL!";
            }
        }

        private static double constrain(double value, double low, double high) {
            return Math.max(low, Math.min(value, high));
        }

        void applyFriction() {
            double inf = Double.POSITIVE_INFINITY;
            this.speed = constrain(speed - FRICTION, 0, inf);
        }

        boolean inside(Goal goal) {
            return 
            (this.x - this.radius > x - WIDTH / 2 &&
            this.x + this.radius < x + WIDTH / 2 &&
            this.y - this.radius > y - HEIGHT / 2 &&
            this.y + this.radius < y + HEIGHT / 2);
        }
    }

    private static Ball[] balls = new Ball[] { 
        new Ball(0.0, HEIGHT, PLAYER_RADIUS, PLAYER_SPEED, Color.BLUE),
        new Ball(WIDTH * 0.25, 40, ENEMY_RADIUS, ENEMY_SPEED, Color.RED),
        new Ball(WIDTH * 0.75, 40, ENEMY_RADIUS, ENEMY_SPEED, Color.RED),
        new Ball(WIDTH / 2, HEIGHT / 2, ENEMY_RADIUS, ENEMY_SPEED, Color.RED) 
    };

    private static class Goal {
        double x = WIDTH / 2;
        double y = 0;
        double w = 100;
        double h = 100;
    }

    private static Goal goal = new Goal();

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (var ball : balls) {
            g.setColor(ball.color);
            g.fillOval((int) (ball.x - ball.radius), (int) (ball.y - ball.radius), (int) ball.radius * 2,
                    (int) ball.radius * 2);
        }
        g.setColor(new Color(255, 255, 255, 128));
        g.fillRect((int) (goal.x - goal.w / 2), (int) (goal.y - goal.h / 2), (int) goal.w, (int) goal.h);
        if (endMessage != null) {
            g.setFont(new Font("Arial", Font.PLAIN, 50));
            g.setColor(Color.RED);
            g.drawString(endMessage, 30, (int) HEIGHT / 2);
        }
    }

    private void runTheAnimation() {
        while (endMessage == null) {
            for (var i = 0; i < balls.length; i++) {
                balls[i].applyFriction();
                balls[i].moveToward(i ==0 ? goal.x : balls[0].x, i == 0 ? goal.y : balls[0].y);
            }
            Ball.adjustIfCollisions();
            Ball.endSimulationIfNecessary();
            repaint();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            String input = args[0];
            RobotSoccerSimulation.input = input;
            RobotSoccerSimulation.PLAYER_RADIUS = Double.parseDouble(args[1]);
            RobotSoccerSimulation.ENEMY_RADIUS = Double.parseDouble(args[2]);
            RobotSoccerSimulation.PLAYER_SPEED = Double.parseDouble(args[3]);
            RobotSoccerSimulation.ENEMY_SPEED = Double.parseDouble(args[4]);
            RobotSoccerSimulation.FRICTION = Double.parseDouble(args[5]);

            var panel = new RobotSoccerSimulation();
            panel.setBackground(Color.GREEN.brighter());
            var frame = new JFrame("Robotic Soccer");
            frame.setSize((int)WIDTH, (int)HEIGHT);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(panel, BorderLayout.CENTER);
            frame.setVisible(true);
            new Thread(() -> panel.runTheAnimation()).start();
        });
    }
}
