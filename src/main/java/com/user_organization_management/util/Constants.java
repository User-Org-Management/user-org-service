package com.user_organization_management.util;

import com.user_organization_management.service.OrganizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {
    public static final Logger logger = LoggerFactory.getLogger(OrganizationService.class);
    public static final String PASSWORD_CAN_NOT_BE_EMPTY = "Password cannot be empty!";
    public static final String ORG_NOT_FOUND = "Organization not found!";
    public static final String ORG_NAME_EXISTS = "Organization name already exists!";
    public static final String ORG_WITH_ID_NOT_FOUND = "Organization with ID %d not found!";
    public static final String USER_NOT_FOUND = "User with ID %d not found!";
    public static final String USER_WITH_EMAIL_NOT_FOUND = "User with email %s not found!";
    public static final String USER_EMAIL_EXISTS = "User Email already exists!";

}
