package com.example.mabaya.consts;

import java.util.List;

public class ValidationMsg {
    public ValidationMsg() {}

    private static final String EMPTY_FILED = "%s cannot be empty";
    public static String emptyFiled(String filedName){
        return String.format(EMPTY_FILED,filedName);
    }

    private static final String NOT_FOUND_IN_DB = "%s - not found in the DB";
    public static String notFoundInDb(String id){
        return String.format(NOT_FOUND_IN_DB,id);
    }
    public static String notFoundInDb(List<String> ids){
        return String.format(NOT_FOUND_IN_DB,String.join(",", ids));
    }
    public static String notFoundInDb(Long id){
        return String.format(NOT_FOUND_IN_DB,id);
    }

    private static final String CANNOT_DELETE_ATTACHED_ENTITY = "Cannot delete %s, attached to %s";
    public static String cannotDeleteAttachedEntity(Long id, List<String> ids){
        return String.format(CANNOT_DELETE_ATTACHED_ENTITY,id,String.join(",", ids));
    }
    public static String cannotDeleteAttachedEntity(Long id, String ids){
        return String.format(CANNOT_DELETE_ATTACHED_ENTITY,id,ids);
    }



}
