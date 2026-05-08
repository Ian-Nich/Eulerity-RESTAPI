package com.taskmanager.dto;

public class SuggestResponse {

    private String suggestedPriority;
    private String suggestedDueDate;
    private String suggestedStatus;
    private String explanation;

    public SuggestResponse() {}

    public SuggestResponse(String suggestedPriority, String suggestedDueDate,
                           String suggestedStatus, String explanation) {
        this.suggestedPriority = suggestedPriority;
        this.suggestedDueDate = suggestedDueDate;
        this.suggestedStatus = suggestedStatus;
        this.explanation = explanation;
    }

    public String getSuggestedPriority() { return suggestedPriority; }
    public void setSuggestedPriority(String suggestedPriority) { this.suggestedPriority = suggestedPriority; }

    public String getSuggestedDueDate() { return suggestedDueDate; }
    public void setSuggestedDueDate(String suggestedDueDate) { this.suggestedDueDate = suggestedDueDate; }

    public String getSuggestedStatus() { return suggestedStatus; }
    public void setSuggestedStatus(String suggestedStatus) { this.suggestedStatus = suggestedStatus; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
}