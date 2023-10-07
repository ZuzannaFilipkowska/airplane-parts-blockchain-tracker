package pl.wut.airplane.parts.storage.model;

import lombok.Data;

@Data

public class AssetProperties {
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
