package nl.tudelft.sem.template.order.services;

import nl.tudelft.sem.template.order.domain.store.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockAuthenticationManager"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class StoreServiceTest {

    @Mock
    private transient StoreRepository storeRepository;

    private transient StoreService storeService;

    @BeforeEach
    void setup() {
        storeService = new StoreService(storeRepository);
    }

    @Test
    void testAddStore() throws StoreNameAlreadyInUseException {
        StoreName name = new StoreName("store name");

        when(storeRepository.existsByName(name)).thenReturn(false);

        Store result = storeService.addStore(name);

        Store expected = new Store(name);

        assertThat(result).isEqualTo(expected);
        verify(storeRepository, times(1)).save(expected);
    }

    @Test
    void testAddStoreThrowsException() {
        StoreName name = new StoreName("store name");

        when(storeRepository.existsByName(name)).thenReturn(true);

        assertThatThrownBy(() -> storeService.addStore(name))
                .isInstanceOf(StoreNameAlreadyInUseException.class);
        verify(storeRepository, never()).save(any());
    }

    @Test
    void testListAll() {
        List<Store> result = new ArrayList<>();
        result.add(new Store(new StoreName("test")));

        when(storeRepository.findAll()).thenReturn(result);
        assertThat(storeService.listAllStores()).isEqualTo(result);
    }

    @Test
    void checkId() {
        when(storeRepository.existsById(3)).thenReturn(true);
        assertThat(storeService.checkId(3)).isTrue();

        when(storeRepository.existsById(3)).thenReturn(false);
        assertThat(storeService.checkId(3)).isFalse();
    }
}
