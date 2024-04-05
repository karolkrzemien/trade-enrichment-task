package com.verygoodbank.tes.interfaces;

import org.springframework.web.multipart.MultipartFile;

public interface TradeEnrichmentService {
    String enrichTradeData(MultipartFile file);
}
