package org.interview;

import org.interview.data.Side;
import org.interview.data.SuspiciousEvent;
import org.interview.data.Order;
import org.interview.data.Trade;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static java.time.Duration.of;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Comparator.comparing;
import static org.interview.data.Side.BUY;
import static org.interview.data.Side.SELL;

public class BasicManipulationDetector implements ManipulationDetector {

    Duration windowTime = of(30, MINUTES);
    double priceRangePercentage = 0.1;

    @Override
    public List<SuspiciousEvent> detect(List<Order> orders, List<Trade> trades) {
        LinkedList<Order> windowOrders = new LinkedList<>();
        List<SuspiciousEvent> suspiciousEvents = new LinkedList<>();

        orders.sort(comparing(Order::getTimestamp));
        trades.sort(comparing(Trade::getTimestamp));
        Iterator<Order> orderIterator = orders.iterator();

        for(Trade trade : trades) {
            List<Order> suspiciousOrders = new LinkedList<>();
            reCalculateOrderWindow(windowOrders, orderIterator, trade.getTimestamp());
            for(Order order : windowOrders) {
                if(areOrderAndTradeSuspicious(order, trade)) {
                    suspiciousOrders.add(order);
                }
            }

            if(!suspiciousOrders.isEmpty()) {
                suspiciousEvents.add(new SuspiciousEvent(suspiciousOrders, trade));
            }
        }

        return suspiciousEvents;
    }

    private void reCalculateOrderWindow(LinkedList<Order> windowOrders, Iterator<Order> currentOrder, LocalDateTime tradeTimestamp) {
        addNewOrdersToTheWindow(windowOrders, currentOrder, tradeTimestamp);
        removeOrdersOutsideOfWindow(windowOrders, tradeTimestamp);
    }

    private void addNewOrdersToTheWindow(List<Order> windowOrders, Iterator<Order> currentOrder, LocalDateTime tradeTimestamp) {
        while (currentOrder.hasNext()) {
            Order order = currentOrder.next();
            if(!isOrderOlderThanTheTrade(order, tradeTimestamp)) {
                if(isOrderInsideOfWindow(order, tradeTimestamp)) {
                    windowOrders.add(order);
                }
            } else {
                return; //The remaining orders are beyond the trade window
            }
        }
    }

    private boolean isOrderOlderThanTheTrade(Order order, LocalDateTime tradeTimestamp) {
        LocalDateTime timestamp = order.getTimestamp();
        return timestamp.isAfter(tradeTimestamp);
    }

    private boolean isOrderInsideOfWindow(Order order, LocalDateTime tradeTimestamp) {
        LocalDateTime timestamp = order.getTimestamp();
        return timestamp.isAfter(tradeTimestamp.minus(windowTime)) && timestamp.isBefore(tradeTimestamp);
    }

    private void removeOrdersOutsideOfWindow(LinkedList<Order> windowOrders, LocalDateTime tradeTimestamp) {
        while(windowOrders.peekFirst() != null && !isOrderInsideOfWindow(windowOrders.peekFirst(), tradeTimestamp)) {
            windowOrders.removeFirst();
        }
    }

    private boolean areOrderAndTradeSuspicious(Order order, Trade trade) {
        return (areOrderAndTradeOpposites(order, trade) && isOrderPriceInRangeOfTradePrice(order, trade));
    }

    private boolean isOrderPriceInRangeOfTradePrice(Order order, Trade trade) {
        Side tradeSide = trade.getSide();
        double orderPrice = order.getPrice();
        double tradePrice = trade.getPrice();

        if((tradeSide == BUY && tradePrice >= 0 ) || (tradeSide == SELL && tradePrice < 0)) {
            return isBetween(orderPrice, tradePrice, tradePrice * (1 + priceRangePercentage));
        } else {
            return isBetween(orderPrice, tradePrice * (1 - priceRangePercentage), tradePrice);
        }
    }

    private boolean isBetween(double value, double min, double max) {
        if(max<min) {
            double k = min;
            min = max;
            max = k;
        }
        return value >= min && value <= max;
    }

    private boolean areOrderAndTradeOpposites(Order order, Trade trade) {
        return order.getSide() != trade.getSide();
    }

}
