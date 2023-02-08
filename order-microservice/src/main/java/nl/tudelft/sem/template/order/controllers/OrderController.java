package nl.tudelft.sem.template.order.controllers;

import nl.tudelft.sem.template.order.authentication.AuthManager;
import nl.tudelft.sem.template.order.domain.order.*;
import nl.tudelft.sem.template.order.domain.product.Product;
import nl.tudelft.sem.template.order.domain.product.ProductService;
import nl.tudelft.sem.template.order.domain.store.StoreService;
import nl.tudelft.sem.template.order.framework.StoreIdAdapter;
import nl.tudelft.sem.template.order.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * The Main Order Controller used for interacting with Orders and Stores
 */
@RestController
public class OrderController {

    private final transient AuthManager authManager;

    private final transient StoreService storeService;

    private final transient OrderService orderService;

    private final transient ProductService productService;

    private final transient ModifyOrderService modifyOrderService;

    private final transient AddToOrderService addToOrderService;

    private final transient StoreIdAdapter storeIdAdapter;

    /**
     * Instantiates a new controller.
     *
     * @param authManager  Spring Security component used to authenticate and authorize the user
     * @param storeService ...
     */
    @Autowired
    public OrderController(AuthManager authManager, StoreService storeService, OrderService orderService,
                           ProductService productService, ModifyOrderService modifyOrderService,
                           StoreIdAdapter storeIdAdapter,
                           AddToOrderService addToOrderService) {
        this.authManager = authManager;
        this.storeService = storeService;
        this.orderService = orderService;
        this.productService = productService;
        this.modifyOrderService = modifyOrderService;
        this.addToOrderService = addToOrderService;
        this.storeIdAdapter = storeIdAdapter;
    }

