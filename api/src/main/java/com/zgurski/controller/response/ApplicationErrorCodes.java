package com.zgurski.controller.response;

public enum ApplicationErrorCodes {

    ROLLBACK_TRANSACTION_SUCCESS(20401),

    /* 400 */
    INVALID_INPUT_VALUE(40001),
    INVALID_VALUE_WHILE_PARSING(40002),

    ENTITY_NOT_UPDATED(40003),

    /* 403 */
    INCORRECT_ENTITY_OWNER(40301),

    /* 404 */
    ENTITY_NOT_FOUND(40401),

    /* 424 */
    EMAIL_NOT_SENT(42401),

    /* 500 */
    FATAL_ERROR(50001);


//    SQL_ERROR(10),
//    INVALID_INPUT_FORMAT_ERROR(20),

//    ENTITY_NOT_FOUND(40),
//    ENTITY_NOT_CREATED(50),


    private int codeId;

    public int getCodeId() {
        return codeId;
    }

    ApplicationErrorCodes(int codeId) {
        this.codeId = codeId;
    }
}
