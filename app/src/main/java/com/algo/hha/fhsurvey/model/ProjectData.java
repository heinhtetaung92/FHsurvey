package com.algo.hha.fhsurvey.model;

/**
 * Created by heinhtetaung on 7/2/15.
 */
public class ProjectData {

    private String _projectID;
    private String _projectName;
    private String _projectName_EE;
    private String _description;
    private String _projectStatus;
    private String _startDate;
    private String _completeDate;
    private String _expireDate;
    private String _status;

    public String get_projectID() {
        return _projectID;
    }

    public void set_projectID(String _projectID) {
        this._projectID = _projectID;
    }

    public String get_projectName() {
        return _projectName;
    }

    public void set_projectName(String _projectName) {
        this._projectName = _projectName;
    }

    public String get_description() {
        return _description;
    }

    public void set_description(String _description) {
        this._description = _description;
    }

    public String get_projectStatus() {
        return _projectStatus;
    }

    public void set_projectStatus(String _projectStatus) {
        this._projectStatus = _projectStatus;
    }

    public String get_startDate() {
        return _startDate;
    }

    public void set_startDate(String _startDate) {
        this._startDate = _startDate;
    }

    public String get_completeDate() {
        return _completeDate;
    }

    public void set_completeDate(String _completeDate) {
        this._completeDate = _completeDate;
    }

    public String get_expireDate() {
        return _expireDate;
    }

    public void set_expireDate(String _expireDate) {
        this._expireDate = _expireDate;
    }

    public String get_status() {
        return _status;
    }

    public void set_status(String _status) {
        this._status = _status;
    }

    public String get_projectName_EE(){
        return _projectName_EE;
    }

    public void set_projectName_EE(String projectName_ee){
        this._projectName_EE = projectName_ee;
    }

}

/*
"ProjectID": "8b0cf458-a595-4a3e-996f-fc1be699520f",
        "ProjectName": "Field survey WIN’S",
        "Description": "Test Project",
        "ProjectStatus": 1,
        "StartDate": "2015-06-15T00:00:00",
        "CompleteDate": "2015-12-15T00:00:00",
        "ExpireDate": "2015-07-01T04:51:58.6535667-05:00",
        "Status": 1
 */
