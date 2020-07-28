package com.techyourchance.unittesting.questions;

import com.techyourchance.unittesting.networking.questions.FetchQuestionDetailsEndpoint;
import com.techyourchance.unittesting.networking.questions.FetchQuestionDetailsEndpoint.Listener;
import com.techyourchance.unittesting.networking.questions.QuestionSchema;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class FetchQuestionDetailsUseCaseTest {

    // region constants
    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String BODY = "body";
    // endregion constants

    // region helper fields
    @Mock
    FetchQuestionDetailsEndpoint fetchQuestionDetailsEndpointMock;
    @Mock
    FetchQuestionDetailsUseCase.Listener listener1;
    @Mock
    FetchQuestionDetailsUseCase.Listener listener2;
    @Captor
    ArgumentCaptor<QuestionDetails> argumentCaptor;
    // endregion helper fields

    FetchQuestionDetailsUseCase SUT;

    @Before
    public void setup() throws Exception {
        SUT = new FetchQuestionDetailsUseCase(fetchQuestionDetailsEndpointMock);
    }

    private QuestionSchema getExpectedQuestionDetailsSchema() {
        QuestionSchema questionSchema = new QuestionSchema(TITLE, ID, BODY);
        return questionSchema;
    }

    @Test
    public void fetchQuestionDetailsAndNotify_success_listenersNotifiedWithCorrectData() {
        // Arrange
        success();
        SUT.registerListener(listener1);
        SUT.registerListener(listener2);
        // Act
        SUT.fetchQuestionDetailsAndNotify(ID);
        // Assert
        verify(listener1).onQuestionDetailsFetched(argumentCaptor.capture());
        verify(listener2).onQuestionDetailsFetched(argumentCaptor.capture());
        List<QuestionDetails> questionList = argumentCaptor.getAllValues();
        assertThat(questionList.get(0), is(getExpectedQuestionDetails()));
        assertThat(questionList.get(1), is(getExpectedQuestionDetails()));
    }

    @Test
    public void fetchQuestionDetailsAndNotify_success_unregisteredListenersNotNotified() {
        // Arrange
        success();
        SUT.registerListener(listener1);
        SUT.registerListener(listener2);
        SUT.unregisterListener(listener2);
        // Act
        SUT.fetchQuestionDetailsAndNotify(ID);
        // Assert
        verify(listener1).onQuestionDetailsFetched(any(QuestionDetails.class));
        verifyNoMoreInteractions(listener2);
    }

    @Test
    public void fetchQuestionDetailsAndNotify_failure_listenersNotifiedOfFailure() {
        // Arrange
        failure();
        SUT.registerListener(listener1);
        SUT.registerListener(listener2);
        // Act
        SUT.fetchQuestionDetailsAndNotify(ID);
        // Assert
        verify(listener1).onQuestionDetailsFetchFailed();
        verify(listener2).onQuestionDetailsFetchFailed();
    }

    // region helper methods

    private void success() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                FetchQuestionDetailsEndpoint.Listener listener = (FetchQuestionDetailsEndpoint.Listener) args[1];
                listener.onQuestionDetailsFetched(getExpectedQuestionDetailsSchema());
                return null;
            }
        }).when(fetchQuestionDetailsEndpointMock).fetchQuestionDetails(anyString(), any(Listener.class));
    }

    private void failure() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Listener listener = (Listener) args[1];
                listener.onQuestionDetailsFetchFailed();
                return null;
            }
        }).when(fetchQuestionDetailsEndpointMock).fetchQuestionDetails(any(String.class), any(Listener.class));
    }

    private QuestionDetails getExpectedQuestionDetails() {
        QuestionDetails questionDetails = new QuestionDetails(ID, TITLE, BODY);
        return questionDetails;
    }

    // endregion helper methods

    // region helper classes

    // endregion helper classes
}