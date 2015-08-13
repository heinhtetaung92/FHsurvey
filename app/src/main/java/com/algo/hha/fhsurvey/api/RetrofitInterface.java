// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.algo.hha.fhsurvey.api;


import java.util.Map;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.PartMap;
import retrofit.http.Path;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

public interface RetrofitInterface
{

    @GET(APIConfig.PROJECTS_URL)
    public void getProjectsByURL(Callback<String> callback);

    @GET(APIConfig.PROJECT_BY_USERID)
    public void getProjectByUserID(@Path("user_id") String user_id, Callback<String> callback);

    @GET(APIConfig.FORMS_LIST_BY_PROJECTID_URL)
    public void getFormListByProjectID(@Path("proj_id") String proj_id, Callback<String> callback);

    @GET(APIConfig.FORMS_BY_ID_URL)
    public void getFormDataByFormID(@Path("form_id") String form_id, Callback<String> callback);



    @GET(APIConfig.SIGN_IN)
    public void signIn(@Path("userid") String userID, @Path("password") String pwd, Callback<String> callback);

    @FormUrlEncoded
    @POST(APIConfig.REGISTER_TO_SERVER)
    public void registerToServer(@Field("UserName") String username,
                                 @Field("Password") String password,
                                 @Field("Image") String image,
                                 @Field("DateOfBirth") String dateofBirth,
                                 @Field("Organization") String organization,
                                 @Field("JobTitle") String job_title,
                                 @Field("Address") String address
                                , Callback<String> callback);


    @Multipart
    @POST(APIConfig.FILE_UPLOAD)
    public void uploadFilesToServer(@Part("user_id") TypedString user_id,
                                    @PartMap Map<String,TypedFile> Files, Callback<String> callback);

    @Multipart
    @POST(APIConfig.SINGLE_FILE_UPLOAD)
    public void uploadSingleFileToServer(@Part("fileUpload1") TypedFile typefile, Callback<String> callback);



    //public abstract void registerToServer(String s, String s1, String s2, String s3, String s4, String s5, String s6,
                                          //Callback callback);


}


