package com.weareforge.qms.Objects;

/**
 * Created by Prajeet on 12/28/2015.
 */
public class TeacherDetails {

    private String Cohort;
    private String teacherFirstName;
    private String teacherLastName;
    private String approved_date;
    private String certificate_issued_date;
    private String expiry_date;
    private String inactive_date;
    private String process_start_date;
    private String withdrawn_date;
    private String staffingCode;
    private String teacherEmploymentStatus;
    private String accreditationName;

    public void setCohort(String cohort) {
        this.Cohort = cohort;
    }

    public String getCohort() {
        return this.Cohort;
    }

    public void setTeacherFirstName(String teacherFirstName) {
        this.teacherFirstName = teacherFirstName;
    }

    public String getTeacherFirstName()
    {
        return this.teacherFirstName;
    }


    public void setTeacherLastName(String teacherLastName) {
        this.teacherLastName = teacherLastName;
    }


    public String setTeacherLastName()
    {
        return this.teacherLastName;
    }

    public void setApproved_date(String approved_date) {
        this.approved_date = approved_date;
    }


    public String setApproved_date()
    {
        return this.approved_date;
    }

    public void setCertificate_issued_date(String certificate_issued_date)
    {
        this.certificate_issued_date =certificate_issued_date;
    }

    public String getCertificate_issued_date()
    {
        return this.certificate_issued_date;
    }

    public void setExpiry_date(String expiry_date)
    {
        this.expiry_date = expiry_date;
    }

    public String getExpiry_date()
    {
        return this.expiry_date;
    }

    public void setInactive_date(String inactive_date)
    {
        this.inactive_date = inactive_date;
    }

    public String getInactive_date()
    {
        return this.inactive_date;
    }

    public void setProcess_start_date(String process_start_date)
    {
        this.process_start_date = process_start_date;
    }

    public String getProcess_start_date()
    {
        return this.process_start_date;
    }

    public void setWithdrawn_date(String withdrawn_date)
    {
        this.withdrawn_date = withdrawn_date;
    }

    public String getWithdrawn_date()
    {
        return this.withdrawn_date;
    }

    public void setStaffingCode(String staffingCode)
    {
        this.staffingCode = staffingCode;
    }

    public String getStaffingCode()
    {
        return this.staffingCode;
    }

    public void setTeacherEmploymentStatus(String teacherEmploymentStatus)
    {
        this.teacherEmploymentStatus = teacherEmploymentStatus;
    }

    public String getTeacherEmploymentStatus()
    {
        return this.teacherEmploymentStatus;
    }

    public void setAccreditationName(String accreditationName)
    {
        this.accreditationName = accreditationName;
    }

    public String getAccreditationName()
    {
        return this.accreditationName;
    }
}
