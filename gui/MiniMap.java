package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.JPanel;
import world.World;

public final class MiniMap extends JPanel {
    
    public static void refresh() {
        GUI.getCanvas().repaint();
    }
    
    public static int[] getDimensions() { return new int[]{GUI.getMiniMap().getWidth(), GUI.getMiniMap().getHeight()}; }
    
    @Override
    protected void paintComponent(Graphics g) {
        
        if (GUI.getMiniMap() == null) return; //ensure that Netbeans editor does not throw an exception
        
        g.setColor(Color.black);
        g.fillRect(0, 0, getWidth(), getHeight());
        
    }
    
}
