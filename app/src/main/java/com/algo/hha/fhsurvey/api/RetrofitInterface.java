// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.algo.hha.fhsurvey.api;


import com.squareup.okhttp.Call;

import java.util.Map;

import retrofit.Callback;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.PartMap;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

public interface RetrofitInterface
{

    @GET(APIConfig.PROJECTS_URL)
    public void getProjectsByURL(Callback<String> callback);

    @GET(APIConfig.FORMS_LIST_BY_PROJECTID_URL)
    public void getFormListByProjectID(@Path("proj_id") String proj_id, Callback<String> callback);

    @GET(APIConfig.FORMS_BY_ID_URL)
    public void getFormDataByFormID(@Path("form_id") String form_id, Callback<String> callback);



    @GET(APIConfig.SIGN_IN)
    public void signIn(@Path("userid") String userID, @Path("password") String pwd, Callback<String> callback);

    @FormUrlEncoded
    @POST(APIConfig.REGISTER_TO_SERVER)
    public void registerToServer(@Field("name") String name, @Field("contact_number") String contactno, @Field("email") String email, @Field("password") String pwd, @Field("password_confirmation") String pwd_conf, Callback<String> callback);

    @Multipart
    @POST(APIConfig.FILE_UPLOAD)
    public void uploadFilesToServer(@Part("user_id") TypedString user_id,
                                    @PartMap Map<String,TypedFile> Files, Callback<String> callback);

    @Multipart
    @POST(APIConfig.SINGLE_FILE_UPLOAD)
    public void uploadSingleFileToServer(@Part("fileUpload1") TypedFile typefile, Callback<String> callback);

}


