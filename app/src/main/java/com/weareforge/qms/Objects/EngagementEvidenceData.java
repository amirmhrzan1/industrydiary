package com.weareforge.qms.Objects;

/**
 * Created by prajit on 2/18/16.
 */
public class EngagementEvidenceData {

    private int engagement_evidence_id;
    private String title;
    private String status;
    private int userid;
    private String current_step;
    private int industry_engagement_id;
    private String reference;
    private String datetime;
    private String hours;
    private String venue;
    private int findOutId;

    public void setEngagementEvidenceId(int id)
    {
        this.engagement_evidence_id = id;
    }

    public int getEngagementEvidenceId()
    {
        return this.engagement_evidence_id;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getTitle()
    {
        return this.title;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return this.status;
    }

    public void setUserid(int id)
    {
        this.userid = id;
    }

    public int getUserid()
    {
        return this.userid;
    }

    public void setCurrentStep(String currentstep)
    {
        this.current_step = currentstep;
    }

    public String getCurrentStep()
    {
        return this.current_step;
    }

    public void setIndustryEngagementId(int id)
    {
        this.industry_engagement_id = id;
    }

    public int getIndustyEngagementId()
    {
        return this.industry_engagement_id;
    }

    public void setReference(String reference)
    {
        this.reference = reference;
    }

    public String getReference()
    {
        return this.reference;
    }

    public void setDatetime(String date)
    {
        this.datetime = date;
    }

    public String getDateTime()
    {
        return this.datetime;
    }

    public void setHours(String hours)
    {
        this.hours = hours;
    }

    public String getHours()
    {
        return this.hours;
    }

    public void setVenue(String venue)
    {
        this.venue = venue;
    }

    public String getVenue()
    {
        return this.venue;
    }

    public void setFindOutId(int id)
    {
        this.findOutId = id;
    }

    public int getFindOutId()
    {
        return this.findOutId;
    }
}
