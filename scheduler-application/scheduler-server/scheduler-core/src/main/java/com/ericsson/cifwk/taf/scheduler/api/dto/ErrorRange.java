package com.ericsson.cifwk.taf.scheduler.api.dto;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * Created by Alexey Nikolaenko alexey.nikolaenko@ericsson.com
 * 16/07/2015
 */
public class ErrorRange {
    private int startLine;
    private int endLine;
    private int startColumn;
    private int endColumn;

    public ErrorRange() {
        this(2, 2, 1, 1);
    }

    public ErrorRange(int startLine, int startColumn, int endLine, int endColumn) {
        this.startLine = startLine;
        this.endLine = endLine;
        this.startColumn = startColumn;
        this.endColumn = endColumn;
    }

    public ErrorRange(int startLine, int startColumn) {
        this(startLine, 1, startLine, startColumn);
    }

    public int getStartLine() {
        return startLine;
    }

    public void setStartLine(int startLine) {
        this.startLine = startLine;
    }

    public int getEndLine() {
        return endLine;
    }

    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }

    public int getStartColumn() {
        return startColumn;
    }

    public void setStartColumn(int startColumn) {
        this.startColumn = startColumn;
    }

    public int getEndColumn() {
        return endColumn;
    }

    public void setEndColumn(int endColumn) {
        this.endColumn = endColumn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ErrorRange that = (ErrorRange) o;
        return Objects.equal(startLine, that.startLine) &&
                Objects.equal(endLine, that.endLine) &&
                Objects.equal(startColumn, that.startColumn) &&
                Objects.equal(endColumn, that.endColumn);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(startLine, endLine, startColumn, endColumn);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("startLine", startLine)
                .add("endLine", endLine)
                .add("startColumn", startColumn)
                .add("endColumn", endColumn)
                .toString();
    }
}
