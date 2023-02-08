package nl.tudelft.sem.template.order.domain;

import nl.tudelft.sem.template.order.authentication.AuthManager;
import nl.tudelft.sem.template.order.controllers.StoreController;
import nl.tudelft.sem.template.order.domain.store.Store;
import nl.tudelft.sem.template.order.domain.store.StoreName;
import nl.tudelft.sem.template.order.domain.store.StoreNameAlreadyInUseException;
import nl.tudelft.sem.template.order.domain.store.StoreService;
import nl.tudelft.sem.template.order.model.AddStoreModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * The type Order controller test.
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockTokenVerifier", "mockAuthenticationManager"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class StoreTests {
    @Autowired
    private transient AuthManager mockAuthenticationManager;

    @Mock
    private transient StoreService storeService;

    private transient StoreController storeController;

    /**
     * Setup.
     */
    @BeforeEach
    void setup() {
        storeController = new StoreController(mockAuthenticationManager, storeService);
    }

    /**
     * Test adding a new store.
     */
    @Test
    public void testAddStore() throws Exception {
        String name = "name";
        StoreName storeName = new StoreName(name);
        Store store = new Store(storeName);

        String name2 = "name2";
        StoreName storeName2 = new StoreName(name2);

        when(storeService.addStore(storeName)).thenReturn(store);

        when(mockAuthenticationManager.getRole()).thenReturn("ADMIN");

        storeController.addStore(new AddStoreModel(name));

        when(storeService.addStore(storeName2)).thenThrow(StoreNameAlreadyInUseException.class);

        verify(storeService, times(1)).addStore(storeName);
        assertThatThrownBy(() -> storeController.addStore(new AddStoreModel(name2))).isInstanceOf(ResponseStatusException.class);
    }
}
