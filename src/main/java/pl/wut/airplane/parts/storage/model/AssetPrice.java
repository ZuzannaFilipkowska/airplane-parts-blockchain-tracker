package pl.wut.airplane.parts.storage.model;

import lombok.Data;

@Data

public class AssetPrice {
    String assetId;
    Long price;
    String tradeId;
}
