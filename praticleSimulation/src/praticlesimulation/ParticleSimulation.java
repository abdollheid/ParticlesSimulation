/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package praticlesimulation;

import java.util.PriorityQueue;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author abdo
 */
public class ParticleSimulation extends Application {

    private Pane pane;
    private Particle ps[];
    private double width = 1200, height = 600, dt = 1000/60.0; 
    private double t = 0 ; 
    private int count = 600 ;
    private Random rand;
    private static PriorityQueue<Event> pq;
    private final char DRAW = 'D', VW = 'V', HW = 'H', P = 'P';
    
    
    public static void main(String args[]){
        for(int i = 0 ; i < args.length  ;++i)
            System.out.println(args[i]); 
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        pq = new PriorityQueue<>();
        pane = new Pane();

        rand = new Random(1);

        ps = new Particle[count];

        for (int i = 0; i < count; ++i) {
            ps[i] = new Particle();
            pane.getChildren().add(ps[i].getCircle());
        }

        Scene scene = new Scene(pane, width, height);

        Frame f = new Frame();
        Timeline animation = new Timeline(
                new KeyFrame(Duration.millis(dt), f));
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();

        pane.setOnMouseClicked(e -> {
            if (animation.getStatus() == Animation.Status.RUNNING) {
                animation.pause();
            } else {
                animation.play();
            }
        });

        initParticles() ; 
        
        
        primaryStage.setTitle("Praticles");
        primaryStage.setScene(scene);
        primaryStage.show();

    }
    
    public void initParticles(){
        for(int i = 0 ; i < ps.length ; ++i){
          predict(ps[i]) ; 
        }
        
        for(int i = 0 ; i < ps.length - 1 ; ++i){
            for(int x = i + 1 ; x <ps.length ; ++x ){
                if(ps[i].timeToHit(ps[x]) == Double.POSITIVE_INFINITY) continue ; 
                double tTemp = ps[i].timeToHit(ps[x]) ; 
                pq.add(new Event(ps[i].timeToHit(ps[x]) , P , ps[i] , ps[x])) ; 
            }
        }
    }

        public void next() {
            
            Event tempEvent = null ; 
                do{
                    tempEvent = pq.poll() ; 
                    if(!tempEvent.isValid()) continue ; 
                    
                    
                    for (int i = 0; i < ps.length; ++i) {
                        ps[i].move(tempEvent.t - t);
                    }
                    
                    t =  tempEvent.t ; 
                    
                    if(tempEvent.EventType == HW){
                        tempEvent.thisP.bounceOffHorizontalWall();
                        predict(tempEvent.thisP) ; 
                    }
                    
                    if(tempEvent.EventType == VW){
                        tempEvent.thisP.bounceOffVerticalWall();
                        predict(tempEvent.thisP) ; 
                    }
                    
                    if(tempEvent.EventType == P){
                        tempEvent.thisP.bounceOff(tempEvent.thatP);
                        predict(tempEvent.thisP) ; 
                        predict(tempEvent.thatP) ; 
                    }
                    
                    
                    
                }while(tempEvent.EventType != DRAW)  ;
                
        
        }
       
        
        
        private void predict(Particle p){
            predictHW(p) ; 
            predictVW(p) ; 
            predictP(p) ; 
        }
        
        private void predictHW(Particle p){
            pq.add(new Event(p.timeToHitHorizontalWall() + t ,HW , p , null))  ;
        }
        private void predictVW(Particle p){
            pq.add(new Event(p.timeToHitVerticalWall() + t ,VW ,  p , null))  ;
        }
        
        
        
        private void predictP(Particle p){
            for(int i = 0; i < ps.length ; ++i){
                if(ps[i] == p) 
                    continue ;
                
                if(p.timeToHit(ps[i]) == Double.POSITIVE_INFINITY) continue ; 
                  
                pq.add(new Event(p.timeToHit(ps[i]) + t , P , p , ps[i])) ; 
            }
        }

    

    private void draw() {
    
        pq.add(new Event(t +dt, DRAW, null, null));

        for (int i = 0; i < ps.length; ++i) {
            ps[i].c.setCenterX(ps[i].rx);
            ps[i].c.setCenterY(ps[i].ry);
        }
        next() ; 
    }


    private double randBetween(double max, double min) {
        return Math.random() * (max - min) + min;
    }

    class Frame implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            draw() ; 
        }

    }

    class Event implements Comparable<Event> {

        public double t;
        public Particle thisP, thatP;
        public char EventType;
        public int thisPCount , thatPCount ; 

        public Event(double t, char EventType, Particle thisP, Particle thatP ) {
            this.t = t;
            this.EventType = EventType;
            this.thisP = thisP;
            this.thatP = thatP;
            
            if(EventType != DRAW){
                thisPCount = thisP.count ; 
            if(thatP != null)
                thatPCount = thatP.count ; 
            } 
            
        }

        @Override
        public int compareTo(Event o) {
            if (t > o.t) {
                return 1;
            }
            if (t < o.t) {
                return -1;
            }
            return 0;
        }
        
        
        public boolean isValid(){
            if(EventType == DRAW) return true ; 
            
            if(thatP == null){
                if(thisP.count ==thisPCount)
                    return true ; 
                
                return false ; 
            }
            if(thisP.count == thisPCount && thatP.count == thatPCount)
                return true ; 
        
                return false ; 
        }
        
        @Override 
        public String toString(){
            return "t" + t  + "\tthisCount:" + thisPCount  + "\tthatCount:" + thatPCount ; 
        }

    }

}
