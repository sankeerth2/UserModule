/**
 * Created by dsanem on 11/28/16.
 */
import React from "react";
import "bootstrap/dist/css/bootstrap.css";
import "rc-dialog/assets/bootstrap.css";
import LoadingSpinner from "./../../../LoadingSpinner";
import cookie from "react-cookie";
import PermissionCheckbox from "./PermissionCheckbox";
require('./../css/style.css');
var Dialog = require('rc-dialog');


const UserDetailsModal = React.createClass({

    getInitialState () {
        return {
            firstName: this.props.firstName,
            userLinkId: this.props.userLinkId,
            lastName: this.props.lastName,
            email: this.props.email,
            mobile: this.props.mobile,
            adminPermissions: this.props.adminPermissions,
            firstNameError: 'hidden',
            lastNameError: 'hidden',
            emailError: 'hidden',
            mobileError: 'hidden',
            validationError: 'hidden',
            viewUserDetails: true,
            viewPermissions: false,
            selectedUserRole: "",
            selectedUsersPermissions: []

        };
    },

    handleFirstname(event) {
        this.setState({firstName: event.target.value});
        this.setState({firstNameError: 'hidden'});
    },

    handleLastname(event) {
        this.setState({lastName: event.target.value});
        this.setState({lastNameError: 'hidden'});
    },

    handleMobile(event) {
        if (event.target.value.length <= 10) {
            this.setState({mobile: event.target.value});
        }
        this.setState({mobileError: 'hidden'});
    },

    handleEmail(event) {
        this.setState({email: event.target.value});
        this.setState({emailError: 'hidden'});
    },

    validateInputs(event) {
        for (let i = 0; i < this.state.selectedUsersPermissions.length; i++) {
            this.state.selectedUsersPermissions.pop();
        }

        var emailRegex = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        if (!this.state.firstName) {
            console.log("Empty String for first name");
            this.setState({firstNameError: 'visible'});
            return;
        }
        if (!this.state.lastName) {
            console.log("Empty string for last name");
            this.setState({lastNameError: 'visible'});

            return;
        }
        if (!emailRegex.test(this.state.email)) {
            console.log("empty string for email");
            this.setState({emailError: 'visible'});
            return;
        }

        if (!this.state.mobile) {
            console.log("empty string for mobile");
            this.setState({mobileError: 'visible'});
            return;
        }
        let updatedData = {
            firstName: this.state.firstName,
            lastName: this.state.lastName,
            email: this.state.email,
            mobile: this.state.mobile,
            userLinkId: this.state.userLinkId
        };
        cookie.save('updatedUserDetails', updatedData, {path: '/'});
        this.loadPermissions();
        this.setState({viewPermissions: true, viewUserDetails: false});
    },

    loadPermissions(){
        let request = new Request('/api/search/permissions/' + this.state.userLinkId + '/userlinkid', {
            method: 'get',
            headers: new Headers({
                'Content-Type': 'application/json',
                'Authorization': cookie.load('efsrToken')
            })
        });
        fetch(request).then(function (responseObj) {
            if (responseObj.status === 200) {
                return responseObj.json();
            } else {
                return false;
            }
        }.bind(this)).then(function (result) {
            if (result) {
                let selectedUsersPermissions = [];
                for (let i = 0; i < this.state.adminPermissions.length; i++) {
                    selectedUsersPermissions.push({
                        checked: false,
                        permission: this.state.adminPermissions[i].permission
                    });
                }
                for (let i = 0; i < selectedUsersPermissions.length; i++) {
                    for (let x = 0; x < result.permissions.length; x++) {
                        if (result.permissions[x].permission === selectedUsersPermissions[i].permission) {
                            selectedUsersPermissions[i].checked = true;
                        }
                    }
                }
                this.setState({selectedUsersPermissions: selectedUsersPermissions});
            }
            else {
                this.setState({showError: true});
            }
        }.bind(this)).catch(function (err) {
            this.setState({showError: true});
            console.error(err.message);
        }.bind(this));

        request = new Request('/api/profile/' + this.state.userLinkId + '/userlinkid', {
            method: 'get',
            headers: new Headers({
                'Content-Type': 'application/json',
                'Authorization': cookie.load('efsrToken')
            })
        });
        fetch(request).then(function (responseObj) {
            if (responseObj.status === 200) {
                return responseObj.json();
            } else {
                return false;
            }
        }.bind(this)).then(function (result) {
            if (result) {
                this.setState({selectedUserRole: result.user.role.name});
            }
            else {
                this.setState({showError: true});
            }
        }.bind(this)).catch(function (err) {
            this.setState({showError: true});
            console.error(err.message);
        }.bind(this));

    },

    handlePermissionSelection(event){

        let selectedUsersPermissions = this.state.selectedUsersPermissions;
        for (let i = 0; i < selectedUsersPermissions.length; i++) {
            if (selectedUsersPermissions[i].permission === event.target.id) {
                selectedUsersPermissions[i].checked = !selectedUsersPermissions[i].checked;
            }
        }
        this.setState({selectedUsersPermissions: selectedUsersPermissions});
    },
    handleUpdate(){
        let updatedUserDetails = cookie.load('updatedUserDetails');
        updatedUserDetails = {
            firstName: updatedUserDetails.firstName,
            lastName: updatedUserDetails.lastName,
            email: updatedUserDetails.email,
            mobile: updatedUserDetails.mobile,
            role: {
                name: this.state.selectedUserRole
            }
        };

        let request = new Request('/api/users/' + this.state.userLinkId + '/userlinkid', {
            method: 'put',
            body: JSON.stringify(updatedUserDetails),
            headers: new Headers({
                'Content-Type': 'application/json',
                'Authorization': cookie.load('efsrToken')
            })
        });
        fetch(request).then(function (responseObj) {
            if (responseObj.status === 200) {
                return responseObj.json();
            } else {
                return false;
            }
        }.bind(this)).then(function (result) {
            if (!result) {
                this.setState({showError: true});
            }
        }.bind(this)).catch(function (err) {
            this.setState({showError: true});
            console.error(err.message);
        }.bind(this));

        let updatedPermissions = [];
        for (let i = 0; i < this.state.selectedUsersPermissions.length; i++) {
            if (this.state.selectedUsersPermissions[i].checked) {
                updatedPermissions.push({permission: this.state.selectedUsersPermissions[i].permission});
            }
        }
        if (updatedPermissions.length <= 0) {
            this.setState({validationError: true});
            return;
        }

        request = new Request('/api/permissions/' + this.state.userLinkId + '/userlinkid', {
            method: 'put',
            body: JSON.stringify(updatedPermissions),
            headers: new Headers({
                'Content-Type': 'application/json',
                'Authorization': cookie.load('efsrToken')
            })
        });
        fetch(request).then(function (responseObj) {
            if (responseObj.status === 200) {
                return responseObj.json();
            } else {
                return false;
            }
        }.bind(this)).then(function (result) {
            if (!result) {
                this.setState({showError: true});
            }
        }.bind(this)).catch(function (err) {
            this.setState({showError: true});
            console.error(err.message);
        }.bind(this));
        this.props.updatePage();
    },

    render() {
        let dialog;
        if (this.props.visible) {
            if (this.state.viewUserDetails) {
                dialog = (
                    <Dialog
                        visible={this.props.visible}
                        animation="slide-fade"
                        maskAnimation="fade"
                        onClose={this.props.exitDialogHandler}
                        style={{width: 600}}
                        bodyStyle={{width: 600, height: 300, top: 25}}
                        title={<div><p class="dialog-title">User Details</p></div>}
                        footer={
                            !this.state.loading ? [
                                <div class="dialog-footer">
                                    <button
                                        type="button"
                                        className="btn btn-default"
                                        id="close-user-details-btn"
                                        onClick={this.props.handler}
                                    >Close
                                    </button>
                                    <button
                                        type="submit"
                                        className="btn btn-primary"
                                        id="role-permissions-btn"
                                        onClick={this.validateInputs}
                                    >
                                        Next
                                    </button>
                                </div>
                            ] :
                                [
                                    <LoadingSpinner/>

                                ]
                        }
                    >
                        <form class="form-user-details">
                            <div>
                                <div class="field-wrap-left">
                                    <input id="firstName" class="form-control" type="text" placeholder="First name"
                                           required
                                           value={this.state.firstName}
                                           onChange={this.handleFirstname}/>
                                    <p class="input-validation-error-message"
                                       style={{visibility: this.state.firstNameError}}>Enter valid value</p>
                                </div>
                                <div class="field-wrap-left">
                                    <input id="lastName" class="form-control" placeholder="Last name "
                                           type="text" value={this.state.lastName}
                                           required
                                           onChange={this.handleLastname}/>
                                    <p class="input-validation-error-message"
                                       style={{visibility: this.state.lastNameError}}>Enter valid value</p>
                                </div>
                                <div class="field-wrap-left">
                                    <input id="mobile" class="form-control" placeholder="Mobile"
                                           type="number"
                                           value={this.state.mobile}
                                           required
                                           onChange={this.handleMobile}/>
                                    <p class="input-validation-error-message"
                                       style={{visibility: this.state.mobileError}}>Enter valid value</p>
                                </div>
                                <div class="field-wrap-left">
                                    <input id="email" class="form-control" placeholder="Email" type="email"
                                           pattern="[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,4}$"
                                           value={this.state.email}
                                           required
                                           onChange={this.handleEmail}/>
                                    <p class="input-validation-error-message"
                                       style={{visibility: this.state.emailError}}>Enter valid value</p>
                                </div>
                            </div>
                        </form>
                    </Dialog>
                );
            }
            if (this.state.viewPermissions) {
                let tempPermissions = [];
                for (let i = 0; i < this.state.selectedUsersPermissions.length; i++) {
                    let tempStr = this.state.selectedUsersPermissions[i].permission.substring(19, this.state.selectedUsersPermissions[i].permission.length);
                    tempStr = tempStr.split(".").join(" ");
                    if (this.state.selectedUsersPermissions[i].checked) {
                        tempPermissions.push(<PermissionCheckbox
                            permission={this.state.selectedUsersPermissions[i].permission}
                            optionLabel={tempStr}
                            key={this.state.selectedUsersPermissions[i].permission}
                            checked={true}
                            handler={this.handlePermissionSelection}/>)
                    } else {
                        tempPermissions.push(<PermissionCheckbox
                            permission={this.state.selectedUsersPermissions[i].permission}
                            optionLabel={tempStr}
                            key={this.state.selectedUsersPermissions[i].permission}
                            checked={false}
                            handler={this.handlePermissionSelection}/>)
                    }
                }
                dialog = (
                    <Dialog
                        visible={this.state.viewPermissions}
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
                                        onClick={this.handleUpdate}
                                    >
                                        Update
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
                                Must Select at-least one Permission</p>
                            <div class="container-title">
                                <p>Permissions</p>
                            </div>
                            <div class="permissions-container">
                                {tempPermissions}
                            </div>
                        </form>
                    </Dialog>
                );
            }
        }
        return (
            <div style={{margin: 20}}>
                {dialog}
            </div>
        );
    },
});

export default UserDetailsModal;
