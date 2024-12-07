package org.interview;

import org.interview.data.Order;
import org.interview.data.Side;
import org.interview.data.SuspiciousEvent;
import org.interview.data.Trade;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.interview.data.Side.BUY;
import static org.interview.data.Side.SELL;
import static org.junit.jupiter.api.Assertions.*;

class BasicManipulationDetectorTest {

    BasicManipulationDetector detector = new BasicManipulationDetector();

    @Test
    void noSuspiciousEventFound() {
        LinkedList<Trade> trades = new LinkedList<>();
        trades.add(generateTrade(12, 29, 100, BUY));

        LinkedList<Order> orders = new LinkedList<>();
        orders.add(generateOrder(12, 28, 89, SELL));
        orders.add(generateOrder(12, 27, 111, SELL));
        orders.add(generateOrder(11, 43, 101, SELL));
        orders.add(generateOrder(12, 30, 101, SELL));
        assertEquals(emptyList(), detector.detect(orders, trades), "There should not be any suspicious event caught");
    }

    @Test
    void detectWithEmptyArguments() {
        assertEquals(emptyList(), detector.detect(emptyList(), emptyList()));
    }

    @Test
    void buyTradeSuspiciousEventFound() {
        LinkedList<Trade> trades = new LinkedList<>();
        Trade trade = generateTrade(12, 29, 100, BUY);
        trades.add(trade);


        LinkedList<Order> orders = new LinkedList<>();
        Order order1 = generateOrder(12, 27, 103, SELL);
        Order order2 = generateOrder(12, 28, 106, SELL);
        orders.add(generateOrder(11, 43, 101, SELL));

        orders.add(order1);
        orders.add(order2);


        SuspiciousEvent event = new SuspiciousEvent(List.of(order1,order2), trade);
        assertEquals(1, detector.detect(orders, trades).size(), "There should be 1 suspicious event caught");
        assertEquals(singletonList(event).toString(), detector.detect(orders, trades).toString(), "The suspicious event caught should match the expected event");
    }

    @Test
    void sellTradeSuspiciousEventFound() {
        LinkedList<Trade> trades = new LinkedList<>();
        Trade trade = generateTrade(12, 29, 100, SELL);
        trades.add(trade);


        LinkedList<Order> orders = new LinkedList<>();
        Order order1 = generateOrder(12, 27, 99, BUY);
        Order order2 = generateOrder(12, 28, 91, BUY);
        orders.add(generateOrder(11, 43, 89.99999, BUY));

        orders.add(order1);
        orders.add(order2);


        SuspiciousEvent event = new SuspiciousEvent(List.of(order1,order2), trade);
        assertEquals(1, detector.detect(orders, trades).size(), "There should be 1 suspicious event caught");
        assertEquals(singletonList(event).toString(), detector.detect(orders, trades).toString(), "The suspicious event caught should match the expected event");
    }

    @Test
    void buyTradeWithNegativeNumbers() {
        LinkedList<Trade> trades = new LinkedList<>();
        Trade trade = generateTrade(12, 29, -1, BUY);
        trades.add(trade);


        LinkedList<Order> orders = new LinkedList<>();
        Order order1 = generateOrder(12, 27, -0.90, SELL);
        Order order2 = generateOrder(12, 28, -0.99, SELL);
        orders.add(generateOrder(12, 23, -1.01, SELL));

        orders.add(order1);
        orders.add(order2);


        SuspiciousEvent event = new SuspiciousEvent(List.of(order1,order2), trade);
        assertEquals(1, detector.detect(orders, trades).size(), "There should be 1 suspicious event caught");
        assertEquals(singletonList(event).toString(), detector.detect(orders, trades).toString(), "The suspicious event caught should match the expected event");
    }

    @Test
    void sellTradeWithNegativeNumbers() {
        LinkedList<Trade> trades = new LinkedList<>();
        Trade trade = generateTrade(12, 29, -1, SELL);
        trades.add(trade);


        LinkedList<Order> orders = new LinkedList<>();
        Order order1 = generateOrder(12, 27, -1.01, BUY);
        Order order2 = generateOrder(12, 28, -1.10, BUY);
        orders.add(generateOrder(12, 20, -0.99, BUY));

        orders.add(order1);
        orders.add(order2);


        SuspiciousEvent event = new SuspiciousEvent(List.of(order1,order2), trade);
        assertEquals(1, detector.detect(orders, trades).size(), "There should be 1 suspicious event caught");
        assertEquals(singletonList(event).toString(), detector.detect(orders, trades).toString(), "The suspicious event caught should match the expected event");
    }


    @Test
    void multipleTradesWithAlmostSameWindow() {
        LinkedList<Trade> trades = new LinkedList<>();
        Trade trade = generateTrade(12, 29, 100, SELL);
        Trade trade2 = generateTrade(12, 30, 101, SELL);
        trades.add(trade);
        trades.add(trade2);

        LinkedList<Order> orders = new LinkedList<>();
        Order order1 = generateOrder(12, 27, 99, BUY);
        Order order2 = generateOrder(12, 28, 98, BUY);
        orders.add(generateOrder(13, 20, 97, BUY));

        orders.add(order1);
        orders.add(order2);


        List<SuspiciousEvent> event = List.of(new SuspiciousEvent(List.of(order1,order2), trade), new SuspiciousEvent(List.of(order1, order2), trade2));
        assertEquals(2, detector.detect(orders, trades).size(), "There should be 1 suspicious event caught");
        assertEquals(event.toString(), detector.detect(orders, trades).toString(), "The suspicious event caught should match the expected event");
    }

    private LocalDateTime generateTime(int hour, int minute) {
        LocalDate date = LocalDate.of(2020, 1, 1);
        LocalTime time = LocalTime.of(hour, minute);
        return LocalDateTime.of(date, time);
    }

    private Trade generateTrade(int hour, int minute, double price, Side side) {
        return Trade.builder()
                .id(1)
                .price(price)
                .volume(1)
                .timestamp(generateTime(hour, minute))
                .side(side)
                .build();
    }

    private Order generateOrder(int hour, int minute, double price, Side side) {
        return Order.builder()
                .id(1)
                .price(price)
                .volume(1)
                .timestamp(generateTime(hour, minute))
                .side(side)
                .build();
    }
}