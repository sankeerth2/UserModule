/**
 * Created by dsanem on 11/29/16.
 */
import React from "react";
import "bootstrap/dist/css/bootstrap.css";
import "rc-dialog/assets/bootstrap.css";
import RoleOption from "./RoleOption";
import cookie from "react-cookie";
import PermissionCheckbox from "./PermissionCheckbox";
import LoadingSpinner from "./../../../LoadingSpinner";
require('./../css/style.css');
var Dialog = require('rc-dialog');
let selectedRole = "";
let selectedPermissions = [];


const PermissionsModal = React.createClass({

    getInitialState(){
        return {
            showDialog: true,
            validationError: 'hidden',
            token: cookie.load('efsrToken'),
            loading: false
        }
    },

    validateSelections(){
        let valid = false;
        if (selectedRole) {
            valid = true;
        }

        for (let i = 0; i < selectedPermissions.length; i++) {
            if (selectedPermissions[i].checked) {
                valid = true;
                break;
            }
        }
        if (valid) {
            this.callCreateNewUser();
        }
        else {
            this.setState({validationError: 'visible'});
        }
    },

    callCreateNewUser(){
        this.setState({validationError: 'hidden', loading: true});
        for (let i = 0; i < selectedPermissions.length; i++) {
            if (selectedPermissions[i].checked) {
                console.log(selectedPermissions[i]);
            }
        }
        let data = cookie.load('newUserDetails');
        data.role = selectedRole;
        let tempPerms = [];
        for (let i = 0; i < selectedPermissions.length; i++) {
            let permission = {permission: selectedPermissions[i].key};
            tempPerms.push(permission);
        }
        data.permissions = tempPerms;
        console.log(JSON.stringify(data));


        let request = new Request('/api/new/user', {
            method: 'post',
            body: JSON.stringify(data),
            headers: new Headers({
                'Content-Type': 'application/json',
                'Authorization': this.state.token
            })
        });
        fetch(request).then(function (responseObj) {
            if (responseObj.status === 201) {
                return responseObj.json();
            } else {
                return false;
            }
        }.bind(this)).then(function (result) {
            if (result) {
                this.props.updateTable(result);
            }
            else {

            }
        }.bind(this)).catch(function (err) {
            this.setState({showError: true});
            console.error(err.message);
        }.bind(this));
    },

    handleRoleSelection(event){
        if (event.target.value === 'on') {
            selectedRole = event.target.id;
        }
    },

    handlePermissionSelection(event){
        let obj = {
            key: event.target.id,
            checked: event.target.checked
        };
        for (let i = 0; i < selectedPermissions.length; i++) {
            if (selectedPermissions[i].key === event.target.id) {
                selectedPermissions[i].checked = event.target.checked;
                return;
            }
        }
        selectedPermissions.push(obj);
    },

    render() {
        let dialog;
        let tempRoles = [];
        let tempPermissions = [];
        for (let i = 0; i < this.props.permissions.length; i++) {
            switch (this.props.permissions[i].permission) {
                case 'permission.efsr.AD.create.super.admin':
                    tempRoles.push(
                        <RoleOption
                            optionLabel="Super Admin"
                            role="SUPER_ADMIN"
                            key={this.props.permissions[i].permission}
                            handler={this.handleRoleSelection}
                        />
                    );
                    break;
                case 'permission.efsr.AD.create.admin':
                    tempRoles.push(
                        <RoleOption
                            optionLabel="Admin"
                            role="ADMIN"
                            key={this.props.permissions[i].permission}
                            handler={this.handleRoleSelection}
                        />
                    );
                    break;
                case 'permission.efsr.AD.create.regional.head':
                    tempRoles.push(
                        <RoleOption
                            optionLabel="Regional Head"
                            role="REGIONAL_HEAD"
                            key={this.props.permissions[i].permission}
                            handler={this.handleRoleSelection}
                        />
                    );
                    break;
                case 'permission.efsr.AD.create.zonal.head':
                    tempRoles.push(
                        <RoleOption
                            optionLabel="Zonal Head"
                            role="ZONAL_HEAD"
                            key={this.props.permissions[i].permission}
                            handler={this.handleRoleSelection}
                        />
                    );
                    break;
                case 'permission.efsr.AD.create.supervisor':
                    tempRoles.push(
                        <RoleOption
                            optionLabel="Supervisor"
                            role="SUPERVISOR"
                            key={this.props.permissions[i].permission}
                            handler={this.handleRoleSelection}
                        />
                    );
                    break;
                case 'permission.efsr.AD.create.technician':
                    tempRoles.push(
                        <RoleOption
                            optionLabel="Technician"
                            role="TECHNICIAN"
                            key={this.props.permissions[i].permission}
                            handler={this.handleRoleSelection}
                        />
                    );
                    break;
                default:
                    tempRoles.push([]);
            }
        }

        for (let i = 0; i < this.props.permissions.length; i++) {
            let tempStr = this.props.permissions[i].permission.substring(19, this.props.permissions[i].permission.length);
            if (!tempStr.includes('create')) {
                tempStr = tempStr.split(".").join(" ");
                tempPermissions.push(<PermissionCheckbox
                    permission={this.props.permissions[i].permission}
                    optionLabel={tempStr}
                    key={this.props.permissions[i].permission}
                    handler={this.handlePermissionSelection}/>)
            }
        }

        dialog = (
            <Dialog
                visible={this.props.visible}
                animation="slide-fade"
                maskAnimation="fade"
                onClose={this.props.exitDialogHandler}
                style={{width: 600}}
                bodyStyle={{width: 595, height: 245, padding: 0}}
                title={<div><p class="dialog-title">Assign Role and Permissions</p></div>}
                footer={
                    !this.state.loading ? [
                        <div class="dialog-footer">
                            <button
                                type="button"
                                className="btn btn-default"
                                id="close-btn"
                                onClick={this.props.exitDialogHandler}
                            >Close
                            </button>
                            <button
                                type="button"
                                className="btn btn-default"
                                id="back-btn"
                                onClick={this.props.handler}
                            >Back
                            </button>
                            <button
                                type="button"
                                className="btn btn-primary"
                                id="create-btn"
                                onClick={this.validateSelections}
                            >
                                Create
                            </button>
                        </div>
                    ] :
                        [
                            <LoadingSpinner/>

                        ]

                }
            >
                <form class="form-user-details">
                    <p class="input-validation-error-message"
                       style={{visibility: this.state.validationError}}>
                        Must Select a Role or at-least one Permission</p>
                    <div class="container-title">
                        <p>Roles</p>
                    </div>
                    <div class="role-container">
                        {tempRoles}
                    </div>
                    <div class="container-title">
                        <p>Permissions</p>
                    </div>
                    <div class="permissions-container">
                        {tempPermissions}
                    </div>
                </form>
            </Dialog>
        );
        return (
            <div style={{margin: 20}}>
                {dialog}
            </div>
        );
    },
});

export default PermissionsModal;
