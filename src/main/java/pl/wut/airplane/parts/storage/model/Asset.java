package pl.wut.airplane.parts.storage.model;

import lombok.Data;

@Data
public class Asset {
    String assetID;
    String publicDescription;
    Boolean isForSale;
    String ownerOrg;
}
