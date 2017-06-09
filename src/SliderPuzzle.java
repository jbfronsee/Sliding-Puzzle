import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

public class SliderPuzzle extends JFrame
{
	private PuzzlePanel puzzleSpace;
	private JPanel buttonSpace;
	
	// For loading an image into a puzzle.
	private JFileChooser loader;
	private JButton loaderButton;
	
	private JButton scrambler;
	private JButton solver;
	
	public SliderPuzzle()
	{
		super("Slider Puzzle");
		
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout(0,0));
		
		// Set up the puzzle space.
		puzzleSpace = new PuzzlePanel();
		cp.add(puzzleSpace, BorderLayout.CENTER);
		puzzleSpace.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent event)
			{
				puzzleSpace.makeMove(event.getPoint());
				puzzleSpace.repaint();
			}
			@Override
			public void mouseEntered(MouseEvent event){}
			@Override
			public void mouseExited(MouseEvent event){}
			@Override
			public void mouseReleased(MouseEvent event){}
			@Override
			public void mousePressed(MouseEvent event){}
		});
		
		// Set up the button space.
		buttonSpace = new JPanel(new GridLayout(1,3));
		
		loader = new JFileChooser();
		
		loaderButton = new JButton("Load Image");
		buttonSpace.add(loaderButton);
		loaderButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event)
			{
				File file = null;
				int state = loader.showOpenDialog(null);
				if(state == JFileChooser.APPROVE_OPTION)
				{
					file = loader.getSelectedFile();
				}
				
				BufferedImage img = null;
				try 
				{
					if(file != null)
					{
						img = ImageIO.read(file);
						puzzleSpace.loadImage(img);
						puzzleSpace.repaint();
					}
				} catch (IOException e)
				{
					System.err.println("Failed to read file!");
				}
				
			}
		});
		
		scrambler = new JButton("Scramble");
		buttonSpace.add(scrambler);
		scrambler.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event)
			{
				scrambler.setText("Pressed!");
			}
		});
		
		solver = new JButton("Solve");
		buttonSpace.add(solver);
		solver.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event)
			{
				solver.setText("Pressed!");
			}
		});
		
		cp.add(buttonSpace, BorderLayout.SOUTH);
		
		// Set up the window.
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle bounds = getBounds();
		setLocation(screenSize.width/2 - bounds.width/2,screenSize.height/2 - bounds.height/2);
		setVisible(true);
	}
	
	public static void main(String[] args)
	{
		try 
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(UnsupportedLookAndFeelException e)
		{
			System.err.println("Unsupported look and feel!");
			System.exit(0);
		} catch(ClassNotFoundException e)
		{
			System.err.println("Class Not Found for look and feel!");
			System.exit(0);
		} catch(InstantiationException e)
		{
			System.err.println("Instantiation failed for look and feel!");
			System.exit(0);
		} catch(IllegalAccessException e)
		{
			System.err.println("Illegal Access for look and feel!");
			System.exit(0);
		}
		
		new SliderPuzzle();
	}
}