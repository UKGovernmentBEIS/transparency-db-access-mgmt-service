package com.beis.subsidy.control.accessmanagementservice.utils;

public class AccessManagementConstant {
    public static final String AWARD_DELETED = "Deleted";
    public static String BEIS_ADMIN_ROLE="BEIS Administrator";
    public static String ADMIN_ROLES[]= {"BEIS Administrator","Granting Authority Administrator"};
    public static String All_ROLES[]= {"BEIS Administrator","Granting Authority Administrator",
            "Granting Authority Approver","Granting Authority Encoder"};
    public static String ROLES[]= {"BEIS Administrator","Granting Authority Administrator",
            "Granting Authority Approver"};
    public static String AAD_ROLE_NAMES[]={"BEISAdministrators",
            "GrantingAuthorityAdministrators","GrantingAuthorityApprovers",
            "GrantingAuthorityEncoders"};
    public static String GA_ADMIN_ROLE="Granting Authority Administrator";
    public static String GA_APPROVER_ROLE="Granting Authority Approver";
    public static String GA_ENCODER_ROLE="Granting Authority Encoder";
    public static String GA_ACTIVE_STATUS="Active";
    public static String GA_INACTIVE_STATUS ="Inactive";

    public static String SCHEME_ACTIVE ="Active";
    public static String SCHEME_INACTIVE ="Inactive";

    public static String AWARD_AWAITING_APPROVAL ="Awaiting approval";
    public static String AWARD_REJECTED ="Rejected";
    public static String AWARD_PUBLISHED_STATUS="Published";
    public static String AWARD_INACTIVE ="Inactive";

    public static int TOP_GA_TO_DISPLAY = 5;
    public static int TOP_SM_TO_DISPLAY = 5;
    public static int TOP_AWARD_TO_DISPLAY = 5;

}
