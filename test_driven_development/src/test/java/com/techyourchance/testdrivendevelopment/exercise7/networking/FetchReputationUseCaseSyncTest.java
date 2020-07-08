package com.techyourchance.testdrivendevelopment.exercise7.networking;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FetchReputationUseCaseSyncTest {

    // region constants
    static int REPUTATION = 100;

    // endregion constants

    // region helper fields
    @Mock
    GetReputationHttpEndpointSync getReputationHttpEndpointSyncMock;

    // endregion helper fields
    FetchReputationUseCaseSync SUT;

    @Before
    public void setup() throws Exception {
        SUT = new FetchReputationUseCaseSync(getReputationHttpEndpointSyncMock);
        success();
    }

    @Test
    public void fetchReputationSync_success_successReturned() {
        // Arrange
        // Act
        FetchReputationUseCaseSync.UseCaseResult result = SUT.fetchReputationSync();
        // Assert
        assertThat(result, is(FetchReputationUseCaseSync.UseCaseResult.SUCCESS));
    }

    @Test
    public void fetchReputationSync_success_fetchedReputationReturned() {
        // Arrange
        // Act
        SUT.fetchReputationSync();
        // Assert
        assertThat(getReputationHttpEndpointSyncMock.getReputationSync().getReputation(), is(REPUTATION));
    }

    @Test
    public void fetchReputationSync_generalError_failedReturned() {
        // Arrange
        generalError();
        // Act
        FetchReputationUseCaseSync.UseCaseResult result = SUT.fetchReputationSync();
        // Assert
        assertThat(result, is(FetchReputationUseCaseSync.UseCaseResult.FAILURE));
    }

    @Test
    public void fetchReputationSync_networkError_failedReturned() {
        // Arrange
        networkError();
        // Act
        FetchReputationUseCaseSync.UseCaseResult result = SUT.fetchReputationSync();
        // Assert
        assertThat(result, is(FetchReputationUseCaseSync.UseCaseResult.FAILURE));
    }

    @Test
    public void fetchReputationSync_generalError_reputationIsZero() {
        // Arrange
        generalError();
        // Act
        SUT.fetchReputationSync();
        // Assert
        assertThat(getReputationHttpEndpointSyncMock.getReputationSync().getReputation(), is(0));
    }

    @Test
    public void fetchReputationSync_networkError_reputationIsZero() {
        // Arrange
        networkError();
        // Act
        SUT.fetchReputationSync();
        // Assert
        assertThat(getReputationHttpEndpointSyncMock.getReputationSync().getReputation(), is(0));
    }

    // region helper methods

    public void success() {
        when(getReputationHttpEndpointSyncMock.getReputationSync()).thenReturn(new GetReputationHttpEndpointSync.EndpointResult(GetReputationHttpEndpointSync.EndpointStatus.SUCCESS, REPUTATION));
    }

    private void generalError() {
        when(getReputationHttpEndpointSyncMock.getReputationSync()).thenReturn(new GetReputationHttpEndpointSync.EndpointResult(GetReputationHttpEndpointSync.EndpointStatus.GENERAL_ERROR, 0));
    }

    private void networkError() {
        when(getReputationHttpEndpointSyncMock.getReputationSync()).thenReturn(new GetReputationHttpEndpointSync.EndpointResult(GetReputationHttpEndpointSync.EndpointStatus.NETWORK_ERROR, 0));
    }

    // endregion helper methods

    // region helper classes

    // endregion helper classes
}