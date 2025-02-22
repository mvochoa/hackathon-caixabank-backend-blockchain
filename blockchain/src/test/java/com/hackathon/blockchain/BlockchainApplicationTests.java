package com.hackathon.blockchain;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
        properties = {
                "server.port = 3001"
        }
)
class BlockchainApplicationTests {

    @Test
    void contextLoads() {
    }

}