    /**
     * Creates a new order and sends it to the corresponding store
     *
     * @param request The customerId and storeId
     * @return 200 OK if the creation is successful
     */
    @SuppressWarnings("PMD")
    @PostMapping("/sendOrder")
    public ResponseEntity<Order> sendOrder(@RequestBody SendOrderModel request,
                                           @RequestHeader("Authorization") String authorizationHeader) {

        if (!authManager.getRole().equals("USER")) { // check user is a customer
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only Users can make orders");
        }
        if (!storeService.checkId(request.getStoreId())) { // Check store id belongs to a store in the database
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Store with id %d does no exist", request.getStoreId()));
        }

        Order order = orderService.sendOrder(authManager.getEmail(), request.getStoreId());

        try {
            if (request.getProducts() != null && !request.getProducts().isEmpty()) {
                addToOrderService.addProductsToOrder(order.getOrderId(), request.getProducts(), authorizationHeader);
            }

            return ResponseEntity.ok().body(order);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @SuppressWarnings("PMD")
    @PostMapping("/cancelOrder")
    public void cancelOrder(@RequestBody CancelOrderRequestModel request) {

        //Check if the account has the correct role
        if (authManager.getRole().equals("USER")) {
            //Check if the user is the creator of the order
            boolean orderBelongsToUser;
            try {
                orderBelongsToUser = orderService.orderBelongsToUser(request.getOrderId(), authManager.getEmail());
            } catch (OrderDoesNotExistException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
            }

            if (!orderBelongsToUser) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "This order was created by another account");
            }
        } else if (!authManager.getRole().equals("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Store owners cannot cancel orders");
        }
        //Cancel the order
        try {
            orderService.cancelOrder(request.getOrderId());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Assigns the given products to the given order
     *
     * @param request             contains the products and the order
     * @param authorizationHeader header for making a request to the Product microservice
     * @return the order
     */
    @PostMapping("/addProductsToOrder")
    public ResponseEntity<OrderResponseModel> addProductsToOrder(@RequestBody AddProductsToOrderRequestModel request,
                                                                 @RequestHeader("Authorization") String authorizationHeader) {
        if (!authManager.getRole().equals("USER")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only Users can modify orders");
        }

        try {
            if (!orderService.orderBelongsToUser(request.getOrderId(), authManager.getEmail())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "This order was created by another account");
            }

            return ResponseEntity.ok().body(
                    addToOrderService.addProductsToOrder
                            (request.getOrderId(), request.getProducts(), authorizationHeader)
            );
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Assigns the given toppings to the given product
     *
     * @param request             contains the toppings and the product
     * @param authorizationHeader header for making a request to the Product microservice
     * @return the order
     */
    @PostMapping("/addToppingsToProduct")
    public ResponseEntity<OrderResponseModel> addToppingsToProduct(@RequestBody AddToppingsToProductRequestModel request,
                                                                   @RequestHeader("Authorization") String authorizationHeader) {
        if (!authManager.getRole().equals("USER")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only Users can modify orders");
        }

        try {
            Product product = productService.getProduct(request.getProductId());
            if (!orderService.orderBelongsToUser(product.getOrder().getOrderId(), authManager.getEmail())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "This order was created by another account");
            }

            return ResponseEntity.ok().body(
                    addToOrderService.addToppingsToProduct
                            (request.getProductId(), request.getToppings(), authorizationHeader)
            );
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Removes the given product from its order
     *
     * @param request contains the product
     * @return the order
     */
    @PostMapping("/removeProductsFromOrder")
    public ResponseEntity<List<OrderResponseModel>> removeProductsFromOrder(
            @RequestBody RemoveProductsFromOrderRequestModel request) {
        if (!authManager.getRole().equals("USER")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only Users can modify orders");
        }

        try {
            if (request.getProducts() != null) {
                for (Long productId : request.getProducts()) {
                    Product product = productService.getProduct(productId);
                    if (!orderService.orderBelongsToUser(product.getOrder().getOrderId(), authManager.getEmail())) {
                        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                                "This order was created by another account");
                    }
                }
            }

            return ResponseEntity.ok().body(modifyOrderService.removeProductsFromOrder(request.getProducts()));
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Removes the given topping from the given product
     *
     * @param request             contains the topping and the product
     * @param authorizationHeader header for making a request to the Product microservice
     * @return the order
     */
    @PostMapping("/removeToppingsFromProduct")
    public ResponseEntity<OrderResponseModel> removeToppingsFromProduct(@RequestBody RemoveToppingsFromProductRequestModel request,
                                                                        @RequestHeader("Authorization") String authorizationHeader) {
        if (!authManager.getRole().equals("USER")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User role is required to modify an order");
        }

        try {
            Product product = productService.getProduct(request.getProductId());

            if (!orderService.orderBelongsToUser(product.getOrder().getOrderId(), authManager.getEmail())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "This order was created by another account");
            }

            return ResponseEntity.ok().body(
                    modifyOrderService.removeToppingsFromProduct
                            (request.getProductId(), request.getToppings(), authorizationHeader)
            );
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Endpoint to pay for an order
     *
     * @param request The orderId and the date (in format "04/02/2011 20:27:05")
     * @return whether the payment was successful
     * @throws ResponseStatusException if the order doesn't exist or was already cancelled or paid
     */
    @PostMapping("/payOrder")
    public ResponseEntity payOrder(@RequestBody PayOrderModel request) {
        // check user is a customer
        if (!authManager.getRole().equals("USER")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only Users can make orders");
        }

        try {
            // Get the order
            Order order = orderService.getOrder(request.getOrderId());

            if (!order.getData().getCustomerId().equals(authManager.getEmail())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You can only pay your orders");
            }
            if (!order.getData().getStatus().equals(Status.UNPAID)) {
                throw new OrderCancelledOrPaidException(order);
            }

            // Get delivery time. Minimum time is 30 minutes, max is 12 hours
            long deliveryTime = Math.min(Math.max(30, request.getDeliveryTime()), 12 * 60L);

            orderService.payOrder(request.getOrderId(), deliveryTime);

            // Once paid, remove products stored in the product repository
            productService.removeProducts(order.getProducts());

            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .build();
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Gets all possible orders. A customer gets all their orders, a store owner the orders of their store and a
     * manager all the orders in the database.
     *
     * @param token the authentication token
     * @return the orders
     */
    @GetMapping("/getOrders")
    public ResponseEntity<List<Order>> getOrders(@RequestHeader("Authorization") String token) {
        if (authManager.getRole().equals("USER")) {
            return ResponseEntity.ok(orderService.findAllByCustomerId(authManager.getEmail()));
        } else if (authManager.getRole().equals("STORE_OWNER")) {
            try {
                int storeId = storeIdAdapter.getUsersStoreId(token);

                return ResponseEntity.ok(orderService.findAllByStoreId(storeId));
            } catch (Exception ex) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
            }
        } else { // manager role
            return ResponseEntity.ok(orderService.listAllOrders());
        }
    }

    /**
     * Gets a specific order, if the user can get access to it (e.g., a customer cannot get info from an order of another customer).
     *
     * @param request the id of the order
     * @param token   the authentication token
     * @return the order
     */
    @GetMapping("/getOrder")
    public ResponseEntity<Order> getOrder(@RequestBody GetOrderModel request,
                                          @RequestHeader("Authorization") String token) {
        try {
            Order order = orderService.getOrder(request.getOrderId());

            switch (authManager.getRole()) {
                case "USER":
                    if (order.getData().getCustomerId().equals(authManager.getEmail())) return ResponseEntity.ok(order);
                    break;
                case "STORE_OWNER":
                    int storeId = storeIdAdapter.getUsersStoreId(token);

                    if (order.getData().getStoreId() == storeId) return ResponseEntity.ok(order);
                    break;
                default: // manager role
                    return ResponseEntity.ok(order);
            }
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized to access this order");
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
