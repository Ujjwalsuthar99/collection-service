package com.synoriq.synofin.collection.collectionservice.rest.request;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CollectionConfigurationDtoRequest {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Long configurationsId;

    private Date createdDate;

    private Long createdBy;

    private Boolean deleted;

    private String configurationName;

    private String configurationValue;

    private String configurationDescription;
}