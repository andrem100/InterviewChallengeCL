package org.interview;

import org.interview.data.SuspiciousEvent;
import org.interview.data.Order;
import org.interview.data.Trade;

import java.util.List;

public interface ManipulationDetector {
    public List<SuspiciousEvent> detect(List<Order> orders, List<Trade> trades);
}
