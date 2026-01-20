import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.http.config.ClientConfig;
import org.example.http.mapper.JsonMapper;
import org.example.http.model.BrandModel;
import org.example.http.model.CategoryModel;
import org.example.http.model.ItemModel;
import org.example.http.service.ClientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ClientConfig.class)
public class ClientServiceTest {

    @Autowired
    private ClientService clientService;

    @Autowired
    private JsonMapper jsonMapper;

}
