package com.synoriq.synofin.collection.collectionservice.repository;

import com.synoriq.synofin.collection.collectionservice.entity.FollowUpEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface FollowUpRepository extends JpaRepository<FollowUpEntity, Long> {

    FollowUpEntity findByFollowupId(Long followupId);

    @Query(nativeQuery = true,value = "\n" +
            "select concat_ws(' ', c.first_name, c.last_name) as name,\n" +
            "c.address1_json->>'address' as address, \n" +
            "f.followups_id as followup_id,\n" +
            "f.loan_id as loanId,\n" +
            "f.created_by as created_by, \n" +
            "date(f.created_date) as created_date,\n" +
            "f.followup_reason as followup_reason,\n" +
            "date(f.next_followup_datetime) as next_followup_date,\n" +
            "f.other_followup_reason as other_followup_reason,\n" +
            "f.remarks as remarks,\n" +
            "la.days_past_due as dpd,\n" +
            "la.product as loan_type,\n" +
            "la.sanctioned_amount as loan_amount\n" +
            "             from collection.followups f \n" +
            "            join (select loan_application_id ,days_past_due,product,sanctioned_amount from lms.loan_application) as la on la.loan_application_id = f.loan_id \n" +
            "            join (select loan_id, customer_id from lms.customer_loan_mapping) as clm on clm.loan_id  = la.loan_application_id \n" +
            "           join (select customer_id,address1_json, first_name, last_name from lms.customer) as c on c.customer_id = clm.customer_id  where f.loan_id = :loanId  \n" +
            "            and created_date between :fromDate and :toDate ")
    List<Map<String,Object>> getFollowupsLoanWiseByDuration(@Param("loanId") Long loanId, @Param("fromDate") Date fromDate
            , @Param("toDate") Date toDate, Pageable pageable);


    @Query(nativeQuery = true,value = "select concat_ws(' ', c.first_name, c.last_name) as name,\n" +
            "c.address1_json->>'address' as address, \n" +
            "f.followups_id as followup_id,\n" +
            "f.loan_id as loan_id,\n" +
            "f.created_by as created_by, \n" +
            "date(f.created_date) as created_date,\n" +
            "f.followup_reason as followup_reason,\n" +
            "date(f.next_followup_datetime) as next_followup_date,\n" +
            "f.other_followup_reason as other_followup_reason,\n" +
            "f.remarks as remarks,\n" +
            "la.days_past_due as dpd \n" +
            "             from collection.followups f \n" +
            "            join (select loan_application_id ,days_past_due from lms.loan_application) as la on la.loan_application_id = f.loan_id \n" +
            "            join (select loan_id, customer_id from lms.customer_loan_mapping) as clm on clm.loan_id  = la.loan_application_id \n" +
            "           join (select customer_id,address1_json, first_name, last_name from lms.customer) as c on c.customer_id = clm.customer_id\n" +
            "           where f.followups_id = :followupId")
    Map<String, Object> getFollowupDetailsById(@Param("followupId") Long followupId);

}
