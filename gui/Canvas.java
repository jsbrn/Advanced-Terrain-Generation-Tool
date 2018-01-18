package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JPanel;

public class Canvas extends JPanel {
    
    private float[] camera;
    
    public Canvas() {
        this.camera = new float[2];
        this.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void mousePressed(MouseEvent e) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void mouseExited(MouseEvent e) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
            
        });
    }
    
    public int[] getWorldCoordinates(int x, int y) {
        return new int[]{x - (int)camera[0], y - (int)camera[1]};
    }
    
    public float[] getOnscreenCoordinates(double x, double y) {
        return new float[]{(getWidth()/2) - camera[0] + (float)x, (getHeight()/2) - camera[1] + (float)y};
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        float[] origin = getOnscreenCoordinates(0, 0);
        g.setColor(Color.red);
        g.drawLine((int)origin[0], 0, (int)origin[0], getHeight());
        g.drawLine(0, (int)origin[1], getWidth(), (int)origin[1]);
    }
    
}
