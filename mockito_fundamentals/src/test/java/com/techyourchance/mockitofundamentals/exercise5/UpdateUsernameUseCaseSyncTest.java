package com.techyourchance.mockitofundamentals.exercise5;

import com.techyourchance.mockitofundamentals.exercise5.eventbus.EventBusPoster;
import com.techyourchance.mockitofundamentals.exercise5.eventbus.UserDetailsChangedEvent;
import com.techyourchance.mockitofundamentals.exercise5.networking.NetworkErrorException;
import com.techyourchance.mockitofundamentals.exercise5.networking.UpdateUsernameHttpEndpointSync;
import com.techyourchance.mockitofundamentals.exercise5.users.User;
import com.techyourchance.mockitofundamentals.exercise5.users.UsersCache;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class UpdateUsernameUseCaseSyncTest {

    private static final String ID = "id";
    private static final String USERNAME = "username";
    UpdateUsernameUseCaseSync SUT;
    UpdateUsernameHttpEndpointSync updateUsernameHttpEndpointSync;
    UsersCache usersCache;
    EventBusPoster eventBusPoster;

    @Before
    public void setUp() throws Exception {
        updateUsernameHttpEndpointSync = Mockito.mock(UpdateUsernameHttpEndpointSync.class);
        usersCache = Mockito.mock(UsersCache.class);
        eventBusPoster = Mockito.mock(EventBusPoster.class);
        SUT = new UpdateUsernameUseCaseSync(updateUsernameHttpEndpointSync, usersCache, eventBusPoster);
        success();
    }

    @Test
    public void updateUsernameSync_success_idAndUsernamePassedToEndpoint() throws Exception {
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        SUT.updateUsernameSync(ID, USERNAME);
        verify(updateUsernameHttpEndpointSync).updateUsername(argumentCaptor.capture(), argumentCaptor.capture());
        List<String> captures = argumentCaptor.getAllValues();
        assertThat(captures.get(0), is(ID));
        assertThat(captures.get(1), is(USERNAME));
    }

    @Test
    public void updateUsernameSync_success_userCached() {
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        SUT.updateUsernameSync(ID, USERNAME);
        verify(usersCache).cacheUser(captor.capture());
        User user = captor.getValue();
        assertThat(user.getUserId(), is(ID));
        assertThat(user.getUsername(), is(USERNAME));
    }

    @Test
    public void updateUsernameSync_success_loggedInEventPosted() {
        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        SUT.updateUsernameSync(ID, USERNAME);
        verify(eventBusPoster).postEvent(captor.capture());
        assertThat(captor.getValue(), is(instanceOf(UserDetailsChangedEvent.class)));
    }

    @Test
    public void updateUsernameSync_success_successReturned() {
        UpdateUsernameUseCaseSync.UseCaseResult result = SUT.updateUsernameSync(ID, USERNAME);
        assertThat(result, is(UpdateUsernameUseCaseSync.UseCaseResult.SUCCESS));
    }

    @Test
    public void updateUsernameSync_networkError_userNotCached() throws NetworkErrorException {
        networkError();
        SUT.updateUsernameSync(ID, USERNAME);
        verifyNoMoreInteractions(usersCache);
    }

    @Test
    public void updateUsernameSync_networkError_noEventPosted() throws NetworkErrorException {
        networkError();
        SUT.updateUsernameSync(ID, USERNAME);
        verifyNoMoreInteractions(eventBusPoster);
    }

    @Test
    public void updateUsernameSync_networkError_networkErrorReturned() throws NetworkErrorException {
        networkError();
        UpdateUsernameUseCaseSync.UseCaseResult result = SUT.updateUsernameSync(ID, USERNAME);
        assertThat(result, is(UpdateUsernameUseCaseSync.UseCaseResult.NETWORK_ERROR));
    }

    @Test
    public void updateUsernameSync_generalError_userNotCached() throws NetworkErrorException {
        generalError();
        SUT.updateUsernameSync(ID, USERNAME);
        verifyNoMoreInteractions(usersCache);
    }

    @Test
    public void updateUsernameSync_generalError_noEventPosted() throws NetworkErrorException {
        generalError();
        SUT.updateUsernameSync(ID, USERNAME);
        verifyNoMoreInteractions(eventBusPoster);
    }

    @Test
    public void updateUsernameSync_generalError_failureReturned() throws NetworkErrorException {
        generalError();
        UpdateUsernameUseCaseSync.UseCaseResult result = SUT.updateUsernameSync(ID, USERNAME);
        assertThat(result, is(UpdateUsernameUseCaseSync.UseCaseResult.FAILURE));
    }

    @Test
    public void updateUsernameSync_authError_userNotCached() throws NetworkErrorException {
        authError();
        SUT.updateUsernameSync(ID, USERNAME);
        verifyNoMoreInteractions(usersCache);
    }

    @Test
    public void updateUsernameSync_authError_noEventPosted() throws NetworkErrorException {
        authError();
        SUT.updateUsernameSync(ID, USERNAME);
        verifyNoMoreInteractions(eventBusPoster);
    }

    @Test
    public void updateUsernameSync_authError_failureReturned() throws NetworkErrorException {
        authError();
        UpdateUsernameUseCaseSync.UseCaseResult result = SUT.updateUsernameSync(ID, USERNAME);
        assertThat(result, is(UpdateUsernameUseCaseSync.UseCaseResult.FAILURE));
    }

    @Test
    public void updateUsernameSync_serverError_userNotCached() throws NetworkErrorException {
        serverError();
        SUT.updateUsernameSync(ID, USERNAME);
        verifyNoMoreInteractions(usersCache);
    }

    @Test
    public void updateUsernameSync_serverError_noEventPosted() throws NetworkErrorException {
        serverError();
        SUT.updateUsernameSync(ID, USERNAME);
        verifyNoMoreInteractions(eventBusPoster);
    }

    @Test
    public void updateUsernameSync_serverError_failureReturned() throws NetworkErrorException {
        serverError();
        UpdateUsernameUseCaseSync.UseCaseResult result = SUT.updateUsernameSync(ID, USERNAME);
        assertThat(result, is(UpdateUsernameUseCaseSync.UseCaseResult.FAILURE));
    }

    private void success() throws NetworkErrorException {
        when(updateUsernameHttpEndpointSync.updateUsername(any(String.class), any(String.class)))
                .thenReturn(new UpdateUsernameHttpEndpointSync.EndpointResult(UpdateUsernameHttpEndpointSync.EndpointResultStatus.SUCCESS, ID, USERNAME));
    }

    private void networkError() throws NetworkErrorException {
        doThrow(new NetworkErrorException()).when(updateUsernameHttpEndpointSync).updateUsername(any(String.class), any(String.class));
    }

    private void generalError() throws NetworkErrorException {
        when(updateUsernameHttpEndpointSync.updateUsername(any(String.class), any(String.class)))
                .thenReturn(new UpdateUsernameHttpEndpointSync.EndpointResult(UpdateUsernameHttpEndpointSync.EndpointResultStatus.GENERAL_ERROR, ID, USERNAME));
    }

    private void authError() throws NetworkErrorException {
        when(updateUsernameHttpEndpointSync.updateUsername(any(String.class), any(String.class)))
                .thenReturn(new UpdateUsernameHttpEndpointSync.EndpointResult(UpdateUsernameHttpEndpointSync.EndpointResultStatus.AUTH_ERROR, ID, USERNAME));
    }

    private void serverError() throws NetworkErrorException {
        when(updateUsernameHttpEndpointSync.updateUsername(any(String.class), any(String.class)))
                .thenReturn(new UpdateUsernameHttpEndpointSync.EndpointResult(UpdateUsernameHttpEndpointSync.EndpointResultStatus.SERVER_ERROR, ID, USERNAME));
    }
}