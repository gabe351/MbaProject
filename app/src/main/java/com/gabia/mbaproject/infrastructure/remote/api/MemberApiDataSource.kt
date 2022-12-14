package com.gabia.mbaproject.infrastructure.remote.api

import com.gabia.mbaproject.model.CreateMemberRequest
import com.gabia.mbaproject.model.Member
import com.gabia.mbaproject.model.MemberRequest
import com.gabia.mbaproject.model.PresenceResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface MemberApiDataSource {

    @POST("api/user/signup")
    suspend fun create(@Body member: CreateMemberRequest): Response<Member>

    @GET("api/user")
    suspend fun getAll(): Response<List<Member>>

    @GET("api/user/{id}")
    suspend fun getById(@Path("id") id: Int): Response<Member>

    @PUT("api/user/{id}")
    suspend fun edit(@Path("id") id: Int, @Body member: MemberRequest): Response<Member>

    @DELETE("api/user/{id}")
    suspend fun delete(@Path("id") id: Int)

    @GET("api/user/relatedRehearsal/{rehearsalId}")
    suspend fun getRelatedWithRehearsal(@Path("rehearsalId") rehearsalId: Int): Response<List<PresenceResponse>>

    @GET("api/user/absentRehearsal/{rehearsalId}")
    suspend fun getUnrelatedWithRehearsal(@Path("rehearsalId") rehearsalId: Int): Response<List<Member>>
}