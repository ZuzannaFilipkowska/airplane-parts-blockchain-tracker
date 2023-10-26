package pl.wut.airplane.parts.storage.model;

import lombok.Data;

@Data

public class AssetPriceJSON {
    String assetID;
    Long price;
    String tradeID;
}
