package org.interview.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@Getter
@ToString
public class SuspiciousEvent {
    List<Order> orders;
    Trade trade;
}
