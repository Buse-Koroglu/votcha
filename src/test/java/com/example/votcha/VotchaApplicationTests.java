package com.example.votcha;

import com.example.votcha.votcha_search.domain.repository.UserElasticRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class VotchaApplicationTests {

    @MockitoBean
    private UserElasticRepository userElasticRepository;

	@Test
	void contextLoads() {
	}

}
