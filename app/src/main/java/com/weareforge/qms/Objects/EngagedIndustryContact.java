package com.weareforge.qms.Objects;

/**
 * Created by prajit on 2/17/16.
 */
public class EngagedIndustryContact {

    private int Industry_Contact_Id;
    private int Industry_Engagement_Contact_Id;
    private int Engagement_Evidence_Id;
    private String Name;
    private String email;

    public void setIndustryContactEngagementId(int Id) {
        Industry_Engagement_Contact_Id = Id;
    }

    public int getIndustryContactEngagementId() {
        return Industry_Engagement_Contact_Id;
    }

    public void setIndustryContactId(int Id) {
        Industry_Contact_Id = Id;
    }

    public int getIndustry_Contact_Id() {
        return Industry_Contact_Id;
    }

    public void setEngagementEvidenceId(int id) {
        Engagement_Evidence_Id = id;
    }

    public int getEngagementEvidenceId() {
        return Engagement_Evidence_Id;
    }

    public void setName(String name)
    {
        this.Name = name;
    }

    public String getName()
    {
        return this.Name;
    }

    public void setEmail(String name)
    {
        this.email = name;
    }

    public String getEmail()
    {
        return this.email;
    }
}
