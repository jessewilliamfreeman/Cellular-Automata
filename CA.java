//Jesse Freeman
//MP2

import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;

public class CA implements Runnable {
	private Cell[][] automata;
	private JFrame gui;
	private JPanel automataGui;
	private JPanel otherButtons;
	private JButton runButton;
	private JButton randomizeButton;
	private JButton clearButton;
	private JButton modeButton;
	private JButton vonNeumannButton;
	private JButton toroidButton;
	private String mode = "GOL";
	private boolean running = false;
	private boolean vonNeumann = true;
	private boolean toroid = true;
	private Thread t;
	
	private class Cell implements ActionListener {
		public JButton button;
		public int value;
		public String mode;
		
		public Cell(String mode) {
			this.button = new JButton();
			this.value = 0;
			this.mode = mode;
			this.button.setBackground(Color.WHITE);
			this.button.setBorder(BorderFactory.createLineBorder(Color.GRAY));
			this.button.addActionListener(this);
		}
		
		//for when a cell is clicked so it'll chenge to the next highest value or 0
		public void actionPerformed(ActionEvent e) {
			if (this.mode.equals("GOL")) {
				this.update((this.value + 1) % 2);
			} else if (this.mode.equals("FC")) {
				this.update((this.value + 1) % 3);
			}
		}
		
		public void update(int updateNumb) {
			switch (updateNumb) {
				case 0:	this.value = 0;
						this.button.setBackground(Color.WHITE);
						break;
				case 1: this.value = 1;
						this.button.setBackground(Color.RED);
						break;
				case 2: this.value = 2;
						this.button.setBackground(Color.BLUE);
						break;
			}
		}
		
	}
	
