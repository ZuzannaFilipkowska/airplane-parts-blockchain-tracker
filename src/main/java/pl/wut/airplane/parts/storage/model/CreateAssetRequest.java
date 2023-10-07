package pl.wut.airplane.parts.storage.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateAssetRequest {
    AssetPrivateData privateData;
    String publicDescription;
    Boolean isForSale;
}
