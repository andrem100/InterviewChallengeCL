In terms of assumptions:

I assumed all the values provided in a trade and order were not trying to go over the double data type limit, even when applying 10% to the value.

Regarding one of the requirements "The orders you are checking for the trade have a price not more than 10% lower or higher than the trade price (depending on the side: if it's a buy trade, then sell orders should be not more than 10% more expensive, and vice versa)"

I interpreted as when it's a buy trade, I should catch the sell orders that will be between the trade price and 10% above. And when is a sell trade, I should catch all the buy orders that are between the trade price and less than 10%. 

I assumed the lists might not be ordered.

I assumed some trades can have the same orders if they enter in the same window range, hence having them attached to each trade in case they are found suspicious.