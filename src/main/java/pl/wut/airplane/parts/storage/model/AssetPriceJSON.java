package pl.wut.airplane.parts.storage.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AssetPriceJSON {
    String assetID;
    Long price;
    String tradeID;
}
