package com.synoriq.synofin.collection.collectionservice.repository;

import com.synoriq.synofin.collection.collectionservice.entity.LoanAllocationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface TaskRepository extends JpaRepository<LoanAllocationEntity, Long> {

//    @Query(nativeQuery = true,value = "select * from collection.followups where loan_id = :loanId " +
//            "and created_date between :fromDate and :toDate ")
//    Page<Object> getFollowupsLoanWiseByDuration(@Param("loanId") Long loanId, @Param("fromDate") Date fromDate
//            , @Param("toDate") Date toDate, Pageable pageable);


    @Query(nativeQuery = true,value = "select loan_application_number from lms.loan_application where loan_application_id = :loanId")
    String getLoanApplicationNumber(@Param("loanId") Long loanId);
    @Query(nativeQuery = true, value = "select la.loan_application_id,\n" +
            "    concat_ws(' ', c.first_name, c.last_name) as customer_name,\n" +
            "    c.address1_json->>'address' as address,\n" +
            "    p.product_name as product,\n" +
            "    (case when overdue_repayment is null then 0 else overdue_repayment end) as overdue_repayment,\n" +
            "    la.loan_application_number,\n" +
            "    (case\n" +
            "       when la.days_past_due between 0 and 30 then '0-30 DPD'\n" +
            "       when la.days_past_due between 31 and 60 then '31-60 DPD'\n" +
            "       when la.days_past_due between 61 and 90 then '61-90 DPD'\n" +
            "       when la.days_past_due between 91 and 120 then '91-120 DPD'\n" +
            "       when la.days_past_due between 121 and 150 then '121-150 DPD'\n" +
            "       when la.days_past_due between 151 and 180 then '151-180 DPD'\n" +
            "       else '180++ DPD' end) as days_past_due_bucket,\n" +
            "   la.days_past_due,\n" +
            "    (case\n" +
            "        when la.days_past_due between 0 and 30 then '#ABCFFF'\n" +
            "        when la.days_past_due between 31 and 60 then '#FDB4FF'\n" +
            "        when la.days_past_due between 61 and 90 then '#FDAAAA'\n" +
            "        when la.days_past_due between 91 and 120 then '#FCDA8B'\n" +
            "        when la.days_past_due between 121 and 150 then '#F2994A'\n" +
            "        when la.days_past_due between 151 and 180 then '#FF5359'\n" +
            "        else '#C83939'\n" +
            "    end) as dpd_bg_color_key,\n" +
            "    (case\n" +
            "        when la.days_past_due between 0 and 30 then '#323232'\n" +
            "        when la.days_past_due between 31 and 60 then '#323232'\n" +
            "        when la.days_past_due between 61 and 90 then '#323232'\n" +
            "        when la.days_past_due between 91 and 120 then '#323232'\n" +
            "        when la.days_past_due between 121 and 150 then '#323232'\n" +
            "        when la.days_past_due between 151 and 180 then '#ffffff'\n" +
            "        else '#ffffff'\n" +
            "    end) as dpd_text_color_key\n" +
            "from\n" +
            "    lms.loan_application la\n" +
            "left join (\n" +
            "    select\n" +
            "        MAX(case when rs.status = 'outstanding' then due_date end) over (partition by rs.loan_id ) as duedate,\n" +
            "        SUM(rs.pending_amount) over (partition by rs.loan_id ) as overdue_repayment,\n" +
            "        MAX(case when rs.status = 'outstanding' then installment_number end) over (partition by rs.loan_id ) as outstanding_installment_number,\n" +
            "        case\n" +
            "            when rs.due_date = (MAX(rs.due_date) over(partition by rs.loan_id )) then \n" +
            "rs.installment_amount\n" +
            "        end as main_emi_amount,\n" +
            "        count(*) over(partition by rs.loan_id ) as number_of_outstanding_emis,\n" +
            "        row_number() over(partition by rs.loan_id\n" +
            "    order by\n" +
            "        due_date desc ) as rank,\n" +
            "        rs.loan_id\n" +
            "    from\n" +
            "        lms.repayment_schedule rs\n" +
            "    where\n" +
            "        rs.status = 'outstanding' ) repay on\n" +
            "    la.loan_application_id = repay.loan_id\n" +
            "    and rank < 2\n" +
            "left join (\n" +
            "    select\n" +
            "        SUM(pending_amount) as overdue_amount_charges,\n" +
            "        loan_id\n" +
            "    from\n" +
            "        lms.loan_charges lc\n" +
            "    join master.charge_definition cd on\n" +
            "        lc.charge_definition_id = cd.charge_definition_id\n" +
            "    where\n" +
            "        payment_status = 'outstanding'\n" +
            "        and cd.charge_state = 'receivable'\n" +
            "    group by\n" +
            "        lc.loan_id ) charges on\n" +
            "    la.loan_application_id = charges.loan_id\n" +
            "left join (\n" +
            "    select\n" +
            "        SUM(pending_amount) as overdue_amount_charges_payable,\n" +
            "        loan_id\n" +
            "    from\n" +
            "        lms.loan_charges lc\n" +
            "    join master.charge_definition cd on\n" +
            "        lc.charge_definition_id = cd.charge_definition_id\n" +
            "    where\n" +
            "        payment_status = 'outstanding'\n" +
            "        and cd.charge_state = 'payable'\n" +
            "    group by\n" +
            "        lc.loan_id ) payable_charges on\n" +
            "    la.loan_application_id = payable_charges.loan_id\n" +
            "join lms.customer_loan_mapping clm on\n" +
            "    la.loan_application_id = clm.loan_id\n" +
            "join lms.customer c on\n" +
            "    clm.customer_id = c.customer_id\n" +
            "left join (\n" +
            "    select\n" +
            "        sum(em2.excess_money) as rest_excess_money,\n" +
            "        loan_id\n" +
            "    from\n" +
            "        lms.excess_money em2\n" +
            "    group by\n" +
            "        em2.loan_id\n" +
            ") excess_money on\n" +
            "    la.loan_application_id = excess_money.loan_id\n" +
            "    join collection.loan_allocation la2 on la2.loan_id = la.loan_application_id \n" +
            "    join (select product_code, product_name from master.product) as p on p.product_code = la.product\n" +
            "where\n" +
            "    clm.\"customer_type\" = 'applicant'\n" +
            "    and la.deleted = false\n" +
            "    and la.loan_status in ( 'active', 'maturity_closure')\n" +
            "    and la2.allocated_to_user_id = :userId\n" +
            "order by\n" +
            "    la.loan_application_id asc")
    List<Map<String,Object>> getTaskDetailsByPages(@Param("userId") Long userId, Pageable pageRequest);


    @Query(nativeQuery = true, value = "select la.loan_application_id,\n" +
            "    concat_ws(' ', c.first_name, c.last_name) as customer_name,\n" +
            "    c.address1_json->>'address' as address,\n" +
            "    p.product_name as product,\n" +
            "    (case when overdue_repayment is null then 0 else overdue_repayment end) as overdue_repayment,\n" +
            "    la.loan_application_number,\n" +
            "    (case\n" +
            "       when la.days_past_due between 0 and 30 then '0-30 DPD'\n" +
            "       when la.days_past_due between 31 and 60 then '31-60 DPD'\n" +
            "       when la.days_past_due between 61 and 90 then '61-90 DPD'\n" +
            "       when la.days_past_due between 91 and 120 then '91-120 DPD'\n" +
            "       when la.days_past_due between 121 and 150 then '121-150 DPD'\n" +
            "       when la.days_past_due between 151 and 180 then '151-180 DPD'\n" +
            "       else '180++ DPD' end) as days_past_due_bucket,\n" +
            "   la.days_past_due,\n" +
            "    (case\n" +
            "        when la.days_past_due between 0 and 30 then '#ABCFFF'\n" +
            "        when la.days_past_due between 31 and 60 then '#FDB4FF'\n" +
            "        when la.days_past_due between 61 and 90 then '#FDAAAA'\n" +
            "        when la.days_past_due between 91 and 120 then '#FCDA8B'\n" +
            "        when la.days_past_due between 121 and 150 then '#F2994A'\n" +
            "        when la.days_past_due between 151 and 180 then '#FF5359'\n" +
            "        else '#C83939'\n" +
            "    end) as dpd_bg_color_key,\n" +
            "    (case\n" +
            "        when la.days_past_due between 0 and 30 then '#323232'\n" +
            "        when la.days_past_due between 31 and 60 then '#323232'\n" +
            "        when la.days_past_due between 61 and 90 then '#323232'\n" +
            "        when la.days_past_due between 91 and 120 then '#323232'\n" +
            "        when la.days_past_due between 121 and 150 then '#323232'\n" +
            "        when la.days_past_due between 151 and 180 then '#ffffff'\n" +
            "        else '#ffffff'\n" +
            "    end) as dpd_text_color_key\n" +
            "from\n" +
            "    lms.loan_application la\n" +
            "left join (\n" +
            "    select\n" +
            "        MAX(case when rs.status = 'outstanding' then due_date end) over (partition by rs.loan_id ) as duedate,\n" +
            "        SUM(rs.pending_amount) over (partition by rs.loan_id ) as overdue_repayment,\n" +
            "        MAX(case when rs.status = 'outstanding' then installment_number end) over (partition by rs.loan_id ) as outstanding_installment_number,\n" +
            "        case\n" +
            "            when rs.due_date = (MAX(rs.due_date) over(partition by rs.loan_id )) then \n" +
            "rs.installment_amount\n" +
            "        end as main_emi_amount,\n" +
            "        count(*) over(partition by rs.loan_id ) as number_of_outstanding_emis,\n" +
            "        row_number() over(partition by rs.loan_id\n" +
            "    order by\n" +
            "        due_date desc ) as rank,\n" +
            "        rs.loan_id\n" +
            "    from\n" +
            "        lms.repayment_schedule rs\n" +
            "    where\n" +
            "        rs.status = 'outstanding' ) repay on\n" +
            "    la.loan_application_id = repay.loan_id\n" +
            "    and rank < 2\n" +
            "left join (\n" +
            "    select\n" +
            "        SUM(pending_amount) as overdue_amount_charges,\n" +
            "        loan_id\n" +
            "    from\n" +
            "        lms.loan_charges lc\n" +
            "    join master.charge_definition cd on\n" +
            "        lc.charge_definition_id = cd.charge_definition_id\n" +
            "    where\n" +
            "        payment_status = 'outstanding'\n" +
            "        and cd.charge_state = 'receivable'\n" +
            "    group by\n" +
            "        lc.loan_id ) charges on\n" +
            "    la.loan_application_id = charges.loan_id\n" +
            "left join (\n" +
            "    select\n" +
            "        SUM(pending_amount) as overdue_amount_charges_payable,\n" +
            "        loan_id\n" +
            "    from\n" +
            "        lms.loan_charges lc\n" +
            "    join master.charge_definition cd on\n" +
            "        lc.charge_definition_id = cd.charge_definition_id\n" +
            "    where\n" +
            "        payment_status = 'outstanding'\n" +
            "        and cd.charge_state = 'payable'\n" +
            "    group by\n" +
            "        lc.loan_id ) payable_charges on\n" +
            "    la.loan_application_id = payable_charges.loan_id\n" +
            "join lms.customer_loan_mapping clm on\n" +
            "    la.loan_application_id = clm.loan_id\n" +
            "join lms.customer c on\n" +
            "    clm.customer_id = c.customer_id\n" +
            "left join (\n" +
            "    select\n" +
            "        sum(em2.excess_money) as rest_excess_money,\n" +
            "        loan_id\n" +
            "    from\n" +
            "        lms.excess_money em2\n" +
            "    group by\n" +
            "        em2.loan_id\n" +
            ") excess_money on\n" +
            "    la.loan_application_id = excess_money.loan_id\n" +
            "join collection.loan_allocation la2 on la2.loan_id = la.loan_application_id \n" +
            "join (select product_code, product_name from master.product) as p on p.product_code = la.product \n" +
            "where\n" +
            "    clm.\"customer_type\" = 'applicant'\n" +
            "    and la.deleted = false\n" +
            "    and la.loan_status in ( 'active', 'maturity_closure')\n" +
            "    and la2.allocated_to_user_id = :userId\n" +
            "    and (LOWER(concat_ws(' ', c.first_name, c.last_name)) like LOWER(concat('%', :searchKey,'%')) or LOWER(la.product) like LOWER(concat('%', :searchKey, '%')) or LOWER(la.loan_application_number) like LOWER(concat('%', :searchKey, '%')))\n" +
            "order by\n" +
            "    la.loan_application_id asc")
    List<Map<String,Object>> getTaskDetailsBySearchKey(@Param("userId") Long userId, String searchKey, Pageable pageRequest);

    @Query(nativeQuery = true, value = "select clm2.loan_id as loan_id from lms.loan_application la \n" +
            "             join lms.customer_loan_mapping clm on clm.loan_id = la.loan_application_id \n" +
            "             left join lms.customer_loan_mapping clm2 on clm2.customer_id = clm.customer_id \n" +
            "        \t where la.loan_application_id = :loanId and clm.customer_type = 'applicant'")
    List<Object> getLoanIdsByLoanId(@Param("loanId") Long loanId);


}
