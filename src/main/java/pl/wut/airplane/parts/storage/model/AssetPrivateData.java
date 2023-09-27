package pl.wut.airplane.parts.storage.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AssetPrivateData {
    String ObjectType;
    String Color;
    Long Size;
}
