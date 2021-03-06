/*
 This file is a part of Free Klondike

 Copyright (C) 2010-2014 by Matt Stephen, Todor Balabanov, Konstantin Tsanov, Ventsislav Medarov, Vanya Gyaurova, Plamena Popova, Hristiana Kalcheva, Yana Genova

 Free Klondike is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Free Klondike is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with FreeKlondike.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.veldsoft.free.klondike;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.event.MouseInputAdapter;

/**
 * 
 * @author Todor Balabanov
 */
class SolitaireBoardFrame extends JFrame {

	/**
	 * Manages the window events.
	 * 
	 * @author Todor Balabanov
	 */
	private class MyWindowListener extends WindowAdapter {

		/**
		 * On closing the main window:
		 * 
		 * @param e
		 *            On closing the main frame.
		 * 
		 * @author Todor Balabanov
		 */
		public void windowClosing(WindowEvent e) {
			// TODO Remove in the final release.
			if (true) {
				return;
			}

			int save = JOptionPane
					.showConfirmDialog(
							SolitaireBoardFrame.this,
							"Closing without saving will result "
									+ "in a loss, would you like to save the current game?",
							"Save Game?", JOptionPane.YES_NO_OPTION);

			if (save == JOptionPane.YES_OPTION) {
				board.recordGame(GameState.GAME_SAVED, deckNumber,
						backgroundNumber, timerCount, timerToRunNextGame,
						timerToRun);
				System.exit(0);
			} else if (save == JOptionPane.NO_OPTION) {
				board.recordGame(GameState.GAME_LOST, deckNumber,
						backgroundNumber, timerCount, timerToRunNextGame,
						timerToRun);
				System.exit(0);
			}
		}
	}

	/**
	 * Timer displaying.
	 * 
	 * @author Todor Balabanov
	 * 
	 */
	private class TimerListener implements ActionListener {

