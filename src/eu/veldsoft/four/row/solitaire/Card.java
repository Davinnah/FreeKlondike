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

/**
 * Class: Card
 * 
 * Description: The Card class holds information pertaining to 1 out of the 52
 * cards per deck.
 * 
 * @author Matt Stephen
 */
class Card {
	/**
	 * Card suit.
	 */
	private CardSuit suit;

	/**
	 * Card number.
	 */
	private CardRank rank;

	/**
	 * Card color.
	 */
	private CardColor color;

	/**
	 * 1 - 52
	 */
	private int fullCardNumber;

	/**
	 * Is the card currently set face-up.
	 */
	private boolean faceUp = false;

	/**
	 * Is the card currently highlighted.
	 */
	private boolean highlighted = false;

	/**
	 * To notify the discard pile of moves from the deck.
	 */
	private String location = "";

	/**
	 * Card constructor Sets the card's suit, number, full number and
	 * back image. Also sets it face-up.
	 * 
	 * @param suit
	 * 		Card suit to be set.
	 * 
	 * @param number
	 * 		Card number to be set.
	 * 
	 * @param fullNumber
	 * 		Full card number to be set.
	 * 
	 * @author Todor Balabanov
	 */
	public Card(CardSuit suit, CardRank number, int fullNumber) {
		this.suit = suit;
		this.rank = number;
		this.fullCardNumber = fullNumber;
	}

	/**
	 * Sets the card's highlighted front image.
	 * 
	 * @author Todor Balabanov
	 */
	public void highlight() {
		highlighted = true;
	}

	/**
	 * Unhighlights a highlighted card. Sets back its unhighlighted face image.
	 * 
	 * @author Todor Balabanov
	 */
	public void unhighlight() {
		highlighted = false;

		setFaceUp();
	}

	/**
	 * Checks if the card is highlighted and returns the result(true/false).
	 * 
	 * @return highlighted
	 * 		Whether the card is highlighted or not (true or false).
	 * 
	 * @author Todor Balabanov
	 */
	public boolean isHighlighted() {
		return highlighted;
	}

	/**
	 * Sets the card face-up.
	 * 
	 * @author Todor Balabanov
	 */
	public void setFaceUp() {
		faceUp = true;
	}

	/**
	 * Sets the card face-down.
	 * 
	 * @author Todor Balabanov
	 */
	public void setFaceDown() {
		faceUp = false;
	}

	/**
	 * Checks if the card is facing up and returns the result(true/false).
	 * 
	 * @return faceUp
	 * 		Whether the card is set face-up or not (true or false).
	 * 
	 * @author Todor Balabanov
	 */
	public boolean isFaceUp() {
		return faceUp;
	}

	/**
	 * Returns the card's number.
	 * 
	 * @return rank
	 * 		Card's rank.
	 * 
	 * @author Todor Balabanov
	 */
	public CardRank getNumber() {
		return rank;
	}

	/**
	 * Returns the card's suit.
	 * 
	 * @return suit
	 * 		Card's suit.
	 * 
	 * @author Todor Balabanov
	 */
	public CardSuit getSuit() {
		return suit;
	}

	/**
	 * Returns the card's color.
	 * 
	 * @return color
	 * 		Card's color.
	 * 
	 * @author Todor Balabanov
	 */
	public CardColor getColor() {
		return color;
	}

	/**
	 * Returns the card's rank.
	 * 
	 * @return rank
	 * 		Card's rank.
	 * 
	 * @author Todor Balabanov
	 */
	public CardRank getRank() {
		return rank;
	}

	/**
	 * Sets the card's rank.
	 * 
	 * @param rank
	 * 		Used to set the card's rank.
	 * 
	 * @author Todor Balabanov
	 */
	public void setRank(CardRank rank) {
		this.rank = rank;
	}

	/**
	 * Sets the card's suit.
	 * 
	 * @param suit
	 * 		Used to set the card's suit.
	 * 
	 * @author Todor Balabanov
	 */
	public void setSuit(CardSuit suit) {
		this.suit = suit;
	}

	/**
	 * Sets the card's color.
	 * 
	 * @param color
	 * 		Used to set the card's color.
	 * 
	 * @author Todor Balabanov
	 */
	public void setColor(CardColor color) {
		this.color = color;
	}

	/**
	 * Returns the card's full number.
	 * 
	 * @return fullCardNumber
	 * 	 	Card's full number (in range 1-52)
	 * 
	 * @author Todor Balabanov
	 */
	public int getFullNumber() {
		return fullCardNumber;
	}

	/**
	 * Notifies the discard pile of moves from the deck.
	 * 
	 * @return location
	 * 		Current card location.
	 * 
	 * @author Todor Balabanov
	 */
	public String getSource() {
		return location;
	}

	/**
	 * Used to set the location of a moved card.
	 * 
	 * @param source
	 * 		Source to which the card has been moved.
	 * 
	 * @author Todor Balabanov
	 */
	public void setSource(String source) {
		location = source;
	}

	/**
	 * Clone a card, that includes the card's suit, number and full number.
	 * 
	 * @return this
	 * 		Copy of the card.
	 * 
	 * @author Todor Balabanov
	 */
	public Card clone() {
		return this;
	}
}
