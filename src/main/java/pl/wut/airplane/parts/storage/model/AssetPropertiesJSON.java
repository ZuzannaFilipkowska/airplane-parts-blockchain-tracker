package pl.wut.airplane.parts.storage.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class AssetPropertiesJSON implements Serializable  {
    String objectType;
    String salt;
    String partName;
    String partNumber;
    String description;
    String manufacturer;
    String length;
    String width;
    String height;
    String status;
    String lastInspectionDate;
    String inspectionPerformedBy;
    String nextInspectionDate;
    Integer lifeLimit;
    Integer currentUsageTimes;
}
