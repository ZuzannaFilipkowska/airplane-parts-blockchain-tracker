package pl.wut.airplane.parts.storage.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class AssetPropertiesJSON implements Serializable  {
    String objectType;
    String color;
    Long size;
    String salt;
}
