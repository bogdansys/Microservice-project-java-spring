package nl.tudelft.sem.template.order.domain;

import nl.tudelft.sem.template.order.domain.order.*;
import nl.tudelft.sem.template.order.domain.providers.TimeProvider;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CancelOrderTests {
    @Test
    public void testCancelOrderWithValidDeliveryTime() throws Exception,
            OrderCancelledException{
        //The delivery time of the order is at least 30 minutes from now
        //Expect: the order can be cancelled
        OrderRepository mockOrderRepository = Mockito.mock(OrderRepository.class);
        TimeProvider mockTimeProvider = mock(TimeProvider.class);

        Order order = new Order("Franklin", 1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss")
                .withChronology(IsoChronology.INSTANCE);
        OrderUtils.pay(formatter.format(LocalDateTime.of(2002, 1, 1, 10, 0)), order);
        OrderService orderService = new OrderService(mockOrderRepository, mockTimeProvider);
        when(mockOrderRepository.findById(1)).thenReturn(Optional.of(order));
        when(mockTimeProvider.getTime()).thenReturn(LocalDateTime.of(2002, 1, 1, 9, 30));

        orderService.cancelOrder(1);
        assertSame(Status.CANCELLED, order.getData().getStatus());
    }

    @Test
    public void testCancelOrderWithInvalidDeliveryTime(){
        //The delivery time of the order is less than 30 minutes from now
        //Expect: the order cannot be cancelled
        OrderRepository mockOrderRepository = Mockito.mock(OrderRepository.class);
        TimeProvider mockTimeProvider = mock(TimeProvider.class);

        Order order = new Order("Franklin", 1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss")
                .withChronology(IsoChronology.INSTANCE);
        OrderUtils.pay(formatter.format(LocalDateTime.of(2002, 1, 1, 10, 0)), order);
        OrderService orderService = new OrderService(mockOrderRepository, mockTimeProvider);
        when(mockOrderRepository.findById(1)).thenReturn(Optional.of(order));
        when(mockTimeProvider.getTime()).thenReturn(LocalDateTime.of(2002, 1, 1, 9, 31));

        assertThrows(TooLateToCancelOrderException.class, () ->  orderService.cancelOrder(1));
    }

    @Test
    public void testCancelCancelledOrder(){
        OrderRepository mockOrderRepository = Mockito.mock(OrderRepository.class);
        TimeProvider mockTimeProvider = mock(TimeProvider.class);
        Order order = new Order("Franklin", 1);
        order.getData().setStatus(Status.CANCELLED);

        when(mockOrderRepository.findById(1)).thenReturn(Optional.of(order));

        OrderService orderService = new OrderService(mockOrderRepository, mockTimeProvider);
        assertThrows(OrderCancelledException.class, () ->  orderService.cancelOrder(1));

    }

    @Test
    public void testCancelNonexistentOrder(){
        OrderRepository mockOrderRepository = Mockito.mock(OrderRepository.class);
        TimeProvider mockTimeProvider = mock(TimeProvider.class);
        when(mockOrderRepository.findById(1)).thenReturn(Optional.empty());

        OrderService orderService = new OrderService(mockOrderRepository, mockTimeProvider);
        assertThrows(OrderDoesNotExistException.class, () ->  orderService.cancelOrder(1));

    }
}
