package nl.tudelft.sem.template.order.domain.order;

import nl.tudelft.sem.template.order.domain.product.Product;

import nl.tudelft.sem.template.order.domain.providers.TimeProvider;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final transient OrderRepository orderRepository;
    private final transient TimeProvider timeProvider;

    public OrderService(OrderRepository orderRepository, TimeProvider timeProvider) {
        this.orderRepository = orderRepository;
        this.timeProvider = timeProvider;
    }

    public Order sendOrder(String customerId, int storeId){
        Order order = new Order(customerId, storeId);
        orderRepository.save(order);
        return order;
    }

    public void saveOrder(Order order) {
        orderRepository.save(order);
    }

    public void cancelOrder(int orderId) throws OrderDoesNotExistException, OrderCancelledException, TooLateToCancelOrderException {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderDoesNotExistException(orderId));

        if (order.getData().getStatus() == Status.CANCELLED){
            throw new OrderCancelledException(orderId);
        }

        if(order.getData().getStatus() == Status.PAID){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss")
                    .withChronology(IsoChronology.INSTANCE);
            LocalDateTime deliveryTime = LocalDateTime.parse(order.getData().getDeliveryTime(), formatter);
            if (deliveryTime.minusMinutes(30L).isBefore(timeProvider.getTime())){
                throw new TooLateToCancelOrderException(orderId);
            }
        }

        order.getData().setStatus(Status.CANCELLED);
        orderRepository.save(order);
    }

    public void payOrder(int orderId, long deliveryTime) throws OrderDoesNotExistException {
        Order order = this.getOrder(orderId);

        // Get the delivery time for the new order
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss")
                .withChronology(IsoChronology.INSTANCE);
        LocalDateTime now = timeProvider.getTime();
        String deliveryDate = dtf.format(now.plusMinutes(deliveryTime));

        OrderUtils.pay(deliveryDate,  order);

        // save the changes in the repository
        this.saveOrder(order);
    }

    public Order sendOrder(String customerId, int storedId, List<Product> products){
        Order order = new Order(customerId, storedId, products);
        orderRepository.save(order);
        return order;
    }

    public Order getOrder(int orderId) throws OrderDoesNotExistException {
        Optional<Order> order = orderRepository.findByOrderId(orderId);
        if(order.isEmpty()) throw new OrderDoesNotExistException(orderId);
        else return order.get();
    }


    public List<Order> listAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> findAllByCustomerId(String email) {
        return orderRepository.findAllByCustomerId(email);
    }

    public List<Order> findAllByStoreId(int storeId) {
        return orderRepository.findAllByStoreId(storeId);
    }

    public boolean orderBelongsToUser(int orderId, String customerId) throws OrderDoesNotExistException {
        return getOrder(orderId).getData().getCustomerId().equals(customerId);
    }
}
