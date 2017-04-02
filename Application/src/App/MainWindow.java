package App;

import java.awt.EventQueue;

import javax.swing.JFrame;

import java.awt.BasicStroke;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.geom.*;
import java.awt.geom.Ellipse2D.Double;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.RepaintManager;
import javax.sql.rowset.serial.SerialArray;
import javax.swing.JButton;
import javax.swing.JComponent;

import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;
import javax.swing.text.StyledEditorKit.ForegroundAction;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.Toolkit;

public class MainWindow extends JFrame {
	protected ArrayList<Line2D.Double> lines = new ArrayList<Line2D.Double>();
	int pX, pY, cX, cY, rX, rY, cicleX, circleY, count, r = 40;
	Point mouse = new Point();
	boolean pressed, clear;

	circle c[] = { new circle(200, 200, r), new circle(200, 40, r), new circle(360, 200, r), new circle(200, 360, r),
			new circle(40, 200, r), new circle(120, 120, r), new circle(280, 120, r), new circle(280, 280, r),
			new circle(120, 280, r), };

	static BufferedImage bufferedImage;
	static boolean imageLoaded = false;

	private JFrame frame;

	abstract public class Shape {
		abstract boolean contains(Point point);

		double distance(Point a, Point b) {
			return Math.sqrt(
					(a.getX() - b.getX()) * (a.getX() - b.getX()) + (a.getY() - b.getY()) * (a.getY() - b.getY()));
		}
	}

	public class JLabelC extends JLabel {
		public JLabelC() {
			// setText(Integer.toString(count));
			setBounds(480, 280, 90, 40);
			setOpaque(true);
			setBackground(Color.BLACK);
			setForeground(Color.RED);
			setFont(new Font("Dialog", Font.BOLD, 30));
		}

		public void update() {
			setText(Integer.toString(count));
		}
	}

	public class JPanelC extends JPanel {
		List lines;
		JLabelC jC = new JLabelC();
		// j2se 1.4-
		// optional generic declaration for j2se 1.5+
		// List<Line2D> lines = new ArrayList<Line2D>();

		public JPanelC() {
			lines = new ArrayList<>();
			// lines = new ArrayList<Line2D>(); // j2se 1.5+
			setBackground(Color.white);
			setPreferredSize(new Dimension(400, 400));
		}

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			TexturePaint texture = new TexturePaint(bufferedImage, new Rectangle(0, 0, 100, 100));
			g2.setPaint(texture);
			g2.setStroke(new BasicStroke(40));

			for (int j = 0; j < lines.size(); j++) {
				g2.draw((Line2D) lines.get(j));
			}

			for (int i = 0; i <= 8; i++) {
				c[i].circleDraw(g2);
			}
		}

		public void recount() {
			count = 0;
			for (int i = 0; i <= 4; i++) {
				count += Math.pow(4, i) * c[i].linesC.size();
				jC.update();
			}
			// System.out.println("Counter:" + count);
		}

		public void addLine(Point start, Point end, int ni) {
			Line2D.Double line = new Line2D.Double(start, end);
			lines.add(line);
			c[ni].linesC.add(line);
			for (int i = 0; i <= 4; i++) {
				System.out.println("Circle" + i + ":" + c[i].linesC.size());
			}
			recount();
			repaint();
		}

		public void clearLines() {
			lines.clear();
			for (int i = 0; i <= 4; i++) {
				c[i].linesC.clear();
				recount();
				repaint();
			}
		}

