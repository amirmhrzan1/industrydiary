package com.weareforge.qms.Objects;

import java.util.Comparator;

/**
 * Created by Prajeet on 12/28/2015.
 */
public class IndustryContactDetails {
    private int Industry_Contact_Id;
    private String Title;
    private String Organization;
    private String Street_Address;
    private String Suburb;
    private String Postal_Code;
    private String Phone;
    private String Email;
    private String Qualification;
    private String Comment;
    private String Opportunities;
    private String Action_Required;
    private String Activity_Recommendation;
    private String User_Id;

    public void setIndustry_Contact_Id(int industry_Contact_Id) {
        this.Industry_Contact_Id = industry_Contact_Id;
    }

    public int getIndustry_Contact_Id() {
        return this.Industry_Contact_Id;
    }

    public void setTitle(String title) {
        this.Title = title;
    }

    public String getTitle() {
        return this.Title;
    }

    public void setOrganization(String organization) {
        this.Organization = organization;
    }

    public String getOrganization() {
        return this.Organization;
    }

    public void setStreet_Address(String street_Address) {
        this.Street_Address = street_Address;
    }

    public String getStreet_Address() {
        return this.Street_Address;
    }

    public void setSuburb(String suburb) {
        this.Suburb = suburb;
    }

    public String getSuburb() {
        return this.Suburb;
    }

    public void setPostal_Code(String postal_Code) {
        this.Postal_Code = postal_Code;
    }

    public String getPostal_Code() {
        return this.Postal_Code;
    }

    public void setPhone(String phone) {
        this.Phone = phone;
    }

    public String getPhone() {
        return this.Phone;
    }

    public void setEmail(String email) {
        this.Email = email;
    }

    public String getEmail() {
        return this.Email;
    }

    public void setQualification(String qualification) {
        this.Qualification = qualification;
    }

    public String getQualification() {
        return this.Qualification;
    }

    public void setComment(String comment)
    {
        this.Comment = comment;
    }

    public String getComment()
    {
        return this.Comment;
    }

    public void setOpportunities(String opportunities)
    {
        this.Opportunities = opportunities;
    }

    public String getOpportunities()
    {
        return this.Opportunities;
    }

    public void setAction_Required(String action_Required)
    {
        this.Action_Required = action_Required;
    }

    public String getAction_Required()
    {
        return this.Action_Required;
    }

    public void setActivity_Recommendation(String action_Required)
    {
        this.Activity_Recommendation = action_Required;
    }

    public String getActivity_Recommendation()
    {
        return this.Activity_Recommendation;
    }

    public void setUser_Id(String user_Id)
    {
        this.User_Id = user_Id;
    }

    public String getUser_Id()
    {
        return this.User_Id;
    }

}
