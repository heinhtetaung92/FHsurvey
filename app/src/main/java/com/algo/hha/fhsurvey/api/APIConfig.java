package com.algo.hha.fhsurvey.api;

/**
 * Created by heinhtetaung on 4/14/15.
 */
public class APIConfig {

    //public static final String BASE_URL = "http://fhsurvey.osakaohshomyanmar.com";
    public static final String BASE_URL = "http://fhsurvey.futurehubmyanmar.com";
    public static final String FILE_UPLOAD = "/api/Project/UploadMultipleFiles2";
    public static final String FORMS_BY_ID_URL = "/api/Project/GetQuestionByFormID/{form_id}";
    public static final String FORMS_LIST_BY_PROJECTID_URL = "/api/Project/GetFormByProjectID/{proj_id}";
    public static final String PROJECTS_URL = "/api/Project/GetAllProject";
    public static final String PROJECT_BY_USERID = "/api/Project/GetProjectByUserID/{user_id}";
    public static final String REGISTER_TO_SERVER = "/api/Project/PostRegisterUser";
    public static final String SIGN_IN = "/api/Project/GetCheckLogIn/{userid}/{password}";
    public static final String SINGLE_FILE_UPLOAD = "/api/Project/UploadSingleFile";

}
