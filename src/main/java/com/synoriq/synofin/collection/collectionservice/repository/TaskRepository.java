package com.synoriq.synofin.collection.collectionservice.repository;

import com.synoriq.synofin.collection.collectionservice.entity.LoanAllocationEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface TaskRepository extends JpaRepository<LoanAllocationEntity, Long> {



    @Query(nativeQuery = true,value = "select loan_application_number from lms.loan_application where loan_application_id = :loanId")
    String getLoanApplicationNumber(@Param("loanId") Long loanId);
    @Query(nativeQuery = true, value = "select la.loan_application_id,\n" +
            "    branch.branch_name as branch,\n" +
            "    concat(lms.decrypt_data(c.first_name, :encryptionKey, :password, :piiPermission), ' ', lms.decrypt_data(c.middle_name, :encryptionKey, :password, :piiPermission), ' ', lms.decrypt_data(c.last_name, :encryptionKey, :password, :piiPermission)) as customer_name,\n" +
            "    c.phone1_json->>'mobile' as mobile,\n" +
            "    c.address1_json->>'address' as address,\n" +
            "    p.product_name as product,\n" +
            "    la.loan_application_number,\n" +
            "    la2.task_purpose,\n" +
            "    (case\n" +
            "\t   when la.days_past_due = 0 then 'Current'\n" +
            "       when la.days_past_due between 1 and 30 then '1-30 DPD'\n" +
            "       when la.days_past_due between 31 and 60 then '31-60 DPD'\n" +
            "       when la.days_past_due between 61 and 90 then '61-90 DPD'\n" +
            "       when la.days_past_due between 91 and 120 then '91-120 DPD'\n" +
            "       when la.days_past_due between 121 and 150 then '121-150 DPD'\n" +
            "       when la.days_past_due between 151 and 180 then '151-180 DPD'\n" +
            "       else '180+ DPD' end) as days_past_due_bucket,\n" +
            "   la.days_past_due,\n" +
            "    (case\n" +
            "\t    when la.days_past_due = 0 then '#a2e890'\n" +
            "        when la.days_past_due between 1 and 30 then '#61B2FF'\n" +
            "        when la.days_past_due between 31 and 60 then '#2F80ED'\n" +
            "        when la.days_past_due between 61 and 90 then '#FDAAAA'\n" +
            "        when la.days_past_due between 91 and 120 then '#F2994A'\n" +
            "        when la.days_past_due between 121 and 150 then '#FF5359'\n" +
            "        when la.days_past_due between 151 and 180 then '#C83939'\n" +
            "        else '#722F37'\n" +
            "    end) as dpd_bg_color_key,\n" +
            "    (case\n" +
            "\t    when la.days_past_due = 0 then '#000000'\n" +
            "        when la.days_past_due between 1 and 30 then '#323232'\n" +
            "        when la.days_past_due between 31 and 60 then '#ffffff'\n" +
            "        when la.days_past_due between 61 and 90 then '#323232'\n" +
            "        when la.days_past_due between 91 and 120 then '#323232'\n" +
            "        when la.days_past_due between 121 and 150 then '#ffffff'\n" +
            "        when la.days_past_due between 151 and 180 then '#ffffff'\n" +
            "        else '#ffffff'\n" +
            "    end) as dpd_text_color_key\n" +
            "from\n" +
            "    lms.loan_application la\n" +
            "    join lms.customer_loan_mapping clm on la.loan_application_id = clm.loan_id\n" +
            "    join lms.customer c on clm.customer_id = c.customer_id\n" +
            "    join collection.loan_allocation la2 on la2.loan_id = la.loan_application_id \n" +
            "    left join (select product_code, product_name from master.product) as p on p.product_code = la.product\n" +
            "    left join (select branch_name, branch_id from master.branch) as branch on branch.branch_id = la.branch_id \n" +
            "where\n" +
            "    clm.\"customer_type\" = 'applicant'\n" +
            "    and la.deleted = false\n and la2.deleted = false\n" +
            "    and la.loan_status in ('active')\n" +
            "    and la2.allocated_to_user_id = :userId\n" +
            "order by\n" +
            "    la.loan_application_id asc")
    List<Map<String,Object>> getTaskDetailsByPages(@Param("userId") Long userId, @Param("encryptionKey") String encryptionKey, @Param("password") String password, @Param("piiPermission") Boolean piiPermission, Pageable pageRequest);


    @Query(nativeQuery = true, value = "select la.loan_application_id,\n" +
            "    branch.branch_name as branch,\n" +
            "    concat(lms.decrypt_data(c.first_name, :encryptionKey, :password, :piiPermission), ' ', lms.decrypt_data(c.middle_name, :encryptionKey, :password, :piiPermission), ' ', lms.decrypt_data(c.last_name, :encryptionKey, :password, :piiPermission)) as customer_name,\n" +
            "    c.phone1_json->>'mobile' as mobile,\n" +
            "    c.address1_json->>'address' as address,\n" +
            "    p.product_name as product,\n" +
            "    la.loan_application_number,\n" +
            "    (case\n" +
            "\t   when la.days_past_due = 0 then 'Current'\t    \n" +
            "       when la.days_past_due between 1 and 30 then '1-30 DPD'\n" +
            "       when la.days_past_due between 31 and 60 then '31-60 DPD'\n" +
            "       when la.days_past_due between 61 and 90 then '61-90 DPD'\n" +
            "       when la.days_past_due between 91 and 120 then '91-120 DPD'\n" +
            "       when la.days_past_due between 121 and 150 then '121-150 DPD'\n" +
            "       when la.days_past_due between 151 and 180 then '151-180 DPD'\n" +
            "       else '180+ DPD' end) as days_past_due_bucket,\n" +
            "   la.days_past_due,\n" +
            "    (case\n" +
            "\t    when la.days_past_due = 0 then '#a2e890'\n" +
            "        when la.days_past_due between 1 and 30 then '#61B2FF'\n" +
            "        when la.days_past_due between 31 and 60 then '#2F80ED'\n" +
            "        when la.days_past_due between 61 and 90 then '#FDAAAA'\n" +
            "        when la.days_past_due between 91 and 120 then '#F2994A'\n" +
            "        when la.days_past_due between 121 and 150 then '#FF5359'\n" +
            "        when la.days_past_due between 151 and 180 then '#C83939'\n" +
            "        else '#722F37'\n" +
            "    end) as dpd_bg_color_key,\n" +
            "    (case\n" +
            "\t    when la.days_past_due = 0 then '#000000'\n" +
            "        when la.days_past_due between 1 and 30 then '#323232'\n" +
            "        when la.days_past_due between 31 and 60 then '#ffffff'\n" +
            "        when la.days_past_due between 61 and 90 then '#323232'\n" +
            "        when la.days_past_due between 91 and 120 then '#323232'\n" +
            "        when la.days_past_due between 121 and 150 then '#ffffff'\n" +
            "        when la.days_past_due between 151 and 180 then '#ffffff'\n" +
            "        else '#ffffff'\n" +
            "    end) as dpd_text_color_key\n" +
            "from\n" +
            "    lms.loan_application la\n" +
            "    join lms.customer_loan_mapping clm on la.loan_application_id = clm.loan_id\n" +
            "    join lms.customer c on clm.customer_id = c.customer_id\n" +
            "join collection.loan_allocation la2 on la2.loan_id = la.loan_application_id \n" +
            "left join (select product_code, product_name from master.product) as p on p.product_code = la.product \n" +
            "left join (select branch_name, branch_id from master.branch) as branch on branch.branch_id = la.branch_id \n" +
            "left join (select loan_id, vehicle_registration_no from lms.collateral_vehicle) as vehicle on vehicle.loan_id = la.loan_application_id \n" +
            "where\n" +
            "    clm.\"customer_type\" = 'applicant'\n" +
            "    and la.deleted = false\n and la2.deleted = false\n" +
            "    and la.loan_status in ('active')\n" +
            "    and la2.allocated_to_user_id = :userId\n" +
            "    and (LOWER(concat_ws(' ', c.first_name, c.last_name)) like LOWER(concat('%', :searchKey,'%')) or LOWER(la.product) like LOWER(concat('%', :searchKey, '%')) or \n" +
            "    LOWER(la.loan_application_number) like LOWER(concat('%', :searchKey, '%')) or LOWER(branch.branch_name) like LOWER(concat('%',:searchKey, '%')) or \n" +
            "    LOWER(vehicle.vehicle_registration_no) like LOWER(concat('%',:searchKey, '%')))\n" +
            "order by\n" +
            "    la.loan_application_id asc")
    List<Map<String,Object>> getTaskDetailsBySearchKey(@Param("userId") Long userId, String searchKey, @Param("encryptionKey") String encryptionKey, @Param("password") String password, @Param("piiPermission") Boolean piiPermission, Pageable pageRequest);

    @Query(nativeQuery = true, value = "select clm2.loan_id as loan_id from lms.loan_application la \n" +
            "             join lms.customer_loan_mapping clm on clm.loan_id = la.loan_application_id \n" +
            "             left join lms.customer_loan_mapping clm2 on clm2.customer_id = clm.customer_id \n" +
            "        \t where la.loan_application_id = :loanId and clm.customer_type = 'applicant'")
    List<Object> getLoanIdsByLoanId(@Param("loanId") Long loanId);


}
