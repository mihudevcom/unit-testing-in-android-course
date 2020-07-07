package com.techyourchance.testdrivendevelopment.exercise6;

import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise6.networking.NetworkErrorException;
import com.techyourchance.testdrivendevelopment.exercise6.users.User;
import com.techyourchance.testdrivendevelopment.exercise6.users.UsersCache;

public class FetchUserUseCaseSyncImpl implements FetchUserUseCaseSync {

    private FetchUserHttpEndpointSync mFetchUserHttpEndpointSync;
    private UsersCache mUsersCache;

    public FetchUserUseCaseSyncImpl(FetchUserHttpEndpointSync mFetchUserHttpEndpointSync, UsersCache mUsersCache) {
        this.mFetchUserHttpEndpointSync = mFetchUserHttpEndpointSync;
        this.mUsersCache = mUsersCache;
    }

    @Override
    public UseCaseResult fetchUserSync(String userId) {

        FetchUserHttpEndpointSync.EndpointResult endpointResult;

        if (mUsersCache.getUser(userId) != null) {
            return new UseCaseResult(Status.SUCCESS, mUsersCache.getUser(userId));
        }

        try {
            endpointResult = mFetchUserHttpEndpointSync.fetchUserSync(userId);
        } catch (NetworkErrorException e) {
            return new UseCaseResult(Status.NETWORK_ERROR, null);
        }

        switch (endpointResult.getStatus()) {
            case SUCCESS:
                mUsersCache.cacheUser(new User(endpointResult.getUserId(), endpointResult.getUsername()));
                return new UseCaseResult(Status.SUCCESS, new User(endpointResult.getUserId(), endpointResult.getUsername()));
            case AUTH_ERROR:
            case GENERAL_ERROR:
                return new UseCaseResult(Status.FAILURE, null);
            default:
                throw new RuntimeException("Invalid response: " + endpointResult);
        }
    }
}
