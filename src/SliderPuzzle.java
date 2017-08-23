import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

public class SliderPuzzle extends JFrame
{
	public static boolean inAnimation;
	
	private PuzzlePanel puzzleSpace;
	private JPanel buttonSpace;
	
	// For loading an image into a puzzle.
	private JFileChooser loader;
	private JButton loaderButton;
	
	private JButton scrambler;
	private JButton solver;
	
	private ButtonGroup puzzGroup;
	private JRadioButton puzzle8;
	private JRadioButton puzzle15;
	private JRadioButton puzzle24;
	
	
	public SliderPuzzle()
	{
		super("Slider Puzzle");
		
		inAnimation = false;
		
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout(0,0));
		
		// Set up the puzzle space.
		puzzleSpace = new PuzzlePanel();
		cp.add(puzzleSpace, BorderLayout.CENTER);
		puzzleSpace.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent event){}
			@Override
			public void mouseEntered(MouseEvent event){}
			@Override
			public void mouseExited(MouseEvent event){}
			@Override
			public void mouseReleased(MouseEvent event){}
			@Override
			public void mousePressed(MouseEvent event)
			{
				if(inAnimation)
					return;
				
				if(puzzleSpace.getEmptyLoc() == null)
				{
					puzzleSpace.setEmptyLoc(event.getPoint());
					puzzleSpace.repaint();
				}
				else
				{
					puzzleSpace.playerMove(event.getPoint());
					puzzleSpace.goalCheck();
					puzzleSpace.repaint();
				}
			}
		});
		
		// Set up the button space.
		buttonSpace = new JPanel(new GridBagLayout());
		
		GridBagConstraints loadc = new GridBagConstraints();
		loadc.fill = GridBagConstraints.HORIZONTAL;
		loadc.weightx = 0.5;
		loadc.gridx = 0;
		loadc.gridy = 0;
		
		loader = new JFileChooser();
		loaderButton = new JButton("Load Image");
		buttonSpace.add(loaderButton, loadc);
		loaderButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event)
			{
				if(inAnimation)
					return;
				
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
		
		GridBagConstraints scrambc = new GridBagConstraints();
		scrambc.fill = GridBagConstraints.HORIZONTAL;
		scrambc.weightx = 0.5;
		scrambc.gridx = 1;
		scrambc.gridy = 0;
		
		scrambler = new JButton("Scramble");
		buttonSpace.add(scrambler, scrambc);
		scrambler.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event)
			{
				if(inAnimation)
					return;
				
				if(puzzleSpace.getImage() != null)
				{
					puzzleSpace.scramble();
					puzzleSpace.repaint();
				}
			}
		});
		
		GridBagConstraints solvec = new GridBagConstraints();
		solvec.fill = GridBagConstraints.HORIZONTAL;
		solvec.weightx = 0.5;
		solvec.gridx = 2;
	    solvec.gridy = 0;
		
		solver = new JButton("Solve");
		buttonSpace.add(solver, solvec);
		solver.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event)
			{
				if(inAnimation)
					return;
				
				puzzleSpace.solve();
				puzzleSpace.repaint();
			}
		});
		
		GridBagConstraints p8c = new GridBagConstraints();
		p8c.fill = GridBagConstraints.HORIZONTAL;
		p8c.weightx = 0.5;
		p8c.gridx = 0;
		p8c.gridy = 1;
		
		puzzle8 = new JRadioButton("8-puzzle", true);
		buttonSpace.add(puzzle8,p8c);
		puzzle8.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event)
			{
				if(inAnimation)
					return;
				
				Options.GRID_ROOT = 3;
				puzzleSpace.splitImage();
				puzzleSpace.repaint();
			}
			
		});
		
		GridBagConstraints p15c = new GridBagConstraints();
		p15c.fill = GridBagConstraints.HORIZONTAL;
		p15c.weightx = 0.5;
		p15c.gridx = 1;
		p15c.gridy = 1;
		
		puzzle15 = new JRadioButton("15-puzzle", false);
		buttonSpace.add(puzzle15,p15c);
		puzzle15.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event)
			{
				if(inAnimation)
					return;
				
				Options.GRID_ROOT = 4;
				puzzleSpace.splitImage();
				puzzleSpace.repaint();
			}
		});
		
		GridBagConstraints p24c = new GridBagConstraints();
		p24c.fill = GridBagConstraints.HORIZONTAL;
		p24c.weightx = 0.5;
		p24c.gridx = 2;
		p24c.gridy = 1;
		
		puzzle24 = new JRadioButton("24-puzzle", false);
		buttonSpace.add(puzzle24,p24c);
		puzzle24.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event)
			{
				if(inAnimation)
					return;
				
				Options.GRID_ROOT = 5;
				puzzleSpace.splitImage();
				puzzleSpace.repaint();
			}
		});
		
		puzzGroup = new ButtonGroup();
		puzzGroup.add(puzzle8);
		puzzGroup.add(puzzle15);
		puzzGroup.add(puzzle24);
		
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
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new SliderPuzzle();
			}
		});
	}
}