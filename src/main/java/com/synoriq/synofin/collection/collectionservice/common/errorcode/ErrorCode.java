package com.synoriq.synofin.collection.collectionservice.common.errorcode;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.Map;

@Getter
public enum ErrorCode {

    DEFAULT_ERROR_CODE(1015000),
    UNAUTHORIZED_API_TRANSACTION(1015002, "Sorry you are not authorized to perform this transaction. Please contact your administrator"),
    DATA_FETCH_ERROR(1017000, "An Exception occurred in fetching data"),
        DATA_SAVE_ERROR(1017001, "An Exception occurred in saving data"),
    NO_DATA_FOUND(1017002, "No Data Found"),
    RECEIPT_AMOUNT_IS_GREATER_THAN_LIMIT(1017003, "Your limit has been exhausted, please contact your IT team"),
    PER_DAY_CASH_COLLECTION_ERROR(1017005, "Per day cash collection limit exceeded"),
    INTERNAL_SERVER_ERROR(1017004, "Error while fetching PDF from LMS"),
    CASH_LIMIT_IS_LESS_THAN_UTILIZED_AMOUNT(1017006, "Total limit of cash cannot be less than utilized limit"),
    CHEQUE_LIMIT_IS_LESS_THAN_UTILIZED_AMOUNT(1017007, "Total limit of cheque cannot be less than utilized limit"),
    UPI_LIMIT_IS_LESS_THAN_UTILIZED_AMOUNT(1017008, "Total limit of upi cannot be less than utilized limit"),
    LIMIT_IS_LESS_THAN_UTILIZED_AMOUNT(1017009, "Total limit of cannot be less than utilized limit"),

//    CREDENTIALS_ERROR(1017002, "Incorrect username or password"),
//    LOGIN_ERROR(1017003, "An Exception occurred in login. Please try again"),
//    LOGIN_SUCCESS_CODE(1017004, "Login successfully"),
//    INVALID_FILE_FORMAT(1017005, "Invalid File Format"),
//    FILE_UPLOAD_SUCCESS(1017006, "File Upload Success"),
//    FILE_UPLOAD_FAILURE(1017007, "Exception Occurred in File Upload"),
//    NO_SUCH_FILE(1017008, "No Such File"),
//    PRODUCT_ID_INVALID(1007009, "Product Id Not Found or Invalid"),
//    INTEREST_RATE_INVALID(1007010, "Interest Rate Not Found or Invalid"),
//    SANCTION_AMOUNT_INVALID(1007011, "Sanction Amount Not Found or Invalid"),
//    DISBURSED_AMOUNT_INVALID(1007012, "Disbursed Amount Not Found or Invalid"),
//    PAID_AMOUNT_INVALID(1007013, "Paid Amount Not Found or Invalid"),
//    SANCTION_DATE_INVALID(1007014, "Sanction Date Not Found or Invalid"),
//    DISBURSED_DATE_INVALID(1007015, "Disbursed Date Not Found or Invalid"),
//    INTEREST_START_DATE_INVALID(1007016, "Interest Date Not Found or Invalid"),
//    REPAYMENT_START_DATE_INVALID(1007017, "Repayment Start Date Not Found or Invalid"),
//    SERVICE_REQUEST_TYPE_INVALID(1017018, "service_request_type_not_found_or_invalid"),
//    LOAN_ID_INVALID(1017019, "loan_id_not_found"),
//    USER_INVALID(1017020, "no_action_taken_on_service_request_as_the_same_user_cannot_act_upon_its_own_request"),
//    REMARKS_INVALID(1017021, "remarks_not_present"),
//    REPAYMENT_SCHEDULE_INVALID(1017022, "repayment_schedule_invalid_or_not_found"),
//    LIST_CANNOT_EMPTY(1017023, "INPUT LIST DATA CANNOT BE EMPTY"),
//    LIST_CANNOT_NULL(1017024, "INPUT LIST DATA CANNOT BE NULL"),
//    LOAN_APPLICATION_NUMBER_INVALID(1017025, "Invalid Loan Application Number"),
//    DATA_SAVE_OK(1017026, "Data Save Success"),
//    ALLOCATED_AMOUNT_INVALID(1017001, "Total amount is not equal to total allocated amount"),
//    ALLOCATED_AMOUNT_GREATER_THAN_PAYABLE_AMOUNT(1017001, "Allocated_amount_greater_than_payable_amount"),
//    TOTAL_PAYABLE_AMOUNT_GREATER_THAN_PAYMENT_AMOUNT(1017001, "Total_payable_amount_greater_than_payment_amount"),
//    RECEIPT_NUMBER_ALREADY_USED(1017001, "Receipt number is already used"),
//    TOTAL_PAYABLE_NOT_EQUAL_TO_TOTAL_RECEIVABLE(1017001, "Total payable amount is not equal to total receivable amount"),
//    LOAN_NOT_ACTIVE(1017032, "Loan is not active"),
//    TOTAL_PAYABLE_IS_LESS_THAN_TOTAL_DUES(1017033, "Total payable is less than total dues"),
//    NET_CLOSURE_IS_INVALID(1017034, "Net closure amount should not be greater than 0"),
//    WAIVED_AMOUNT_INVALID(1017035, "Waived amount should be less than or equal to the outstanding amount"),
//    WAIVED_AMOUNT_IS_INVALID(1017036, "Outstanding amount not to be negative"),
//    LOAN_IS_INACTIVE(1017037, "Loan is inactive"),
//    LOAN_IS_CANCELLED(1017038, "Loan is cancelled"),
//    LOAN_IS_FORECLOSED(1017039, "Loan is foreclosed"),
//    OUTSTANDING_AMOUNT_SMALL_THAN_ALLOCATED_AMOUNT(101749, "Allocated amount greater than outstanding amount"),
//    INSUFFICIENT_LIMIT_AMOUNT(101750, "Insufficient limit"),
//    INVALID_PAYMENT_REQUEST(101751, "Invalid payment request"),
//    ACCOUNT_NUMBER_NOT_EXIST(101752, "Account number not exist"),
//    GROSS_PER_PAY_AMOUNT_INVALID(101753, "Gross per pay amount should be less than or equals to excess amount"),
//    RECEIPT_DATE_SHOULD_BE_BEFORE_BUSINESS_DATE(101754, "Receipt date can be on or before business date"),
//    RECEIPT_DATE_INVALID(101754, "Receipt date should be of current month"),
//    INSTALMENT_AMOUNT_INVALID(101755, "Instalment amount should be more than the interest amount"),
//    RECEIPT_CANNOT_CANCEL(101756, "Cannot cancel the receipt"),
//    INVALID_DATE(101756, "Please select valid date"),
//    INVALID_PART_PAYMENT_DATE(101756, "Effective date should be less than Next due date and greater last due date"),
//    INVALID_PAYMENT_DATE(101757, "Payment date  cannot be future"),
//    PAYMENT_DATE_ERROR(101758, "Payment date should be current date"),
//    DEBIT_FREEZE_ERROR(101759, "Debit has frozen"),
//    TOTAL_FREEZE_ERROR(101760, "Can't make receipt due to total freeze"),
//    LIEN_MARKING_ERROR(101761, "Limit has frozen"),
//    INVALID_LIEN_AMOUNT(101762, "Lien amount should be less than or equal to drawing limit"),
//    AMOUNT_RANGE_INVALID(101763, "Invalid Amount range, Check Minimum and Maximum amount"),
//    FULL_DISBURSED_PRINCIPAL_RECOVERY_ERROR(101763, "Principal recovery should be True in the case of fully disbursed case"),
//    PARTIALLY_DISBURSED_PRINCIPAL_RECOVERY_ERROR(101764, " start principle recovery not found"),
//    TRANCHE_DISBURSED_AMOUNT_ERROR(101765, "Disbursed amount should not be greater than loan Amount"),
//    INVALID_INSTALLMENT_DATE_CHANGE(101764, "Effective date should be less than next due date"),
//    ALREADY_DISBURSED(101764, "Loan is fully disbursed"),
//    EOD_RUNNING(101769, "EOD is RUNNING...CANNOT PROCEED LOAN ACTION"),
//    LOAN_CANCELLATION(101770, "Loan Cancellation can not be performed. Kindly check pending dues for the corresponding Loan Id."),
//    SET_PRINCIPLE_RECOVERY(101771, "Kindly set Principle Recovery as Yes."),
//    DISBURSAL_AMOUNT_ERROR(101772, "Enter the disbursal breakup correctly"),
//    INVALID_FREEZE_REQUEST(101771, "Loan is not freeze"),
//    PARTIALLY_DISBURSED_NOT_ALLOWED(101773, "Partial Disbursement not allowed"),
//    ALREADY_INITIATED_REQUEST(101774, "Please approve or reject already initiated requests"),
//    DISBURSAL_STATUS_PARTIAL(101775, "Please check disbursal status."),
//    FIFO_REQUEST_APPROVE(101775, "Cannot approve this request"),
//    RECEIVABLE_AMOUNT_INVALID(101776, "Not sufficient available limit"),
//    AMOUNT_GREATER_THAN_ZERO(101779, "Entered Amount must be greater than Zero."),
//    CANNOT_FREEZE_DUE_TO_PENDING_UNREALIZED_AMOUNT(101777, "Cannot freeze due to unrealized amount"),
//    RECEIPT_AMOUNT_INVALID(101778, "Receipt amount must be less than total outstanding"),
//    SERVICE_REQUEST_INVALID(101777, "Previous request for is not approved for this loan"),
//    WAIVER_AMOUNT_INVALID(101780, "Enter Valid Waiver Amount."),
//    MATURITY_CLOSURE_PAID_REPAYMENT_ERROR(101781, "Cannot Perform Maturity Closure, all EMIs are not paid"),
//    BALANCE_PRINCIPAL_GREATER_THAN_ZERO(101782, "Balance principal greater than 0, cannot perform loan action"),
//    LOAN_ALREADY_EXISTS(101783, "LOAN Already Exist in System. Cannot create new loan"),
//    APPLICANT_CUSTOMER_ID_INVALID(101784, "Applicant CustomerId Not Generated, rolling back changes"),
//    KEY_LOAN_ID_NOT_EXISTS(101785, "Exception Occurred in creating loanAccount, rolling back changes"),
//    RECEIVABLE_DUE_DATE_INVALID(101786, "Date must be before business date and after previous month due date"),
//    INSTALMENT_AMOUNT_GRATER_THEN_BALANCE_PRINCIPAL(101787, "Installment amount cannot be more than balance principal"),
//    INVALID_FORCLOSURE(101784, "Cannot proceed for Forclosure, if Loan is not fully Disbursed."),
//    REQUEST_ALREADY(101785, "Request already exsist."),
//    PRINCIPAL_RECOVERY(101785, "Principal recovery should be yes for curtail case."),
//    NACH_UPDATE_ERROR(101788, "UMRN Number does not exist"),
//    NACH_STATUS_INVALID(101789, "NACH status is invalid"),
//    PAYABLE_AMOUNT_INVALID(101790, "Total amount cannot exceed utilized limit"),
//    PAYABLE_AMOUNT_INVALID_DUE_TO_PREVIOUS_REQUEST(101791, "Check previous request. Total amount (including previous request amount) cannot exceed utilized limit"),
//    RECEIVABLE_AMOUNT_INVALID_DUE_TO_PREVIOUS_REQUEST(101792, "Check previous request. Total amount (including previous request amount and unrealized amount) cannot exceed remaining limit"),
//    PAYABLE_DATE_INVALID(101793, "Date must be business date only"),
//    INVALID_DEBIT_FREEZE_REQUEST(101794, "Loan is already debit freeze "),
//    INVALID_TOTAL_FREEZE_REQUEST(101795, "Loan is already total freeze "),
//    INVALID_LIEN_MARK_REQUEST(101796, "Loan is already lien marked"),
//    INVALID_FREEZE_DATE(101797, "Date cannot be before current date"),
//    INITIATED_FREEZE_REQUEST(101797, "Already freeze request is initiated"),
//    INITIATED_UNFREEZE_REQUEST(101798, "Already unfreeze request is initiated "),
//    INVALID_UNFREEZE_DATE(101799, "Date cannot be before current date"),
//    CURTAIL_DISBURSAL_STATUS(101800, "Loan should be fully disbursed"),
//    INTEREST_TILL_DATE_NOT_FUTURE_DATE(101804, "Interest till date should not be greater then business date"),
//    LOAN_BLOCKED_FOR_DOCUMENTS(101803, "Loan doesn't have permission to download this document"),
//    ADHOC_RECEIVABLE_DATE_INVALID_DUE_ACCRUAL(101801, "Receivable due date is invalid due to previous dates utilization"),
//    DUE_DATE_CANNOT_BEFORE_INTEREST_START_DATE(101802, "Due date cannot be before interest start date"),
//    INTEREST_TILL_DATE_NOT_LESS_THEN_CLOSURE_DATE(101805, "Interest till date should not be less then closure date"),
//    CLOSURE_DATE_NOT_LESS_THEN_LAST_EMI_DATE(101806, "Closure date should not be less then last emi date"),
//    INTEREST_TILL_DATE_SHOULD_NOT_BE_GREATER_THEN_MATUEIRTY_DATE(101807, "Interest till date should not be greater then maturity date"),
//    BUSINESS_DATE_IS_LESS_THEN_NEXT_EMI_DATE(101808, "Cannot initiate request before next EMI date"),
//    ADJUSTED_AMOUNT_SHOULD_BE_LESS_THAN_OR_EQUAL_TO_GROSS_PRE_PAY_AMOUNT(101803, "Adjusted amount should be less than gross pre pay amount"),
//    TRANCHE_APPROVAL_DATE_AND_REQUEST_DATE_SHOULD_BE_SAME(101804, "Tranche approval date and request date should be same."),
//    PAYMENT_DATE_LESS_DISBURSAL_DATE(101808, "Payment Date cannot be less than Disbursal Date."),
//    DEBIT_FREEZE_REQUEST_ERROR(101805, "Cannot raise the debit freeze and lien marking request as Payment request is not yet approved or reject"),
//    TOTAL_FREEZE_REQUEST_ERROR(101806, "Cannot raise the request for total freeze as payment and receipt request is not approved or reject"),
    REQUESTED_PARAM_CANNOT_BE_NULL(101809, "Requested parameter cannot be null or blank"),
//    SCHEME_CODE_INVALID(100807, "Scheme Code Not Found or Invalid"),
//    MARITAL_STATUS(100808, "Marital Status Not Found or Invalid"),
//    REPAYMENT_ALREADY_BILLED(101809, " Tranche cannot be cancelled after installment has been billed"),
//    LIFO_TRANCHE_ERROR(101810, "Latest Tranche Must Be Cancelled First"),
//    TWO_PART_PAYMENT_CANNOT_INITIALIZE_IN_SAME_MONTH(101811, "2 pre part payment can not be allowed in a month"),
//    INVALID_PRINCIPAL_RECOVERY_PARTIAL_CASE(101815, "Principal Recovery and Extend Tenure cannot be true, incase of Partially Disbursed Loan."),
//    PART_PAYMENT_DATE_ERROR(101817, "You cannot allocate the fund which is due on or after the effective date"),
//    BULKUPLOAD_ALREADY_INPROCESS(101816, "Another Bulk Upload already in process. Kindly try after sometime."),
//    AMOUNT_ALLOCATED_IS_INVALID(101817, "Amount allocated is incorrect. Kindly check again "),
//    CANCELLATION_CLOSURE_REQUEST_EXISTS(101818, "Loan Cancellation or For-Closure request exists in system, Please take action before creating  this request"),
//    AMOUNT_ALLOCATED_IS_INVALID_FOR_INSTALLMENT_CHARGE(101819, "Installment and Charges are already allocated in another request, Please take action on previous request "),
//    AMOUNT_ALLOCATED_IS_INVALID_FOR_INSTALLMENT(101820, "Installments are already allocated in another request, Please take action on previous request"),
//    AMOUNT_ALLOCATED_IS_INVALID_FOR_CHARGE(101821, "Charges are already allocated in another request, Please take action on previous request"),
//    PAYMENT_RECEIPT_VALIDATION_ERROR(101822, "Cannot create new request, please take action on the previous request."),
//    WAIVED_AMOUNT_ERROR(101823, "Waived amount should be less than pending amount"),
//    LOAN_CHARGES_MISSING(101824, "Loan charges not present in the request"),
//    INTERNAL_SERVER_ERROR(1016000, "Internal Server Error"),
//    ATLEAST_30_DAYS_GAP_BETWEEN_INTEREST_START_DATE_AND_REPAYMENT_START_DATE(101825, "There should be atleast 30 days gap between Interest Start Date and Repayment Start Date"),
//    INVALID_RECEIPT_AMOUNT(1016001, "Invalid Receipt Amount"),
    INVALID_RECEIPT_DATE(1016002, "Receipt date cannot be before disbursal date"),
//    RECEIPT_AMOUNT_ALREADY_ALLOCATED(1016003, "Receipt amount is already allocated"),
//    ALLOCATED_AMOUNT_MORE_THEN_TOTAL_OUTSTANDING_AMOUNT(1016004, "Allocated amount can't be more than net outstanding amount"),
//    WAIVED_AMOUNT_MORE_THEN_TOTAL_OUTSTANDING_AMOUNT(1016005, "Waived amount can't be more than net outstanding amount"),
//    NPA_ACCOUNT_ERROR(1016006, "Can't Process Payment Account is NPA"),
//    PAYMENT_DATE_SHOULD_BE_BEFORE_BUSINESS_DATE(1016007, "Payment date can be on or before business date"),
//    PLEASE_REALIZED_ALL_THE_RECEIPTS_BEFORE_FORECLOSURE(1016008, "Please Realize all the receipts before foreclosure"),
//    PLEASE_SELECT_THE_CHARGE(1016009, "Please select the charge(s)"),
//    INVALID_PHONE_NUMBER(1016010, "Invalid Phone Number"),
//    INVALID_TENURE_NUMBER(1016011, "Invalid Loan Tenure Number"),
//    INVALID_DISBURSAL_DATE(1016012, "Invalid Disbursal Date"),
//    INVALID_AADHAR_NUMBER(1016013, "Invalid Aadhar Number"),
//    INVALID_VOTER_NUMBER(1016014, "Invalid Voter Number"),
//    INVALID_CKYC_NUMBER(1016015, "Invalid CKYC Number"),
//    INVALID_VEHICLE_NUMBER(1016016, "Invalid Vehicle Number"),
//    INVALID_REQUEST_ID_NUMBER(1016017, "Invalid Request Id"),
//    INVALID_PAN_NUMBER(1016018, "Invalid PAN Number"),
//    INVALID_DISBURSAL_STATUS(1016019, "Invalid Disbursal Status"),
//    SELECT_DEPOSIT_RECEIPT(1016009, "Please Select Deposit Receipt"),
//    LINKED_LOAN_ALREADY_EXIST(1016020, "Linked loan already exist"),
//    INVALID_RECEIPT_REJECT_REQUEST(1016021, "Please act on already initiated deposit request first"),
//    INVALID_ACTION_DATE_DUE_TO_BUSINESS_DATE(1016022, "%s date should not be greater than business date"),
//    INVALID_ACTION_DATE_DUE_TO_REPO_DATE(1016023, "%s date should not be less than repo date"),
//    INVALID_RECEIPT_ACTION(1016024, "Action is already taken on this receipt"),
    RECORD_NOT_FOUND(1016025, "Requested record not found in the database"),
    NO_ACTIVITY_DEVICE(1016026, "No activity since long time!, Please contact IT support for your device activation"),
    DEVICE_ALREADY_ACTIVE(1016027, "You are already active with one device!, Please contact IT support for new device registration"),
    CHECK_THE_REQUEST_BODY(1016028, "Please check the values passed from your end!"),
    DONT_HAVE_PERMISSION_TO_ACTION(1016029, "You Don't Have Permission to Action on this transfer"),
    ALREADY_HAD_ACTIONED_ON_THIS_TRANSFER(1016030, "This transfer is already been actioned"),
    RECEIVER_HAVE_EXCEED_THEIR_LIMIT(1016031, "Cannot process the request, because receiver limit exceeded"),
    STATUS_NOT_MATCHED(1016032, "Request status is not matched"),
    LMS_CREATE_RECEIPT_ERROR(1016033, "LMS create receipt is getting null response"),
    REGEX_NOT_MATCHING(1016034, "Input is not matched with the REGEX"),
    DATA_NOT_FOUND_IN_LMS(1016035, "Loan Number is not found in LMS"),
    TRANSFER_TO_USER_ID(1016036, "Transfer to user id is getting null against this entry"),
    LIMIT_CHECK_ON_APPROVAL(1016037, "Cannot process the request as your collection limit is exceeded"),
    RECEIPT_TIME_BOUND_LIMIT(1016038, "Receipt collection time bound on same loan, try after 10 min"),
    TRANSACTION_NUMBER_VALIDATE(1016039, "UTR number already exists"),
    GETTING_ERROR_WHILE_FETCHING_LMS_DATA(1016040, "Data fetching error from LMS"),
    USER_IS_NOT_EXIST(1016041, "User is not exist"),
    DEPOSIT_CHALLAN_ERROR(1016042, "Deposit challan error"),
    PER_MONTH_CASH_COLLECTION_ERROR(1016043, "Per month cash collection limit exceeded"),
    UTR_NUMBER_ALREADY_EXIST_IN_DIGITAL_PAYMENT_TRANSACTION(1016044, "UTR number already exist"),
    TRANSACTION_IS_ALREADY_GOT_SUCCESSFUL(1016045, "The transaction has been already successfully updated"),
    REPOSSESSION_ALREADY_INITIATED(1016046, "Repossession already initiated for this loan"),
    EMPLOYEE_MOBILE_NUMBER_VALIDATION(1016047, "Mobile number is already registered with our employee database"),
    FOLLOWUP_CLOSE_DATE_VALIDATION(1016048, "Can't Close the followup as the receipt is of previous month"),
    RECEIPT_ID_NOT_FOUND(1016049, "Receipt id not there for this loan Id"),
    RECEIPT_TRANSFERRED_ALREADY(1016050, "Receipt already has been transferred"),
    FOLLOWUP_CLOSED_RECEIPT_ID(1016051, "FollowUp was already closed using this Receipt Id"),
    QR_CODE_DATA_NULL(1016052, "Getting Error from bank side while generating QR Code"),
    RECEIPT_AMOUNT_IS_GREATER_THAN_LIMIT_QR_CODE(1016053, "Yo do not have such limit to generate the QR code, contact with IT team"),
    UNABLE_TO_CREATE_FOLLOW(1016054, "Follow up already exists on this loan in pending stage"),
    S3_UPLOAD_DATA_ERROR(1016055, "Image isn't uploaded on S3, Kindly contact to admin team"),
    IMAGE_TYPE_UNSUPPORTED(1016056, "Image format not supported, Allowed format: jpeg, png, jpg"),
    TRANSACTION_STATUS_CHECK(1016057, "Transaction Status Failed, Because of Data is Null"),
    MULTIPLE_HIT_VALIDATION(1016058, "Check status will be available at"),
    USER_DETAIL_NULL(1016059, "User details getting null"),
    EMITRA_CONNECTOR_EXCEPTION(1016060, "Unable to get the correct checksum"),
    RECEIPT_ALREADY_APPROVED(1016061, "Unable to transfer this Receipt")
    ;
    private Integer codeValue;
    @Setter
    private String responseMessage;

    ErrorCode(int codeValue) {
        this.codeValue = codeValue;
        this.responseMessage = "Something went wrong. Please try again after some time.";
    }

    ErrorCode(int codeValue, String message) {
        this.codeValue = codeValue;
        this.responseMessage = message;
    }

    @Override
    public String toString() {
        String var10000 = this.name();
        return var10000 + "(" + this.getCodeValue() + ")";
    }

    private static final Map<Integer, ErrorCode> integerErrorCodeMap;

    static {
        integerErrorCodeMap = Maps.uniqueIndex(Arrays.asList(ErrorCode.values()), ErrorCode::getErrorCodeValue);
    }


    public static ErrorCode getErrorCode(Integer codeValue) throws IllegalArgumentException {
        return integerErrorCodeMap.get(codeValue);
    }

    public static ErrorCode getErrorCode(Integer codeValue, String message) throws IllegalArgumentException {
        ErrorCode errorMessage = integerErrorCodeMap.get(codeValue);
        errorMessage.setResponseMessage(message);
        return errorMessage;
    }

    public Integer getErrorCodeValue() {
        return this.codeValue;
    }

}
