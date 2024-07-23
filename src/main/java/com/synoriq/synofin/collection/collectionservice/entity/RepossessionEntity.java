package com.synoriq.synofin.collection.collectionservice.entity;

import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.Objects;

import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.COLLECTION;

@Transactional
@Entity
@TypeDef(name = "json", typeClass = JsonType.class)
@Table(name = "repossession", schema = COLLECTION)
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class RepossessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "repossession_id", nullable = false)
    private Long repossessionId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Date createdDate;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "deleted")
    private Boolean deleted;

    @Column(name = "assigned_to")
    private Long assignedTo;

    @Column(name = "loan_id")
    private Long loanId;

    @Column(name = "status")
    private String status;

    @Column(name = "remarks")
    @Type(type = "json")
    private Object remarks;

    @Column(name = "lms_repo_id")
    private Long lmsRepoId;

    @Column(name = "recovery_agency")
    private String recoveryAgency;

    @Column(name = "yard_details_json")
    @Type(type = "json")
    private Object yardDetailsJson;

    @Column(name = "collateral_json")
    @Type(type = "json")
    private Object collateralJson;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        RepossessionEntity that = (RepossessionEntity) o;
        return repossessionId != null && Objects.equals(repossessionId, that.repossessionId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
