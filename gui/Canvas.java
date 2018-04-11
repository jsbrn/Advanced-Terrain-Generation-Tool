package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.JPanel;
import world.World;

/**
 * A custom Swing element used to render the World and other visual elements.
 * @author Jeremy
 */
public final class Canvas extends JPanel {
    
    private int[] camera;
    
    private boolean showElevationMap;

    public Canvas() {
        this.camera = new int[2];
        this.addMouseMotionListener(new MouseMotionListener(){

            private int[] last_mouse = new int[2];
            
            @Override
            public void mouseDragged(MouseEvent e) {
                int dx = e.getX() - last_mouse[0];
                int dy = e.getY() - last_mouse[1];
                camera[0] -= dx; camera[1] -= dy;
                last_mouse[0] += dx; last_mouse[1] += dy;
                Canvas.refresh();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                last_mouse[0] = e.getX();
                last_mouse[1] = e.getY();
            }
            
        });
    }
    
    /**
     * Refresh the Canvas. Calls repaint() on the element in GUI.
     * @see GUI
     */
    public static void refresh() {
        GUI.getCanvas().repaint();
    }
    public static int[] getDimensions() { return new int[]{GUI.getCanvas().getWidth(), GUI.getCanvas().getHeight()}; }
    
    /**
     * Get the world-coordinates of the camera.
     * @return An integer array {x, y}.
     */
    public static int[] getCamera() { return new int[]{(int)GUI.getCanvas().camera[0], (int)GUI.getCanvas().camera[1]}; }
    /**
     * Set the camera position in the world.
     * @param x The new world x-coordinate.
     * @param y The new world y-coordinate.
     */
    public static void setCamera(int x, int y) { GUI.getCanvas().camera = new int[]{x, y}; }
    
    /**
     * Get the currently active layer.
     * @return The layer index selected by the JList element in GUI.
     * @see GUI#getLayerList() 
     */
    public static int layer() { return GUI.getLayerList().getSelectedIndex(); }
    
    public void setShowElevationMap(boolean showElevationMap) {
        this.showElevationMap = showElevationMap;
    }

    public boolean getShowElevationMap() {
        return showElevationMap;
    }
    
    /**
     * Draw the world, and other visual elements like the axis lines.
     * @param g The Graphics instance used by Swing.
     * @see World#draw(java.awt.Graphics) 
     * @see World#getOnscreenCoordinates(double, double) 
     */
    @Override
    protected void paintComponent(Graphics g) {
        
        g.setColor(Color.black);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.white);
        
        if (GUI.getCanvas() == null) return; //ensure that Netbeans editor does not throw an exception

        World world = World.getWorld();
        
        if (world == null) { g.drawString("No world loaded!", 15, 10); return; } else { world.draw(g, showElevationMap); }

        int[] origin = World.getWorld().getOnscreenCoordinates(0, 0);
        
        g.setColor(Color.green);
        g.drawRect(origin[0], origin[1], World.getWorld().width(), World.getWorld().height());
        
        g.setColor(Color.red);
        g.drawLine((int)origin[0], 0, (int)origin[0], getHeight());
        g.drawLine(0, (int)origin[1], getWidth(), (int)origin[1]);
        
        g.setColor(Color.white);
        g.drawString(camera[0]+", "+camera[1], 15, 25);
        g.fillRect((getWidth()/2) - 2, (getHeight()/2) - 2, 4, 4);
        g.setColor(Color.black);
        g.drawRect((getWidth()/2) - 2, (getHeight()/2) - 2, 4, 4);
        
    }
    
}
