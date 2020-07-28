package com.techyourchance.unittesting.screens.questionslist;

import com.techyourchance.unittesting.questions.FetchLastActiveQuestionsUseCase;
import com.techyourchance.unittesting.questions.Question;
import com.techyourchance.unittesting.screens.common.screensnavigator.ScreensNavigator;
import com.techyourchance.unittesting.screens.common.toastshelper.ToastsHelper;
import com.techyourchance.unittesting.testdata.QuestionsTestData;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class QuestionsListControllerTest {

    // region constants
    private static final List<Question> QUESTIONS = QuestionsTestData.getQuestions();
    private static final Question QUESTION = QuestionsTestData.getQuestion();
    // endregion constants

    // region helper fields
    private UseCaseTd useCaseTd;
    @Mock
    ScreensNavigator screensNavigatorMock;
    @Mock
    ToastsHelper toastsHelperMock;
    @Mock
    QuestionsListViewMvc questionsListViewMvcMock;

    // endregion helper fields
    QuestionsListController SUT;

    @Before
    public void setup() throws Exception {
        useCaseTd = new UseCaseTd();
        SUT = new QuestionsListController(useCaseTd, screensNavigatorMock, toastsHelperMock);
        SUT.bindView(questionsListViewMvcMock);
    }

    @Test
    public void onStart__successfulResponse_questionsBoundToView() {
        // Arrange
        success();
        // Act
        SUT.onStart();
        // Assert
        verify(questionsListViewMvcMock).bindQuestions(QUESTIONS);
    }

    @Test
    public void onStart__successfulResponse_progressIndicationHidden() {
        // Arrange
        success();
        // Act
        SUT.onStart();
        // Assert
        verify(questionsListViewMvcMock).hideProgressIndication();
    }

    @Test
    public void onStart__failure_progressIndicationHidden() {
        // Arrange
        success();
        // Act
        SUT.onStart();
        // Assert
        verify(questionsListViewMvcMock).hideProgressIndication();
    }

    @Test
    public void onStart__secondTimeAfterSuccessfulResponse_questionsBoundToTheViewFromCache() {
        // Arrange
        success();
        // Act
        SUT.onStart();
        SUT.onStart();
        // Assert
        verify(questionsListViewMvcMock, times(2)).bindQuestions(QUESTIONS);
        assertThat(useCaseTd.getCallCount(), is(1));
    }

    @Test
    public void onStart_failure_errorToastShown() {
        // Arrange
        failure();
        // Act
        SUT.onStart();
        // Assert
        verify(toastsHelperMock).showUseCaseError();
    }

    @Test
    public void onStart_progressIndicationShown() {
        // Arrange
        // Act
        SUT.onStart();
        // Assert
        verify(questionsListViewMvcMock).showProgressIndication();
    }

    @Test
    public void onStart_failure_questionsNotBoundToView() {
        // Arrange
        failure();
        // Act
        SUT.onStart();
        // Assert
        verify(questionsListViewMvcMock, never()).bindQuestions(any(List.class));
    }


    @Test
    public void onStart_listenersRegistered() {
        // Arrange
        // Act
        SUT.onStart();
        // Assert
        verify(questionsListViewMvcMock).registerListener(SUT);
        useCaseTd.verifyListenerRegistered(SUT);
    }

    @Test
    public void onStop_listenersUnregistered() {
        // Arrange
        // Act
        SUT.onStop();
        // Assert
        verify(questionsListViewMvcMock).unregisterListener(SUT);
        useCaseTd.verifyListenerNotRegistered(SUT);
    }

    @Test
    public void onQuestionClicked_navigatedToQuestionDetailsScreen() {
        // Arrange
        // Act
        SUT.onQuestionClicked(QUESTION);
        // Assert
        verify(screensNavigatorMock).toQuestionDetails(QUESTION.getId());
    }

    // region helper methods

    private void success() {
        // currently no-op
    }

    private void failure() {
        useCaseTd.failure = true;
    }

    // endregion helper methods

    // region helper classes

    private static class UseCaseTd extends FetchLastActiveQuestionsUseCase {

        private boolean failure;
        private int callCount;

        public UseCaseTd() {
            super(null);
        }

        @Override
        public void fetchLastActiveQuestionsAndNotify() {
            callCount++;
            for (FetchLastActiveQuestionsUseCase.Listener listener : getListeners()) {
                if (failure) {
                    listener.onLastActiveQuestionsFetchFailed();
                } else {
                    listener.onLastActiveQuestionsFetched(QUESTIONS);
                }
            }
        }

        public void verifyListenerRegistered(QuestionsListController candidate) {
            for (Listener listener : getListeners()) {
                if (listener == candidate) {
                    return;
                }
            }
            throw new RuntimeException("Listener not registered");
        }

        public void verifyListenerNotRegistered(QuestionsListController candidate) {
            for (Listener listener : getListeners()) {
                if (listener == candidate) {
                    throw new RuntimeException("Listener not unregistered");
                }
            }
        }

        public int getCallCount() {
            return callCount;
        }
    }

    // endregion helper classes
}