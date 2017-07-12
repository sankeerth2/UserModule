/**
 * Created by sanemdeepak on 11/7/16.
 */
import TableHeader from "./TableHeader";
import React from "react";
import cookie from "react-cookie";
import LoadingSpinner from "./LoadingSpinner";
import ErrorModal from "./modal/errorModal/js/ErrorModal";
import UserDetailsModal from "./modal/userDetailsModal/js/UserDetailsModal";
import NewUserDetailsModal from "./modal/newUserDetailsModal/js/NewUserDetailsModal";
import PermissionsModal from "./modal/newUserDetailsModal/js/PermissionsModal";
require('./../css/style.css');
const ReactRedirect = require("react-redirect");

export default class Layout extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            loadPage: false,
            isAuthenticated: false,
            loading: true,
            users: [],
            rows: [],
            token: cookie.load('efsrToken'),
            showError: false,
            showNewUserDetailsModal: false,
            showPermissionsModal: false,
            showUserDetailsModal: false,
            permissions: [],
            firstName: "",
            lastName: "",
            email: "",
            mobile: "",
            roleOptions: [],
            permissionOptions: []
        };
        this.handleAddNewUser = this.handleAddNewUser.bind(this);
        this.handleActiveChange = this.handleActiveChange.bind(this);
        this.handleDelete = this.handleDelete.bind(this);
        this.handleDialogClose = this.handleDialogClose.bind(this);
        this.loadCurrentProfile = this.loadCurrentProfile.bind(this);
        this.handleUserDialogModal = this.handleUserDialogModal.bind(this);
        this.handlePermissionModal = this.handlePermissionModal.bind(this);
        this.appendNewUserRow = this.appendNewUserRow.bind(this);
        this.updateUserDetails = this.updateUserDetails.bind(this);
    }

    componentWillMount() {
        cookie.remove('newUserDetails');
        cookie.remove('userDetails');
    }

    componentDidMount() {
        let request = new Request('/api/current/users', {
            method: 'get',
            headers: new Headers({
                'Content-Type': 'application/json',
                'Authorization': this.state.token
            })
        });

        fetch(request).then(function (responseObj) {
            if (responseObj.status === 200) {
                return responseObj.json();
            }
            else {
                return null;
            }
        }.bind(this)).then(function (data) {
            if (data) {
                this.setState({
                    isAuthenticated: true,
                    loadPage: true,
                    loading: false,
                    users: data.users
                });

                this.loadCurrentProfile();
                this.populateRows(data.users);
            }
            else {
                this.setState({
                    isAuthenticated: false,
                    loadPage: false,
                    loading: false
                });
            }
        }.bind(this)).catch(function (err) {
            console.error(err.message);
        });
    }

    handleActiveChange(event) {
        let usersTemp = this.state.users;
        let index = 0;
        for (let i = 0; i < usersTemp.length; i++) {
            if (usersTemp[i].userLinkId === event.target.id) {
                index = i;
                break;
            }
        }
        let action = (usersTemp[index].active === 1 || usersTemp[index].active === true) ? 'deactivate' : 'activate';
        let request = new Request('/api/users/' + event.target.id + '/userlinkid/' + action, {
            method: 'put',
            headers: new Headers({
                'Content-Type': 'application/json',
                'Authorization': this.state.token
            })
        });
        fetch(request).then(function (responseObj) {
            if (responseObj.status === 200) {
                return true;
            } else {
                return false;
            }
        }.bind(this)).then(function (result) {
            if (result) {
                usersTemp[index].active = !usersTemp[index].active;
                this.populateRows(usersTemp);
            }
            else {
                this.setState({showError: true});
            }
        }.bind(this)).catch(function (err) {
            this.setState({showError: true});
            console.error(err.message);
        }.bind(this));
    }

    handleDelete(event) {
        let userLinkId = event.target.id;
        let request = new Request('/api/users/' + userLinkId + '/userlinkid/delete', {
            method: 'delete',
            headers: new Headers({
                'Content-Type': 'application/json',
                'Authorization': this.state.token
            })
        });
        fetch(request).then(function (responseObj) {
            if (responseObj.status === 200) {
                return true;
            } else {
                return false;
            }
        }.bind(this)).then(function (result) {
            if (result) {
                console.log(result + userLinkId);
                let usersTemp = [];
                for (let i = 0; i < this.state.users.length; i++) {
                    if (this.state.users[i].userLinkId != userLinkId) {
                        usersTemp.push(this.state.users[i]);
                    }
                }
                this.setState({users: usersTemp});
                this.populateRows(usersTemp);
            }
            else {
                this.setState({showError: true});
            }
        }.bind(this)).catch(function (err) {
            this.setState({showError: true});
            console.error(err.message);
        }.bind(this));
    }

    handleAddNewUser(event) {
        this.setState({showNewUserDetailsModal: true});
    }

    handleUserDialogModal(event) {
        if (event.target.id === 'close-user-details-btn') {
            this.setState({showNewUserDetailsModal: false});
        }
        if (event.target.id === 'role-permissions-btn') {
            let obj = {
                firstName: event.data.firstName,
                lastName: event.data.lastName,
                email: event.data.email,
                mobile: event.data.mobile,
                role: "",
                permissions: []
            }
            cookie.save('newUserDetails', obj, {path: '/'});
            this.setState({showPermissionsModal: true});
        }
    }

    handlePermissionModal(event) {
        if (event.target.id === 'back-btn') {
            this.setState({showPermissionsModal: false});
        }
        if (event.target.id === 'create-btn') {
            console.log('create clicked');
            this.getPermissionStrings(null);
        }
    }

    handleDialogClose(event) {
        this.setState({showNewUserDetailsModal: false});
        this.setState({showPermissionsModal: false});
        this.setState({showUserDetailsModal: false});
        this.setState({showError: false});
        cookie.remove('userDetails');
    }

    loadCurrentProfile(event) {
        let request = new Request('/api/current/profile', {
            method: 'get',
            headers: new Headers({
                'Content-Type': 'application/json',
                'Authorization': this.state.token
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
                this.setState({permissions: result.user.permissions});
                this.setState({firstName: result.user.firstName});
                this.setState({lastName: result.user.lastName});
                this.setState({email: result.user.email});
                this.setState({mobile: result.user.mobile});
            }
            else {
                this.setState({showError: true});
            }
        }.bind(this)).catch(function (err) {
            this.setState({showError: true});
            console.error(err.message);
        }.bind(this));
    }

    appendNewUserRow(result) {
        let data = cookie.load('newUserDetails');
        cookie.remove('newUserDetails');
        let rows = this.state.rows;
        let users = this.state.users;
        rows.push(<UserRow
            name={data.firstName + " " + data.lastName}
            email={data.email}
            onUserClick={this.handleUserClick.bind(this)}
            mobile={data.mobile}
            key={result.invite.userLinkId}
            active={false}
            userLinkId={result.invite.userLinkId}
            handleActiveChange={this.handleActiveChange.bind(this)}
            handleDelete={this.handleDelete.bind(this)}
        />);

        users.push({
            firstName: data.firstName,
            lastName: data.lastName,
            email: data.email,
            mobile: data.mobile,
            userLinkId: result.invite.userLinkId,
            active: 0,
            newUser: 1,
            role: "",
            permissions: ""
        });
        this.setState({users: users});
        this.setState({rows: rows});
        this.handleDialogClose();
    }

    updateUserDetails() {
        let data = cookie.load('updatedUserDetails');
        cookie.remove('updatedUserDetails');
        let users = this.state.users;
        for (let i = 0; i < users.length; i++) {
            if (users[i].userLinkId === data.userLinkId) {
                users[i].firstName = data.firstName;
                users[i].lastName = data.lastName;
                users[i].email = data.email;
                users[i].mobile = data.mobile;
            }
        }
        this.setState({users: users});
        this.populateRows(users);
        this.handleDialogClose();
    }

    handleUserClick(event) {
        let userDetail = "";
        for (let i = 0; i < this.state.users.length; i++) {
            if (this.state.users[i].userLinkId === event.target.id) {
                userDetail = {
                    firstName: this.state.users[i].firstName,
                    lastName: this.state.users[i].lastName,
                    email: this.state.users[i].email,
                    mobile: this.state.users[i].mobile,
                    userLinkId: this.state.users[i].userLinkId,
                    adminPermissions: this.state.permissions
                };
                break;
            }
        }
        cookie.save("userDetails", userDetail, {path: '/'});
        this.setState({showUserDetailsModal: true});
    }

    populateRows(users) {
        for (let i = 0; i < this.state.rows.length; i++) {
            this.state.rows.pop()
        }
        let rows = [];
        for (let i = 0; i < users.length; i++) {
            rows.push(<UserRow
                name={users[i].firstName + " " + users[i].lastName}
                onUserClick={this.handleUserClick.bind(this)}
                email={users[i].email}
                mobile={users[i].mobile}
                key={users[i].userLinkId}
                active={users[i].active}
                userLinkId={users[i].userLinkId}
                handleActiveChange={this.handleActiveChange.bind(this)}
                handleDelete={this.handleDelete.bind(this)}
            />);
        }
        this.setState({rows: rows});
    }

    handleUpdateUserDetails(event) {

    }

    render() {
        let dialog;
        if (this.state.showUserDetailsModal) {
            let userDetails = cookie.load('userDetails');
            dialog = (
                <UserDetailsModal
                    firstName={userDetails.firstName}
                    lastName={userDetails.lastName}
                    email={userDetails.email}
                    mobile={userDetails.mobile}
                    visible={this.state.showUserDetailsModal}
                    userLinkId={userDetails.userLinkId}
                    adminPermissions={userDetails.adminPermissions}
                    exitDialogHandler={this.handleDialogClose}
                    updatePage={this.updateUserDetails}/>

            );
        }
        if (this.state.showNewUserDetailsModal) {
            dialog = (
                <NewUserDetailsModal
                    visible={this.state.showNewUserDetailsModal}
                    handler={this.handleUserDialogModal}
                    exitDialogHandler={this.handleDialogClose}/>

            );
        }
        if (this.state.showPermissionsModal) {
            dialog = (
                <PermissionsModal
                    visible={this.state.showPermissionsModal}
                    permissions={this.state.permissions}
                    exitDialogHandler={this.handleDialogClose}
                    handler={this.handlePermissionModal}
                    permissionOptions={this.state.permissionOptions}
                    updateTable={this.appendNewUserRow}/>
            );
        }
        if (this.state.showError) {
            dialog = (
                <ErrorModal
                    visible={this.state.showError}
                    exitDialogHandler={this.handleDialogClose}/>
            )
        }
        return (
            this.state.isAuthenticated && this.state.loadPage ?
                <div>
                    {dialog}
                    <div class="search">
                        <input type="text"
                               id="search-box"
                               class="search-box"
                               placeholder="search"
                               required=""/>
                        <button class="button add-user-button"
                                onClick={this.handleAddNewUser}>
                            Add User
                        </button>
                    </div>
                    <div class="scrollTable">
                        <table id="userContainer" class="container">
                            <TableHeader/>
                            <tbody>
                            {this.state.rows}
                            </tbody>
                        </table>
                    </div>
                </div>
                :
                this.state.loading ?
                    <table class="container">
                        <TableHeader/>
                        <tbody>
                        <tr>
                            <LoadingSpinner/>
                        </tr>
                        </tbody>
                    </table>
                    :
                    <ReactRedirect location='login'/>
        );
    }
}

var UserRow = React.createClass({
    render() {
        return (
            <tr id='abcd'>
                <td>
                    <a id={this.props.userLinkId}
                       onClick={this.props.onUserClick}>
                        {this.props.name}
                    </a>
                </td>
                <td>{this.props.email}</td>
                <td>{this.props.mobile}</td>
                <td>
                    <div class="active">
                        <input type="checkbox" name="check"
                               checked={this.props.active}
                               onChange={this.props.handleActiveChange}/>
                        <label for="active"
                               id={this.props.userLinkId}
                               onClick={this.props.handleActiveChange}>
                        </label>
                    </div>
                </td>
                <td>
                    <div>
                        <i class="fa fa-times fa-2x"
                           aria-hidden="true"
                           id={this.props.userLinkId}
                           onClick={this.props.handleDelete}
                        />
                    </div>
                </td>
            </tr>
        );
    }

});

var LoadingRow = React.createClass({
    render() {
        return (
            <tr id='abcd'>
                <Loading isLoading={this.props.isLoading}
                         spinner={LoadingSpinner}/>
            </tr>
        );
    }

});