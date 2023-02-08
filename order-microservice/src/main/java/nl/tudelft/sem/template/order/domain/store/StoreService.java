package nl.tudelft.sem.template.order.domain.store;

import org.springframework.stereotype.Service;

import java.util.*;

/**
 * A DDD service for adding a new store.
 */
@Service
public class StoreService {
    private final transient StoreRepository storeRepository;

    /**
     * Instantiates a new StoreService.
     *
     * @param storeRepository  the store repository
     */
    public StoreService(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    /**
     * Add a new store.
     *
     * @param name    The Name of the store
     * @throws StoreNameAlreadyInUseException if the store already exists
     */
    public Store addStore(StoreName name) throws StoreNameAlreadyInUseException {

        if (isNameUnique(name)) {

            // Create new account
            Store newStore = new Store(name);
            storeRepository.save(newStore);

            return newStore;
        }

        throw new StoreNameAlreadyInUseException(name);
    }

    /**
     * Lists all stores in the database
     * @return list of stores
     */
    public List<Store> listAllStores() {
        return storeRepository.findAll();
    }

    public boolean isNameUnique(StoreName name) {
        return !storeRepository.existsByName(name);
    }

    public boolean checkId(int storeId) {
        return storeRepository.existsById(storeId);
    }
}
