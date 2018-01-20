package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JDesktopPane;
import javax.swing.JPanel;

public final class Canvas extends JPanel {
    
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
    
    public static int[] getWorldCoordinates(int x, int y) {
        return new int[]{x - (int)GUI.getCanvas().camera[0], y - (int)GUI.getCanvas().camera[1]};
    }
    
    public static float[] getOnscreenCoordinates(double x, double y) {
        return new float[]{(GUI.getCanvas().getWidth()/2) - GUI.getCanvas().camera[0] + (float)x, 
            (GUI.getCanvas().getHeight()/2) - GUI.getCanvas().camera[1] + (float)y};
    }
    
    public static void refresh() {
        GUI.getCanvas().repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        
        if (GUI.getCanvas() == null) return; //ensure that Netbeans editor does not throw an exception
        
        g.setColor(Color.black);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        float[] origin = getOnscreenCoordinates(0, 0);
        g.setColor(Color.red);
        g.drawLine((int)origin[0], 0, (int)origin[0], getHeight());
        g.drawLine(0, (int)origin[1], getWidth(), (int)origin[1]);
        
    }
    
}
