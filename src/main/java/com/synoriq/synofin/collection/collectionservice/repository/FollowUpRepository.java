package com.synoriq.synofin.collection.collectionservice.repository;

import com.synoriq.synofin.collection.collectionservice.entity.FollowUpEntity;
import io.lettuce.core.dynamic.annotation.Param;
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

    List<FollowUpEntity> findByLoanIdAndFollowUpStatus(Long loanId, String status);

    @Query(nativeQuery = true,value = "select " +
//            "concat_ws(' ', c.first_name, c.last_name) as name,\n" +
            "concat(lms.decrypt_data(c.first_name, :encryptionKey, :password, :piiPermission), ' ', lms.decrypt_data(c.last_name, :encryptionKey, :password, :piiPermission)) as name,\n" +
            "c.address1_json->>'address' as address, \n" +
            "f.followups_id as followup_id,\n" +
            "f.loan_id as loanId,\n" +
            "la.loan_application_number as loan_number,\n" +
            "(select username from master.users where user_id = f.created_by) as created_by,\n" +
            "f.created_date as created_date,\n" +
            "f.followup_reason as followup_reason,\n" +
            "f.next_followup_datetime as next_followup_date,\n" +
            "f.other_followup_reason as other_followup_reason,\n" +
            "f.remarks as remarks,\n" +
            "la.days_past_due as dpd,\n" +
            "    (case\n" +
            "       when la.days_past_due between 0 and 30 then '0-30 DPD'\n" +
            "       when la.days_past_due between 31 and 60 then '31-60 DPD'\n" +
            "       when la.days_past_due between 61 and 90 then '61-90 DPD'\n" +
            "       when la.days_past_due between 91 and 120 then '91-120 DPD'\n" +
            "       when la.days_past_due between 121 and 150 then '121-150 DPD'\n" +
            "       when la.days_past_due between 151 and 180 then '151-180 DPD'\n" +
            "       else '180+ DPD' end) as days_past_due_bucket,\n" +
            "    (case\n" +
            "        when la.days_past_due between 0 and 30 then '#61B2FF'\n" +
            "        when la.days_past_due between 31 and 60 then '#2F80ED'\n" +
            "        when la.days_past_due between 61 and 90 then '#E1D153'\n" +
            "        when la.days_past_due between 91 and 120 then '#F2994A'\n" +
            "        when la.days_past_due between 121 and 150 then '#FF5359'\n" +
            "        when la.days_past_due between 151 and 180 then '#C83939'\n" +
            "        else '#722F37'\n" +
            "    end) as dpd_bg_color_key,\n" +
            "    (case\n" +
            "        when la.days_past_due between 0 and 30 then '#323232'\n" +
            "        when la.days_past_due between 31 and 60 then '#ffffff'\n" +
            "        when la.days_past_due between 61 and 90 then '#323232'\n" +
            "        when la.days_past_due between 91 and 120 then '#323232'\n" +
            "        when la.days_past_due between 121 and 150 then '#ffffff'\n" +
            "        when la.days_past_due between 151 and 180 then '#ffffff'\n" +
            "        else '#ffffff'\n" +
            "    end) as dpd_text_color_key,\n" +
            "la.product as loan_type,\n" +
            "cast(cal.images as text) as followup_images, cast(cal.geo_location_data as text) as geo_location_data,\n" +
            "COUNT(f.followups_id) OVER () AS total_rows,\n" +
            "la.sanctioned_amount as loan_amount\n" +
            "             from collection.followups f \n" +
            "            join (select loan_application_id ,days_past_due,product,sanctioned_amount, loan_application_number from lms.loan_application) as la on la.loan_application_id = f.loan_id \n" +
            "            join (select loan_id, customer_id, customer_type from lms.customer_loan_mapping) as clm on clm.loan_id  = la.loan_application_id \n" +
            "           join (select customer_id,address1_json, first_name, last_name from lms.customer) as c on c.customer_id = clm.customer_id\n" +
            "           join collection.collection_activity_logs cal on cal.collection_activity_logs_id = f.collection_activity_logs_id\n" +
            " where f.loan_id = :loanId and clm.customer_type = 'applicant' \n" +
            "            and f.next_followup_datetime between :fromDate and :toDate")
    List<Map<String,Object>> getFollowupsLoanWiseByDuration(@Param("loanId") Long loanId, @Param("fromDate") Date fromDate
            , @Param("toDate") Date toDate, @Param("encryptionKey") String encryptionKey, @Param("password") String password, @Param("piiPermission") Boolean piiPermission, Pageable pageable);

    @Query(nativeQuery = true,value = "\n" +
//            "select concat_ws(' ', c.first_name, c.last_name) as name,\n" +
            "select concat(lms.decrypt_data(c.first_name, :encryptionKey, :password, :piiPermission), ' ', lms.decrypt_data(c.last_name, :encryptionKey, :password, :piiPermission)) as name,\n" +
            "c.address1_json->>'address' as address, \n" +
            "f.followups_id as followup_id,\n" +
            "f.loan_id as loanId,\n" +
            "la.loan_application_number as loan_number,\n" +
            "f.created_by as created_by, \n" +
            "f.followup_status as status, \n" +
            "date(f.created_date) as created_date,\n" +
            "f.followup_reason as followup_reason,\n" +
            "date(f.next_followup_datetime) as next_followup_date,\n" +
            "f.other_followup_reason as other_followup_reason,\n" +
            "f.remarks as remarks,\n" +
            "la.days_past_due as dpd,\n" +
            "    (case\n" +
            "       when la.days_past_due between 0 and 30 then '0-30 DPD'\n" +
            "       when la.days_past_due between 31 and 60 then '31-60 DPD'\n" +
            "       when la.days_past_due between 61 and 90 then '61-90 DPD'\n" +
            "       when la.days_past_due between 91 and 120 then '91-120 DPD'\n" +
            "       when la.days_past_due between 121 and 150 then '121-150 DPD'\n" +
            "       when la.days_past_due between 151 and 180 then '151-180 DPD'\n" +
            "       else '180+ DPD' end) as days_past_due_bucket,\n" +
            "    (case\n" +
            "        when la.days_past_due between 0 and 30 then '#61B2FF'\n" +
            "        when la.days_past_due between 31 and 60 then '#2F80ED'\n" +
            "        when la.days_past_due between 61 and 90 then '#E1D153'\n" +
            "        when la.days_past_due between 91 and 120 then '#F2994A'\n" +
            "        when la.days_past_due between 121 and 150 then '#FF5359'\n" +
            "        when la.days_past_due between 151 and 180 then '#C83939'\n" +
            "        else '#722F37'\n" +
            "    end) as dpd_bg_color_key,\n" +
            "    (case\n" +
            "        when la.days_past_due between 0 and 30 then '#323232'\n" +
            "        when la.days_past_due between 31 and 60 then '#ffffff'\n" +
            "        when la.days_past_due between 61 and 90 then '#323232'\n" +
            "        when la.days_past_due between 91 and 120 then '#323232'\n" +
            "        when la.days_past_due between 121 and 150 then '#ffffff'\n" +
            "        when la.days_past_due between 151 and 180 then '#ffffff'\n" +
            "        else '#ffffff'\n" +
            "    end) as dpd_text_color_key,\n" +
            "    la.product as loan_type,\n" +
            "    count(f.followups_id) over () as total_count," +
            "    (select coalesce(sum(rs.pending_amount), 0) from lms.repayment_schedule rs where rs.status = 'outstanding' and rs.loan_id = la.loan_application_id group by rs.loan_id) as overdue_repayment\n" +
            "             from collection.followups f \n" +
            "            join (select loan_application_id ,days_past_due,product, loan_application_number from lms.loan_application) as la on la.loan_application_id = f.loan_id \n" +
            "                join (select loan_id, customer_id, customer_type from lms.customer_loan_mapping) as clm on clm.loan_id  = la.loan_application_id \n" +
            "               join (select customer_id,address1_json, first_name, last_name from lms.customer) as c on c.customer_id = clm.customer_id  \n" +
            " where f.created_by = :userId and clm.customer_type = 'applicant' \n" +
            "            and f.created_date between :fromDate and :toDate and f.followup_status in :#{#statusList}")
    List<Map<String,Object>> getFollowupsUserWiseByDurationForCreated(@Param("userId") Long userId, @Param("fromDate") Date fromDate
            , @Param("toDate") Date toDate, @Param("statusList") List<String> statusList, @Param("encryptionKey") String encryptionKey, @Param("password") String password, @Param("piiPermission") Boolean piiPermission, Pageable pageable);


    @Query(nativeQuery = true,value = "select " +
//            "concat_ws(' ', c.first_name, c.last_name) as name,\n" +
            "concat(lms.decrypt_data(c.first_name, :encryptionKey, :password, :piiPermission), ' ', lms.decrypt_data(c.last_name, :encryptionKey, :password, :piiPermission)) as name,\n" +
            "c.address1_json->>'address' as address, \n" +
            "f.followups_id as followup_id,\n" +
            "f.loan_id as loan_id,\n" +
            "f.created_by as created_by, \n" +
            "f.created_date as created_date,\n" +
            "f.followup_reason as followup_reason,\n" +
            "f.next_followup_datetime as next_followup_date,\n" +
            "f.other_followup_reason as other_followup_reason,\n" +
            "f.remarks as remarks,\n" +
            "f.followup_status as status, \n" +
            "f.closing_remarks, \n" +
            "f.service_request_id, \n" +
            "la.days_past_due as dpd,\n" +
            "la.loan_application_number as loan_application_number,\n" +
            "    (case\n" +
            "       when la.days_past_due between 0 and 30 then '0-30 DPD'\n" +
            "       when la.days_past_due between 31 and 60 then '31-60 DPD'\n" +
            "       when la.days_past_due between 61 and 90 then '61-90 DPD'\n" +
            "       when la.days_past_due between 91 and 120 then '91-120 DPD'\n" +
            "       when la.days_past_due between 121 and 150 then '121-150 DPD'\n" +
            "       when la.days_past_due between 151 and 180 then '151-180 DPD'\n" +
            "       else '180+ DPD' end) as days_past_due_bucket,\n" +
            "    (case\n" +
            "        when la.days_past_due between 0 and 30 then '#61B2FF'\n" +
            "        when la.days_past_due between 31 and 60 then '#2F80ED'\n" +
            "        when la.days_past_due between 61 and 90 then '#E1D153'\n" +
            "        when la.days_past_due between 91 and 120 then '#F2994A'\n" +
            "        when la.days_past_due between 121 and 150 then '#FF5359'\n" +
            "        when la.days_past_due between 151 and 180 then '#C83939'\n" +
            "        else '#722F37'\n" +
            "    end) as dpd_bg_color_key,\n" +
            "    (case\n" +
            "        when la.days_past_due between 0 and 30 then '#323232'\n" +
            "        when la.days_past_due between 31 and 60 then '#ffffff'\n" +
            "        when la.days_past_due between 61 and 90 then '#323232'\n" +
            "        when la.days_past_due between 91 and 120 then '#323232'\n" +
            "        when la.days_past_due between 121 and 150 then '#ffffff'\n" +
            "        when la.days_past_due between 151 and 180 then '#ffffff'\n" +
            "        else '#ffffff'\n" +
            "    end) as dpd_text_color_key,\n" +
            "    (select coalesce(sum(rs.pending_amount), 0) from lms.repayment_schedule rs where rs.status = 'outstanding' and rs.loan_id = la.loan_application_id group by rs.loan_id) as overdue_repayment,\n" +
            "    CAST(cal.geo_location_data as TEXT) as geo_location_data,\n" +
            "    CAST(cal.images as TEXT) as images\n" +
            "             from collection.followups f \n" +
            "            join (select loan_application_id, loan_application_number, days_past_due from lms.loan_application) as la on la.loan_application_id = f.loan_id \n" +
            "            join (select loan_id, customer_id from lms.customer_loan_mapping where customer_type = 'applicant') as clm on clm.loan_id  = la.loan_application_id \n" +
            "           join (select customer_id,address1_json, first_name, last_name from lms.customer) as c on c.customer_id = clm.customer_id\n" +
            "           join (select collection_activity_logs_id, geo_location_data, images from collection.collection_activity_logs) as cal on cal.collection_activity_logs_id  = f.collection_activity_logs_id\n" +
            "           where f.followups_id = :followupId")
    Map<String, Object> getFollowupDetailsById(@Param("followupId") Long followupId, @Param("encryptionKey") String encryptionKey, @Param("password") String password, @Param("piiPermission") Boolean piiPermission);

    List<FollowUpEntity> findDataByServiceRequestId(Long serviceRequestId);
}
