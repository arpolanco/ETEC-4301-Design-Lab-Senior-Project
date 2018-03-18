
package server;

import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class VideoStream {
    JFrame frame;
    JPanel panel = new JPanel();
    final int HEIGHT;
    final int WIDTH;
    public VideoStream(int w, int h){
        WIDTH = w;
        HEIGHT = h;
        
        frame = new JFrame("");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.setSize(WIDTH, HEIGHT);
        frame.setVisible(true);
    }
    
    public void draw(BufferedImage image, double framerate){
        panel.getGraphics().drawImage(image, 0, 0, WIDTH, HEIGHT, null);
        panel.getGraphics().clearRect(0, 0, 256, 24);
        panel.getGraphics().drawString(Double.toString(framerate), 128, 12);
    }
}
