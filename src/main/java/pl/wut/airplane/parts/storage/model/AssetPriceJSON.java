package pl.wut.airplane.parts.storage.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AssetPriceJSON {
    String asset_id;
    String trade_id;
    Long price;
}