	public CA(int automataX, int automataY) {
		this.automata = new Cell[automataX][automataY];
		this.mode = "GOL";
		this.gui = new JFrame("Cellular Automata");
		this.gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.automataGui = new JPanel();
		this.gui.add(automataGui, BorderLayout.CENTER);
		this.otherButtons = new JPanel();
		this.gui.add(otherButtons, BorderLayout.SOUTH);
		
		this.automataGui.setLayout(new GridLayout(automataX,automataY));
		for (int x = 0; x < automataX; x++) {
			for (int y = 0; y < automataY; y++) {
				automata[x][y] = new Cell(this.mode);
				automataGui.add(this.automata[x][y].button);
			}
		}
		this.runButton = new JButton("Run");
		this.runButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (running) {
					running = false;
					runButton.setText("Run");
				} else {
					running = true;
					runButton.setText("Pause");
				}
			}
		});
		this.otherButtons.add(this.runButton);
		
		this.randomizeButton = new JButton("Randomize");
		this.randomizeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Random numberGen = new Random();
				for (int x = 0; x < automata.length; x++) {
					for (int y = 0; y < automata[x].length; y++) {
						if (mode.equals("GOL")) {
							automata[x][y].update(numberGen.nextInt(2));
						} else if (mode.equals("FC")) {
							automata[x][y].update(numberGen.nextInt(3));
						}
					}
				}
			}
		});
		this.otherButtons.add(this.randomizeButton);
		
		this.clearButton = new JButton("Clear");
		this.clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (int x = 0; x < automata.length; x++) {
					for (int y = 0; y < automata[x].length; y++) {
						automata[x][y].update(0);
					}
				}
			}
		});
		this.otherButtons.add(this.clearButton);
		
		this.modeButton = new JButton("Game of Life");
		this.modeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (mode.equals("GOL")) {
					mode = "FC";
					modeButton.setText("Food Chain");
				} else if (mode.equals("FC")) {
					mode = "GOL";
					modeButton.setText("Game of Life");
				}
				for (int x = 0; x < automata.length; x++) {
					for (int y = 0; y < automata[x].length; y++) {
						automata[x][y].mode = mode;
						if (mode.equals("GOL") && automata[x][y].value > 1) {
							automata[x][y].update(1);
						}
					}
				}
			}
		});
		this.otherButtons.add(this.modeButton);
		
		this.vonNeumannButton = new JButton("von Neumann");
		this.vonNeumannButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (vonNeumann) {
					vonNeumann = false;
					vonNeumannButton.setText("Moore");
				} else {
					vonNeumann = true;
					vonNeumannButton.setText("von Neumann");
				}
			}
		});
		this.otherButtons.add(this.vonNeumannButton);
		
		//way to change from toroid to linear and back
		this.toroidButton = new JButton("Toroid");
		this.toroidButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (toroid) {
					toroid = false;
					toroidButton.setText("Linear");
				} else {
					toroid = true;
					toroidButton.setText("Toroid");
				}
			}
		});
		this.otherButtons.add(this.toroidButton);
		
		this.gui.setSize(600,600);
		this.gui.setVisible(true); 
		
		this.t = new Thread(this);
		this.t.start();
	}
	
	public void update() {
		Integer[][] updates = new Integer[automata.length][automata[0].length];
		for (int x = 0; x < automata.length; x++) {
			for (int y = 0; y < automata[x].length; y++) {
				int count = 0;
				//Game of Life default
				int valueCheck = 1;
				
				//Food Chain value to check for
				if (this.mode.equals("FC")) {
					valueCheck = (automata[x][y].value + 1) % 3;
				}
				
				//checks left
				if (x == 0) {
					if(this.toroid && automata[automata.length - 1][y].value == valueCheck) {
						count++;
					}
				} else if (automata[x - 1][y].value == valueCheck) {
					count++;
				}
				//checks right
				if (x == automata.length - 1) {
					if(this.toroid && automata[0][y].value == valueCheck) {
						count++;
					}
				} else if (automata[x + 1][y].value == valueCheck) {
					count++;
				}
				//checks top
				if (y == 0) {
					if(this.toroid && automata[x][automata[x].length - 1].value == valueCheck) {
						count++;
					}
				} else if (automata[x][y - 1].value == valueCheck) {
					count++;
				}
				//checks bottom
				if (y == automata.length - 1) {
					if(this.toroid && automata[x][0].value == valueCheck) {
						count++;
					}
				} else if (automata[x][y + 1].value == valueCheck) {
					count++;
				}
					
				if (this.vonNeumann) {
					//checks top-left
					if (x == 0 && y == 0) {
						if (this.toroid && automata[automata.length - 1][automata[x].length - 1].value == valueCheck) {
							count++;
						}
					} else if (x == 0) {
						if (this.toroid && automata[automata.length - 1][y - 1].value == valueCheck) {
							count++;
						}
					} else if (y == 0) {
						if (this.toroid && automata[x - 1][automata[x].length - 1].value == valueCheck) {
							count++;
						}
					} else if (automata[x - 1][y - 1].value == valueCheck) {
						count++;
					}
						
					//checks top-right
					if (x == automata.length - 1 && y == 0) {
						if (this.toroid && automata[0][automata[x].length - 1].value == valueCheck) {
							count++;
						}
					} else if (x == automata.length - 1) {
						if (this.toroid && automata[0][y - 1].value == valueCheck) {
							count++;
						}
					} else if (y == 0) {
						if (this.toroid && automata[x + 1][automata[x].length - 1].value == valueCheck) {
							count++;
						}
					} else if (automata[x + 1][y - 1].value == valueCheck) {
						count++;
					}
						
					//checks bottom-left
					if (x == 0 && y == automata[x].length - 1) {
						if (this.toroid && automata[automata.length - 1][0].value == valueCheck) {
							count++;
						}
					} else if (x == 0) {
						if (this.toroid && automata[automata.length - 1][y + 1].value == valueCheck) {
							count++;
						}
					} else if (y == automata[x].length - 1) {
						if (this.toroid && automata[x - 1][0].value == valueCheck) {
							count++;
						}
					} else if (automata[x - 1][y + 1].value == valueCheck) {
						count++;
					}
						
					//checks bottom-right
					if (x == automata.length - 1 && y == automata[x].length - 1) {
						if (this.toroid && automata[0][0].value == valueCheck) {
							count++;
						}
					} else if (x == automata.length - 1) {
						if (this.toroid && automata[0][y + 1].value == valueCheck) {
							count++;
						}
					} else if (y == automata[x].length - 1) {
						if (this.toroid && automata[x + 1][0].value == valueCheck) {
							count++;
						}
					} else if (automata[x + 1][y + 1].value == valueCheck) {
						count++;
					}
				}
					
					
				//adds the update to do into the undate array
				if (this.mode.equals("GOL")) {
					if (count < 2) {
						updates[x][y] = new Integer(0);
					} else if (count == 3) {
						updates[x][y] = new Integer(1);
					} else if (count > 3) {
						updates[x][y] = new Integer(0);
					}
				} else if (this.mode.equals("FC") && count > 2) {
					updates[x][y] = new Integer(valueCheck);
				}
			}
		}
		
		//updates the appropreate cells
		for (int x = 0; x < updates.length; x++) {
			for (int y = 0; y < updates[x].length; y++) {
				if (updates[x][y] != null) {
					automata[x][y].update(updates[x][y].intValue());
				}
			}
		}
	}
	
	public static void main(String [] args) {
		// Swing issue
		// Citation:
		// http://stackoverflow.com/questions/13575224/comparison-method-violates-its-general-contract-timsort-and-gridlayout
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		
		//edit this to change grid size 
		new CA(50, 50);
	}
	
	@Override
    public void run() {
		while (true) {
			if (this.running) {
				this.update();
			}
			try {
				t.sleep(100);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}