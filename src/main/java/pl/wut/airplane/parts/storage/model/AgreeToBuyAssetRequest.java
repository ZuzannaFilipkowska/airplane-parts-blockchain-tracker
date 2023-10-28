package pl.wut.airplane.parts.storage.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AgreeToBuyAssetRequest {
    AssetPrivateData privateData;
    String assetId;
    Long price;
}
