package com.ericsson.cifwk.taf.operators.view;

import com.ericsson.cifwk.taf.ui.core.UiComponent;
import com.ericsson.cifwk.taf.ui.core.UiComponentMapping;
import com.ericsson.cifwk.taf.ui.sdk.Button;
import com.ericsson.cifwk.taf.ui.sdk.GenericViewModel;
import com.ericsson.cifwk.taf.ui.sdk.TextBox;
import com.google.common.base.Preconditions;

import java.util.List;

public class ReviewCommentsPopup extends GenericViewModel {

    public static final String REVIEW_COMMENTS_HOLDER = ".commentsModal";
    public static final String COMMENT_CONTENT_CLASS = ".commentsModal-commentContent";
    public static final String COMMENT_AUTHOR_CLASS = ".commentsModal-commentAuthor";

    @UiComponentMapping(selector = REVIEW_COMMENTS_HOLDER)
    private UiComponent reviewCommentsHolder;

    @UiComponentMapping(selector = REVIEW_COMMENTS_HOLDER + " .commentsModal-commentsBlock > .commentsModal-noResults")
    private UiComponent noCommentsMessage;

    @UiComponentMapping(selector = REVIEW_COMMENTS_HOLDER + " .commentsModal-commentsBlock > .commentsModal-comment")
    private List<UiComponent> commentsList;

    @UiComponentMapping(selector = REVIEW_COMMENTS_HOLDER + " .commentsModal-commentTextarea")
    private TextBox commentBox;

    @UiComponentMapping(selector = REVIEW_COMMENTS_HOLDER + " .commentsModal-addBlock > .btn.btn-primary")
    private Button addCommentButton;

    @UiComponentMapping(selector = REVIEW_COMMENTS_HOLDER + " .modal-footer > .btn.btn-default")
    private Button closePopupButton;

    @Override
    public boolean isCurrentView() {
        return reviewCommentsHolder.isDisplayed();
    }

    public String getCommentBoxText() {
        return commentBox.getText();
    }

    public boolean isAddCommentButtonEnabled() {
        return addCommentButton.isEnabled();
    }

    public void addCommentAction() {
        addCommentButton.click();
    }

    public void closePopup() {
        closePopupButton.click();
    }

    public String getEmptyCommentsMessage() {
        if (noCommentsMessage.isDisplayed()) {
            return noCommentsMessage.getText();
        }
        return "";
    }

    public void setCommentText(String comment) {
        commentBox.setText(comment);
    }

    public int getCommentsCount() {
        return commentsList.size();
    }

    public String getCommentTextByIndex(int index) {
        Preconditions.checkState(index < getCommentsCount(), "Comments index is bigger than size of Comments List.");
        return commentsList.get(index).getDescendantsBySelector(COMMENT_CONTENT_CLASS).get(0).getText();
    }

    public String getCommentAuthorByIndex(int index) {
        Preconditions.checkState(index < getCommentsCount(), "Comments index is bigger than size of Comments List.");
        return commentsList.get(index).getDescendantsBySelector(COMMENT_AUTHOR_CLASS).get(0).getText();
    }
}
