package org.interview.data;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;

@Data
@Builder
public class Order {
    long id;
    double price;
    double volume;

    @NonNull
    Side side;

    @NonNull
    LocalDateTime timestamp;
}
