package App;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
  
public class lineTest extends JFrame
{
    // Instance variables
    DrawingPanel imagepanel;
  
    public static void main(String[] args)
    {
        JFrame window = new lineTest();
        window.setVisible(true);
    }
  
    // constructor
    lineTest()
    {
        //... Create constant pane, layout components
        JPanel content = new JPanel();
        content.setLayout(new BorderLayout());
        // Create JPanel canvas to hold the picture
        imagepanel = new DrawingPanel();
        LineWrangler wrangler = new LineWrangler(imagepanel);
        imagepanel.addMouseListener(wrangler);
        imagepanel.addMouseMotionListener(wrangler);
  
        // Create JScrollPane to hold the canvas containing the picture
        JScrollPane scroller = new JScrollPane(imagepanel);
        scroller.setPreferredSize(new Dimension(500,300));
        scroller.setViewportBorder(BorderFactory.createLineBorder(Color.black));
        // Add scroller pane to Panel
        content.add(scroller,"Center");
  
        //Set window characteristics
        this.setTitle("File Browse and View");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setContentPane(content);
        this.pack();
    }
  
    class DrawingPanel extends JPanel
    {
        List lines;                      // j2se 1.4-
        // optional generic declaration for j2se 1.5+
        //List<Line2D> lines = new ArrayList<Line2D>();
  
        public DrawingPanel()
        {
            lines = new ArrayList();            // j2se 1.4-
            //lines = new ArrayList<Line2D>();  // j2se 1.5+
            setBackground(Color.white);
            setPreferredSize(new Dimension(750,950));
        } 
  
        protected void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            for(int j = 0; j < lines.size(); j++)
                g2.draw((Line2D)lines.get(j));
        }
  
        public void addLine(Point start, Point end)
        {
            lines.add(new Line2D.Double(start,end));
            repaint();
        }
  
       /* public void moveLine(Line2D line, Point p)
        {
            double x1 = line.getX1();
            double x2 = line.getX2();
            line.setLine(x1, p.y, x2, p.y);
            repaint();
        }*/
  
        public void removeLine(Line2D line)
        {
            lines.remove(line);
            repaint();
        }
    }
}
  
class LineWrangler extends MouseInputAdapter
{
    lineTest.DrawingPanel drawingPanel;
    Point start,end;
    Line2D selectedLine;
    boolean dragging;
    final int MIN_DIST = 3;
    final int MIN_DRAG_DIST = 5;
  
    public LineWrangler(lineTest.DrawingPanel dp)
    {
        drawingPanel = dp;
        dragging = false;
    }
    public void mousePressed(MouseEvent e)
    {
        Point p = e.getPoint();
        boolean haveSelection = false;
        //List<Line2D> list = drawingPanel.lines;   // j2se 1.5+
        List list = drawingPanel.lines;             // j2se 1.4-
        for(int j = 0; j < list.size(); j++)
        {
            Line2D line = (Line2D)list.get(j);
            if(line.ptLineDist(p) < MIN_DIST)
            {
                // we have selected a line
                haveSelection = true;
                // if user has pressed the right mouse button
                // remove selectedLine
                if(SwingUtilities.isRightMouseButton(e))
                    drawingPanel.removeLine(line);
                else
                {
                    // wait and see if the user drags selectedLine
                    start = p;
                    selectedLine = line;
                }
                // we're done here so let's move on
                break;
            }
        }
        if(!haveSelection)
            start = e.getPoint();
        // if p is not near a line, add a line at p
        
    }
  
    public void mouseReleased(MouseEvent e)
    {
        if(dragging){
            dragging = false;
            end = e.getPoint();
            drawingPanel.addLine(start, end);
        }
    }
  
    public void mouseDragged(MouseEvent e)
    {
        Point p = e.getPoint();
        /*if(dragging)
            drawingPanel.moveLine(selectedLine, p);
        else*/ if(p.distance(start) >= MIN_DRAG_DIST)
        {
            // we have a bite, set the hook
            dragging = true;
        }
    }
}