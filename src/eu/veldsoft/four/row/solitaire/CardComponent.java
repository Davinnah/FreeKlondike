/*
 This file is a part of Four Row Solitaire

 Copyright (C) 2010-2014 by Matt Stephen, Todor Balabanov, Konstantin Tsanov, Ventsislav Medarov, Vanya Gyaurova, Plamena Popova, Hristiana Kalcheva

 Four Row Solitaire is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Four Row Solitaire is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with FourRowSolitaire.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.veldsoft.four.row.solitaire;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

/**
 * 
 * @author Todor Balabanov
 */
class CardComponent extends JComponent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Card instances.
	 */
	private static CardComponent cards[] = { null, null, null, null, null,
			null, null, null, null, null, null, null, null, null, null, null,
			null, null, null, null, null, null, null, null, null, null, null,
			null, null, null, null, null, null, null, null, null, null, null,
			null, null, null, null, null, null, null, null, null, null, null,
			null, null, null, };

	/**
	 * Maps non GUI card objects with GUI card objects.
	 */
	static final Map<Card, CardComponent> cardsMapping = new HashMap<Card, CardComponent>();

	/**
	 * Initialize static data.
	 */
	static {
		for (int i = 0; i < cards.length; i++) {
			cards[i] = new CardComponent(Card.valueBy(i + 1));
		}
	}

	/**
	 * The back design.
	 */
	private String cardBack;

	/**
	 * The card front.
	 */
	private String cardImageString;

	/**
	 * The highlighted card front.
	 */
	private String cardHighlighted;

	/**
	 * Takes either card back or front.
	 */
	private BufferedImage image = null;

	/**
	 * Buffer.
	 */
	Card card = null;

	/**
	 * It is used instead of constructor. Implement lazy initialization.
	 * 
	 * @param number
	 *            Will be used to set the card's number.
	 * 
	 * @return Card with updated card number.
	 * 
	 * @author Todor Balabanov
	 */
	public static CardComponent valueBy(int number) {
		int index = number - 1;

		if (cards[index] == null) {
			cards[index] = CardComponent.cardsMapping.get(Card.valueBy(number));
		}

		return (cards[index]);
	}

	/**
	 * Paint procedure.
	 * 
	 * @param g
	 *            Graphic context.
	 * @author Todor Balabanov
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		updateImage();
		
		g.drawImage(image, 0, 0, null);
	}

	/**
	 * Constructor. Sets the card's parameters, sets it face up and gets its
	 * image.
	 * 
	 * @param suit
	 *            Card's suit to be set.
	 * 
	 * @param number
	 *            Card's number to be set.
	 * 
	 * @param fullNumber
	 *            Card's full number to be set.
	 * 
	 * @author Todor Balabanov
	 */
	private CardComponent(Card card) {
		if (SolitaireFrame.deckNumber >= 1
				&& SolitaireFrame.deckNumber <= ChangeAppearance.NUM_DECKS) {
			cardBack = SolitaireFrame.IMAGES_PATH + "/cardbacks/cardback"
					+ SolitaireFrame.deckNumber + ".png";
		} else {
			cardBack = SolitaireFrame.IMAGES_PATH + "/cardbacks/cardback3.png";
		}

		this.card = card;

		initializeCardImageString();

		setFaceUp();

		cardsMapping.put(card, this);
	}

	/**
	 * Update image pointer according internal card state.
	 * 
	 * @author Todor Balabanov
	 */
	public void updateImage() {
		if (card.isFaceUp()) {
			if (card.isHighlighted()) {
				try {
					// TODO Load images only once.
					image = ImageIO.read(this.getClass().getResource(
							cardHighlighted));
				} catch (NullPointerException e) {
				} catch (IOException e) {
				}
			} else if (card.isUnhighlighted()) {
				try {
					// TODO Load images only once.
					image = ImageIO.read(this.getClass().getResource(
							cardImageString));
				} catch (NullPointerException e) {
				} catch (IOException e) {
				}
			}
		} else if (card.isFaceDown()) {
			try {
				// TODO Load images only once.
				image = ImageIO.read(this.getClass().getResource(cardBack));
			} catch (NullPointerException e) {
			} catch (IOException e) {
			}
		}
	} 
	
	/**
	 * Returns the card's buffered image (either back or front).
	 * 
	 * @return image Buffered image..
	 * 
	 * @author Todor Balabanov
	 */
	public BufferedImage getImage() {
		return image;
	}

	/**
	 * Sets the card's highlighted front image.
	 * 
	 * @author Todor Balabanov
	 */
	public void highlight() {
		card.highlight();
	}

	/**
	 * Unhighlights a highlighted card. Sets back its unhighlighted face image.
	 * 
	 * @author Todor Balabanov
	 */
	public void unhighlight() {
		card.unhighlight();
		setFaceUp();
	}

	/**
	 * Sets the card face-up and sets its face image.
	 * 
	 * @author Todor Balabanov
	 */
	public void setFaceUp() {
		card.setFaceUp();
	}

	/**
	 * Sets the card face-down and sets its back image.
	 * 
	 * @author Todor Balabanov
	 */
	public void setFaceDown() {
		card.setFaceDown();
	}

	/**
	 * Returns a card.
	 * 
	 * @return card Card from a stack.
	 * 
	 * @author Todor Balabanov
	 */
	public Card getCard() {
		return (card);
	}

	/**
	 * Sets the card's face image and highlighted face image based on its suit
	 * and rank.
	 * 
	 * @author Todor Balabanov
	 */
	private void initializeCardImageString() {
		cardImageString = SolitaireFrame.IMAGES_PATH + "/cardfaces/";
		cardHighlighted = SolitaireFrame.IMAGES_PATH + "/highlightedfaces/";

		if (card.getSuit().equals(CardSuit.SPADES)) {
			cardImageString += "s";
			cardHighlighted += "s";
			card.setColor(CardColor.BLACK);
		} else if (card.getSuit().equals(CardSuit.CLUBS)) {
			cardImageString += "c";
			cardHighlighted += "c";
			card.setColor(CardColor.BLACK);
		} else if (card.getSuit().equals(CardSuit.DIAMONDS)) {
			cardImageString += "d";
			cardHighlighted += "d";
			card.setColor(CardColor.RED);
		} else if (card.getSuit().equals(CardSuit.HEARTS)) {
			cardImageString += "h";
			cardHighlighted += "h";
			card.setColor(CardColor.RED);
		}

		if (card.getRank().equals(CardRank.ACE)) {
			cardImageString += "Ace";
			cardHighlighted += "Ace";
		} else if (card.getRank().equals(CardRank.TWO)) {
			cardImageString += "Two";
			cardHighlighted += "Two";
		} else if (card.getRank().equals(CardRank.THREE)) {
			cardImageString += "Three";
			cardHighlighted += "Three";
		} else if (card.getRank().equals(CardRank.FOUR)) {
			cardImageString += "Four";
			cardHighlighted += "Four";
		} else if (card.getRank().equals(CardRank.FIVE)) {
			cardImageString += "Five";
			cardHighlighted += "Five";
		} else if (card.getRank().equals(CardRank.SIX)) {
			cardImageString += "Six";
			cardHighlighted += "Six";
		} else if (card.getRank().equals(CardRank.SEVEN)) {
			cardImageString += "Seven";
			cardHighlighted += "Seven";
		} else if (card.getRank().equals(CardRank.EIGHT)) {
			cardImageString += "Eight";
			cardHighlighted += "Eight";
		} else if (card.getRank().equals(CardRank.NINE)) {
			cardImageString += "Nine";
			cardHighlighted += "Nine";
		} else if (card.getRank().equals(CardRank.TEN)) {
			cardImageString += "Ten";
			cardHighlighted += "Ten";
		} else if (card.getRank().equals(CardRank.JACK)) {
			cardImageString += "Jack";
			cardHighlighted += "Jack";
		} else if (card.getRank().equals(CardRank.QUEEN)) {
			cardImageString += "Queen";
			cardHighlighted += "Queen";
		} else if (card.getRank().equals(CardRank.KING)) {
			cardImageString += "King";
			cardHighlighted += "King";
		}

		cardImageString += ".png";
		cardHighlighted += "H.png";
	}

	/**
	 * Clone a card, that includes the card's suit, number and full number.
	 * 
	 * @return this Copy of a card.
	 * 
	 * @author Todor Balabanov
	 */
	public CardComponent clone() {
		return this;
	}
}
