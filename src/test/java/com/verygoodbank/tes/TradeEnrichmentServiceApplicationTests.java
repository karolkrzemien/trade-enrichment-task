package com.verygoodbank.tes;

import com.verygoodbank.tes.service.TradeEnrichmentServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TradeEnrichmentServiceApplicationTests {

    private final String TRADE_CSV_PATH = "src/test/resources/trade.csv";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TradeEnrichmentServiceImpl service;

    @Test
    public void uploadFileAndExpectSuccess() throws Exception {
        MockMultipartFile file = createMultipartFile(TRADE_CSV_PATH);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/enrich")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("date,product_name,currency,price")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("20160101,Treasury Bills Domestic,EUR,10")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("20160101,Corporate Bonds Domestic,EUR,20.1")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("20160101,REPO Domestic,EUR,30.34")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("20160101,Missing Product Name,EUR,35.34")));
    }

    private static MockMultipartFile createMultipartFile(String path) {

        try {
            FileInputStream input = new FileInputStream(path);
            return new MockMultipartFile("file", "trade.csv", "text/csv", input);
        } catch (IOException e) {
            throw new RuntimeException("Unable to create MockMultipartFile: " + e.getMessage(), e);
        }
    }

    @Test
    void enrichTradeData_MissingProductMapping_WarnsMissingMapping() throws Exception {

        MockMultipartFile file = createMultipartFile(TRADE_CSV_PATH);
        String result = service.enrichTradeData(file);

        assert (result.contains("Missing Product Name"));
    }

    @Test
    void enrichTradeData_InvalidDate_LogsError() {
        String tradeCsv = "date,product_id,currency,price\ninvalidDate,1,USD,100.0";
        MockMultipartFile file = new MockMultipartFile("file", "trade.csv", "text/csv", tradeCsv.getBytes());

        String result = service.enrichTradeData(file);

        assert (!result.contains("invalidDate,1,USD,100.0"));
    }

    @Test
    void shouldReturnTrueWhenCurrencyIsInvalid() {
        String tradeCsv = "date,product_id,currency,price\n20230101,1,USDD,100.0";
        MockMultipartFile file = new MockMultipartFile("file", "trade.csv", "text/csv", tradeCsv.getBytes());

        String result = service.enrichTradeData(file);

        assert (!result.contains("20230101,1,USDD,100.0"));
    }

    @Test
    void shouldReturnTrueWhenDateIsInvalid() {
        String tradeCsv = "date,product_id,currency,price\n20230177,1,USD,100.0";
        MockMultipartFile file = new MockMultipartFile("file", "trade.csv", "text/csv", tradeCsv.getBytes());

        String result = service.enrichTradeData(file);

        assert (!result.contains("20230177,1,USD,100.0"));
    }

    @Test
    void shouldReturnTrueWhenPriceIsNegative() {
        String tradeCsv = "date,product_id,currency,price\n20230105,1,USD,-100.0";
        MockMultipartFile file = new MockMultipartFile("file", "trade.csv", "text/csv", tradeCsv.getBytes());

        String result = service.enrichTradeData(file);

        assert (!result.contains("20230105,1,USD,-100.0"));
    }
}
