package nl.tudelft.sem.template.order.domain.product;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final transient ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Saves the given product to the database
     * @param product the product
     * @return the saved product
     */
    public Product saveProduct(Product product){
        return productRepository.save(product);
    }

    public List<Product> saveProducts(List<Product> products){
        return productRepository.saveAll(products);
    }

    public List<Product> getProductsByOrderId(int orderId) {
        return productRepository.getProductsByOrderId(orderId);
    }

    public List<Product> listAllOrders() {
        return productRepository.findAll();
    }

    /**
     * Gets the given product from the database
     * @param productId the id of the product
     * @return the product
     * @throws ProductDoesNotExistException when there is no product with the given id
     */
    public Product getProduct(Long productId) throws ProductDoesNotExistException {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductDoesNotExistException(productId));
    }

    public void removeProducts(List<Product> products) {
        productRepository.deleteAll(products);
    }

    public void removeProduct(Product product) {
        productRepository.delete(product);
    }
}
