package com.ericsson.cifwk.taf.operators.components;

import com.ericsson.cifwk.taf.ui.core.UiComponent;
import com.ericsson.cifwk.taf.ui.core.UiComponentMapping;
import com.ericsson.cifwk.taf.ui.sdk.TextBox;

import java.util.List;

/**
 * Created by eniakel on 05/04/2016.
 */
public class CommentsModalDialog extends BasicModalDialog {

    @UiComponentMapping(".commentsModal-commentsBlock > .commentsModal-noResults")
    private UiComponent noCommentsMessage;

    @UiComponentMapping(".commentsModal-commentsBlock > .commentsModal-comment")
    private List<UiComponent> commentsList;

    @UiComponentMapping(".commentsModal-commentTextarea")
    private TextBox commentBox;

    public UiComponent getNoCommentsMessage() {
        return noCommentsMessage;
    }

    public List<UiComponent> getCommentsList() {
        return commentsList;
    }

    public TextBox getCommentBox() {
        return commentBox;
    }
}