		public void removeLine(Line2D line) {
			lines.remove(line);
			for (int i = 0; i <= 4; i++)
				c[i].linesC.remove(line);
			recount();
			repaint();
		}
	}

	class LineWrangler extends MouseInputAdapter {
		int i_start, i_end;
		MainWindow.JPanelC panelC;
		Point start, end;
		Line2D selectedLine;
		boolean dragging;
		final int MIN_DIST = 20;
		final int MIN_DRAG_DIST = 50;

		public LineWrangler() {

		}

		public LineWrangler(MainWindow.JPanelC pc) {

			panelC = pc;
			dragging = false;
		}

		public void mousePressed(MouseEvent e) {
			Point p = e.getPoint();
			// boolean haveSelection = false;
			for (int i = 0; i <= 8; i++) {
				if (c[i].contains(p)) {
					start = c[i].center;
					i_start = i;
					// System.out.println("Contains" + i);
				}
			}

			List<Line2D> list = panelC.lines; // j2se 1.5+
			for (int j = 0; j < list.size(); j++) {

				Line2D line = (Line2D) list.get(j);
				if (line.getBounds().contains(p)) {

					// line.ptLineDist(p) < MIN_DIST
					// we have selected a line
					// haveSelection = true;
					// if user has pressed the right mouse button
					// remove selectedLine
					if (SwingUtilities.isRightMouseButton(e))
						panelC.removeLine(line);

					// we're done here so let's move on
					break;
				}
			}
			// if(!haveSelection)
			// start = e.getPoint();
			// if p is not near a line, add a line at p

		}

		public void mouseReleased(MouseEvent e) {
			Random random = new Random();
			Point p = e.getPoint();
			int ni;
			if (dragging) {
				for (int i = 0; i <= 8; i++) {
					if (c[i].contains(p)) {
						i_end = i;

						end = c[i].center;
						// System.out.println("Released in" + i);
					}
				}
				dragging = false;
				// end = e.getPoint();
				// System.out.println(start.distance(end));
				if (start != null && end != null && start.distance(end) <= 114) {
					for (int i = 0; i <= 4; i++) {
						if (i == i_start || i == i_end) {
							ni = i;
							if (c[ni].linesC.size() < 3)
								panelC.addLine(start, end, ni);
						}
					}
					start = end = null;
				}
			}
		}

		public void mouseDragged(MouseEvent e) {
			Point p = e.getPoint();
			if (start != null)
				if ((p.distance(start) >= MIN_DRAG_DIST) && SwingUtilities.isLeftMouseButton(e)) {
					// we have a bite, set the hook
					dragging = true;
				}
		}
	}

	public class circle extends Shape {
		List linesC;
		Point center = new Point();
		int X, Y, R;

		circle(int x, int y, int r) {
			X = x;
			Y = y;
			R = r;
			linesC = new ArrayList<>();
		}

		public void circleDraw(Graphics g) {
			g.setColor(Color.BLUE);
			center.x = X;
			center.y = Y;
			g.fillOval(X - R, Y - R, R * 2, R * 2);
		}

		public boolean contains(Point point) {
			// System.out.println(distance(point, center));
			return (distance(point, center) <= R);
		}

	}

	Toolkit tk = Toolkit.getDefaultToolkit();
	int xSize = (int) tk.getScreenSize().getWidth();
	int ySize = (int) tk.getScreenSize().getHeight();

	/**
	 * Launch the application.
	 */

	public static void main(String[] args) {
		ImageObserver myImageObserver = new ImageObserver() {

			@Override
			public boolean imageUpdate(Image image, int flags, int x, int y, int height, int width) {

				if ((flags & ALLBITS) != 0) {
					imageLoaded = true;
					return false;
				}
				return true;
			}
		};

		URL blobImg = null;
		try {
			blobImg = new URL("http://109.120.138.13/Quest/blob_texture.jpg");
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Image sourceImg = Toolkit.getDefaultToolkit().getImage(blobImg);
		sourceImg.getWidth(myImageObserver);

		while (!imageLoaded) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}

		bufferedImage = new BufferedImage(sourceImg.getWidth(null), sourceImg.getHeight(null),
				BufferedImage.TYPE_INT_RGB);
		bufferedImage.getGraphics().drawImage(sourceImg, 0, 0, null);

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	public void initialize() {

		frame = new JFrame();
		// frame.setSize(xSize,ySize);
		frame.setSize(600, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.getContentPane().setLayout(null);

		JPanel panelContainer = new JPanel();
		panelContainer.setBounds(0, 0, 600, 600);
		frame.getContentPane().add(panelContainer);
		panelContainer.setLayout(new CardLayout(0, 0));

		// java.awt.Shape circle = new Ellipse2D.Double(100, 100, 200, 100);

		JPanel panel1 = new JPanel() {
			// protected void paintComponent(Graphics g) {
			// }
		};
		if (clear) {
			System.out.println("clear");
			clear = false;
			panel1.removeAll();
		}
		panelContainer.add(panel1, "panel1");
		panel1.setLayout(null);

		JButton btnP1Button = new JButton("Next Page");
		btnP1Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				CardLayout cl = (CardLayout) panelContainer.getLayout();
				cl.show(panelContainer, "panel2");
			}
		});
		btnP1Button.setBounds(10, 11, 158, 70);
		panel1.add(btnP1Button);

		JLabel lblPage1 = new JLabel("Page 1");
		lblPage1.setFont(new Font("Dialog", Font.PLAIN, 30));
		lblPage1.setHorizontalAlignment(SwingConstants.CENTER);
		lblPage1.setEnabled(true);
		lblPage1.setBounds(192, 11, 158, 70);
		panel1.add(lblPage1);

		/*
		 * ======================Villein panel ===============================
		 */
		JPanelC panelC = new JPanelC();
		panelC.setBounds(50, 100, 400, 400);
		LineWrangler wrangler = new LineWrangler(panelC);

		panel1.add(panelC.jC);

		panelC.addMouseListener(wrangler);
		panelC.addMouseMotionListener(wrangler);
		panel1.add(panelC);
		panelC.setLayout(null);

		/*
		 * =====================================================================
		 */

		JButton btnReset = new JButton("Reset");
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				panelC.clearLines();
			}
		});
		btnReset.setBounds(480, 230, 90, 40);
		panel1.add(btnReset);

		JPanel panel2 = new JPanel();
		panelContainer.add(panel2, "panel2");
		panel2.setLayout(null);

		JButton btnP2Button = new JButton("Next Page");
		btnP2Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				CardLayout cl = (CardLayout) panelContainer.getLayout();
				cl.show(panelContainer, "panel1");
			}
		});
		btnP2Button.setBounds(10, 11, 158, 70);
		panel2.add(btnP2Button);

		JLabel lblPage2 = new JLabel("Page 2");
		lblPage2.setBounds(220, 11, 158, 70);
		panel2.add(lblPage2);
		lblPage2.setHorizontalAlignment(SwingConstants.CENTER);
		lblPage2.setFont(new Font("Dialog", Font.PLAIN, 30));
		lblPage2.setEnabled(true);

	}

	public void coordOutput(String method, int x, int y) {
		// System.out.println("Method:" + method + "|X:" + x + "|Y:" + y);
	}

}