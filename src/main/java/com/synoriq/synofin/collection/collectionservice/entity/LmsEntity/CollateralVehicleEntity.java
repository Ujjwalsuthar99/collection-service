package com.synoriq.synofin.collection.collectionservice.entity.LmsEntity;

import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.Date;

@Transactional
@AllArgsConstructor
@NoArgsConstructor
@Entity
@TypeDef(name = "json", typeClass = JsonType.class)
@Table(name = "collateral_vehicle", schema = "lms")
@Data
public class CollateralVehicleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "collateral_id")
    private Long collateralId;

    @Column(name = "collateral_type")
    private String collateralType;

    @Column(name = "collateral_subtype")
    private String collateralSubtype;

    @Column(name = "identifier")
    private String identifier;

    @Column(name = "description")
    private String description;

    @Column(name = "loan_id")
    private Long loanId;

    @Column(name = "asset_type")
    private String assetType;

    @Column(name = "vehicle_type")
    private String vehicle_type;

    @Column(name = "manufacturer")
    private String manufacturer;

    @Column(name = "product")
    private String product;

    @Column(name = "model")
    private String model;

}
