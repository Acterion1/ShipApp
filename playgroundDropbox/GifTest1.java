package playgroundDropbox;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
//from w w w . j  av a 2 s  .c o  m
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class GifTest1 {
  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        JFrame frame = new JFrame();
        frame.add(new ImagePanel());

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setVisible(true);
      }
    });
  }
}

class ImagePanel extends JPanel {

  Image image;

  public ImagePanel() {
    image = Toolkit.getDefaultToolkit().createImage("TorGif.gif");
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (image != null) {
      g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
    }
  }

}