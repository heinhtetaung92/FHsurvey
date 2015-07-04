package com.algo.hha.fhsurvey.api;

/**
 * Created by heinhtetaung on 4/14/15.
 */
public class APIConfig {

    public static final String BASE_URL = "http://fhsurvey.futurehubmyanmar.com";

    public static final String PROJECTS_URL = "/api/Project/GetAllProject";

    public static final String FORMS_LIST_BY_PROJECTID_URL = "/api/Project/GetFormByProjectID/{proj_id}";

    public static final String FORMS_BY_ID_URL = "/api/Project/GetQuestionByFormID/{form_id}";

}
