package pl.wut.airplane.parts.storage.model;

import lombok.Data;

@Data

public class AssetJSON {
    String objectType;
    String assetID;
    String publicDescription;
    String ownerOrg;

}
