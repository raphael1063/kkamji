package com.wonjin.android.Retrofit;

import com.wonjin.android.UploadObject;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RetroService {

    //닉네임 중복확인
    @FormUrlEncoded
    @POST("user/nickname_check.php")
    Call<ResponseBody> nicknameCheck(
            @Field(value = "user_nickname", encoded = true) String nickname);

    //이메일 중복확인
    @FormUrlEncoded
    @POST("user/email_check.php")
    Call<ResponseBody> emailCheck(
            @Field(value = "user_email", encoded = true) String user_email);

    // 회원가입시 유저정보 서버로 전송
    @FormUrlEncoded
    @POST("user/register.php")
    Call<ResponseBody> user_Info(
            @Field(value = "user_name", encoded = true) String name,
            @Field(value = "user_nickname", encoded = true) String nickname,
            @Field(value = "user_email", encoded = true) String user_email,
            @Field(value = "user_password", encoded = true) String password,
            @Field(value = "marketing", encoded = true) int marketing,
            @Field(value = "user_profile_image", encoded = true) String profile);

    // 회원가입시 프로필사진 서버로 전송
    @Multipart
    @POST("user/profile_image.php")
    Call<UploadObject> uploadFile(
            @Part MultipartBody.Part file,
            @Part("name") RequestBody name);

    // 로그인시 이메일,패스워드 서버로 전송
    @FormUrlEncoded
    @POST("user/login.php")
    Call<ResponseBody> login(
            @Field(value = "user_email", encoded = true) String user_email,
            @Field(value = "pw", encoded = true) String pw);

    // 회원가입시 유저정보 서버로 전송
    @FormUrlEncoded
    @POST("user/get_user_info.php")
    Call<ResponseBody> get_user_Info(
            @Field(value = "user_email", encoded = true) String user_email);

    //이름 변경
    @FormUrlEncoded
    @POST("user/edit_name.php")
    Call<ResponseBody> edit_name(
            @Field(value = "user_name", encoded = true) String name,
            @Field(value = "user_email", encoded = true) String user_email);

    //닉네임 변경
    @FormUrlEncoded
    @POST("user/edit_nickname.php")
    Call<ResponseBody> edit_nickname(
            @Field(value = "user_nickname", encoded = true) String nickname,
            @Field(value = "user_email", encoded = true) String user_email);

    //패스워드 변경
    @FormUrlEncoded
    @POST("user/edit_password.php")
    Call<ResponseBody> edit_password(
            @Field(value = "user_password", encoded = true) String password,
            @Field(value = "user_email", encoded = true) String user_email);


    //탈퇴
    @FormUrlEncoded
    @POST("user/withdrawal.php")
    Call<ResponseBody> withdrawal(
            @Field(value = "user_email", encoded = true) String user_email);

    // 단어장 추가
    @FormUrlEncoded
    @POST("vocabook/save_vocabook.php")
    Call<ResponseBody> save_vocabook(
            @Field(value = "user_email", encoded = true) String user_email,
            @Field(value = "vocabook_num", encoded = true) int vocabook_num,
            @Field(value = "vocabook_title", encoded = true) String vocabook_title,
            @Field(value = "vocabook_cover_image", encoded = true) String vocabook_cover_image);

    // 단어장 삭제
    @FormUrlEncoded
    @POST("vocabook/delete_vocabook.php")
    Call<ResponseBody> delete_vocabook(
            @Field(value = "user_email", encoded = true) String user_email,
            @Field(value = "vocabook_title", encoded = true) String vocabook_title,
            @Field(value = "image_route", encoded = true) String image_route);

    // 단어장 수정
    @FormUrlEncoded
    @POST("vocabook/edit_vocabook.php")
    Call<ResponseBody> edit_vocabook(
            @Field(value = "user_email", encoded = true) String user_email,
            @Field(value = "vocabook_num", encoded = true) int vocabook_num,
            @Field(value = "vocabook_title_original", encoded = true) String vocabook_title_original,
            @Field(value = "vocabook_title", encoded = true) String vocabook_title,
            @Field(value = "original_image_route", encoded = true) String image_route,
            @Field(value = "new_vocabook_cover_image", encoded = true) String new_vocabook_cover_image);

    // 단어장 인덱스 업데이트
    @FormUrlEncoded
    @POST("vocabook/update_vocabook_index.php")
    Call<ResponseBody> update_vocabook_index(
            @Field(value = "user_email", encoded = true) String user_email,
            @Field(value = "vocabook_title", encoded = true) String vocabook_title,
            @Field(value = "new_vocabook_num", encoded = true) int new_vocabook_num);

    // 단어장 커버 이미지 서버로 전송
    @Multipart
    @POST("vocabook/vocabook_cover_image.php")
    Call<UploadObject> upload_vocabook_cover_image(
            @Part MultipartBody.Part file,
            @Part("name") RequestBody name);

    //단어장 목록 불러오기
    @FormUrlEncoded
    @POST("vocabook/get_vocabook_list.php")
    Call<ResponseBody> get_vocabook_list(
            @Field(value = "user_email", encoded = true) String user_email);

    //단어장 이미지 가져오기
    @FormUrlEncoded
    @POST("vocabook/get_vocabook_cover_image.php")
    Call<ResponseBody> get_vocabook_cover_image(
            @Field(value = "user_email", encoded = true) String user_email,
            @Field(value = "vocabook_title", encoded = true) String vocabook_title);

    // 단어장에 포함된 단어개수가져오기
    @FormUrlEncoded
    @POST("vocabook/get_num_of_word_vocabook.php")
    Call<ResponseBody> get_num_of_word_vocabook(
            @Field(value = "user_email", encoded = true) String user_email,
            @Field(value = "vocabook_title", encoded = true) String vocabook_title);

    // 챕터 추가
    @FormUrlEncoded
    @POST("vocabook/save_chapter.php")
    Call<ResponseBody> save_chapter(
            @Field(value = "user_email", encoded = true) String user_email,
            @Field(value = "vocabook_title", encoded = true) String vocabook_title,
            @Field(value = "chapter_num", encoded = true) int chapter_num,
            @Field(value = "chapter_title", encoded = true) String chapter_title);

    // 챕터 내용 추가
    @FormUrlEncoded
    @POST("vocabook/save_chapter_content.php")
    Call<ResponseBody> save_chapter_content(
            @Field(value = "user_email", encoded = true) String user_email,
            @Field(value = "vocabook_title", encoded = true) String vocabook_title,
            @Field(value = "chapter_title", encoded = true) String chapter_title,
            @Field(value = "word", encoded = true) String word,
            @Field(value = "meaning1", encoded = true) String meaning1,
            @Field(value = "meaning2", encoded = true) String meaning2,
            @Field(value = "meaning3", encoded = true) String meaning3,
            @Field(value = "meaning4", encoded = true) String meaning4,
            @Field(value = "meaning5", encoded = true) String meaning5,
            @Field(value = "word_image_uri", encoded = true) String word_image_uri);

    // 챕터 가져오기
    @FormUrlEncoded
    @POST("vocabook/get_chapter_list.php")
    Call<ResponseBody> get_chapter_list(
            @Field(value = "user_email", encoded = true) String user_email,
            @Field(value = "vocabook_title", encoded = true) String vocabook_title);

    // 챕터 가져오기
    @FormUrlEncoded
    @POST("vocabook/get_all_chapter_list.php")
    Call<ResponseBody> get_all_chapter_list(
            @Field(value = "user_email", encoded = true) String user_email);

    // 챕터 인덱스 업데이트
    @FormUrlEncoded
    @POST("vocabook/update_chapter_index.php")
    Call<ResponseBody> update_chapter_index(
            @Field(value = "user_email", encoded = true) String user_email,
            @Field(value = "vocabook_title", encoded = true) String vocabook_title,
            @Field(value = "chapter_title", encoded = true) String chapter_title,
            @Field(value = "new_chapter_num", encoded = true) int new_chapter_num);

    // 챕터 내용물 가져오기
    @FormUrlEncoded
    @POST("vocabook/get_word_list.php")
    Call<ResponseBody> get_word_list(
            @Field(value = "user_email", encoded = true) String user_email,
            @Field(value = "vocabook_title", encoded = true) String vocabook_title,
            @Field(value = "chapter_title", encoded = true) String chapter_title);

    // 챕터 내용물 가져오기
    @FormUrlEncoded
    @POST("vocabook/get_num_of_word_chapter.php")
    Call<ResponseBody> get_num_of_word_chapter(
            @Field(value = "user_email", encoded = true) String user_email,
            @Field(value = "vocabook_title", encoded = true) String vocabook_title,
            @Field(value = "chapter_title", encoded = true) String chapter_title);

    // 단어연상 이미지 서버로 전송
    @Multipart
    @POST("vocabook/word_image.php")
    Call<UploadObject> upload_word_image(
            @Part MultipartBody.Part file,
            @Part("name") RequestBody name);

    // 챕터 수정
    @FormUrlEncoded
    @POST("vocabook/edit_chapter.php")
    Call<ResponseBody> edit_chapter(
            @Field(value = "user_email", encoded = true) String user_email,
            @Field(value = "vocabook_title", encoded = true) String vocabook_title,
            @Field(value = "chapter_title", encoded = true) String chapter_title,
            @Field(value = "word", encoded = true) String word,
            @Field(value = "meaning1", encoded = true) String meaning1,
            @Field(value = "meaning2", encoded = true) String meaning2,
            @Field(value = "meaning3", encoded = true) String meaning3,
            @Field(value = "meaning4", encoded = true) String meaning4,
            @Field(value = "meaning5", encoded = true) String meaning5,
            @Field(value = "word_image_file_name", encoded = true) String word_image_file_name,
            @Field(value = "new_word_image_uri", encoded = true) String new_word_image_uri);

    // 챕터 초기화
    @FormUrlEncoded
    @POST("vocabook/reset_chapter.php")
    Call<ResponseBody> reset_chapter(
            @Field(value = "user_email", encoded = true) String user_email,
            @Field(value = "vocabook_title", encoded = true) String vocabook_title,
            @Field(value = "chapter_num", encoded = true) int chapter_num,
            @Field(value = "chapter_title", encoded = true) String chapter_title);
    // 챕터 삭제
    @FormUrlEncoded
    @POST("vocabook/delete_chapter.php")
    Call<ResponseBody> delete_chapter(
            @Field(value = "user_email", encoded = true) String user_email,
            @Field(value = "vocabook_title", encoded = true) String vocabook_title,
            @Field(value = "chapter_num", encoded = true) int chapter_num,
            @Field(value = "chapter_title", encoded = true) String chapter_title);

    // 학습시간 업데이트
    @FormUrlEncoded
    @POST("vocabook/update_study_date.php")
    Call<ResponseBody> update_study_date(
            @Field(value = "user_email", encoded = true) String user_email,
            @Field(value = "vocabook_title", encoded = true) String vocabook_title,
            @Field(value = "chapter_title", encoded = true) String chapter_title,
            @Field(value = "recent_study_date", encoded = true) String recent_study_date);

    // 이미지로 프로필사진 가져오기
    @FormUrlEncoded
    @POST("game/get_user_image_by_nickname.php")
    Call<ResponseBody> get_user_image_by_nickname(
            @Field(value = "user_nickname", encoded = true) String nickname);
}