		/**
		 * Action performed.
		 * 
		 * @param e
		 *            On timer switched on.
		 * 
		 * @author Todor Balabanov
		 */
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() != timer) {
				return;
			}
			timerCount++;
			timerLabel.setText("Time: " + timerCount);
			statusBar.repaint();
		}
	}

	/**
	 * Manages the mouse events.
	 * 
	 * @author Todor Balabanov
	 */
	private class MyMouseListener extends MouseInputAdapter {

		/**
		 * If true, the player hasn't completed a move.
		 */
		private boolean hasSelected = false;

		/**
		 * If true, the selected stack is only one card.
		 */
		private boolean singleCardSelected = false;

		/**
		 * 
		 */
		private Card clickedCard;

		/**
		 * 
		 */
		private CardStackLayeredPane source;

		/**
		 * 
		 */
		private CardStackLayeredPane destination;

		/**
		 * 
		 */
		private Vector<Card> temp;

		/**
		 * For right clicking discard pile view.
		 */
		private Card tempCard;

		/**
		 * To prevent clicking cards from the right click view.
		 */
		private boolean rightClicked = false;

		/**
		 * Checks if the game is won
		 * 
		 * @author Todor Balabanov
		 */
		private void checkWin() {
			if (board.isSolved() == false) {
				return;
			}

			if (WinScreen.animation != 0 || WinScreen.sounds != 0) {
				new WinScreen();
			}

			// TODO fix this thing.
			if (timerToRun) {
				final TopTimes top = new TopTimes();
				int pos = top.IsTopTime(timerCount);

				top.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						top.setVisible(false);
						int playAgain = JOptionPane.showConfirmDialog(
								SolitaireBoardFrame.this, "Play Again?",
								"You Won!", JOptionPane.YES_NO_OPTION);

						if (playAgain == JOptionPane.YES_OPTION) {
							board.recordGame(GameState.GAME_WON, deckNumber,
									backgroundNumber, timerCount,
									timerToRunNextGame, timerToRun);
							board.newGame(GameState.GAME_WON);
						} else if (playAgain == JOptionPane.NO_OPTION) {
							board.recordGame(GameState.GAME_WON, deckNumber,
									backgroundNumber, timerCount,
									timerToRunNextGame, timerToRun);
							System.exit(0);
						}
					}
				});

				if (pos >= 0) {
					top.setProperties(timerCount);
					top.setVisible(true);
				} else {
					top.dispose();
				}
			}

			int playAgain = JOptionPane.showConfirmDialog(
					SolitaireBoardFrame.this, "Play Again?", "You Won!",
					JOptionPane.YES_NO_OPTION);

			if (playAgain == JOptionPane.YES_OPTION) {
				board.recordGame(GameState.GAME_WON, deckNumber,
						backgroundNumber, timerCount, timerToRunNextGame,
						timerToRun);
				board.newGame(GameState.GAME_WON);
			} else if (playAgain == JOptionPane.NO_OPTION) {
				board.recordGame(GameState.GAME_WON, deckNumber,
						backgroundNumber, timerCount, timerToRunNextGame,
						timerToRun);
				System.exit(0);
			}
		}

		/**
		 * On click on the discard pile.
		 * 
		 * @param e
		 *            Mouse pressed event.
		 * 
		 * @author Todor Balabanov
		 */
		public void mousePressed(MouseEvent e) {
			if (e.getButton() != MouseEvent.BUTTON3
					|| e.getSource() != discardPile) {
				return;
			}
			if (board.discardPile.getNumViewableCards() != 1
					&& (board.discardPile.getNumViewableCards() != 0 || board.discardPile
							.isEmpty() == true)) {
				return;
			}

			tempCard = board.discardPile.pop();
			rightClicked = true;

			redrawAll();
		}

		/**
		 * On mouse released on the deal deck.
		 * 
		 * @param e
		 *            Mouse released event.
		 * 
		 * @author Todor Balabanov
		 */
		public void mouseReleased(MouseEvent e) {
			if (e.getButton() != MouseEvent.BUTTON3 || tempCard == null) {
				return;
			}

			board.discardPile.push(tempCard);
			rightClicked = false;
			tempCard = null;

			redrawAll();
		}

		private void dealDeckClicked() {
			/*
			 * Clear cards highlighting.
			 */
			if (hasSelected == true) {
				hasSelected = false;

				for (int i = 0; i < temp.size(); i++) {
					board.sourceList
							.getLast()
							.getCardAtLocation(
									board.sourceList.getLast().length() - i - 1)
							.unhighlight();
				}

				if (temp.size() == 0) {
					board.sourceList.getLast().peek().unhighlight();
				}

				redrawAll();

				board.sourceList.removeLast();
				board.numCardsInDiscardView.removeLast();
				board.numCards.removeLast();
			}

			board.numCardsInDiscardView.add(board.discardPile
					.getNumViewableCards());
			clickedCard = source.pop();

			if (clickedCard != null) {
				board.sourceList.add(board.dealDeck);
				board.destinationList.add(board.discardPile);
				board.numCards.add(board.discardPile.getNumViewableCards());
			} else if (board.dealDeck.hasDealsLeft() == true) {
				/*
				 * The deck was reset but the player hasn't used up the times
				 * through the deck.
				 */
				board.sourceList.add(board.dealDeck);
				board.destinationList.add(board.discardPile);
				board.numCards.add(0);
			} else {
				board.numCardsInDiscardView.removeLast();
			}
		}

		private void singleCardDoubleClicked() {
			/*
			 * The card is Ace and should be collected in Ace Pile.
			 */
			if (source.peek().getRank().equals(CardRank.ACE)) {
				Card card = source.pop();
				AcePileLayeredPane pile = acePiles[card.getSuit().getIndex()];
				card.unhighlight();

				pile.push(card);
				board.destinationList.add(pile.acePile);

				hasSelected = false;
				return;
			}

			/*
			 * The card is not Ace and should be collected in Ace Pile only if
			 * suite and rank are matching with Ace Pile state.
			 */
			for (int i = 0; i < board.acePiles.length; i++) {
				if (board.acePiles[i].isEmpty() == true) {
					continue;
				}
				if (source.peek().getSuit()
						.equals(board.acePiles[i].peek().getSuit()) == false) {
					continue;
				}
				if (source.peek().getRank()
						.isLessByOneThan((board.acePiles[i].peek().getRank())) == false) {
					continue;
				}

				Card card = source.pop();
				card.unhighlight();
				board.acePiles[i].push(card);

				board.destinationList.add(board.acePiles[i]);
				hasSelected = false;

				/*
				 * If four kings are in the ace piles the game is finished.
				 */
				if (card.getRank().equals(CardRank.KING)) {
					checkWin();
				}

				return;
			}

			/*
			 * The card is in the Cell and should go in some destination (Ace
			 * Pile or Column).
			 */
			for (int i = 0; i < board.cells.length; i++) {
				if (board.cells[i].isEmpty() == false) {
					continue;
				}

				Card card = source.pop();
				card.unhighlight();
				board.cells[i].push(card);

				board.destinationList.add(board.cells[i]);
				hasSelected = false;

				return;
			}

			source.peek().unhighlight();
			return;
		}

		/**
		 * On mouse click.
		 * 
		 * @param e
		 *            Mouse-clicked event.
		 * 
		 * @author Todor Balabanov
		 */
		public void mouseClicked(MouseEvent e) {
			// redrawAll();

			if (!timer.isRunning() && timerToRun) {
				timer.start();
			}

			/*
			 * Do not handle anything else except left mouse button when right
			 * mouse button was not clicked.
			 */
			if ((e.getButton() != MouseEvent.BUTTON1) || rightClicked == true) {
				return;
			}

			if (e.getClickCount() == 2 && hasSelected == true
					&& singleCardSelected == true) {
				singleCardDoubleClicked();
				redrawAll();
			} else if (e.getClickCount() == 2 && hasSelected == true
					&& singleCardSelected == false) {
				hasSelected = false;

				if (temp.size() > 0) {
					for (int i = 0; i < temp.size(); i++) {
						source.getCardAtLocation(source.length() - i - 1)
								.unhighlight();
					}
				}

				board.sourceList.removeLast();
				board.numCardsInDiscardView.removeLast();
				board.numCards.removeLast();
			} else if (e.getSource() instanceof DealDeckLayeredPane) {
				source = (CardStackLayeredPane) e.getSource();
				dealDeckClicked();
			} else if (hasSelected == false && e.getClickCount() == 1) {
				source = (CardStackLayeredPane) e.getSource();
				board.numCardsInDiscardView.add(board.discardPile
						.getNumViewableCards());
				clickedCard = source.getCardAtLocation(e.getPoint());

				if (clickedCard != null) {
					hasSelected = true;
					temp = source.getStack(clickedCard);

					if (source instanceof AcePileLayeredPane) {
						board.sourceList
								.add(((AcePileLayeredPane) source).acePile);
					} else if (source instanceof ColumnLayeredPane) {
						board.sourceList
								.add(((ColumnLayeredPane) source).column);
					} else if (source instanceof DealDeckLayeredPane) {
						board.sourceList
								.add(((DealDeckLayeredPane) source).dealDeck);
					} else if (source instanceof DiscardPileLayeredPane) {
						board.sourceList
								.add(((DiscardPileLayeredPane) source).discardPile);
					} else if (source instanceof SingleCellLayeredPane) {
						board.sourceList
								.add(((SingleCellLayeredPane) source).singleCell);
					}
					board.numCards.add(temp.size());

					if (temp.size() > 1) {
						singleCardSelected = false;
					} else {
						singleCardSelected = true;
					}
				} else {
					board.numCardsInDiscardView.removeLast();
					hasSelected = false;
				}
			} else if (e.getClickCount() == 1 && hasSelected == true
					&& singleCardSelected == true) {
				/*
				 * Card already selected.
				 */
				destination = (CardStackLayeredPane) e.getSource();

				if (destination.isValidMove(clickedCard) == true) {
					Card card = source.pop();
					card.unhighlight();
					destination.push(card);

					/*
					 * If move is valid, add destination info for undo.
					 */
					if (destination instanceof AcePileLayeredPane) {
						board.destinationList
								.add(((AcePileLayeredPane) destination).acePile);
					} else if (destination instanceof ColumnLayeredPane) {
						board.destinationList
								.add(((ColumnLayeredPane) destination).column);
					} else if (destination instanceof DealDeckLayeredPane) {
						board.destinationList
								.add(((DealDeckLayeredPane) destination).dealDeck);
					} else if (destination instanceof DiscardPileLayeredPane) {
						board.destinationList
								.add(((DiscardPileLayeredPane) destination).discardPile);
					} else if (destination instanceof SingleCellLayeredPane) {
						board.destinationList
								.add(((SingleCellLayeredPane) destination).singleCell);
					}

					if (destination instanceof AcePileLayeredPane
							&& clickedCard.getRank().equals(CardRank.KING)) {
						checkWin();
					}
				} else {
					/*
					 * Not needed with highlighting version.
					 */
					source.peek().unhighlight();

					/*
					 * Upon invalid move, remove undo information for cards.
					 */
					board.sourceList.removeLast();
					board.numCards.removeLast();
					board.numCardsInDiscardView.removeLast();
				}

				singleCardSelected = false;
				hasSelected = false;
				temp = null;
				clickedCard = null;
			} else if (e.getClickCount() == 1 && hasSelected == true
					&& singleCardSelected == false) {
				/*
				 * Stack already selected.
				 */
				destination = (CardStackLayeredPane) e.getSource();

				if (destination.isValidMove(temp)) {
					Vector<Card> stack = new Vector<Card>();
					for (int i = temp.size(); i > 0; i--) {
						Card card = source.pop();
						card.unhighlight();

						stack.add(card);
					}

					destination.push(stack);

					/*
					 * If move is valid, add destination info for undo.
					 */
					if (source instanceof AcePileLayeredPane) {
						board.destinationList
								.add(((AcePileLayeredPane) destination).acePile);
					} else if (source instanceof ColumnLayeredPane) {
						board.destinationList
								.add(((ColumnLayeredPane) destination).column);
					} else if (source instanceof DealDeckLayeredPane) {
						board.destinationList
								.add(((DealDeckLayeredPane) destination).dealDeck);
					} else if (source instanceof DiscardPileLayeredPane) {
						board.destinationList
								.add(((DiscardPileLayeredPane) destination).discardPile);
					} else if (source instanceof SingleCellLayeredPane) {
						board.destinationList
								.add(((SingleCellLayeredPane) destination).singleCell);
					}
				} else {
					for (int i = temp.size() - 1; i >= 0; i--) {
						source.getCardAtLocation(source.length() - i - 1)
								.unhighlight();
					}

					/*
					 * Upon invalid move, remove undo information for cards.
					 */
					board.sourceList.removeLast();
					board.numCards.removeLast();
					board.numCardsInDiscardView.removeLast();
				}

				singleCardSelected = false;
				hasSelected = false;
				temp = null;
				clickedCard = null;
			}

			/*
			 * Redraw frame after all card moves.
			 */
			redrawAll();
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Images resources path.
	 */
	final static String IMAGES_PATH = "images/iana";

	/**
	 * Find better OOP modeling alternative! Use enumerated type for card back.
	 */
	static int deckNumber = 3;

	/**
	 * Find better OOP modeling alternative! Use enumerated type for card back.
	 */
	static int backgroundNumber = 2;

	/**
	 * Solitaire Board.
	 */
	private SolitaireBoard board = new SolitaireBoard();

	/**
	 * The four columns for the main playing field.
	 */
	private ColumnLayeredPane[] columns = new ColumnLayeredPane[4];

	/**
	 * The discard pile.
	 */
	private DiscardPileLayeredPane discardPile = new DiscardPileLayeredPane();
	/* Initialize internal references. */{
		discardPile.discardPile = board.discardPile;
	}

	/**
	 * The deal pile.
	 */
	private DealDeckLayeredPane dealDeck = new DealDeckLayeredPane(discardPile);
	/* Initialize internal references. */{
		dealDeck.dealDeck = board.dealDeck;
	}

	/**
	 * The four ace piles (to stack Ace - King of a single suit).
	 */
	private AcePileLayeredPane[] acePiles = new AcePileLayeredPane[4];

	/**
	 * The four top individual cells.
	 */
	private SingleCellLayeredPane[] cells = new SingleCellLayeredPane[4];

	/**
	 * Timer.
	 */
	private Timer timer = new Timer(1000, new TimerListener());

	/**
	 * Timer count.
	 */
	private int timerCount = 0;

	/**
	 * Timer to next game.
	 */
	private int timerToRunNextGame = 0;

	/**
	 * Timer to run.
	 */
	private boolean timerToRun = false;

	/**
	 * 
	 */
	private SolitairePanel mainPanel = null;

	/**
	 * Status bar.
	 */
	private JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.CENTER));

	/**
	 * Timer label.
	 */
	private JLabel timerLabel = new JLabel("Time: OFF");

	/**
	 * 
	 */
	private MyMouseListener ml = new MyMouseListener();

	/**
	 * 
	 */
	protected WindowListener wl = new MyWindowListener();

	/**
	 * Repaint all card stacks.
	 * 
	 * @author Todor Balabanov
	 */
	private void redrawAll() {
		for (int i = 0; i < columns.length; i++) {
			columns[i].repaint();
			columns[i].revalidate();
		}
		for (int i = 0; i < cells.length; i++) {
			cells[i].repaint();
			cells[i].revalidate();
		}
		for (int i = 0; i < acePiles.length; i++) {
			acePiles[i].repaint();
			acePiles[i].revalidate();
		}
		dealDeck.repaint();
		dealDeck.revalidate();
		discardPile.repaint();
		discardPile.revalidate();

		repaint();
		revalidate();
	}

	/**
	 * Unhighlights highlighted cards.
	 * 
	 * @author Todor Balabanov
	 */
	private void unhighlighCards() {
		for (int i = 0; i < columns.length; i++) {
			for (int j = 0; j < columns[i].column.length(); j++) {
				columns[i].column.getCardAtLocation(j);
			}
		}
		for (int i = 0; i < cells.length; i++) {
			cells[i].singleCell.getCardAtLocation(0);
		}
		for (int i = 0; i < acePiles.length; i++) {
			for (int j = 0; j < acePiles[i].acePile.length(); j++) {
				acePiles[i].acePile.getCardAtLocation(j);
			}
		}
	}

	/**
	 * 
	 * @throws HeadlessException
	 *             Thrown when code that dephends on a keyboard,display or mouse
	 *             is called in an environment that does not support a keyboard,
	 *             display or mouse.
	 * 
	 * @author Todor Balabanov
	 */
	public SolitaireBoardFrame() throws HeadlessException {
		super();
		setTitle("Free Klondike");
		setSize(806, 700);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setIconImage(new ImageIcon(getClass().getResource(
				SolitaireBoardFrame.IMAGES_PATH + "/logo.png")).getImage());

		setVisible(true);

		addWindowListener(wl);
	}

	/**
	 * Returns the card back number.
	 * 
	 * @return deckNumber Current card back number.
	 * 
	 * @author Todor Balabanov
	 */
	public int getDeckNumber() {
		return deckNumber;
	}

	/**
	 * Sets the card back number.
	 * 
	 * @param deckNum
	 *            Card back number to be set.
	 * 
	 * @author Todor Balabanov
	 */
	public void setDeckNumber(int deckNum) {
		deckNumber = deckNum;

		if (deckNumber > ChangeAppearance.NUM_DECKS || deckNumber <= 0) {
			deckNumber = ChangeAppearance.FRS_DECK;
		}
	}

	/**
	 * Returns the background number.
	 * 
	 * @return backgroundNumber Current background number.
	 * 
	 * @author Todor Balabanov
	 */
	public int getBackgroundNumber() {
		return backgroundNumber;
	}

	/**
	 * Sets the background number.
	 * 
	 * @param backNum
	 *            To be used for the bacgroundNumber.
	 * 
	 * @author Todor Balabanov
	 */
	public void setBackgroundNumber(int backNum) {
		backgroundNumber = backNum;

		if (backgroundNumber > ChangeAppearance.NUM_BACKGROUNDS
				|| backgroundNumber <= 0) {
			backgroundNumber = ChangeAppearance.FRS_BACKGROUND;
		}
	}

	/**
	 * Sets the timer counter.
	 * 
	 * @param time
	 *            Timer.
	 * 
	 * @author Todor Balabanov
	 */
	public void setTimer(int time) {
		timerCount = time;
	}

	/**
	 * Returns the timer status.
	 * 
	 * @return Timer status.
	 * 
	 * @author Todor Balabanov
	 */
	public int getTimerStatus() {
		if (timer.isRunning()) {
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * Returns next game timer status.
	 * 
	 * @return timerToRunNextGame Timer to next game.
	 * 
	 * @author Todor Balabanov
	 */
	public int getTimerNextGameStatus() {
		return timerToRunNextGame;
	}

	/**
	 * Sets timer status.
	 * 
	 * @param timerInt
	 *            To set the timer status.
	 * 
	 * @author Todor Balabanov
	 */
	public void setTimerStatus(int timerInt) {
		if (timerInt == 1) {
			timerToRunNextGame = 1;
		} else if (timerInt == 0) {
			timerToRunNextGame = 0;
		}

		if (timerInt != 0 && timerInt != 1) {
			timerToRunNextGame = 0;
		}
	}

	/**
	 * Manages the appearance.
	 * 
	 * @param deck
	 *            Deck number to be set.
	 * 
	 * @param background
	 *            Background number to be set.
	 * 
	 * @author Todor Balabanov
	 */
	public void setAppearance(int deck, int background) {
		deckNumber = deck;
		backgroundNumber = background;

		mainPanel.changeBackground(backgroundNumber);
	}

	/**
	 * Manages the hints.
	 * 
	 * @author Todor Balabanov
	 */
	@SuppressWarnings("fallthrough")
	public void getHint() {
		String hint[] = board.getHint();
		JOptionPane.showMessageDialog(this, hint[0], hint[1],
				JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Used to undo a move.
	 * 
	 * @author Todor Balabanov
	 */
	public synchronized void undoMove() {
		if (board.sourceList.size() > board.destinationList.size()) {
			if (board.numCards.getLast() == 1) {
				ml.clickedCard = null;
				ml.hasSelected = false;
				ml.singleCardSelected = false;
				ml.temp = null;
			} else {
				ml.clickedCard = null;
				ml.hasSelected = false;
				ml.temp = null;
			}
		}

		CardStack tempSource = null;
		CardStack tempDest = null;
		if (board.sourceList.size() > board.destinationList.size()) {
			tempSource = board.sourceList.getLast();
		} else if (!(board.sourceList.getLast() instanceof DealDeck)) {
			tempSource = board.sourceList.getLast();
			tempDest = board.destinationList.getLast();
		}

		board.undoMove();

		redrawAll();
	}

	/**
	 * Clears the board.
	 * 
	 * @author Todor Balabanov
	 */
	private void clearBoard() {
		board.clearBoard();
		redrawAll();
	}

	/**
	 * For starting a new game.
	 * 
	 * @param winOrLoss
	 *            Game state.
	 * 
	 * @author Todor Balabanov
	 */
	public void newGame(GameState winOrLoss) {
		/*
		 * If the game was won, the win was already reported.
		 */
		if (winOrLoss == GameState.GAME_WON
				|| winOrLoss == GameState.DO_NOTHING) {
			return;
		}

		// TODO Remove this comment in final release.
		// if (JOptionPane.showConfirmDialog(this,
		// "Quitting the current game will result in a loss.\n"
		// + "Do you wish to continue?", "Continue?",
		// JOptionPane.PLAIN_MESSAGE) != JOptionPane.YES_OPTION) {
		// return;
		// }

		board.recordGame(GameState.GAME_LOST, deckNumber, backgroundNumber,
				timerCount, timerToRunNextGame, timerToRun);
		board.newGame(winOrLoss);
		redrawAll();
		dealOutBoard();
	}

	/**
	 * Used to reset the stats.
	 * 
	 * @author Todor Balabanov
	 */
	public void resetStats() {
		board.recordGame(GameState.RESET_STATS, deckNumber, backgroundNumber,
				timerCount, timerToRunNextGame, timerToRun);
	}

	/**
	 * Save options.
	 * 
	 * @author Todor Balabanov
	 */
	public void saveOptions() {
		board.recordGame(GameState.DO_NOTHING, deckNumber, backgroundNumber,
				timerCount, timerToRunNextGame, timerToRun);
	}

	/**
	 * Used to record the game.
	 * 
	 * @param winOrLoss
	 *            Game state.
	 * 
	 * @author Todor Balabanov
	 */
	void recordGame(GameState winOrLoss) {
		board.recordGame(winOrLoss, deckNumber, backgroundNumber, timerCount,
				timerToRunNextGame, timerToRun);
	}

	/**
	 * Used to deal the cards on the board after opening a saved game.
	 * 
	 * @param numbers
	 *            List of cards.
	 * 
	 * @param numViewableCards
	 *            Number of viewable cards.
	 * 
	 * @author Todor Balabanov
	 */
	private void dealOutCustomBoard(LinkedList<Integer> numbers,
			int numViewableCards) {
		board.dealOutCustomBoard(numbers, numViewableCards);
		redrawAll();

		if (timerToRunNextGame == 1) {
			timer.stop();
			timerLabel.setText("Time: " + (timerCount == -1 ? 0 : timerCount));

			timerToRun = true;
		} else {
			timer.stop();
			timerLabel.setText("Time: OFF");

			timerToRun = false;
		}

		mainPanel.revalidate();
	}

	/**
	 * Creates the solitaire board.
	 * 
	 * @param cards
	 *            List of integers.
	 * 
	 * @param numViewableCards
	 *            Number of viewable cards.
	 * 
	 * @author Todor Balabanov
	 */
	public void createBoard(LinkedList<Integer> cards, int numViewableCards) {
		board.createBoard(cards, numViewableCards);
		for (int i = 0; i < columns.length; i++) {
			columns[i] = new ColumnLayeredPane();
			columns[i].column = board.columns[i];
		}

		for (int i = 0; i < cells.length; i++) {
			cells[i] = new SingleCellLayeredPane();
			cells[i].singleCell = board.cells[i];
		}

		for (int i = 0; i < acePiles.length; i++) {
			acePiles[i] = new AcePileLayeredPane();
			acePiles[i].acePile = board.acePiles[i];
		}

		redrawAll();

		mainPanel = new SolitairePanel();
		mainPanel.setLayout(new SolitaireLayout());

		mainPanel.changeBackground(backgroundNumber);

		for (int i = 0; i < board.columns.length; i++) {
			columns[i].addMouseListener(ml);
		}

		mainPanel.add(columns[0], SolitaireLayout.COLUMN_ONE);
		mainPanel.add(columns[1], SolitaireLayout.COLUMN_TWO);
		mainPanel.add(columns[2], SolitaireLayout.COLUMN_THREE);
		mainPanel.add(columns[3], SolitaireLayout.COLUMN_FOUR);

		for (int i = 0; i < board.cells.length; i++) {
			cells[i].addMouseListener(ml);
		}

		mainPanel.add(cells[0], SolitaireLayout.CELL_ONE);
		mainPanel.add(cells[1], SolitaireLayout.CELL_TWO);
		mainPanel.add(cells[2], SolitaireLayout.CELL_THREE);
		mainPanel.add(cells[3], SolitaireLayout.CELL_FOUR);

		dealDeck.addMouseListener(ml);
		discardPile.addMouseListener(ml);

		mainPanel.add(dealDeck, SolitaireLayout.DECK);
		mainPanel.add(discardPile, SolitaireLayout.DISCARD_PILE);

		for (int i = 0; i < board.acePiles.length; i++) {
			acePiles[i].addMouseListener(ml);
		}

		mainPanel.add(acePiles[0], SolitaireLayout.SPADES_ACE_PILE);
		mainPanel.add(acePiles[1], SolitaireLayout.CLUBS_ACE_PILE);
		mainPanel.add(acePiles[2], SolitaireLayout.DIAMONDS_ACE_PILE);
		mainPanel.add(acePiles[3], SolitaireLayout.HEARTS_ACE_PILE);

		statusBar.add(timerLabel);

		JPanel p1 = new JPanel(new BorderLayout());
		p1.add(mainPanel, BorderLayout.CENTER);
		p1.add(statusBar, BorderLayout.SOUTH);
		add(p1);

		if (cards != null) {
			dealOutCustomBoard(cards, numViewableCards);
		} else {
			dealOutBoard();
		}
	}

	/**
	 * Deals the cards.
	 * 
	 * @author Todor Balabanov
	 */
	private void dealOutBoard() {
		board.dealOutBoard();

		timerCount = 0;

		if (timerToRunNextGame == 1) {
			timer.stop();
			timerLabel.setText("Timer: 0");
			timerToRun = true;
		} else {
			timer.stop();
			timerLabel.setText("Time: OFF");

			timerToRun = false;
		}

		redrawAll();
		mainPanel.revalidate();
	}

	/**
	 * 
	 * @param newDrawCount
	 *            New draw count to be set.
	 * 
	 * @author Todor Balabanov
	 */
	public void setNewDrawCount(int newDrawCount) {
		board.setNewDrawCount(newDrawCount);
	}

	/**
	 * 
	 * @param drawCount
	 *            Draw count to be set.
	 * 
	 * @author Todor Balabanov
	 */
	public void setDrawCount(int drawCount) {
		board.setDrawCount(drawCount);
	}

	/**
	 * 
	 * @param easy
	 *            Game difficulty to be set.
	 * 
	 * @author Todor Balabanov
	 */
	public void setDifficulty(GameDifficulty easy) {
		board.setDifficulty(easy);
	}

	/**
	 * 
	 * @param easy
	 *            New game difficulty to be set.
	 * 
	 * @author Todor Balabanov
	 */
	public void setNewDifficulty(GameDifficulty easy) {
		board.setNewDifficulty(easy);
	}

	/**
	 * 
	 * @param deckThroughs
	 *            Deck throughs to be set.
	 * 
	 * @author Todor Balabanov
	 */
	public void setDeckThroughs(int deckThroughs) {
		board.setDeckThroughs(deckThroughs);
	}

	/**
	 * 
	 * @return board.getNewDrawCount() New draw count.
	 * 
	 * @author Todor Balabanov
	 */
	public int getNewDrawCount() {
		return board.getNewDrawCount();
	}

	/**
	 * 
	 * @return New difficulty value.
	 * 
	 * @author Todor Balabanov
	 */
	public int getNewDifficulty() {
		return board.getNewDifficulty().getValue();
	}
}
