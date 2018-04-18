
package praticlesimulation;

import java.util.Random;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Particle {

    public Circle c;
    public double r,rx , ry  , vx, vy, mass;
    public int count;
    private double width = 1200, height = 600, dt = 1000 / 60;
    private double maxSpeed = .2, minSpeed = .01, maxR = 10, minR = 5, minX = width / 4, minY = height / 4, maxX = 3 * width / 4, maxY = 3 * height / 4;
    private Random rand = new Random() ; 

    public Particle() {

        r = randBetween(maxR, minR);
        mass = r ; 
        rx = randBetween(maxX, minX) ; 
        ry = randBetween(minY, maxY) ; 
        c = new Circle(rx,ry, r);
        vx = randBetween(maxSpeed, minSpeed);
//        vx = .1 ; 
        vy = randBetween(maxSpeed , minSpeed);
//        vy = .1 ; 

    }

    public double timeToHit(Particle that) {
        if (this == that) {
            return Double.POSITIVE_INFINITY;
        }

        double dx = that.rx - this.rx;
        double dy = that.ry - this.ry;
        
        double dvx = that.vx - this.vx;
        double dvy = that.vy - this.vy;
        
        double dvdr = dx * dvx + dy * dvy;
        
        if (dvdr >= 0) {
            return Double.POSITIVE_INFINITY;
        }

        double dvdv = dvx * dvx + dvy * dvy;
        double drdr = dx * dx + dy * dy;
        double sigma = this.r + that.r;
        double d = (dvdr * dvdr) - dvdv * (drdr - sigma * sigma);
         if (drdr < sigma*sigma) {
//             System.out.println("overlapping particles");
             return Double.POSITIVE_INFINITY;
         } 
        if (d < 0) {
            return Double.POSITIVE_INFINITY;
        }

        return -(dvdr + Math.sqrt(d)) / dvdv;
    }

    public double timeToHitVerticalWall() {
        if (vx > 0) {

            
            return (width - rx -r) / vx;
        } else if (vx < 0) {
            return (r -rx ) / vx;
        } else {
            return Double.POSITIVE_INFINITY;
        }
    }

    public double timeToHitHorizontalWall() {
        if (vy > 0) {
            return (height - ry- r) / vy;
        } else if (vy < 0) {
            return (r - ry) / vy;
        } else {
            return Double.POSITIVE_INFINITY;
        }
    }

    public void bounceOff(Particle that) {
        double dx = that.rx - this.rx;
        double dy = that.ry - this.ry;
        double dvx = that.vx - this.vx;
        double dvy = that.vy - this.vy;
        double dvdr = dx * dvx + dy * dvy;             // dv dot dr
        double dist = this.r + that.r;   // distance between particle centers at collison

        // magnitude of normal force
        double magnitude = 2 * this.mass * that.mass * dvdr / ((this.mass + that.mass) * dist);
//        System.out.println("mag:" + magnitude) ; 
//        double magnitude = 110 ; 

        // normal force, and in x and y directions
        double fx = magnitude * dx / dist;
        double fy = magnitude * dy / dist;

        // update velocities according to normal force
        this.vx += fx / this.mass;
        this.vy += fy / this.mass;
        that.vx -= fx / that.mass;
        that.vy -= fy / that.mass;
        

        // update collision counts
        this.count++;
        that.count++;
    }

    public void bounceOffVerticalWall() {
        vx *= -1;
        count++;
    }

    public void bounceOffHorizontalWall() {
        vy *= -1;
        count++;
    }

    
    public void move(double dt){
        rx += vx * dt ; 
        ry += vy * dt ;
    }
    public Circle getCircle() {
        c.setFill(Color.rgb(rand.nextInt(200), rand.nextInt(200), rand.nextInt(200)));
        return c;
    }

    private double randBetween(double max, double min) {
        return Math.random() * (max - min) + min;
    }

}
