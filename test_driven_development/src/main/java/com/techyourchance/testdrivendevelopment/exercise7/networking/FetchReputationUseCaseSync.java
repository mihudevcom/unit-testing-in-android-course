package com.techyourchance.testdrivendevelopment.exercise7.networking;

public class FetchReputationUseCaseSync {

    enum UseCaseResult {
        FAILURE,
        SUCCESS
    }

    private final GetReputationHttpEndpointSync getReputationHttpEndpointSync;

    public FetchReputationUseCaseSync(GetReputationHttpEndpointSync getReputationHttpEndpointSyncMock) {
        this.getReputationHttpEndpointSync = getReputationHttpEndpointSyncMock;
    }

    public UseCaseResult fetchReputationSync() {
        GetReputationHttpEndpointSync.EndpointResult result = getReputationHttpEndpointSync.getReputationSync();
        switch (result.getStatus()) {
            case GENERAL_ERROR:
            case NETWORK_ERROR:
                return UseCaseResult.FAILURE;
        }
        return UseCaseResult.SUCCESS;
    }
}
